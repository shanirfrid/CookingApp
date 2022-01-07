package com.example.shanir.cookingappofshanir.classs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shanir on 03/03/2018.
 */

public class Ingredients {
    private String kindOfingredient;
    private int calories;
    private String name;
    private double amount;
    private String units;



    public Ingredients(String kindOfingredient, int calories, String name, double amount, String units) {
        this.kindOfingredient = kindOfingredient;
        this.calories = calories;
        this.name = name;
        this.amount = amount;
        this.units = units;
    }

    public Ingredients()
    {}

    public String getKindOfingredient() {
        return kindOfingredient;
    }

    public void setKindOfingredient(String kindOfingredient) {
        this.kindOfingredient = kindOfingredient;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
