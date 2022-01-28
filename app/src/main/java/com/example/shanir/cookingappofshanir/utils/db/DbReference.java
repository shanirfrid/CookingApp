package com.example.shanir.cookingappofshanir.utils.db;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DbReference {

    public static DatabaseReference getDbRefToUserIngredients(String userID) {
        String userIngredientsTablePath = DbConstants.USER_INGREDIENTS_TABLE_NAME +
                "/" + userID + "/" + DbConstants.USER_INGREDIENTS_SUB_TABLE_NAME;
        return FirebaseDatabase.getInstance()
                .getReference(userIngredientsTablePath);
    }

    public static DatabaseReference getDbRefToRecipes() {
        return FirebaseDatabase.getInstance()
                .getReference(DbConstants.RECIPE_TABLE_NAME);
    }

    public static DatabaseReference getDbRefToRecipe(String recipeName) {
        String recipePath = DbConstants.RECIPE_TABLE_NAME + "/" + recipeName;
        return FirebaseDatabase.getInstance().getReference(recipePath);
    }

    public static DatabaseReference getDbRefToUserFavoriteRecipes(String userID) {
        String userFavoriteRecipesTablePath = DbConstants.FAVORITE_RECIPES +
                "/" + userID + "/" + DbConstants.RECIPE_FAVORITE_NAMES;
        return FirebaseDatabase.getInstance()
                .getReference(userFavoriteRecipesTablePath);
    }

    public static StorageReference getDbRefToRecipeBitmap(String bitmap) {
        return FirebaseStorage.getInstance().
                getReferenceFromUrl(DbConstants.APP_RECIPE_IMAGES_FULL_URL)
                .child(bitmap);
    }

    public static StorageReference getDbRefToImageBitmap(String directory, String bitmap) {
        return FirebaseStorage.getInstance().getReference()
                .child(directory).child(bitmap);
    }

    public static StorageReference getDbFullRefToImageBitmap(String pathToBitmap) {
        return FirebaseStorage.getInstance().getReferenceFromUrl(pathToBitmap);
    }

    public static DatabaseReference getDbRefToUser(String userID) {
        return FirebaseDatabase.getInstance()
                .getReference(DbConstants.USER_TABLE_NAME).child(userID);
    }
}
