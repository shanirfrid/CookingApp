package com.example.shanir.cookingappofshanir;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.utils.DbConstants;
import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.ImageUtilities;
import com.example.shanir.cookingappofshanir.utils.Ingredient;
import com.example.shanir.cookingappofshanir.utils.Recipe;
import com.example.shanir.cookingappofshanir.utils.TextFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailsOnRecipeActivity extends AppCompatActivity {
    private TextView mRecipeNameTextView, mRecipeDifficultyTextView, mRecipeTimeTextView;
    private Button mFavoriteRecipeButton, mMakeRecipeButton;
    private ImageView mRecipeImageView;
    private ListView mIngredientsListView;
    private ProgressBar mProgressBar;
    private String mRecipeTime, mRecipeName = "";
    private FirebaseAuth mFireBaseAuth;
    private IngredientsListAdapter mIngredientsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_on_recipe);
        mProgressBar = findViewById(R.id.progressbardetailss);
        mRecipeNameTextView = findViewById(R.id.tvheadnameOfrecipe);
        mRecipeDifficultyTextView = findViewById(R.id.tvheaddificultyOfrecipe);
        mRecipeTimeTextView = findViewById(R.id.tvheadtimefrecipe);
        mRecipeImageView = findViewById(R.id.ivdetailsrecipe);

        mFireBaseAuth = FirebaseAuth.getInstance();

        initFavoriteRecipeButton();
        initIngredientsListView();
        retrieveRecipeDetails();
        initMakeRecipeButton();

        mRecipeNameTextView.setText(mRecipeName);
        setListViewHeightBasedOnChildren(mIngredientsListView);
    }

    private void initMakeRecipeButton() {
        mMakeRecipeButton = findViewById(R.id.btmadenow);
        mMakeRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DetailsOnRecipeActivity.this, TimerActivity.class);
            intent.putExtra("totalTimeInMinutes", Integer.parseInt(mRecipeTime));
            startActivity(intent);
        });
    }

    private void initIngredientsListView() {
        mIngredientsListView = findViewById(R.id.lvdetailsonrecipe);
        mIngredientsListAdapter = new IngredientsListAdapter(this, new ArrayList<>());
        mIngredientsListView.setAdapter(mIngredientsListAdapter);
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

    private void initFavoriteRecipeButton() {
        mFavoriteRecipeButton = findViewById(R.id.btsaverecipedeails);
        Intent i = getIntent();
        if (i.getExtras() != null) {
            mRecipeName = i.getExtras().getString("comeFromSuitableRecipes",
                    "");
            if (mRecipeName.equals("")) {
                mRecipeName = i.getExtras().getString("comeFromFavoriteRecipes");
                mFavoriteRecipeButton.setEnabled(false);
                mFavoriteRecipeButton.setVisibility(View.GONE);
            }
        }
        mFavoriteRecipeButton.setOnClickListener(v -> addRecipeToFavoriteRecipes());
    }

    private void fetchRecipeDetails(Recipe recipe) {
        mRecipeNameTextView.setText(mRecipeName);
        mRecipeDifficultyTextView.setText(recipe.getDifficulty());
        mRecipeTime = Integer.toString(recipe.getTime());
        mRecipeTimeTextView.setText(TextFormatter.formatRecipeTime(Integer.parseInt(mRecipeTime)));
        if (!recipe.getBitmap().equals("none"))
            ImageUtilities.loadImage(
                    DbConstants.APP_RECIPE_IMAGES_FULL_URL +
                            recipe.getBitmap(), mRecipeImageView, mProgressBar);
    }

    private void retrieveRecipeDetails() {
        DbReference.getDbRefToRecipe(mRecipeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);
                if (recipe == null)
                    return;

                fetchRecipeDetails(recipe);
                mIngredientsListAdapter.setIngredientList(recipe.getIngredientList());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addRecipeToFavoriteRecipes() {
        HashMap<String, Object> recipeFavoriteNamesMap = new HashMap<>();
        DbReference.getDbRefToUserFavoriteRecipes(mFireBaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                ArrayList<String> recipesFavoriteNames = (ArrayList<String>) snapshot.getValue();

                if (recipesFavoriteNames != null) {
                    if (recipesFavoriteNames.contains(mRecipeName)) {
                        Toast.makeText(DetailsOnRecipeActivity.this,
                                "You already added this recipe to your favorite recipes",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                recipeFavoriteNamesMap.put(String.valueOf(count + 1), mRecipeName);
                DbReference.getDbRefToUserFavoriteRecipes(mFireBaseAuth.getUid())
                        .updateChildren(recipeFavoriteNamesMap).addOnCompleteListener(
                        task -> {
                            Intent intent = new Intent(
                                    DetailsOnRecipeActivity.this,
                                    UserFavoriteRecipesActivity.class);
                            startActivity(intent);
                            finish();
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private class IngredientsListAdapter extends BaseAdapter {
        ArrayList<Ingredient> mIngredientList;
        Context mContext;

        public IngredientsListAdapter(Context context,
                                      ArrayList<Ingredient> ingredientList) {
            mIngredientList = ingredientList;
            mContext = context;
            setListViewHeightBasedOnChildren(mIngredientsListView);
        }

        public void add(Ingredient ingredient) {
            mIngredientList.add(ingredient);
            mIngredientsListAdapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(mIngredientsListView);
        }

        public void setIngredientList(ArrayList<Ingredient> ingredientList) {
            mIngredientList = ingredientList;
            notifyDataSetChanged();
            setListViewHeightBasedOnChildren(mIngredientsListView);
        }

        @Override
        public int getCount() {
            return mIngredientList.size();
        }

        @Override
        public Object getItem(int i) {
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
                    .inflate(R.layout.ingredient_and_unit_item_layout, null);

            Ingredient ingredient = mIngredientList.get(i);
            ((TextView) view.findViewById(R.id.ingredient_unit_textview))
                    .setText(ingredient.getUnits());
            ((TextView) view.findViewById(R.id.ingredient_name_textview))
                    .setText(ingredient.getName());
            view.findViewById(R.id.delete_ingredient_with_unit_image_view)
                    .setVisibility(View.GONE);

            return view;
        }
    }
}
