package com.example.shanir.cookingappofshanir;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.utils.General;
import com.example.shanir.cookingappofshanir.utils.Adapter;
import com.example.shanir.cookingappofshanir.utils.Ingredients;
import com.example.shanir.cookingappofshanir.utils.Recipe;
import com.example.shanir.cookingappofshanir.utils.TextFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailsOnRecipeActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvname, tvdifficulty, tvtime;
    Button btsaverecipe, btmakenow;
    ImageView imageView;
    Recipe recipe;
    ListView listView;
    ProgressBar progressBar;
    String lastSelected;
    String stname = "";
    TextView tvheaddialog, tvamount, tvcalories, tvunits, tvkindingredient;
    Button btback;
    String kind, time, difficulty;
    List<Recipe> recipes;
    FirebaseAuth firebaseAuth;
    Dialog dialogdetaileOnIngredientinrecipe;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference postRef;
    String units;
    ArrayList<Ingredients> ingredientsArrayList;
    Adapter adapter;
    ArrayList<String> liststring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_on_recipe);
        listView = (ListView) findViewById(R.id.lvdetailsonrecipe);
        progressBar = (ProgressBar) findViewById(R.id.progressbardetailss);
        tvname = (TextView) findViewById(R.id.tvheadnameOfrecipe);
        tvdifficulty = (TextView) findViewById(R.id.tvheaddificultyOfrecipe);
        tvtime = (TextView) findViewById(R.id.tvheadtimefrecipe);
        imageView = (ImageView) findViewById(R.id.ivdetailsrecipe);
        btsaverecipe = (Button) findViewById(R.id.btsaverecipedeails);
        btmakenow = (Button) findViewById(R.id.btmadenow);
        btmakenow.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        btsaverecipe.setOnClickListener(this);

        Intent i = getIntent();
        if (i.getExtras() != null) {
            stname = i.getExtras().getString("detailsrecipe");
        }
        tvname.setText(tvname.getText().toString() + stname);

        retrieveDataR();

    }

    public void retrieveDataR() {
        Intent i = getIntent();
        if (i.getExtras() != null) {
            stname = i.getExtras().getString("detailsrecipe", "");
            if (stname.equals("")) {
                stname = i.getExtras().getString("nameOfSelectedRecipe");
                btsaverecipe.setEnabled(false);
                btsaverecipe.setVisibility(View.GONE);

            }
        }
        recipes = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String tableIdR = General.RECIPE_TABLE_NAME;
        postRef = FirebaseDatabase.getInstance().getReference(tableIdR);
        postRef.addValueEventListener(new ValueEventListener() {
            Recipe r;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    r = postSnapshot.getValue(Recipe.class);

                    if (r.getNameOfrecipe().equals(stname)) {
                        tvname.setText(stname);
                        difficulty = r.getDifficulty();
                        tvdifficulty.setText(difficulty);
                        time = Integer.toString(r.getTime());
                        tvtime.setText(TextFormatter.formatRecipeTime(Integer.parseInt(time)));
                        liststring = r.getListNameIngredientOnRecipe();
                        ingredientsArrayList = r.getList();
                        recipe = r;
                        if (!recipe.getBitmap().equals("none"))
                            loadImage(recipe.getBitmap());

                    }
                }
                setlist(liststring, ingredientsArrayList);

                Log.d("dd", "Num of recipes : " + recipes.size());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadImage(String name) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://cookingappofshanir.appspot.com/images/").child(name);
        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        //download file as a byte array
        progressBar.setVisibility(View.VISIBLE);

        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            @Override
            public void onSuccess(byte[] bytes) {
                progressBar.setVisibility(View.GONE);

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });

    }

    public void setlist(ArrayList<String> listingredientname, ArrayList<Ingredients> ingredientsArrayList1) {
        liststring = listingredientname;
        ingredientsArrayList = ingredientsArrayList1;
        adapter = new Adapter(this, 0, liststring);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastSelected = (String) adapter.getItem(position);
                for (int n = 0; n < ingredientsArrayList.size(); n++) {
                    if (ingredientsArrayList.get(n).getName().equals(lastSelected)) {
                        units = ingredientsArrayList.get(n).getUnits();
                    }
                }
                createDialog();
            }
        });

    }

    public void createDialog() {
        dialogdetaileOnIngredientinrecipe = new Dialog(this);
        dialogdetaileOnIngredientinrecipe.setContentView(R.layout.dialogdetailsoningredientsinrecipe);
        dialogdetaileOnIngredientinrecipe.setTitle("Ingredient" + lastSelected + "dialog screen");
        dialogdetaileOnIngredientinrecipe.setCancelable(true);
        tvheaddialog = (TextView) dialogdetaileOnIngredientinrecipe.findViewById(R.id.tvheaddetailsoningredientt);
        tvheaddialog.setText(tvheaddialog.getText().toString() + lastSelected);
        tvunits = (TextView) dialogdetaileOnIngredientinrecipe.findViewById(R.id.tvunits);
        tvunits.setText(tvunits.getText().toString() + units);
        btback = (Button) dialogdetaileOnIngredientinrecipe.findViewById(R.id.btbackdetailsoningredient);
        btback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogdetaileOnIngredientinrecipe.dismiss();

            }
        });
        dialogdetaileOnIngredientinrecipe.show();
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        if (v == btmakenow) {
            intent = new Intent(this, TimerActivity.class);
            intent.putExtra("totalTimeInMinutes", Integer.parseInt(time));
            startActivity(intent);
        } else if (v == btsaverecipe) {
            addRecipeToSaveRecipes();
        }
    }

    public void addRecipeToSaveRecipes() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        String mPathToUserSavedRecipes = General.FAVORITE_RECIPES + "/" + uid +"/"+ General.RECIPE_FAVORITE_NAMES;
        DatabaseReference mReferenceToUserSavedRecipes
                = FirebaseDatabase.getInstance().getReference(mPathToUserSavedRecipes);
        HashMap<String, Object> map = new HashMap<>();
        mReferenceToUserSavedRecipes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long count = snapshot.getChildrenCount();
                ArrayList<String> recipesFavoriteNames = (ArrayList<String>) snapshot.getValue();
                if (recipesFavoriteNames != null) {
                    if (recipesFavoriteNames.contains(stname)) {
                        Toast.makeText(DetailsOnRecipeActivity.this,
                                "You already added this recipe to your favorite recipes",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                map.put(String.valueOf(count + 1), stname);
                mReferenceToUserSavedRecipes.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(DetailsOnRecipeActivity.this, UserFavoriteRecipesActivity.class);
                        intent.putExtra("nameofrecipedetails", stname);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_back:

                Intent intent = new Intent(getApplicationContext(), UserSuitableRecipesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

        }
        return true;
    }


}
