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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.General;
import com.example.shanir.cookingappofshanir.utils.NavigationMenu;
import com.example.shanir.cookingappofshanir.utils.Recipe;
import com.example.shanir.cookingappofshanir.utils.RecipeListAdapter;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserSuitableRecipesActivity extends AppCompatActivity {

    TextView mRecipesNumberTextView;
    FirebaseAuth mFirebaseAuth;
    ListView mRecipeListView;
    RecipeListAdapter mRecipeListAdapter;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ImageView mMenuImageView;
    final long ONE_MEGABYTE = 1024 * 1024 * 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_recipe);
        mRecipesNumberTextView = findViewById(R.id.tvnumrecipe);
        mMenuImageView = findViewById(R.id.right_arrow_image_view);
        mFirebaseAuth = FirebaseAuth.getInstance();

        initRecipeList();
        retrieveUserIngredients();

        mRecipesNumberTextView.setText(
                resultsMessageBuilder(mRecipeListAdapter.getCount()));

        mDrawerLayout = findViewById(R.id.mainLayoutConsumers);
        mNavigationView = findViewById(R.id.navigation_menu);
        mNavigationView.setNavigationItemSelectedListener(new NavigationMenu(this, mMenuImageView, mDrawerLayout));
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

    public void retrieveUserIngredients() {
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
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Recipe recipe = postSnapshot.getValue(Recipe.class);

                    if (recipe == null)
                        continue;

                    if (!userIngredients.containsAll(
                            recipe.getListNameIngredientOnRecipe()))
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
        DbReference.getDbRefToRecipeImages()
                .child(recipe.getBitmap())
                .getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    recipe.setNameBitmap(bitmap);
                })
                .addOnCompleteListener(task -> {
                    mRecipeListAdapter.add(recipe);
                    mRecipesNumberTextView.setText(
                            resultsMessageBuilder(mRecipeListAdapter.getCount()));
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
