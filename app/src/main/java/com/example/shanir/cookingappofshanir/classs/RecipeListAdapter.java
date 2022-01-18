package com.example.shanir.cookingappofshanir.classs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.shanir.cookingappofshanir.Profile;
import com.example.shanir.cookingappofshanir.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
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

    public List<Recipe> getRecipeList(){
        return this.recipeList;
    }

    public void setRecipeList(List<Recipe> recipeList){
        this.recipeList = recipeList;
    }

    public void addRecipe(Recipe recipe) {
        if (this.recipeList.contains(recipe))
            return;

        this.recipeList.add(recipe);
    }

    public void deleteRecipe(int position) {
        this.recipeList.remove(position);
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
        TextView tvkind = (TextView) v.findViewById(R.id.tvkindrecipeitem);
        ImageView ivRecipeImage = (ImageView) v.findViewById(R.id.recipe_image);

        tvname.setText(recipeList.get(position).getNameOfrecipe());
        tvtime.setText(String.valueOf(recipeList.get(position).getTime()));
        tvkind.setText(recipeList.get(position).getKindOfrecipe());
        tvdifficult.setText(recipeList.get(position).getDifficulty());
        Bitmap recipeBitmap = recipeList.get(position).getNameBitmap();
        if (recipeBitmap != null)
            ivRecipeImage.setImageBitmap(recipeBitmap);

        v.setTag(recipeList.get(position).gettID());
        return v;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
