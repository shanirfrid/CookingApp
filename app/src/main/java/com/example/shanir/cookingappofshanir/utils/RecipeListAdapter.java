package com.example.shanir.cookingappofshanir.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.shanir.cookingappofshanir.R;
import com.google.firebase.database.annotations.Nullable;
import java.util.List;

public class RecipeListAdapter extends BaseAdapter {
    private Context mContext;
    private List<Recipe> mRecipeList;

    public RecipeListAdapter() {
    }

    public RecipeListAdapter(Context context, List<Recipe> recipeList) {
        mContext = context;
        mRecipeList = recipeList;
    }

    public List<Recipe> getRecipeList(){
        return this.mRecipeList;
    }

    public void add(Recipe recipe) {
        if (mRecipeList.contains(recipe))
            return;

        mRecipeList.add(recipe);
        notifyDataSetChanged();
    }

    public void deleteRecipe(int position) {
        mRecipeList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mRecipeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecipeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.recipe_item_list, null);
        TextView recipeNameTextView = (TextView) v.findViewById(R.id.tvnamerecipeitem);
        TextView recipeDifficultyTextView = (TextView) v.findViewById(R.id.vdifiitem);
        TextView recipeTimeTextView = (TextView) v.findViewById(R.id.tvtimerecipeitem);
        ImageView recipeImageView = (ImageView) v.findViewById(R.id.recipe_image);

        Recipe recipe = (Recipe) getItem(position);
        recipeNameTextView.setText(recipe.getNameOfrecipe());
        recipeTimeTextView.setText(TextFormatter.formatRecipeTime(recipe.getTime()));
        recipeDifficultyTextView.setText(recipe.getDifficulty());
        Bitmap recipeBitmap = recipe.getNameBitmap();

        if (recipeBitmap != null)
            recipeImageView.setImageBitmap(recipeBitmap);

        v.setTag(recipe.gettID());

        return v;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
