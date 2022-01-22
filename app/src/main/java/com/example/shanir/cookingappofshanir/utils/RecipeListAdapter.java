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
        TextView recipeNameTextView = v.findViewById(R.id.tvnamerecipeitem);
        TextView recipeDifficultTextView = v.findViewById(R.id.vdifiitem);
        TextView recipeTimeTextView = v.findViewById(R.id.tvtimerecipeitem);
        ImageView recipeImageView = v.findViewById(R.id.recipe_image);

        recipeNameTextView.setText(mRecipeList.get(position).getNameOfrecipe());
        recipeTimeTextView.setText(String.valueOf(mRecipeList.get(position).getTime()));
        recipeDifficultTextView.setText(mRecipeList.get(position).getDifficulty());
        Bitmap recipeBitmap = mRecipeList.get(position).getNameBitmap();

        if (recipeBitmap != null)
            recipeImageView.setImageBitmap(recipeBitmap);

        v.setTag(mRecipeList.get(position).gettID());
        return v;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
