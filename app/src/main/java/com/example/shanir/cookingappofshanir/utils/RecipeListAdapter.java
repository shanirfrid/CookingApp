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

/**
 * Created by Shanir on 02/04/2018.
 */

public class RecipeListAdapter extends BaseAdapter {
    private Context context;
    private List<Recipe> recipeList;

    public RecipeListAdapter() {
    }


    public RecipeListAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    public List<Recipe> getRecipeList() {
        return this.recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public void add(Recipe recipe) {
        if (this.recipeList.contains(recipe))
            return;

        this.recipeList.add(recipe);
        notifyDataSetChanged();

    }

    public void deleteRecipe(int position) {
        this.recipeList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return recipeList.size();
    }

    @Override
    public Object getItem(int position) {
        return recipeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.recipe_item_list, null);
        TextView tvname = (TextView) v.findViewById(R.id.tvnamerecipeitem);
        TextView tvdifficult = (TextView) v.findViewById(R.id.vdifiitem);
        TextView tvtime = (TextView) v.findViewById(R.id.tvtimerecipeitem);
        ImageView ivRecipeImage = (ImageView) v.findViewById(R.id.recipe_image);

        Recipe recipe = (Recipe) getItem(position);
        tvname.setText(recipe.getNameOfrecipe());
        tvtime.setText(TextFormatter.formatRecipeTime(recipe.getTime()));
        tvdifficult.setText(recipe.getDifficulty());
        Bitmap recipeBitmap = recipe.getNameBitmap();

        if (recipeBitmap != null)
            ivRecipeImage.setImageBitmap(recipeBitmap);

        v.setTag(recipe.gettID());
        return v;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
