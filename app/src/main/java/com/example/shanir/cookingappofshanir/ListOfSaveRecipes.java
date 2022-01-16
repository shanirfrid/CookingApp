package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.classs.Navigation;
import com.example.shanir.cookingappofshanir.classs.Recipe;
import com.example.shanir.cookingappofshanir.classs.RecipeListAdapter;
import com.example.shanir.cookingappofshanir.classs.UserItems;
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
import java.util.List;

public class ListOfSaveRecipes extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView mSavedRecipesListView;
    String mNewRecipeName = "";
    FirebaseAuth firebaseAuth;
    DatabaseReference mReferenceToUserSavedRecipes;
    DatabaseReference mReferenceToAllRecipes;
    private String mPathToUserSavedRecipes;
    private String mPathToAllRecipes;
    private UserItems mUserItems;
    RecipeListAdapter mRecipeListAdapter;
    Recipe mSelectedRecipe;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ImageView mOpenMenuImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_save_recipes);

        mSavedRecipesListView = (ListView) findViewById(R.id.lvsaverecipes);
        mOpenMenuImageView = (ImageView) findViewById(R.id.right_arrow_image_view);
        mNavigationView = findViewById(R.id.navigation_menu);


        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        initSavedRecipeListView();
        initMenu();
        setReferenceToUserSaveRecipes();
        setReferenceToAllRecipes();
        retrieveData();

        mSavedRecipesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe selectedRecipe = (Recipe) mRecipeListAdapter.getItem(position);
                Intent i = new Intent(getApplicationContext(), DetailsOnRecipe.class);
                i.putExtra("detailslistofsaverecipe", selectedRecipe.getNameOfrecipe());
                startActivity(i);
            }
        });
    }

    private void initMenu() {
        mNavigationView.setNavigationItemSelectedListener(new Navigation(this));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainlayoutsaverecipe);
        mOpenMenuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initSavedRecipeListView() {
        mRecipeListAdapter = new RecipeListAdapter(this, new ArrayList<Recipe>());
        mSavedRecipesListView.setAdapter(mRecipeListAdapter);

        mSavedRecipesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedRecipe = (Recipe) mRecipeListAdapter.getItem(position);
                Intent i = new Intent(getApplicationContext(), DetailsOnRecipe.class);
                i.putExtra("detailsSaverecipe", mSelectedRecipe.getNameOfrecipe());
                startActivity(i);
            }
        });
    }

    public void setEventListenerForGettingRecipes(ArrayList<String> recipesNameList) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final long ONE_MEGABYTE = 1024 * 1024 * 5;

        mReferenceToAllRecipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Recipe recipe = postSnapshot.getValue(Recipe.class);

                    if (recipesNameList.contains(recipe.getNameOfrecipe())) {
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
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    // Get Item list from database
    public void retrieveData() {
        mReferenceToUserSavedRecipes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserItems = dataSnapshot.getValue(UserItems.class);

                if (mUserItems == null)
                    mUserItems = new UserItems();

                Intent intent = getIntent();
                if (intent.getExtras() != null) {
                    mNewRecipeName = intent.getExtras().getString("nameofrecipedetails");
                    if (!mUserItems.getItems().contains(mNewRecipeName))
                        mUserItems.add(mNewRecipeName);
                }
                setEventListenerForGettingRecipes(mUserItems.getItems());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setReferenceToUserSaveRecipes() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        mPathToUserSavedRecipes = General.SAVE_RECIPES + "/" + uid;
        mReferenceToUserSavedRecipes = FirebaseDatabase.getInstance().getReference(mPathToUserSavedRecipes);
    }

    private void setReferenceToAllRecipes() {
        mPathToAllRecipes = General.RECIPE_TABLE_NAME;
        mReferenceToAllRecipes = FirebaseDatabase.getInstance().getReference(mPathToAllRecipes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

            case R.id.mnItemConsumers:
                Intent intent2 = new Intent(getBaseContext(), Consumers.class);
                startActivity(intent2);
                break;
            case R.id.mnItemListOfRecipes:
                Intent intent3 = new Intent(getBaseContext(), ListOfRecipe.class);
                startActivity(intent3);
                break;

            case R.id.mnItemProfile:
                Intent intent6 = new Intent(getBaseContext(), Profile.class);
                startActivity(intent6);
                break;
            case R.id.mnItemListofsaverecipes:
                Toast.makeText(getApplicationContext(), "אתה נמצא במסך זה", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getApplicationContext(), DetailsOnRecipe.class);
        mSelectedRecipe = (Recipe) mRecipeListAdapter.getItem(position);
        i.putExtra("detailslistofsaverecipe", mSelectedRecipe.getNameOfrecipe());
        i.putExtra("numlistofsverecipe", 0);
        startActivity(i);
    }
}
