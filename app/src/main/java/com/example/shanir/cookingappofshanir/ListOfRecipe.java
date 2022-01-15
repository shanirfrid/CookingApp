package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.classs.Navigation;
import com.example.shanir.cookingappofshanir.classs.Recipe;
import com.example.shanir.cookingappofshanir.classs.RecipeListAdapter;
import com.example.shanir.cookingappofshanir.classs.UserItems;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOfRecipe extends AppCompatActivity {

    TextView tvhowmanyrecipe;
    ArrayList<String> userIngredients;
    private UserItems item;
    Recipe lastSelected;
    ArrayList<Recipe> itemsRecipe;
    ArrayList<Recipe> suitableRecipes;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference postRef,postRefr;;
    ListView listView;
    RecipeListAdapter adapter;
    private  String tableId;
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
                Intent i=new Intent(getApplicationContext(),DetailsOnRecipe.class);
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
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });
    }

    public  void datalistingredientuser()
    {

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    item = postSnapshot.getValue(UserItems.class);
                    item.setUid(firebaseAuth.getCurrentUser().getUid());
                    item.setKey(postSnapshot.getKey());
                }
                if(item==null) //never get here
                {
                    Toast.makeText(getApplicationContext(),"אין לך שום מצרכים "+"\n"+ "בבקשה הכנס מצרכים" ,Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),Consumers.class);
                    startActivity(intent);
                }
                else
                    {
                    Setlistuser(item);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void Setlistuser(UserItems list)
    {
        if (list!=null)
        userIngredients = list.getItems();


    }

    public void datatlistingredientrecipe()
    {
        suitableRecipes = new ArrayList<>();
        firebaseDatabase= FirebaseDatabase.getInstance();
        String uid=General.UIDUSER;
        String tableIdR  = General.RECIPE_TABLE_NAME+"/"+uid;
        postRefr= FirebaseDatabase.getInstance().getReference(tableIdR);
        postRefr.addValueEventListener(new ValueEventListener() {
            Recipe r;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    r = postSnapshot.getValue(Recipe.class);
                    r.settID(firebaseAuth.getCurrentUser().getUid());
                    r.setKey(postSnapshot.getKey());
                    suitableRecipes.add(r);

                }
                if (suitableRecipes !=null)
               SetlistRecipe(suitableRecipes);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void SetlistRecipe(ArrayList<Recipe> recipes)
    {
        if( userIngredients !=null)
        {
            itemsRecipe = recipes;
            suitableRecipes = new ArrayList<Recipe>();

            for (Recipe currentRecipe : recipes)
                if (userIngredients.containsAll(
                        currentRecipe.getListNameIngredientOnRecipe()))
                    suitableRecipes.add(currentRecipe);

            adapter.add(suitableRecipes);
            adapter.notifyDataSetChanged();
            tvhowmanyrecipe.setText(
                    resultsMessageBuilder(suitableRecipes.size()));
        }
        else
        {
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

    private void setRefToTables()
    {
        String uid=firebaseAuth.getCurrentUser().getUid();
        tableId  = General.RECIPE_ITEM_NAME+"/"+uid;
        postRef= FirebaseDatabase.getInstance().getReference(tableId);
    }

}
