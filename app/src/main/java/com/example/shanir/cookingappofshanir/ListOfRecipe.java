package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.classs.Adapter;
import com.example.shanir.cookingappofshanir.classs.Navigation;
import com.example.shanir.cookingappofshanir.classs.Recipe;
import com.example.shanir.cookingappofshanir.classs.UserItems;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOfRecipe extends AppCompatActivity {

    TextView tvhowmanyrecipe;
    ArrayList<String> items;
    private UserItems item;
    String lastSelected;
    ArrayList<Recipe> itemsRecipe;
    ArrayList<Recipe> recipes;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference postRef,postRefr;;
    ListView listView;
    Adapter adapter;
    private  String tableId;
    ArrayList<String> liststring;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ImageView mRightArrowImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_recipe);
        tvhowmanyrecipe = (TextView) findViewById(R.id.tvnumrecipe);
        listView = (ListView) findViewById(R.id.listviewOfRecipes);
        mRightArrowImageView = (ImageView) findViewById(R.id.right_arrow_image_view);
        firebaseAuth = FirebaseAuth.getInstance();
        liststring = new ArrayList<String>();
        adapter = new Adapter(this, 0, liststring);
        listView.setAdapter(adapter);
        setRefToTables();
        datalistingredientuser();
        datatlistingredientrecipe();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastSelected = (String) adapter.getItem(position);
                Intent i=new Intent(getApplicationContext(),DetailsOnRecipe.class);
                i.putExtra("detailsrecipe",lastSelected);
                startActivity(i);
            }
        });

        navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new Navigation(this));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainLayoutlistofrecipe);
        mRightArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });
    }

    public  void datalistingredientuser()
    {

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    item = postSnapshot.getValue(UserItems.class);
                    item.setUid(firebaseAuth.getCurrentUser().getUid());
                    item.setKey(postSnapshot.getKey());
                }
                if(item==null) //never get here
                {
                    Toast.makeText(getApplicationContext(),"אין לך שום מצרכים "+"\n"+ "בבקשה הכנס מצרכים" ,Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),Consumers.class);
                    startActivity(intent);
                }
                else
                    {
                    Setlistuser(item);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void Setlistuser(UserItems list)
    {
        if (list!=null)
        items = list.getItems();


    }

    public void datatlistingredientrecipe()
    {
        recipes = new ArrayList<>();
        firebaseDatabase= FirebaseDatabase.getInstance();
        String uid=General.UIDUSER;
        String tableIdR  = General.RECIPE_TABLE_NAME+"/"+uid;
        postRefr= FirebaseDatabase.getInstance().getReference(tableIdR);
        postRefr.addValueEventListener(new ValueEventListener() {
            Recipe r;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    r = postSnapshot.getValue(Recipe.class);
                    r.settID(firebaseAuth.getCurrentUser().getUid());
                    r.setKey(postSnapshot.getKey());
                    recipes.add(r);

                }
                if (recipes!=null)
               SetlistRecipe(recipes);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void SetlistRecipe(ArrayList<Recipe> list)
    {
        if( items!=null)
        {
            itemsRecipe = list;
            int n = 0;
            for (int i = 0; i < itemsRecipe.size(); i++)
            {
                if (items.containsAll(itemsRecipe.get(i).getListNameIngredientOnRecipe())) {
                    adapter.add(itemsRecipe.get(i).getNameOfrecipe());
                    n++;
                    adapter.notifyDataSetChanged();
                }
            }

            tvhowmanyrecipe.setText("מספר המתכונים המותאמים בשבילך הוא: " + Integer.toString(n));
        }
        else
        {
            tvhowmanyrecipe.setText("מספר המתכונים המותאמים בשבילך הוא: " + "0");
        }


    }


    private void setRefToTables()
    {
        String uid=firebaseAuth.getCurrentUser().getUid();
        tableId  = General.RECIPE_ITEM_NAME+"/"+uid;
        postRef= FirebaseDatabase.getInstance().getReference(tableId);
    }

}
