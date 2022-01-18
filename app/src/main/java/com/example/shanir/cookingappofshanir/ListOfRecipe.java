package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.classs.Navigation;
import com.example.shanir.cookingappofshanir.classs.Recipe;
import com.example.shanir.cookingappofshanir.classs.RecipeListAdapter;
import com.example.shanir.cookingappofshanir.classs.UserIngredients;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ListOfRecipe extends AppCompatActivity {

    TextView tvhowmanyrecipe;
    ArrayList<String> userIngredients;
    private UserIngredients item;
    Recipe lastSelected;
    ArrayList<Recipe> itemsRecipe;
    ArrayList<Recipe> suitableRecipes;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference postRef, postRefr;
    ListView listView;
    RecipeListAdapter adapter;
    private String tableId;
    ArrayList<String> liststring;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ImageView mRightArrowImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_recipe);
        tvhowmanyrecipe = (TextView) findViewById(R.id.tvnumrecipe);
        listView = (ListView) findViewById(R.id.listviewOfRecipes);
        mRightArrowImageView = (ImageView) findViewById(R.id.right_arrow_image_view);
        firebaseAuth = FirebaseAuth.getInstance();
        liststring = new ArrayList<String>();
        adapter = new RecipeListAdapter(this, new ArrayList<Recipe>());
        listView.setAdapter(adapter);
        setRefToTables();
        datalistingredientuser();
        datatlistingredientrecipe();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastSelected = (Recipe) adapter.getItem(position);
                Intent i = new Intent(getApplicationContext(), DetailsOnRecipe.class);
                i.putExtra("detailsrecipe", lastSelected.getNameOfrecipe());
                startActivity(i);
            }
        });

        navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new Navigation(this));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainLayoutlistofrecipe);
        mRightArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void datalistingredientuser() {

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item = dataSnapshot.getValue(UserIngredients.class);

                if (item == null) //never get here
                {
                    Toast.makeText(getApplicationContext(),
                            "אין לך שום מצרכים " + "\n" + "בבקשה הכנס מצרכים", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MyIngredientsActivity.class);
                    startActivity(intent);
                } else {
                    Setlistuser(item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void Setlistuser(UserIngredients list) {
        if (list != null)
            userIngredients = list.getIngredients();
    }

    public void datatlistingredientrecipe() {
        suitableRecipes = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String tableIdR = General.RECIPE_TABLE_NAME;
        postRefr = FirebaseDatabase.getInstance().getReference(tableIdR);
        postRefr.addValueEventListener(new ValueEventListener() {
            Recipe r;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    r = postSnapshot.getValue(Recipe.class);
                    suitableRecipes.add(r);
                }
                if (suitableRecipes != null)
                    SetlistRecipe(suitableRecipes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void SetlistRecipe(ArrayList<Recipe> recipes) {
        if (userIngredients != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            final long ONE_MEGABYTE = 1024 * 1024 * 5;

            itemsRecipe = recipes;
            suitableRecipes = new ArrayList<Recipe>();
            int suituableRecipesAmount = 0;

            for (Recipe currentRecipe : recipes) {
                if (userIngredients.containsAll(
                        currentRecipe.getListNameIngredientOnRecipe())) {
                    suituableRecipesAmount++;
                    StorageReference storageRef = storage.getReferenceFromUrl
                            ("gs://cookingappofshanir.appspot.com/images/").child(currentRecipe.getBitmap());

                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            currentRecipe.setNameBitmap(bitmap);
                        }

                    }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                        @Override
                        public void onComplete(@NonNull Task<byte[]> task) {
                            suitableRecipes.add(currentRecipe);
                            adapter.addRecipe(currentRecipe);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            tvhowmanyrecipe.setText(
                    resultsMessageBuilder(suituableRecipesAmount));
        } else {
            tvhowmanyrecipe.setText(resultsMessageBuilder(0));
        }
    }

    private static String resultsMessageBuilder(int recipesAmount) {
        switch (recipesAmount) {
            case 0:
                return "No recipe was found!";
            case 1:
                return "1 recipe was found!";
            default:
                return recipesAmount + " recipes were found!";
        }
    }

    private void setRefToTables() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        tableId = General.USER_INGREDIENTS_TABLE_NAME + "/" + uid + "/"+General.USER_INGREDIENTS_SUB_TABLE;
        postRef = FirebaseDatabase.getInstance().getReference(tableId);
    }

}
