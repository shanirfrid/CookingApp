package com.example.shanir.cookingappofshanir.Admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.ListOfRecipe;
import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.classs.Adapter;
import com.example.shanir.cookingappofshanir.classs.FileHelper;
import com.example.shanir.cookingappofshanir.classs.Ingredients;
import com.example.shanir.cookingappofshanir.classs.Permission;
import com.example.shanir.cookingappofshanir.classs.Recipe;
import com.example.shanir.cookingappofshanir.classs.UpdateProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
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
    String difficult = "";
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference postRef;
    ArrayList<Ingredients> list;
    ImageView imageViewadd;
    EditText etadd, etnamerecipe;
    ListView listView;
    TextView tv;
    Adapter adapter;
    Recipe recipe;
    ArrayList<String> liststring;
    String lastSelected;
    Dialog dialogdetaileOnIngredients;
    EditText etkindofingredients, etamount, etunits, etcalories;
    Button btsaveingredient;
    String kindingredient, units, amount, calories, nameingredient;
    private int GALLERY = 1, CAMERA = 2;
    Uri imageUri;
    final String PIC_FILE_NAME = "userpic";
    boolean imageHasChanged = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details_on_recipe_admin);
        ettime = (EditText) findViewById(R.id.ettimeadddetailsadmin);
        ivrecipe = (ImageView) findViewById(R.id.ivadmindetails);
        etnamerecipe = (EditText) findViewById(R.id.etnameofrecipeadddetails);
        ivrecipe.setOnClickListener(this);
        btsaverecipe = (Button) findViewById(R.id.btsaverecipeee);
        rg1 = (RadioGroup) findViewById(R.id.rgDiff);
        recipe = new Recipe();
        firebaseAuth = FirebaseAuth.getInstance();

        rg1.setOnCheckedChangeListener(this);
        btsaverecipe.setOnClickListener(this);

        etadd = (EditText) findViewById(R.id.etwriteconsumersa);
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
                createDialogIngredients();
            }
        });
    }

    private void createDialogIngredients() {
        dialogdetaileOnIngredients = new Dialog(this);
        dialogdetaileOnIngredients.setContentView(R.layout.cdialogdetailsoningredients);
        dialogdetaileOnIngredients.setTitle("Ingredient" + lastSelected + "dialog screen");
        dialogdetaileOnIngredients.setCancelable(true);
        tvheaddialog = (TextView) dialogdetaileOnIngredients.findViewById(R.id.tvheadadmindetailsoningredient);
        tvheaddialog.setText(tvheaddialog.getText() + lastSelected);
        btsaveingredient = (Button) dialogdetaileOnIngredients.findViewById(R.id.btdetailsoningredient);
        btsaveingredient.setOnClickListener(this);
        dialogdetaileOnIngredients.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup == rg1) {
            if (i == R.id.rbEasy) {
                difficult = "Easy";
            } else if (i == R.id.rbMedium) {
                difficult = "Medium";
            } else if (i == R.id.rbHard) {
                difficult = "Hard";
            }
        }
    }

    @Override
    public void onClick(View view) {

        if (view == btsaverecipe) {
            Addrecipe(list);
            Intent intent = new Intent(this, AdminListOfRecipes.class);
            startActivity(intent);

        } else if (view == imageViewadd)
        {
            String st = etadd.getText().toString().toLowerCase();
            if (st.trim().isEmpty()) {
                etadd.setError("צריך להכניס מצרך");
                return;
            }

            if (!adapter.Getlist().contains(etadd.getText().toString().toLowerCase())) {
                adapter.add(st);
                adapter.notifyDataSetChanged();
            } else {

                Toast.makeText(getApplicationContext(), "מרכיב זה כבר נמצא ברשימה", Toast.LENGTH_SHORT).show();

            }
            etadd.setText("");


        } else if (view == ivrecipe) {
            Permission permission = new Permission(this, getApplicationContext());
            permission.requestMultiplePermissions();
            showPictureDialog();
           
        } else if (view == btsaveingredient) {

            etkindofingredients = (EditText) dialogdetaileOnIngredients.findViewById(R.id.etkindofIngredient);
            etamount = (EditText) dialogdetaileOnIngredients.findViewById(R.id.etamount);
            etcalories = (EditText) dialogdetaileOnIngredients.findViewById(R.id.etcalories);
            etunits = (EditText) dialogdetaileOnIngredients.findViewById(R.id.etunits);
            amount = etamount.getText().toString();
            nameingredient = lastSelected;
            units = etunits.getText().toString();
            calories = etcalories.getText().toString();
            kindingredient = etkindofingredients.getText().toString();

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
            Ingredients ingredients = new Ingredients(kindingredient, Integer.parseInt(calories), lastSelected,
                    Double.parseDouble(amount), units);

            recipe.addingredienttolist(ingredients);
            Toast.makeText(getApplicationContext(), "מרכיב נשמר", Toast.LENGTH_SHORT).show();
            dialogdetaileOnIngredients.dismiss();


        }
    }


    private void Addrecipe(ArrayList<Ingredients> list) {

        if (ettime.getText().toString().trim().isEmpty()) {
            ettime.setError("צריך זמן");
            ettime.requestFocus();
            return;
        }
        if (etnamerecipe.getText().toString().trim().isEmpty()) {
            etnamerecipe.setError("You need to enter recipe name");
            etnamerecipe.requestFocus();
            return;
        }
        if (difficult.equals("")) {
            Toast.makeText(getApplicationContext(), "You need to insert a difficult", Toast.LENGTH_SHORT).show();
            return;
        }


        int time = 0;
        if (!ettime.getText().toString().isEmpty())
            time = Integer.parseInt(ettime.getText().toString());
        else
            time = 0;

        recipe.setDifficulty(difficult);
        recipe.setTime(time);
        String nameRecipe = etnamerecipe.getText().toString();
        recipe.setNameOfrecipe(nameRecipe);

        if (imageUri != null) {
            namebitmap = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
            uploadImage(imageUri);
        }
        recipe.setBitmap(namebitmap);

        firebaseDatabase = FirebaseDatabase.getInstance();

        String tableIdR = General.RECIPE_TABLE_NAME +"/"+ recipe.getNameOfrecipe();
        postRef = FirebaseDatabase.getInstance().getReference(tableIdR);

        postRef.setValue(recipe, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReferfinence) {
                if (databaseError == null) {
                    Toast.makeText(AddDetailsOnRecipeAdmin.this, "Recipe has been saved successfully",
                            Toast.LENGTH_SHORT).show();

                    finish();
                    startActivity(getIntent());
                } else
                    Toast.makeText(AddDetailsOnRecipeAdmin.this,
                            "Internet connection is lost - recipe didn't save", Toast.LENGTH_SHORT).show();
            }

        });
    }



    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Add Photo!");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Take Photo"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                imageUri = data.getData();
                try {
                    Bitmap bitmapGallery = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    ivrecipe.setImageBitmap(bitmapGallery);
                    imageHasChanged = true;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddDetailsOnRecipeAdmin.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {

            Bitmap bitmapCamera = (Bitmap) data.getExtras().get("data");
            ivrecipe.setImageBitmap(bitmapCamera);
            imageHasChanged = true;
            FileHelper.saveBitmapToFile(bitmapCamera, AddDetailsOnRecipeAdmin.this, PIC_FILE_NAME);
            File tmpFile = new File(getFilesDir() + "/" + PIC_FILE_NAME);
            imageUri = Uri.fromFile(tmpFile);
            Log.d("dd", "onActivityResult: " + imageUri);
            Toast.makeText(AddDetailsOnRecipeAdmin.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("images/").child(namebitmap);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddDetailsOnRecipeAdmin.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddDetailsOnRecipeAdmin.this, " Failed", Toast.LENGTH_SHORT).show();

            }
        })
        ;
    }


}

