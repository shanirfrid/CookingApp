package com.example.shanir.cookingappofshanir;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.utils.General;
import com.example.shanir.cookingappofshanir.utils.Navigation;
import com.example.shanir.cookingappofshanir.utils.NavigationMenu;
import com.example.shanir.cookingappofshanir.utils.UserIngredients;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserIngredientsActivity extends AppCompatActivity {
    private EditText mAddIngredientEditText;
    private ImageView mAddIngredientImageView, mMenuImageView;
    private ListView mIngredientsListView;
    private Button mSaveIngredientsButton, mFindRecipesButton;
    private ArrayList<String> mIngredientsList;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUserIngredientsDbRef;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private String mUserIngredientsTablePath;
    private UserIngredients mUserIngredients;
    private IngredientListAdapter ingredientListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumers);
        mAddIngredientEditText = findViewById(R.id.etwriteconsumers);
        mMenuImageView = findViewById(R.id.menu_image_view);
        mIngredientsListView = findViewById(R.id.listviewconsumers);

        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }

        this.initSaveIngredientsButton();
        this.initFindRecipesButton();
        this.initAddIngredientButton();

        setRefToTables();
        retrieveData();

        mDrawerLayout = findViewById(R.id.mainLayoutConsumers);
        mNavigationView = findViewById(R.id.navigation_menu);
        mNavigationView.setNavigationItemSelectedListener(new NavigationMenu(this, mMenuImageView, mDrawerLayout));
    }

    private void initAddIngredientButton() {
        mAddIngredientImageView = findViewById(R.id.ivconsumersadd);
        mAddIngredientImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String st = mAddIngredientEditText.getText().toString().toLowerCase();
                if (st.trim().isEmpty()) {
                    mAddIngredientEditText.setError("You don't have any ingredients!");
                    return;
                }

                ingredientListAdapter.add(st);
                ingredientListAdapter.notifyDataSetChanged();
                mAddIngredientEditText.setText("");
            }
        });
    }

    private void initSaveIngredientsButton() {
        mSaveIngredientsButton = findViewById(R.id.btsaveconsumers);
        mSaveIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateItemsList();
                Toast.makeText(getApplicationContext(), "Your ingredients were saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initFindRecipesButton() {
        mFindRecipesButton = findViewById(R.id.btmovetorecipelist);
        mFindRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserIngredientsActivity.this,
                        UserSuitableRecipesActivity.class);
                startActivity(intent);
            }
        });

    }

    // Get Item list from database
    public void retrieveData() {
        mUserIngredientsDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserIngredients = dataSnapshot.getValue(UserIngredients.class);

                if (mUserIngredients == null) {
                    mUserIngredients = new UserIngredients();
                }
                setAdapter(mUserIngredients);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Adapter to item list
    private void setAdapter(UserIngredients userIngredients) {
        mIngredientsList = userIngredients.getIngredients();
        ingredientListAdapter = new IngredientListAdapter(getApplicationContext(), 0, userIngredients.getIngredients());
        mIngredientsListView.setAdapter(ingredientListAdapter);
    }


    public class DialogListener implements DialogInterface.OnClickListener {
        private int position;

        public DialogListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
                mIngredientsList.remove(position);
                updateItemsList();
                ingredientListAdapter.notifyDataSetChanged();
                Toast.makeText(UserIngredientsActivity.this, "The item was deleted",
                        Toast.LENGTH_SHORT).show();
            } else if (which == AlertDialog.BUTTON_NEGATIVE)
                Toast.makeText(UserIngredientsActivity.this, "You didn't agree to delete this item",
                        Toast.LENGTH_SHORT).show();
        }
    }

    private void updateItemsList() {
        mUserIngredients.add(mIngredientsList);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(mUserIngredientsTablePath);
        database.setValue(mUserIngredients);
    }


    /*
    postRef - Items list
    postRefR - recipes list
     */
    private void setRefToTables() {
        String uid = mFirebaseAuth.getCurrentUser().getUid();
        mUserIngredientsTablePath = General.USER_INGREDIENTS_TABLE_NAME + "/" + uid;
        mUserIngredientsDbRef = FirebaseDatabase.getInstance().getReference(mUserIngredientsTablePath);
    }

    public class IngredientListAdapter extends ArrayAdapter<String> {
        List<String> mProducts;
        private Context mContext;

        public IngredientListAdapter(@NonNull Context context, int resource, List<String> mProducts) {
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
            View v = View.inflate(mContext, R.layout.product_item_list, null);
            ((TextView) v.findViewById(R.id.product_name)).setText(getItem(i));
            ImageView imageView = (ImageView) v.findViewById(R.id.delete_product_image_view);
            imageView.setTag(new Integer(i));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserIngredientsActivity.this);
                    builder.setTitle("מחיקת מרכיב                        ");
                    builder.setMessage("האם אתה מאשר את מחיקת הפריט?" + "\n" + "בחר פה את תשובתך");
                    builder.setCancelable(true);
                    builder.setNegativeButton("לא מסכים", new DialogListener((int) view.getTag()));
                    builder.setPositiveButton("מסכים", new DialogListener((int) view.getTag()));
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            return v;
        }
    }
}
