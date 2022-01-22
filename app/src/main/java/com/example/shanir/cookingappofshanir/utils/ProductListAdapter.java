package com.example.shanir.cookingappofshanir.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.shanir.cookingappofshanir.R;

import java.util.List;

public class ProductListAdapter extends ArrayAdapter<String> {
    List<String> mProducts;
    private Context mContext;

    public ProductListAdapter(@NonNull Context context, int resource, List<String> mProducts) {
        super(context, resource, mProducts);
        this.mProducts = mProducts;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mProducts.size();
    }

    @Override
    public String getItem(int i) {
        return mProducts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v =View.inflate(mContext, R.layout.product_item_list,null);
        ((TextView) v.findViewById(R.id.product_name)).setText(getItem(i));
        ((ImageView) v.findViewById(R.id.delete_product_image_view)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return v;
    }
}
