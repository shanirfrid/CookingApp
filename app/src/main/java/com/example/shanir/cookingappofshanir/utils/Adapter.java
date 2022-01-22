package com.example.shanir.cookingappofshanir.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.shanir.cookingappofshanir.R;
import com.google.firebase.database.annotations.Nullable;
import java.util.List;

public class Adapter extends ArrayAdapter {
    private Context mContext;
    private List<String> mList;

    public Adapter(@NonNull Context context, int resource,
                   @NonNull List<String> list) {
        super(context, resource, list);
        mContext = context;
        mList = list;
    }

    public List<String> getList() {
        return mList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) mContext).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_layout, parent, false);
        TextView textView = view.findViewById(R.id.tvcustomlayout);
        String temp = mList.get(position);
        textView.setText(temp);

        return view;
    }
}
