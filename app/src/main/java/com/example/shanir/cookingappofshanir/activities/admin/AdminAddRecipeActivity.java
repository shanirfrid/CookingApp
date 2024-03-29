package com.example.shanir.cookingappofshanir.activities.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.shanir.cookingappofshanir.activities.user.ImagePromptActivity;
import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.utils.db.DbConstants;
import com.example.shanir.cookingappofshanir.utils.db.DbReference;
import com.example.shanir.cookingappofshanir.utils.Ingredient;
import com.example.shanir.cookingappofshanir.utils.Permission;
import com.example.shanir.cookingappofshanir.utils.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdminAddRecipeActivity extends ImagePromptActivity {
    private Button mAddRecipeButton;
    private EditText mTimeInMinutesEditText;
    private RadioGroup mDifficultyRadioGroup;
    private ImageView mAddIngredientImageView;
    private EditText mIngredientEditText, mRecipeNameEditText;
    private ListView mIngredientsListView;
    private IngredientsListAdapter mIngredientsListAdapter;
    private EditText mIngredientUnitsEditText;
    private String mDifficulty = "";
    private Recipe mRecipe;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_recipe);
        mTimeInMinutesEditText = findViewById(R.id.add_recipe_time_edit_text);
        mRecipeNameEditText = findViewById(R.id.add_recipe_name_text_view);
        mIngredientEditText = findViewById(R.id.add_recipe_ingredient_name_edit_text);
        mIngredientUnitsEditText = findViewById(R.id.add_recipe_ingredien_units_edit_text);
        mProgressBar = findViewById(R.id.add_recipe_progress_bar);
        mImageFileNameCamera = DbConstants.ADD_RECIPE_IMAGE_FILE_NAME_CAMERA;

        initRecipeImageView();
        initDifficultyRadioGroupButton();
        initAddRecipeButton();
        initAddIngredientImageView();
        initIngredientsListView();
        setListViewHeightBasedOnChildren(mIngredientsListView);
    }

    private void initIngredientsListView() {
        mIngredientsListView = findViewById(R.id.add_recipe_ingredients_list_view);
        mIngredientsListAdapter = new IngredientsListAdapter(this,
                new ArrayList<>());
        mIngredientsListView.setAdapter(mIngredientsListAdapter);
    }

    private void initRecipeImageView() {
        mImageView = findViewById(R.id.add_recipe_image_view);
        mImageView.setOnClickListener(v -> {
            Permission permission = new Permission(
                    AdminAddRecipeActivity.this, getApplicationContext());
            permission.requestMultiplePermissions();
            showPictureDialog();
        });
    }

    private void initAddRecipeButton() {
        mAddRecipeButton = findViewById(R.id.add_recipe_save_button);
        mAddRecipeButton.setOnClickListener(v -> {
            addRecipe();
        });
    }

    private void initDifficultyRadioGroupButton() {
        mDifficultyRadioGroup = findViewById(R.id.add_recipe_difficulty_radio_group);
        mDifficultyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.add_recipe_easy_radio_button) {
                mDifficulty = "Easy";
            } else if (checkedId == R.id.add_recipe_medium_radio_button) {
                mDifficulty = "Medium";
            } else if (checkedId == R.id.add_recipe_hard_radio_button) {
                mDifficulty = "Hard";
            }
        });
    }

    private void    initAddIngredientImageView() {
        mAddIngredientImageView = findViewById(R.id.add_recipe_add_ingredient_button);
        mAddIngredientImageView.setOnClickListener(v -> {

            String ingredientName = mIngredientEditText.getText().toString(),
                    ingredientUnits = mIngredientUnitsEditText.getText().toString();

            if (ingredientName.trim().isEmpty()) {
                mIngredientEditText.setError("Enter an ingredient");
                return;
            }
            if (ingredientUnits.trim().isEmpty()) {
                mIngredientUnitsEditText.setError("Enter ingredient units");
                return;
            }

            Ingredient ingredientToAdd = new Ingredient(ingredientName, ingredientUnits);
            mIngredientsListAdapter.add(ingredientToAdd);
            setListViewHeightBasedOnChildren(mIngredientsListView);

            mIngredientEditText.setText("");
            mIngredientUnitsEditText.setText("");
            mIngredientUnitsEditText.requestFocus();
        });
    }

    private int convertTextToTime(String text) {
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 50;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    protected void uploadImage(String directoryImage) {
        mBitmapName = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        DbReference.getDbRefToImageBitmap(directoryImage, mBitmapName)
                .putFile(mImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    mRecipe.setBitmap(mBitmapName);
                    uploadRecipe();
                })
                .addOnFailureListener(e -> {
                        Toast.makeText(this,
                                "Failed to upload image",
                                Toast.LENGTH_SHORT).show();
                        updateIsUploadInProgress(false);
                });
    }

    private void uploadRecipe() {
        DbReference.getDbRefToRecipe(mRecipe.getNameOfrecipe())
                .setValue(mRecipe)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(AdminAddRecipeActivity.this,
                            "Recipe has been saved successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnCompleteListener(task ->
                        updateIsUploadInProgress(false));
    }

    private void addRecipe() {
        mRecipe = new Recipe();

        if (mTimeInMinutesEditText.getText().toString().trim().isEmpty()) {
            mTimeInMinutesEditText.setError("Time is required");
            mTimeInMinutesEditText.requestFocus();
            return;
        }

        if (mRecipeNameEditText.getText().toString().trim().isEmpty()) {
            mRecipeNameEditText.setError("You need to insert a recipe name");
            mRecipeNameEditText.requestFocus();
            return;
        }

        if (mDifficulty.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "You need to insert a difficult", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mIngredientsListAdapter.getList().isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "You need to insert an ingredient", Toast.LENGTH_SHORT).show();
            return;
        }
        updateIsUploadInProgress(true);
        mRecipe.setNameOfrecipe(mRecipeNameEditText.getText().toString());
        mRecipe.setDifficulty(mDifficulty);
        mRecipe.setTime(convertTextToTime(mTimeInMinutesEditText.getText().toString()));
        mRecipe.setIngredientList(mIngredientsListAdapter.getList());

        DbReference.getDbRefToRecipe(mRecipe.getNameOfrecipe())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            updateIsUploadInProgress(false);
                            Toast.makeText(AdminAddRecipeActivity.this, "This recipe is already exists",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (mImageUri != null)
                            uploadImage(DbConstants.RECIPE_IMAGES_URL);
                        else
                            uploadRecipe();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminAddRecipeActivity.this,
                                "Error occurred - Could not save recipe",
                                Toast.LENGTH_SHORT).show();
                        updateIsUploadInProgress(false);
                    }
                });
    }

    private void updateIsUploadInProgress(boolean inProgress) {
        mProgressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        mAddRecipeButton.setEnabled(!inProgress);
    }

    private class DialogListener implements DialogInterface.OnClickListener {
        private final int position;

        public DialogListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
                mIngredientsListAdapter.remove(position);
            }
        }
    }

    public class IngredientsListAdapter extends BaseAdapter {
        ArrayList<Ingredient> mIngredientList;
        Context mContext;

        public IngredientsListAdapter(Context context,
                                      ArrayList<Ingredient> ingredientList) {
            mIngredientList = ingredientList;
            mContext = context;
        }

        public void add(Ingredient ingredient) {
            if (mIngredientList.contains(ingredient))
                return;

            mIngredientList.add(ingredient);
            this.notifyDataSetChanged();
        }

        public void remove(int position) {
            mIngredientList.remove(mIngredientList.get(position));
            notifyDataSetChanged();
            setListViewHeightBasedOnChildren(mIngredientsListView);
        }

        public ArrayList<Ingredient> getList() {
            return mIngredientList;
        }

        @Override
        public int getCount() {
            return mIngredientList.size();
        }

        @Override
        public Ingredient getItem(int i) {
            return mIngredientList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.ingredient_combo_list_item, null);

            Ingredient ingredient = mIngredientList.get(i);
            ((TextView) view.findViewById(R.id.ingredient_combo_unit_text_view))
                    .setText(ingredient.getUnits());
            ((TextView) view.findViewById(R.id.ingredient_combo_name_text_view))
                    .setText(ingredient.getName());
            ImageView imageView = view.findViewById(R.id.ingredient_combo_delete_image_view);
            imageView.setTag(i);
            String ingredientName = mIngredientsListAdapter.getItem(i).getName();
            imageView.setOnClickListener(view1 -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddRecipeActivity.this);
                builder.setTitle("Delete ingredient")
                        .setMessage("Do you approve to delete " +
                                ingredientName + "?" + "\n" + "Choose here your answer")
                        .setCancelable(true)
                        .setNegativeButton("No", new DialogListener((int) view1.getTag()))
                        .setPositiveButton("Yes", new DialogListener((int) view1.getTag()));
                AlertDialog dialog = builder.create();
                dialog.show();
            });
            return view;
        }
    }
}

