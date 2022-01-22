package com.example.shanir.cookingappofshanir.Admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.classs.Adapter;
import com.example.shanir.cookingappofshanir.classs.FileHelper;
import com.example.shanir.cookingappofshanir.classs.Ingredients;
import com.example.shanir.cookingappofshanir.classs.Permission;
import com.example.shanir.cookingappofshanir.classs.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    IngredientsListAdapter adapter;
    Recipe recipe;
    ArrayList<Ingredients> liststring;
    String lastSelected;
    Dialog dialogdetaileOnIngredients;
    EditText etunits, mIngredientUnitsEditText;
    Button btsaveingredient;
    String units, nameingredient;
    private int GALLERY = 1, CAMERA = 2;
    boolean recipeInDB;
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
        mIngredientUnitsEditText =
                (EditText) findViewById(R.id.add_recipe_ingredients_units);

        imageViewadd = (ImageView) findViewById(R.id.ivconsumersaddaa);
        imageViewadd.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listviewconsumersa);
        tv = (TextView) findViewById(R.id.tvheadingredients);

        liststring = new ArrayList<Ingredients>();
        adapter = new IngredientsListAdapter(this, liststring);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastSelected = (String) adapter.getItem(position);
                createDialogIngredients();
            }
        });

        setListViewHeightBasedOnChildren(listView);
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 50;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
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


        } else if (view == imageViewadd) {
            String st = etadd.getText().toString().toLowerCase(),
                    ingredientUnits = mIngredientUnitsEditText.getText().toString();

            if (st.trim().isEmpty()) {
                etadd.setError("You need to enter an ingredient");
                return;
            }
            if (ingredientUnits.trim().isEmpty()) {
                mIngredientUnitsEditText.setError("Enter ingredient units");
                return;
            }

            if (!adapter.getlist().contains(etadd.getText().toString().toLowerCase())) {
                adapter.add(new Ingredients(st, ingredientUnits));
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This ingredients already exist in this list", Toast.LENGTH_SHORT).show();

            }
            etadd.setText("");
            mIngredientUnitsEditText.setText("");



        } else if (view == ivrecipe) {
            Permission permission = new Permission(this, getApplicationContext());
            permission.requestMultiplePermissions();
            showPictureDialog();

        } else if (view == btsaveingredient) {

            etunits = (EditText) dialogdetaileOnIngredients.findViewById(R.id.etunits);
            nameingredient = lastSelected;
            units = etunits.getText().toString();

            if (units.trim().isEmpty()) {
                etunits.setError("You need to add units/amount");
                etunits.requestFocus();
                return;
            }
            Ingredients ingredients = new Ingredients(lastSelected, units);

            recipe.addingredienttolist(ingredients);
            Toast.makeText(getApplicationContext(), "Ingredient has been saved successfully", Toast.LENGTH_SHORT).show();
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

        if (adapter.getlist().isEmpty()) {
            Toast.makeText(getApplicationContext(), "You need to insert an ingredient", Toast.LENGTH_SHORT).show();
            return;
        }

        int time = 0;
        if (!ettime.getText().toString().isEmpty())
            time = Integer.parseInt(ettime.getText().toString());
        else
            time = 0;

        String nameRecipe = etnamerecipe.getText().toString();
        recipe.setNameOfrecipe(nameRecipe);
        recipe.setDifficulty(difficult);
        recipe.setTime(time);

        if (imageUri != null) {
            namebitmap = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
            uploadImage(imageUri);
        }
        recipe.setBitmap(namebitmap);

        firebaseDatabase = FirebaseDatabase.getInstance();

        String tableIdR = General.RECIPE_TABLE_NAME + "/" + recipe.getNameOfrecipe();
        postRef = FirebaseDatabase.getInstance().getReference(tableIdR);

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    postRef.setValue(recipe).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddDetailsOnRecipeAdmin.this, "Recipe has been saved successfully",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AddDetailsOnRecipeAdmin.this, AdminListOfRecipes.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    Toast.makeText(AddDetailsOnRecipeAdmin.this, "This recipe already exists",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddDetailsOnRecipeAdmin.this,
                        "Error occurs - recipe didn't save", Toast.LENGTH_SHORT).show();
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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddDetailsOnRecipeAdmin.this, " Failed", Toast.LENGTH_SHORT).show();

            }
        })
        ;
    }

    private class IngredientsListAdapter extends BaseAdapter {
        List<Ingredients> mIngredientList;
        Context mContext;

        public IngredientsListAdapter(Context context,
                                      List<Ingredients> ingredientList) {
            mIngredientList = ingredientList;
            mContext = context;
        }

        public void add(Ingredients ingredient) {
            mIngredientList.add(ingredient);
            setListViewHeightBasedOnChildren(listView);
        }

        public List<Ingredients> getlist() {
            return mIngredientList;
        }

        @Override
        public int getCount() {
            return mIngredientList.size();
        }

        @Override
        public Object getItem(int i) {
            return mIngredientList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.ingredient_and_unit_item_layout, null);

            Ingredients ingredient = mIngredientList.get(i);
            ((TextView) view.findViewById(R.id.ingredient_unit_textview))
                    .setText(ingredient.getUnits());
            ((TextView) view.findViewById(R.id.ingredient_name_textview))
                    .setText(ingredient.getName());

            return view;
        }
    }
}

