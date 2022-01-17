package com.example.shanir.cookingappofshanir.classs;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.shanir.cookingappofshanir.MyIngredientsActivity;
import com.example.shanir.cookingappofshanir.ListOfRecipe;
import com.example.shanir.cookingappofshanir.FavoriteRecipesActivity;
import com.example.shanir.cookingappofshanir.MainActivity;
import com.example.shanir.cookingappofshanir.Profile;
import com.example.shanir.cookingappofshanir.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Navigation implements NavigationView.OnNavigationItemSelectedListener {
    private Activity activity;

    public Navigation(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(activity.getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                break;

            case R.id.mnItemListOfRecipes:
                intent = new Intent(activity.getBaseContext(), ListOfRecipe.class);
                break;

            case R.id.mnItemListofsaverecipes:
                intent = new Intent(activity.getBaseContext(), FavoriteRecipesActivity.class);
                break;

            case R.id.mnItemProfile:
                intent = new Intent(activity.getBaseContext(), Profile.class);
                break;

            case R.id.mnItemConsumers:
                intent = new Intent(activity.getBaseContext(), MyIngredientsActivity.class);
                break;
            default:
                return false;
        }
        if (intent.getComponent().getClassName()
                .equals(activity.getComponentName().getClassName()))
            Toast.makeText(activity.getApplicationContext(), "You are already in this page",
                    Toast.LENGTH_SHORT).show();
        else
            activity.startActivity(intent);
        return true;

    }
}


