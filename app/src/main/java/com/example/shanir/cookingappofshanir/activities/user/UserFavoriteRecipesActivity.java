package com.example.shanir.cookingappofshanir.activities.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.utils.db.DbReference;
import com.example.shanir.cookingappofshanir.utils.db.DbConstants;
import com.example.shanir.cookingappofshanir.utils.NavigationMenu;
import com.example.shanir.cookingappofshanir.utils.ProgressBarManager;
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
    private ProgressBarManager mProgressBarManager;
    private LinearLayout mNoFavoriteRecipesLayout;
    private RecipeListAdapter mRecipeListAdapter;
    private Recipe mSelectedRecipe;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ImageView mMenuImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favorite_recipes);
        mProgressBarManager = new ProgressBarManager(
                findViewById(R.id.favorite_recipes_progress_bar));
        mNoFavoriteRecipesLayout = findViewById(R.id.favorite_recipe_0_results_layout);
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
        mMenuImageView = findViewById(R.id.favorite_recipes_menu_image_view);
        mDrawerLayout = findViewById(R.id.favorite_recipes_drawer_layout);
        mNavigationView = findViewById(R.id.favorite_recipes_navigation_menu);
        mNavigationView.setNavigationItemSelectedListener
                (new NavigationMenu(this, mMenuImageView, mDrawerLayout));
    }

    private void initSavedRecipeListView() {
        mSavedRecipesListView = findViewById(R.id.favorite_recipes_list_view);
        mRecipeListAdapter = new RecipeListAdapter(this, new ArrayList<>());
        mSavedRecipesListView.setAdapter(mRecipeListAdapter);

        mSavedRecipesListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(getApplicationContext(), DetailsOnRecipeActivity.class);
            mSelectedRecipe = (Recipe) mRecipeListAdapter.getItem(position);
            i.putExtra("comeFromFavoriteRecipes", mSelectedRecipe.getNameOfrecipe());
            startActivity(i);
        });
    }

    private void retrieveUserFavoriteRecipes() {
        DbReference.getDbRefToUserFavoriteRecipes(mFirebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> userFavoriteRecipesNames =
                                (ArrayList<String>) dataSnapshot.getValue();

                        if (userFavoriteRecipesNames == null) {
                            setUINoRecipesView(true);
                            return;
                        }

                        getFavoriteRecipes(userFavoriteRecipesNames);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void setUINoRecipesView(boolean areNoRecipes) {
         mNoFavoriteRecipesLayout.setVisibility(areNoRecipes ? View.VISIBLE :
                 View.GONE);
         mSavedRecipesListView.setVisibility(areNoRecipes ? View.GONE :
                 View.VISIBLE);
    }

    private void getFavoriteRecipes(ArrayList<String> favoriteRecipesNameList) {
        setUINoRecipesView(false);

        for (String favoriteRecipeName : favoriteRecipesNameList) {
            DbReference.getDbRefToRecipe(favoriteRecipeName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Recipe recipe = snapshot.getValue(Recipe.class);

                    if (recipe == null)
                        return;

                    fetchRecipeDetails(recipe);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UserFavoriteRecipesActivity.this,
                            "There isn't such recipe " + favoriteRecipeName, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchRecipeDetails(Recipe recipe) {
        mProgressBarManager.requestVisible();
        DbReference.getDbRefToRecipeBitmap(recipe.getBitmap())
                .getBytes(DbConstants.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            recipe.setNameBitmap(bitmap);
        }).addOnCompleteListener(task -> {
            mRecipeListAdapter.add(recipe);
            mProgressBarManager.requestGone();
        });
    }
}
