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
    Collection<String> mUserIngredientList;
    ArrayList<Recipe> itemsRecipe;
    ArrayList<Recipe> mSuitableRecipesList;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference mUserIngredientsDbRef, mReferenceToRecipeTableDb;
    ListView mRecipeListView;
    RecipeListAdapter mRecipeListAdapter;
    private String mUserIngredientsTablePath;
    ArrayList<String> liststring;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ImageView mMenuImageView;
    FirebaseStorage mFirebaseStorage;
    final long ONE_MEGABYTE = 1024 * 1024 * 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_recipe);
        mRecipesNumberTextView = findViewById(R.id.tvnumrecipe);

        mMenuImageView = findViewById(R.id.right_arrow_image_view);
        mFirebaseAuth = FirebaseAuth.getInstance();
        liststring = new ArrayList<String>();
        mFirebaseStorage = FirebaseStorage.getInstance();

        initRecipeList();
        setUserIngredientsDbRef();
        setRecipeDbRef();
        retrieveUserIngredients();
//        datatlistingredientrecipe();

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

        mRecipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe selectedRecipe = (Recipe) mRecipeListAdapter.getItem(position);
                Intent i = new Intent(getApplicationContext(), DetailsOnRecipeActivity.class);
                i.putExtra("detailsrecipe", selectedRecipe.getNameOfrecipe());
                startActivity(i);
            }
        });
    }

    public void retrieveUserIngredients() {
        mUserIngredientsDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
        mReferenceToRecipeTableDb.addListenerForSingleValueEvent(new ValueEventListener() {
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
        StorageReference storageRef = mFirebaseStorage.getReferenceFromUrl
                (General.RECIPE_IMAGES_URL).child(recipe.getBitmap());

        storageRef.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        recipe.setNameBitmap(bitmap);
                    }

                })
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        mRecipeListAdapter.add(recipe);
                        mRecipesNumberTextView.setText(
                                resultsMessageBuilder(mRecipeListAdapter.getCount()));
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

    private void setUserIngredientsDbRef() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserIngredientsTablePath = General.USER_INGREDIENTS_TABLE_NAME +
                "/" + userId + "/" + General.USER_INGREDIENTS_SUB_TABLE_NAME;
        mUserIngredientsDbRef = FirebaseDatabase.getInstance()
                .getReference(mUserIngredientsTablePath);
    }

    private void setRecipeDbRef() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        String tableIdR = General.RECIPE_TABLE_NAME;
        mReferenceToRecipeTableDb = FirebaseDatabase.getInstance().getReference(tableIdR);
    }

}
