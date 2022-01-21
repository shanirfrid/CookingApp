package com.example.shanir.cookingappofshanir.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.SignInActivity;
import com.example.shanir.cookingappofshanir.UserIngredientsActivity;
import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.General;
import com.example.shanir.cookingappofshanir.utils.RecipeListAdapter;
import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.utils.Recipe;
import com.example.shanir.cookingappofshanir.utils.TextFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdminRecipesActivity extends AppCompatActivity {

    private FirebaseAuth mFireBaseAuth;
    private ProgressBar mProgressBar;
    private ImageView mLogoutImageView;
    private ListView mRecipesListView;
    private RecipeListAdapter mRecipeListAdapter;
    private TextView mNumberOfRecipesTextView;
    private Button mAddRecipeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list_of_recipes);
        mProgressBar = findViewById(R.id.pbadminlistofrecipes);
        mNumberOfRecipesTextView = findViewById(R.id.tvheadaminnumrecipe);
        mFireBaseAuth = FirebaseAuth.getInstance();

        initRecipeListView();
        initRecipeAddButton();
        initLogoutImageView();
        retrieveRecipeList();
    }

    private void initLogoutImageView() {
        mLogoutImageView = findViewById(R.id.admin_logout);
        mLogoutImageView.setOnClickListener(v -> {
            mFireBaseAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void initRecipeAddButton() {
        mAddRecipeButton = findViewById(R.id.add_recipe_btn);
        mAddRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AdminAddRecipeActivity.class);
            startActivity(intent);
        });
    }

    private void initRecipeListView() {
        mRecipesListView = findViewById(R.id.listviewlistofallrecipe);
        mRecipeListAdapter = new RecipeListAdapter(this, new ArrayList<Recipe>());
        mRecipesListView.setAdapter(mRecipeListAdapter);
        mRecipesListView.setOnItemClickListener((parent, view, position, id) -> {
            String nameOfSelectedRecipe = mRecipeListAdapter.getRecipeList()
                    .get(position).getNameOfrecipe();
            AlertDialog.Builder builder = new AlertDialog.Builder(AdminRecipesActivity.this);
            builder.setTitle("Delete recipe")
                    .setMessage("Do you approve to delete " +
                            nameOfSelectedRecipe + "?" + "\n" + "Choose here your answer")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogListener(position))
                    .setNegativeButton("No", new DialogListener(position));
            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }

    private class DialogListener implements DialogInterface.OnClickListener {
        private int mPosition;

        public DialogListener(int position) {
            this.mPosition = position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
                String recipeName = mRecipeListAdapter.getRecipeList()
                        .get(mPosition).getNameOfrecipe();

                DbReference.getDbRefToRecipe(recipeName).removeValue();
                mRecipeListAdapter.deleteRecipe(mPosition);
            }
        }
    }

    private void retrieveRecipeList() {
        DbReference.getDbRefToRecipes().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Recipe recipe = postSnapshot.getValue(Recipe.class);

                    if (recipe == null)
                        continue;

                    fetchRecipeDetails(recipe);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchRecipeDetails(Recipe recipe){
        DbReference.getDbRefToRecipeBitmap(recipe.getBitmap())
                .getBytes(General.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            recipe.setNameBitmap(bitmap);
        }).addOnCompleteListener(task -> {
            mRecipeListAdapter.add(recipe);
            mProgressBar.setVisibility(View.GONE);
            mNumberOfRecipesTextView.setText(TextFormatter.foundRecipesNumber(mRecipeListAdapter.getCount()));
        });
    }
}
