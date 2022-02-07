package com.example.shanir.cookingappofshanir.activities.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.utils.db.DbReference;
import com.example.shanir.cookingappofshanir.utils.db.DbConstants;
import com.example.shanir.cookingappofshanir.utils.NavigationMenu;
import com.example.shanir.cookingappofshanir.utils.ProgressBarManager;
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
    private ProgressBarManager mProgressBarManager;
    private ListView mRecipeListView;
    private RecipeListAdapter mRecipeListAdapter;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ImageView mMenuImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_suitable_recipes);
        mRecipesNumberTextView = findViewById(R.id.suitable_recipes_amount_text_view);
        mProgressBarManager = new ProgressBarManager(
                findViewById(R.id.suitable_recipes_progress_bar));
        mFirebaseAuth = FirebaseAuth.getInstance();

        initRecipeList();
        initMenu();
        retrieveUserIngredients();
    }

    private void initMenu() {
        mMenuImageView = findViewById(R.id.suitable_recipes_menu_image_view);
        mDrawerLayout = findViewById(R.id.suitable_recipes_drawer_layout);
        mNavigationView = findViewById(R.id.suitable_recipes_navigation_menu);
        mNavigationView.setNavigationItemSelectedListener
                (new NavigationMenu(this, mMenuImageView, mDrawerLayout));
    }

    private void initRecipeList() {
        mRecipeListView = findViewById(R.id.suitable_recipes_list_view);
        mRecipeListAdapter = new RecipeListAdapter(getApplicationContext(), new ArrayList<>());
        mRecipeListView.setAdapter(mRecipeListAdapter);

        mRecipeListView.setOnItemClickListener((parent, view, position, id) -> {
            Recipe selectedRecipe = (Recipe) mRecipeListAdapter.getItem(position);
            Intent i = new Intent(getApplicationContext(), DetailsOnRecipeActivity.class);
            i.putExtra("comeFromSuitableRecipes", selectedRecipe.getNameOfrecipe());
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

                        if (userIngredientsMap == null) {
                            mRecipesNumberTextView.setText(
                                    TextFormatter.foundRecipesNumber(0));
                            return;
                         }

                        getSuitableRecipes(userIngredientsMap.values());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        indicateErrorToUser();
                    }
                });
    }

    private void getSuitableRecipes(Collection<String> userIngredients) {
        DbReference.getDbRefToRecipes().addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int recipesAmount = 0;
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
                    mRecipesNumberTextView.setText(
                            TextFormatter.foundRecipesNumber(++recipesAmount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                indicateErrorToUser();
            }
        });
    }

    private void fetchRecipeDetails(Recipe recipe) {
        mProgressBarManager.requestVisible();
        DbReference.getDbRefToRecipeBitmap(recipe.getBitmap())
                .getBytes(DbConstants.ONE_MEGABYTE)
                .addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    recipe.setNameBitmap(bitmap);
                })
                .addOnCompleteListener(task -> {
                    mRecipeListAdapter.add(recipe);
                    mProgressBarManager.requestGone();
                });
    }

    private void indicateErrorToUser() {
        Toast.makeText(UserSuitableRecipesActivity.this,
                "OOPS! something went wrong...",
                Toast.LENGTH_SHORT).show();
        mRecipesNumberTextView.setText(TextFormatter.foundRecipesNumber(0));
    }
}
