package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.General;
import com.example.shanir.cookingappofshanir.utils.NavigationMenu;
import com.example.shanir.cookingappofshanir.utils.Recipe;
import com.example.shanir.cookingappofshanir.utils.RecipeListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserFavoriteRecipesActivity extends AppCompatActivity {
    private ListView mSavedRecipesListView;
    private FirebaseAuth mFirebaseAuth;
    private RecipeListAdapter mRecipeListAdapter;
    private Recipe mSelectedRecipe;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ImageView mMenuImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_save_recipes);

        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }

        initSavedRecipeListView();
        initMenu();
        retrieveUserFavoriteRecipes();
    }

    private void initMenu() {
        mMenuImageView = findViewById(R.id.right_arrow_image_view);
        mDrawerLayout = findViewById(R.id.mainlayoutsaverecipe);
        mNavigationView = findViewById(R.id.navigation_menu);
        mNavigationView.setNavigationItemSelectedListener
                (new NavigationMenu(this, mMenuImageView, mDrawerLayout));
    }

    private void initSavedRecipeListView() {
        mSavedRecipesListView = findViewById(R.id.lvsaverecipes);
        mRecipeListAdapter = new RecipeListAdapter(this, new ArrayList<Recipe>());
        mSavedRecipesListView.setAdapter(mRecipeListAdapter);

        mSavedRecipesListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(getApplicationContext(), DetailsOnRecipeActivity.class);
            mSelectedRecipe = (Recipe) mRecipeListAdapter.getItem(position);
            i.putExtra("nameOfSelectedRecipe", mSelectedRecipe.getNameOfrecipe());
            startActivity(i);
        });
    }

    private void retrieveUserFavoriteRecipes() {
        DbReference.getDbRefToUserFavoriteRecipes(mFirebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> userFavoriteRecipesNames = (ArrayList<String>) dataSnapshot.getValue();

                        if (userFavoriteRecipesNames == null)
                            return;

                        getFavoriteRecipes(userFavoriteRecipesNames);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void getFavoriteRecipes(ArrayList<String> favoriteRecipesNameList) {
        for (String favoriteRecipeName : favoriteRecipesNameList) {
            DbReference.getDbRefToRecipe(favoriteRecipeName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Recipe recipe = snapshot.getValue(Recipe.class);

                    if (recipe == null)
                        return;

                    fetchRecipeDetails(recipe);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserFavoriteRecipesActivity.this, "There isn't such recipe " + favoriteRecipeName, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchRecipeDetails(Recipe recipe) {
        DbReference.getDbRefToRecipeBitmap(recipe.getBitmap())
                .getBytes(General.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            recipe.setNameBitmap(bitmap);
        }).addOnCompleteListener(task -> {
            mRecipeListAdapter.add(recipe);
        });
    }
}
