package com.example.shanir.cookingappofshanir.classs;

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

/**
 * Created by Shanir on 05/03/2018.
 */

public class Adapter extends ArrayAdapter {
    private Context context;
    private List<String> objects;

    public Adapter(@NonNull Context context, int resource,
                   @NonNull List objects) {
        super(context, resource, objects);
        this.context=context;
        this.objects=objects;

    }

    public void clearAdapter()
    {
        objects.clear();
        notifyDataSetChanged();
    }

    public void setObjects(List<String> objects) {
        this.objects = objects;
    }

    public List<String> getlist()
    {
        return objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=((Activity)context).getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.custom_layout,parent,false);

                TextView textView=(TextView)view.findViewById(R.id.tvcustomlayout);
        String temp=objects.get(position);
        textView.setText(temp);



        return view;

    }
}
