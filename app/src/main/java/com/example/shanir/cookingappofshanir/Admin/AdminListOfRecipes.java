package com.example.shanir.cookingappofshanir.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.MainActivity;
import com.example.shanir.cookingappofshanir.classs.RecipeListAdapter;
import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.classs.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminListOfRecipes extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    FirebaseAuth firebaseAuth;
    ProgressBar pb;
    Context context = this;
    ImageView mLogoutImg;
    DatabaseReference mReferenceToAllRecipes;
    private ListView mRecipesListView;
    RecipeListAdapter mRecipeListAdapter;
    TextView tvNumberOfRecipes;
    Button addRecipeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list_of_recipes);
        mRecipesListView = (ListView) findViewById(R.id.listviewlistofallrecipe);
        pb = (ProgressBar) findViewById(R.id.pbadminlistofrecipes);
        firebaseAuth = FirebaseAuth.getInstance();
        tvNumberOfRecipes = (TextView) findViewById(R.id.tvheadaminnumrecipe);
        addRecipeBtn = (Button) findViewById(R.id.add_recipe_btn);
        mLogoutImg = (ImageView) findViewById(R.id.admin_logout);


        initRecipeListView();
        setReferenceToAllRecipes();
        retrieveRecipeList();

        addRecipeBtn.setOnClickListener(this);
        mLogoutImg.setOnClickListener(this);
        mRecipesListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mRecipesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminListOfRecipes.this);
                final int pos = position;
                List<Recipe> recipeList = mRecipeListAdapter.getRecipeList();
                String nameOfSelectedRecipe = recipeList.get(pos).getNameOfrecipe();
                builder.setTitle("Delete recipe                        ");
                builder.setMessage("Do you approve to delete " +
                        nameOfSelectedRecipe + " ?" + "\n" + "choose here your answer");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String tableIdR = General.RECIPE_TABLE_NAME;
                        String recipeName = recipeList.get(pos).getNameOfrecipe();
                        DatabaseReference referenveToSelectedRecipe = FirebaseDatabase.getInstance().getReference(tableIdR + "/" + recipeName);
                        referenveToSelectedRecipe.removeValue();

                        //TODO - FIX DELETE
                       //initRecipeListView();
                        recipeList.remove(pos);
                        mRecipeListAdapter = new RecipeListAdapter(AdminListOfRecipes.this, recipeList);
                        mRecipesListView.setAdapter(mRecipeListAdapter);
                        retrieveRecipeList();

                        Toast.makeText(context, "Recipe was deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context, "You choose not to delete the recipe", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.admin_logout:
                firebaseAuth.signOut();
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;

            case R.id.add_recipe_btn:
                intent = new Intent(getApplicationContext(), AddDetailsOnRecipeAdmin.class);
                startActivity(intent);
                break;

        }
    }

    private void initRecipeListView() {
        mRecipeListAdapter = new RecipeListAdapter(this, new ArrayList<Recipe>());
        mRecipesListView.setAdapter(mRecipeListAdapter);
    }


    private void setReferenceToAllRecipes() {
        mReferenceToAllRecipes = FirebaseDatabase.getInstance().getReference(General.RECIPE_TABLE_NAME);
    }

    public void retrieveRecipeList() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        mReferenceToAllRecipes.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Recipe recipe = postSnapshot.getValue(Recipe.class);

                    StorageReference storageRef = storage.getReferenceFromUrl
                            ("gs://cookingappofshanir.appspot.com/images/").child
                            (recipe.getBitmap());

                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            recipe.setNameBitmap(bitmap);
                        }

                    }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                        @Override
                        public void onComplete(@NonNull Task<byte[]> task) {
                            mRecipeListAdapter.addRecipe(recipe);
                            mRecipeListAdapter.notifyDataSetChanged();
                        }
                    });

                }


                Log.d("dd", "Number of recipes : " + snapshot.getChildrenCount());
                tvNumberOfRecipes.setText(resultsMessageBuilder((int) snapshot.getChildrenCount()));

                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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


}
