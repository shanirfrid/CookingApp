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

import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.classs.Adapter;
import com.example.shanir.cookingappofshanir.classs.Ingredients;
import com.example.shanir.cookingappofshanir.classs.Recipe;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DetailsOnRecipe extends AppCompatActivity implements View.OnClickListener {
    TextView tvname,tvkind,tvdifficulty,tvtime;
    Button btsaverecipe,btmakenow;
    ImageView imageView;
    Recipe recipe;
    ListView listView;
    ProgressBar progressBar;
    String lastSelected;
    String stname="";
    TextView tvheaddialog,tvamount,tvcalories,tvunits,tvkindingredient;
    Button btback;
    String kind,time,difficulty;
    List<Recipe> recipes;
    FirebaseAuth firebaseAuth;
    Dialog dialogdetaileOnIngredientinrecipe;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference postRef;
    String kindingredient,amount,units,calories;
    ArrayList<Ingredients> ingredientsArrayList;
    Adapter adapter;
    ArrayList<String> liststring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_on_recipe);
        listView=(ListView)findViewById(R.id.lvdetailsonrecipe) ;
        progressBar=(ProgressBar)findViewById(R.id.progressbardetailss);
        tvname=(TextView)findViewById(R.id.tvheadnameOfrecipe);
        tvkind=(TextView)findViewById(R.id.tvheadkindOfrecipe);
        tvdifficulty=(TextView)findViewById(R.id.tvheaddificultyOfrecipe);
        tvtime=(TextView)findViewById(R.id.tvheadtimefrecipe);
        listView = (ListView) findViewById(R.id.lvdetailsonrecipe);
        imageView=(ImageView)findViewById(R.id.ivdetailsrecipe) ;
        btsaverecipe=(Button)findViewById(R.id.btsaverecipedeails);
        btmakenow=(Button)findViewById(R.id.btmadenow);
        btmakenow.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        btsaverecipe.setOnClickListener(this);

        Intent i=getIntent();
        if (i.getExtras()!=null)
        {
             stname = i.getExtras().getString("detailsrecipe");
        }
        tvname.setText(tvname.getText().toString()+stname);

        retrieveDataR();

    }

        public void retrieveDataR ()
        {
            Intent i=getIntent();
            if (i.getExtras()!=null)
            {
                stname = i.getExtras().getString("detailsrecipe","");
                if (stname.equals(""))
                {
                    stname=i.getExtras().getString("detailslistofsaverecipe");
                    btsaverecipe.setEnabled(false);
                    btsaverecipe.setVisibility(View.GONE);

                }
            }
            recipes = new ArrayList<>();
            firebaseDatabase = FirebaseDatabase.getInstance();
            String uid = General.UIDUSER;
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

                        if (r.getNameOfrecipe().equals(stname))
                       {
                           tvname.setText("שם המתכון: "+stname);
                           kind=r.getKindOfrecipe();
                           tvkind.setText(tvkind.getText().toString()+kind);
                           difficulty=r.getDifficulty();
                           tvdifficulty.setText(tvdifficulty.getText().toString() +difficulty);
                           time=Integer.toString(r.getTime());
                           tvtime.setText(tvtime.getText().toString()+time);
                           liststring=r.getListNameIngredientOnRecipe();
                           ingredientsArrayList=r.getList();
                           recipe=r;
                           if(!recipe.getBitmap().equals("none"))
                               loadImage(recipe.getBitmap());

                       }
                    }
                    setlist(liststring,ingredientsArrayList);

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
        final long ONE_MEGABYTE = 1024 * 1024 *5;
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

    public void setlist(ArrayList<String> listingredientname,ArrayList<Ingredients> ingredientsArrayList1)
    {
        liststring=listingredientname;
        ingredientsArrayList=ingredientsArrayList1;
        adapter = new Adapter(this, 0, liststring);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastSelected = (String) adapter.getItem(position);
                for (int n=0;n<ingredientsArrayList.size();n++)
                {
                    if (ingredientsArrayList.get(n).getName().equals(lastSelected))
                    {
                        kindingredient=ingredientsArrayList.get(n).getKindOfingredient();
                        amount=String.valueOf(ingredientsArrayList.get(n).getAmount());
                        units=ingredientsArrayList.get(n).getUnits();
                        calories=Integer.toString(ingredientsArrayList.get(n).getCalories());
                    }
                }
                createDialog();
            }
        });

    }
    public void createDialog()
    {
        dialogdetaileOnIngredientinrecipe=new Dialog(this);
        dialogdetaileOnIngredientinrecipe.setContentView(R.layout.dialogdetailsoningredientsinrecipe);
        dialogdetaileOnIngredientinrecipe.setTitle("Ingredient" + lastSelected+ "dialog screen");
        dialogdetaileOnIngredientinrecipe.setCancelable(true);
        tvheaddialog=(TextView)dialogdetaileOnIngredientinrecipe.findViewById(R.id.tvheaddetailsoningredientt) ;
        tvheaddialog.setText(tvheaddialog.getText().toString()+ lastSelected );
        tvamount=(TextView)dialogdetaileOnIngredientinrecipe.findViewById(R.id.tvamount) ;
        tvunits=(TextView)dialogdetaileOnIngredientinrecipe.findViewById(R.id.tvunits) ;
        tvkindingredient=(TextView) dialogdetaileOnIngredientinrecipe.findViewById(R.id.tvkindingredient);
        tvcalories=(TextView)dialogdetaileOnIngredientinrecipe.findViewById(R.id.tvcalories) ;
        tvamount.setText(tvamount.getText().toString()+amount );
        tvcalories.setText(tvcalories.getText().toString()+calories);
        tvkindingredient.setText(tvkindingredient.getText().toString()+kindingredient);
        tvunits.setText(tvunits.getText().toString()+units);
        btback=(Button) dialogdetaileOnIngredientinrecipe.findViewById(R.id.btbackdetailsoningredient);
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
        Intent m=null;
        if (v==btmakenow)
        {
            m=new Intent(this,Timer.class);
             m.putExtra("timedtailsonrecipe",time);
            startActivity(m);
        }
        else if (v==btsaverecipe)
        {
            m=new Intent(this,ListOfSaveRecipes.class);
            m.putExtra("nameofrecipedetails",stname);
            startActivity(m);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_back:

                Intent intent = new Intent(getApplicationContext(), ListOfRecipe.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

        }
        return true;
    }


}
