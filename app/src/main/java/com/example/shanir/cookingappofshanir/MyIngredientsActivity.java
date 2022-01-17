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

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.classs.Navigation;
import com.example.shanir.cookingappofshanir.classs.UserIngredients;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyIngredientsActivity extends AppCompatActivity implements View.OnClickListener{
    EditText etadd;
    ImageView imageViewadd, mRightArrowImageView;
    ListView listView;
    TextView tv;
    Button btsave, btrecipelist;
    ArrayList<String> productList;
    Context context = this;
    FirebaseAuth firebaseAuth;
    private DatabaseReference postRef;
    private DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    private String tableId;
    private UserIngredients item;
    ProductListAdapter productListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumers);
        etadd = (EditText) findViewById(R.id.etwriteconsumers);
        btsave = (Button) findViewById(R.id.btsaveconsumers);
        btrecipelist = (Button) findViewById(R.id.btmovetorecipelist);
        imageViewadd = (ImageView) findViewById(R.id.ivconsumersadd);
        mRightArrowImageView = (ImageView) findViewById(R.id.menu_image_view);
        imageViewadd.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.listviewconsumers);
        tv = (TextView) findViewById(R.id.tvheadconsumers);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        btsave.setOnClickListener(this);
        btrecipelist.setOnClickListener(this);
        setRefToTables();
        retrieveData();

        navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new Navigation(this));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainLayoutConsumers);
        mRightArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }

    // Get Item list from database
    public void retrieveData() {
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item = dataSnapshot.getValue(UserIngredients.class);

                if (item == null) {
                    item = new UserIngredients();
                }
                setAdapter(item);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //Adapter to item list
    private void setAdapter(UserIngredients list) {
        productList = list.getIngredients();
        productListAdapter = new ProductListAdapter(getApplicationContext(), 0, list.getIngredients());
        listView.setAdapter(productListAdapter);
    }


    public class DialogListener implements DialogInterface.OnClickListener {
        private int position;

        public DialogListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {
                productList.remove(position);
                updateItemsList();
                productListAdapter.notifyDataSetChanged();
                Toast.makeText(context, "The item was deleted",
                        Toast.LENGTH_SHORT).show();
            } else if (which == AlertDialog.BUTTON_NEGATIVE)
                Toast.makeText(context, "You didn't agree to delete this item",
                        Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == imageViewadd) {
            String st = etadd.getText().toString().toLowerCase();
            if (st.trim().isEmpty()) {
                etadd.setError("You don't have any ingredients!");
                return;
            }

            productListAdapter.add(st);
            productListAdapter.notifyDataSetChanged();
            etadd.setText("");
        } else if (v == btsave) {
            updateItemsList();
            Toast.makeText(getApplicationContext(), "Your ingredients were saved", Toast.LENGTH_SHORT).show();

        } else if (v == btrecipelist) {
            Intent intent = new Intent(this, ListOfRecipe.class);
            startActivity(intent);
        }


    }

    private void updateItemsList() {
        item.add(productList);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(tableId);
        database.setValue(item);
    }


    /*
    postRef - Items list
    postRefR - recipes list
     */
    private void setRefToTables() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        tableId = General.USER_INGREDIENTS_TABLE_NAME + "/" + uid + General.USER_INGREDIENTS_SUB_TABLE;
        postRef = FirebaseDatabase.getInstance().getReference(tableId);

    }


    public class ProductListAdapter extends ArrayAdapter<String> {
        List<String> mProducts;
        private Context mContext;

        public ProductListAdapter(@NonNull Context context, int resource, List<String> mProducts) {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
