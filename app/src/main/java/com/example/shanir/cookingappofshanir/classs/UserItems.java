package com.example.shanir.cookingappofshanir.classs;

import java.util.ArrayList;

/**
 * Created by gul on 25/03/2018.
 */

public class UserItems {


    private ArrayList<String> items = new ArrayList<>();
    private String uid;
    private String key;

    public UserItems() {

    }

    public void add(ArrayList<String> list) {
        items = new ArrayList<>();
        for (String s:list)
            items.add(s);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void add(String name)
    {
        items.add(name);
    }

    public ArrayList<String> getItems() {
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }
}
