package com.example.shanir.cookingappofshanir.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.activities.user.SignInActivity;
import com.example.shanir.cookingappofshanir.activities.user.UserFavoriteRecipesActivity;
import com.example.shanir.cookingappofshanir.activities.user.UserIngredientsActivity;
import com.example.shanir.cookingappofshanir.activities.user.UserProfileActivity;
import com.example.shanir.cookingappofshanir.activities.user.UserSuitableRecipesActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class NavigationMenu
        implements NavigationView.OnNavigationItemSelectedListener {
    private Activity mActivity;
    private ImageView mMenuImageView;
    private DrawerLayout mDrawerLayout;

    public NavigationMenu(Activity mActivity, ImageView mMenuImageView,
                          DrawerLayout mDrawerLayout) {
        this.mActivity = mActivity;
        this.mMenuImageView = mMenuImageView;
        this.mDrawerLayout = mDrawerLayout;

        this.initMenuButton();
    }

    private void initMenuButton() {
        mMenuImageView.setOnClickListener(view ->
                mDrawerLayout.openDrawer(GravityCompat.START));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuLogout) {
            handleLogOut();
            return false;
        }

        Intent intent;

        switch (item.getItemId()) {
            case R.id.mnItemListOfRecipes:
                intent = new Intent(mActivity.getBaseContext(), UserSuitableRecipesActivity.class);
                break;

            case R.id.mnItemListofsaverecipes:
                intent = new Intent(mActivity.getBaseContext(), UserFavoriteRecipesActivity.class);
                break;

            case R.id.mnItemProfile:
                intent = new Intent(mActivity.getBaseContext(), UserProfileActivity.class);
                break;

            case R.id.mnItemConsumers:
                intent = new Intent(mActivity.getBaseContext(), UserIngredientsActivity.class);
                break;
            default:
                return false;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (intent.getComponent().getClassName()
                .equals(mActivity.getComponentName().getClassName()))
            Toast.makeText(mActivity.getApplicationContext(),
                    "You are already in this page",
                    Toast.LENGTH_SHORT).show();
        else {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mActivity.startActivity(intent);
        }
        return true;
    }

    private void handleLogOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(mActivity.getApplicationContext(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mActivity.startActivity(intent);
        mActivity.finish();
    }
}
