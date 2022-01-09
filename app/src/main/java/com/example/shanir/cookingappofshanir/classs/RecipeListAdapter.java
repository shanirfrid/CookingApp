package com.example.shanir.cookingappofshanir.classs;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shanir.cookingappofshanir.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shanir on 02/04/2018.
 */

public class RecipeListAdapter extends BaseAdapter {
    private Context context;
    private List<Recipe> recipeList;
    private ArrayList<String> items = new ArrayList<>();
    public RecipeListAdapter()
    {}


    public RecipeListAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }
    public ArrayList<String> getItems() {
        return items;
    }
    public void add(List <Recipe> list)
    {
        recipeList = new ArrayList<>();
        for (Recipe r:list)
            recipeList.add(r);
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
        View v=View.inflate(context, R.layout.recipe_item_list,null);
        TextView tvname=(TextView)v.findViewById(R.id.tvnamerecipeitem);
        TextView tvdifficult=(TextView)v.findViewById(R.id.vdifiitem);
        TextView tvtime=(TextView)v.findViewById(R.id.tvtimerecipeitem);
        TextView tvkind=(TextView)v.findViewById(R.id.tvkindrecipeitem);

        tvname.setText("שם:  "+ recipeList.get(position).getNameOfrecipe());
        tvtime.setText("זמן(דקות): "+String.valueOf(recipeList.get(position).getTime())+" דקות ");
        tvkind.setText("סוג המתכון: "+ recipeList.get(position).getKindOfrecipe());
        tvdifficult.setText("קושי: "+ recipeList.get(position).getDifficulty());
        v.setTag(recipeList.get(position).gettID());

        return v;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
