package com.example.shanir.cookingappofshanir;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.utils.DbReference;
import com.example.shanir.cookingappofshanir.utils.NavigationMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserIngredientsActivity extends AppCompatActivity {
    private EditText mAddIngredientEditText;
    private ImageView mAddIngredientImageView, mMenuImageView;
    private ListView mIngredientsListView;
    private Button mFindRecipesButton;
    private FirebaseAuth mFirebaseAuth;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private IngredientListAdapter mIngredientsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_ingredients);
        mAddIngredientEditText = findViewById(R.id.user_add_ingredient_edit_text);
        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }

        this.initFindRecipesButton();
        this.initAddIngredientButton();
        this.initIngredientList();
        this.initMenu();
        setDbEventListener();
    }

    private void initMenu(){
        mMenuImageView = findViewById(R.id.menu_image_view);
        mDrawerLayout = findViewById(R.id.mainLayoutConsumers);
        mNavigationView = findViewById(R.id.navigation_menu);
        mNavigationView.setNavigationItemSelectedListener(new NavigationMenu(this, mMenuImageView, mDrawerLayout));
    }

    private void initIngredientList() {
        mIngredientsListView = findViewById(R.id.user_ingredients_list_view);
        mIngredientsListAdapter = new IngredientListAdapter(getApplicationContext(), new ArrayList<>());
        mIngredientsListView.setAdapter(mIngredientsListAdapter);
    }

    private void initAddIngredientButton() {
        mAddIngredientImageView = findViewById(R.id.user_add_ingredient_image_view);
        mAddIngredientImageView.setOnClickListener(v -> {
            String ingredientToAdd = mAddIngredientEditText.getText().toString().toLowerCase();

            if (ingredientToAdd.trim().isEmpty()) {
                mAddIngredientEditText.setError("You don't have any ingredients!");
                return;
            }

            try {
                Integer.parseInt(ingredientToAdd);
                mAddIngredientEditText.setError("Ingredient cannot be a number!");
                return;
            } catch (Exception ignored) {

            }

            if (!mIngredientsListAdapter.isIngredientInList(ingredientToAdd)) {
                DbReference.getDbRefToUserIngredients(mFirebaseAuth.getUid())
                        .child(ingredientToAdd)
                        .setValue(ingredientToAdd);
            } else {
                Toast.makeText(this, "This ingredient is already added",Toast.LENGTH_SHORT).show();
            }
            mAddIngredientEditText.setText("");
        });
    }

    private void initFindRecipesButton() {
        mFindRecipesButton = findViewById(R.id.user_find_recipes_button);
        mFindRecipesButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserIngredientsActivity.this,
                    UserSuitableRecipesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

    }

    private void setDbEventListener() {
        DbReference.getDbRefToUserIngredients(mFirebaseAuth.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        mIngredientsListAdapter.add((String) snapshot.getValue());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        mIngredientsListAdapter.remove((String) snapshot.getValue());

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private class DialogListener implements DialogInterface.OnClickListener {
        private int position;

        public DialogListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
                String ingredientName = mIngredientsListAdapter.getItem(position);
                DbReference.getDbRefToUserIngredients(mFirebaseAuth.getUid())
                        .child(ingredientName).setValue(null);
            }
        }
    }

    private class IngredientListAdapter extends BaseAdapter {
        List<String> mIngredientList;
        private final Context mContext;

        public IngredientListAdapter(@NonNull Context context,
                                     List<String> ingredientList) {
            mIngredientList = ingredientList;
            mContext = context;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void add(String ingredient) {
            mIngredientList.add(ingredient);
            mIngredientList.sort(Comparator.comparing(String::toString));
            notifyDataSetChanged();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void remove(String ingredient) {
            mIngredientList.remove(ingredient);
            mIngredientList.sort(Comparator.comparing(String::toString));
            notifyDataSetChanged();
        }

        public boolean isIngredientInList(String ingredientName){
            return mIngredientList.contains(ingredientName);
        }

        @Override
        public int getCount() {
            return mIngredientList.size();
        }

        @Override
        public String getItem(int i) {
            return mIngredientList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = View.inflate(mContext, R.layout.product_item_list, null);
            ((TextView) v.findViewById(R.id.product_name)).setText(getItem(i));
            ImageView imageView = v.findViewById(R.id.delete_product_image_view);
            String ingredientName = mIngredientsListAdapter.getItem(i);
            imageView.setTag(new Integer(i));
            imageView.setOnClickListener(view1 -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserIngredientsActivity.this);
                builder.setTitle("Delete ingredient")
                        .setMessage("Do you approve to delete " +
                                ingredientName + " ?" + "\n" + "Choose here your answer")
                        .setCancelable(true)
                        .setNegativeButton("No", new DialogListener((int) view1.getTag()))
                        .setPositiveButton("Yes", new DialogListener((int) view1.getTag()));
                AlertDialog dialog = builder.create();
                dialog.show();
            });
            return v;
        }
    }
}
