package com.example.shanir.cookingappofshanir.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.MainActivity;
import com.example.shanir.cookingappofshanir.classs.RecipeListAdapter;
import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.classs.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminListOfRecipes extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    List<Recipe> recipes;
    FirebaseAuth firebaseAuth;
    ProgressBar pb;
    FirebaseUser firebaseUser;
    Context context = this;
    boolean flag = true;
    ImageView mLogoutImg;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference postRef;
    private ListView listView;
    RecipeListAdapter adapter;
    private List<Recipe> recipeList;
    TextView tvnumrecipe;
    Button addRecipeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list_of_recipes);
        listView = (ListView) findViewById(R.id.listviewlistofallrecipe);
        pb = (ProgressBar) findViewById(R.id.pbadminlistofrecipes);
        firebaseAuth = FirebaseAuth.getInstance();
        tvnumrecipe = (TextView) findViewById(R.id.tvheadaminnumrecipe);
        addRecipeBtn = (Button) findViewById(R.id.add_recipe_btn);
        mLogoutImg = (ImageView)findViewById(R.id.admin_logout);
        mLogoutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        recipeList = new ArrayList<>();
        retrieveDataR();
        addRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),AddDetailsOnRecipeAdmin.class);
                startActivity(intent);
            }
        });


    }



    public void retrieveDataR() {
        recipes = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        String tableIdR = General.RECIPE_TABLE_NAME + "/" + uid;
        postRef = FirebaseDatabase.getInstance().getReference(tableIdR);
        postRef.addValueEventListener(new ValueEventListener() {
            Recipe r;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    r = postSnapshot.getValue(Recipe.class);
                    r.settID(firebaseAuth.getCurrentUser().getUid());
                    r.setKey(postSnapshot.getKey());
                    if (flag) {
                        recipes.add(r);
                        String kind = r.getKindOfrecipe();
                        String name = r.getNameOfrecipe();
                        String difficulty = r.getDifficulty();
                        int time = r.getTime();

                        recipeList.add(new Recipe(name, kind, difficulty, time));
                    }

                }

                Log.d("dd", "Num of recipes : " + recipes.size());
                tvnumrecipe.setText("מספר מתכונים: " + recipes.size());
                adapter = new RecipeListAdapter(getApplicationContext(), recipeList);
                Setadapter(adapter);

                pb.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void Setadapter(RecipeListAdapter adapter1) {
        listView.setAdapter(adapter1);
        listView.setOnItemLongClickListener(this);
    }



    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int pos = position;
        builder.setTitle("מחיקת מתכון                        ");
        builder.setMessage("האם אתה מאשר את מחיקת הפריט?" + "\n" + "בחר פה את תשובתך");
        builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String uid = General.UIDUSER;

                String tableIdR = General.RECIPE_TABLE_NAME + "/" + uid;
                String tid = recipes.get(pos).getKey();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(tableIdR + "/" + tid);
                ref.removeValue();
                recipes.remove(pos);
                recipeList.remove(pos);
                flag = false;
                adapter.notifyDataSetChanged();
                Toast.makeText(context, "המתכון נמחק", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(context, "בחרת לא למחוק את המתכון", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }



}
