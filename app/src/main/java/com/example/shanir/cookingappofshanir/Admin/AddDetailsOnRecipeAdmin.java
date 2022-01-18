package com.example.shanir.cookingappofshanir.Admin;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.classs.Adapter;
import com.example.shanir.cookingappofshanir.classs.Ingredients;
import com.example.shanir.cookingappofshanir.classs.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddDetailsOnRecipeAdmin extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    TextView tvheaddialog;
    Button btsaverecipe;
    EditText ettime;
    ImageView ivrecipe;
    RadioGroup rg1;
    String namebitmap;
    int CODE=299;
    String difficult="";
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference postRef;
    ArrayList<Ingredients> list;
    ArrayList<Recipe> recipes;
    ImageView imageViewadd;
    EditText etadd,etnamerecipe;
    ListView listView;
    TextView tv;
    Adapter adapter;
    Recipe recipe;
    ArrayList<String> liststring;
    String lastSelected;
    Dialog dialogdetaileOnIngredients;
    EditText etkindofingredients,etamount,etunits,etcalories,
            mIngredientUnitsEditText;
    Button btsaveingredient;
    String kindingredient,units,amount,calories,nameingredient;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details_on_recipe_admin);
        ettime = (EditText) findViewById(R.id.ettimeadddetailsadmin);
        ivrecipe = (ImageView) findViewById(R.id.ivadmindetails);
        etnamerecipe=(EditText)findViewById(R.id.etnameofrecipeadddetails) ;
        ivrecipe.setOnClickListener(this);
        btsaverecipe=(Button)findViewById(R.id.btsaverecipeee) ;
        rg1=(RadioGroup)findViewById(R.id.rgDiff);
        recipe = new Recipe();
        firebaseAuth = FirebaseAuth.getInstance();
        retrieveDataR();

        rg1.setOnCheckedChangeListener(this);
        btsaverecipe.setOnClickListener(this);

        etadd = (EditText) findViewById(R.id.etwriteconsumersa);
        mIngredientUnitsEditText =
                (EditText) findViewById(R.id.add_recipe_ingredients_units);

        imageViewadd = (ImageView) findViewById(R.id.ivconsumersaddaa);
        imageViewadd.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listviewconsumersa);
        tv = (TextView) findViewById(R.id.tvheadingredients);

        liststring = new ArrayList<String>();
        adapter = new Adapter(this, 0, liststring);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastSelected = (String) adapter.getItem(position);
                createDialog();
            }
        });


    }

    // Get Recipe list from database
    public void retrieveDataR()
    {
        recipes = new ArrayList<>();
        firebaseDatabase= FirebaseDatabase.getInstance();
        String uid=firebaseAuth.getCurrentUser().getUid();

        String tableIdR  = General.RECIPE_TABLE_NAME+"/"+uid;
        postRef= FirebaseDatabase.getInstance().getReference(tableIdR);
        postRef.addValueEventListener(new ValueEventListener() {
            Recipe r;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    r = postSnapshot.getValue(Recipe.class);
                    r.settID(firebaseAuth.getCurrentUser().getUid());
                    r.setKey(postSnapshot.getKey());
                    recipes.add(r);
                }
                Log.d("dd", "Num of recipes : "+recipes.size());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createDialog()
    {
        dialogdetaileOnIngredients=new Dialog(this);
        dialogdetaileOnIngredients.setContentView(R.layout.cdialogdetailsoningredients);
        dialogdetaileOnIngredients.setTitle("Ingredient" + lastSelected+ "dialog screen");
        dialogdetaileOnIngredients.setCancelable(true);
        tvheaddialog=(TextView)dialogdetaileOnIngredients.findViewById(R.id.tvheadadmindetailsoningredient) ;
        tvheaddialog.setText( tvheaddialog.getText()+lastSelected);
        btsaveingredient=(Button) dialogdetaileOnIngredients.findViewById(R.id.btdetailsoningredient);
        btsaveingredient.setOnClickListener(this);
        dialogdetaileOnIngredients.show();
    }



    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup == rg1)
        {
            if (i == R.id.rbEasy) {
                difficult="קל";
            }
            else if (i== R.id.rbMedium)
            {
                difficult="בינוני";
            }
            else if (i== R.id.rbHard)
            {
                difficult="קשה";
            }
        }
    }
    @Override
    public void onClick(View view) {

        if (view==btsaverecipe)
        {
            Addrecipe(list);
        }
       else if (view == imageViewadd)
        {
            String st=etadd.getText().toString().toLowerCase(),
                    ingredientUnits = mIngredientUnitsEditText.getText().toString();

            if(st.trim().isEmpty())
            {
                etadd.setError("צריך להכניס מצרך");
                return;
            }
            if (ingredientUnits.trim().isEmpty()) {
                mIngredientUnitsEditText.setError("Enter ingredient units");
                return;
            }
            if (!adapter.Getlist().contains(etadd.getText().toString().toLowerCase())) {

                adapter.add(ingredientUnits + " - " + st);
                adapter.notifyDataSetChanged();
            }
            else
            {

              Toast.makeText(getApplicationContext(), "מרכיב זה כבר נמצא ברשימה", Toast.LENGTH_SHORT).show();

            }
            etadd.setText("");
            mIngredientUnitsEditText.setText("");
        }
        else if (view==ivrecipe)
        {
            Intent i=new Intent(this,Picture.class);
            startActivityForResult(i,CODE);
        }
        else if(view== btsaveingredient)
        {

            etkindofingredients=(EditText)dialogdetaileOnIngredients.findViewById(R.id.etkindofIngredient);
            etamount =(EditText)dialogdetaileOnIngredients.findViewById(R.id.etamount);
            etcalories=(EditText)dialogdetaileOnIngredients.findViewById(R.id.etcalories) ;
            etunits=(EditText)dialogdetaileOnIngredients.findViewById(R.id.etunits);
            amount=etamount.getText().toString();
            nameingredient=lastSelected;
            units=etunits.getText().toString();
            calories=etcalories.getText().toString();
            kindingredient=etkindofingredients.getText().toString();

            if (kindingredient.trim().isEmpty()) {
                etkindofingredients.setError("צריך סוג");
                etkindofingredients.requestFocus();
                return;
            }

            if (units.trim().isEmpty()) {
                etunits.setError("צריך יחידות");
                etunits.requestFocus();
                return;
            }
            if (calories.trim().isEmpty()) {
                etcalories.setError("צריך קלוריות");
                etcalories.requestFocus();
                return;
            }
            if (amount.trim().isEmpty()) {
                etamount.setError("צריך כמות");
                etamount.requestFocus();
                return;
            }
            Ingredients ingredients = new Ingredients(kindingredient,Integer.parseInt(calories),lastSelected,
                    Double.parseDouble(amount),units);


               recipe.addingredienttolist(ingredients);
               Toast.makeText(getApplicationContext(), "מרכיב נשמר", Toast.LENGTH_SHORT).show();
            dialogdetaileOnIngredients.dismiss();



        }
    }




    private void Addrecipe(ArrayList<Ingredients> list) {

        if (ettime.getText().toString().trim().isEmpty()) {
            ettime.setError("צריך זמן");
            ettime.requestFocus();
        }

        if (etnamerecipe.getText().toString().trim().isEmpty()) {
            etnamerecipe.setError("צריך שם מתכון");
            etnamerecipe.requestFocus();


        }
        if (difficult.equals(""))
        {
            Toast.makeText(getApplicationContext(),"חסר קושי",Toast.LENGTH_SHORT).show();
            return;
        }



        int time=0;
        if(!ettime.getText().toString().isEmpty())
             time = Integer.parseInt(ettime.getText().toString());
        else
            time=0;

        recipe.setDifficulty(difficult);
        recipe.setBitmap(namebitmap);
        recipe.setTime(time);
        String nameRecipe=etnamerecipe.getText().toString();
        recipe.setNameOfrecipe(nameRecipe);
        firebaseDatabase= FirebaseDatabase.getInstance();
        String uid=firebaseAuth.getCurrentUser().getUid();

        String tableIdR  = General.RECIPE_TABLE_NAME+"/"+uid;
        postRef= FirebaseDatabase.getInstance().getReference(tableIdR).push();
        recipe.setKey(postRef.getKey());
        postRef= FirebaseDatabase.getInstance().getReference(tableIdR+"/"+recipe.getKey());
        postRef.setValue(recipe);
        postRef.setValue(recipe, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReferfinence) {
                if(databaseError==null)
                {
                    Toast.makeText(AddDetailsOnRecipeAdmin.this, "מתכון נשמר בהצלחה",
                            Toast.LENGTH_SHORT).show();

                    finish();
                    startActivity(getIntent());
                } else
                    Toast.makeText(AddDetailsOnRecipeAdmin.this,
                            "בעייה בקישור לאינטרנט - מתכון לא נשמר", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CODE && resultCode==RESULT_OK)
        {
            String s = data.getStringExtra("uri");
            imageUri = Uri.parse(s);
            String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
            namebitmap = timeStamp;
            ivrecipe.setImageURI(imageUri);
            uploadImage();
        }
    }

    private void uploadImage(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("images/").child(namebitmap);
        storageReference.putFile(imageUri);
    }


}

