package com.example.shanir.cookingappofshanir.activities.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.activities.user.SignInActivity;
import com.example.shanir.cookingappofshanir.utils.db.DbReference;
import com.example.shanir.cookingappofshanir.utils.db.DbConstants;
import com.example.shanir.cookingappofshanir.utils.ProgressBarManager;
import com.example.shanir.cookingappofshanir.utils.RecipeListAdapter;
import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.utils.Recipe;
import com.example.shanir.cookingappofshanir.utils.TextFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class AdminRecipesActivity extends AppCompatActivity {
    private FirebaseAuth mFireBaseAuth;
    private ProgressBarManager mProgressBarManager;
    private ImageView mLogoutImageView;
    private ListView mRecipesListView;
    private RecipeListAdapter mRecipeListAdapter;
    private TextView mNumberOfRecipesTextView;
    private Button mAddRecipeButton;
    private int mRecipesAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_recipes);
        mProgressBarManager = new ProgressBarManager(
                findViewById(R.id.admin_recipes_progress_bar));
        mNumberOfRecipesTextView = findViewById(R.id.admin_recipes_recipes_amount_text_view);
        mFireBaseAuth = FirebaseAuth.getInstance();
        mRecipesAmount = 0;

        initRecipeListView();
        initRecipeAddButton();
        initLogoutImageView();
        setDbRecipesEventListener();
    }

    private void initLogoutImageView() {
        mLogoutImageView = findViewById(R.id.admin_recipes_sign_out_image_view);
        mLogoutImageView.setOnClickListener(v -> {
            mFireBaseAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void initRecipeAddButton() {
        mAddRecipeButton = findViewById(R.id.admin_recipes_add_recipe_button);
        mAddRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AdminAddRecipeActivity.class);
            startActivity(intent);
        });
    }

    private void initRecipeListView() {
        mRecipesListView = findViewById(R.id.admin_recipes_list_view);
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
            }
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

    private void setDbRecipesEventListener() {
        DbReference.getDbRefToRecipes()
                .addChildEventListener(new ChildEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot,
                                             @Nullable String previousChildName) {
                        fetchRecipeDetails(snapshot.getValue(Recipe.class));
                        mNumberOfRecipesTextView.setText(
                                TextFormatter.foundRecipesNumber(++mRecipesAmount));
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot,
                                               @Nullable String previousChildName) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        mRecipeListAdapter
                                .deleteRecipe(snapshot.getValue(Recipe.class));
                        mNumberOfRecipesTextView.setText(
                                TextFormatter.foundRecipesNumber(--mRecipesAmount));
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot,
                                             @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
