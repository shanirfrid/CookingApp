package com.example.shanir.cookingappofshanir.Admin;

import com.example.shanir.cookingappofshanir.classs.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class General {

    public static final String USER_TABLE_NAME = "Users";
    public static final String FAVORITE_RECIPES ="UserFavoriteRecipes";
    public static final String ADMIN_EMAIL ="shanir207@gmail.com";
    public static final String USER_INGREDIENTS_TABLE_NAME = "UserIngredients";
    public static final String USER_INGREDIENTS_SUB_TABLE = "ingredients";
    public static final String RECIPE_FAVORITE_NAMES = "recipeNames";
    public static final String RECIPE_TABLE_NAME = "Recipes";
    public static User user;
    public static String userKey;

    public static String userEmail;



}
