package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.DbConstants;
import com.example.shanir.cookingappofshanir.utils.NavigationMenu;
import com.example.shanir.cookingappofshanir.utils.Recipe;
import com.example.shanir.cookingappofshanir.utils.RecipeListAdapter;
import com.example.shanir.cookingappofshanir.utils.TextFormatter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserSuitableRecipesActivity extends AppCompatActivity {

    private TextView mRecipesNumberTextView;
    private FirebaseAuth mFirebaseAuth;
    private ListView mRecipeListView;
    private RecipeListAdapter mRecipeListAdapter;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ImageView mMenuImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_recipe);
        mRecipesNumberTextView = findViewById(R.id.tvnumrecipe);
        mFirebaseAuth = FirebaseAuth.getInstance();

        this.initRecipeList();
        this.initMenu();
        retrieveUserIngredients();

        mRecipesNumberTextView.setText(
                TextFormatter.foundRecipesNumber(mRecipeListAdapter.getCount()));

    }

    private void initMenu() {
        mMenuImageView = findViewById(R.id.right_arrow_image_view);
        mDrawerLayout = findViewById(R.id.mainLayoutlistofrecipe);
        mNavigationView = findViewById(R.id.navigation_menu);
        mNavigationView.setNavigationItemSelectedListener
                (new NavigationMenu(this, mMenuImageView, mDrawerLayout));
    }

    private void initRecipeList() {
        mRecipeListView = findViewById(R.id.listviewOfRecipes);
        mRecipeListAdapter = new RecipeListAdapter(getApplicationContext(), new ArrayList<>());
        mRecipeListView.setAdapter(mRecipeListAdapter);

        mRecipeListView.setOnItemClickListener((parent, view, position, id) -> {
            Recipe selectedRecipe = (Recipe) mRecipeListAdapter.getItem(position);
            Intent i = new Intent(getApplicationContext(), DetailsOnRecipeActivity.class);
            i.putExtra("detailsrecipe", selectedRecipe.getNameOfrecipe());
            startActivity(i);
        });
    }

    private void retrieveUserIngredients() {
        DbReference.getDbRefToUserIngredients(mFirebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, String> userIngredientsMap =
                                (HashMap<String, String>) dataSnapshot.getValue();

                        if (userIngredientsMap == null)
                            return;

                        getSuitableRecipes(userIngredientsMap.values());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getSuitableRecipes(Collection<String> userIngredients) {
        DbReference.getDbRefToRecipes().addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Recipe recipe = postSnapshot.getValue(Recipe.class);

                    if (recipe == null)
                        continue;

                    ArrayList<String> recipeIngredients = new ArrayList<>();

                    recipe.getIngredientList().forEach(ingredient ->
                            recipeIngredients.add(ingredient.getName())
                    );

                    if (!userIngredients.containsAll(recipeIngredients))
                        continue;

                    fetchRecipeDetails(recipe);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchRecipeDetails(Recipe recipe) {
        DbReference.getDbRefToRecipeBitmap(recipe.getBitmap())
                .getBytes(DbConstants.ONE_MEGABYTE)
                .addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    recipe.setNameBitmap(bitmap);
                })
                .addOnCompleteListener(task -> {
                    mRecipeListAdapter.add(recipe);
                    mRecipesNumberTextView.setText(
                            TextFormatter.foundRecipesNumber(mRecipeListAdapter.getCount()));
                });
    }

}
