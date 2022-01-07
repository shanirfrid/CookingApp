package com.example.shanir.cookingappofshanir.classs;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.classs.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shanir on 02/04/2018.
 */

public class ProductListAdapter extends BaseAdapter {
    private Context context;
    private List<Recipe> rProductList;
    private ArrayList<String> items = new ArrayList<>();
    public ProductListAdapter()
    {}


    public ProductListAdapter(Context context, List<Recipe> rProductList) {
        this.context = context;
        this.rProductList = rProductList;
    }
    public ArrayList<String> getItems() {
        return items;
    }
    public void add(List <Recipe> list)
    {
        rProductList = new ArrayList<>();
        for (Recipe r:list)
            rProductList.add(r);
    }


    @Override
    public int getCount() {
        return rProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return rProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=View.inflate(context, R.layout.item_product_list,null);
        TextView tvname=(TextView)v.findViewById(R.id.tvnamerecipeitem);
        TextView tvdifficult=(TextView)v.findViewById(R.id.vdifiitem);
        TextView tvtime=(TextView)v.findViewById(R.id.tvtimerecipeitem);
        TextView tvkind=(TextView)v.findViewById(R.id.tvkindrecipeitem);

        tvname.setText("שם:  "+rProductList.get(position).getNameOfrecipe());
        tvtime.setText("זמן(דקות): "+String.valueOf(rProductList.get(position).getTime())+" דקות ");
        tvkind.setText("סוג המתכון: "+rProductList.get(position).getKindOfrecipe());
        tvdifficult.setText("קושי: "+rProductList.get(position).getDifficulty());
        v.setTag(rProductList.get(position).gettID());

        return v;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
