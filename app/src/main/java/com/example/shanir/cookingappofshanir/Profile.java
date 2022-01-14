package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.Admin.Picture;
import com.example.shanir.cookingappofshanir.classs.FileHelper;
import com.example.shanir.cookingappofshanir.classs.Navigation;
import com.example.shanir.cookingappofshanir.classs.User;
import com.example.shanir.cookingappofshanir.classs.UserItems;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static com.example.shanir.cookingappofshanir.Register.user;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    ImageView imageViewphone, imageViewcemra, imageView, mRightArrowImageView;
    private static final int CHOOSE_IMAGE = 101;
    TextView textView, textViewdetails;
    Button btsave;
    TextView textViewpofil;
    String tableusers;
    final String PIC_FILE_NAME_PROFIL = "userpicprofil";
    private DatabaseReference postRef;
    Uri uriProfileImage;
    ProgressBar pb, pbupdate;
    TextView tvheadprofil, tvheadprofil2;
    User userprofil;
    int codetakepicutre = 30;
    Bitmap bitmapcemra;
    FirebaseAuth mAuth;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        textViewpofil = (TextView) findViewById(R.id.tvtextprofil);
        textViewdetails = (TextView) findViewById(R.id.tvdetailsprofil);
        tvheadprofil2 = (TextView) findViewById(R.id.tvheadprofile2);
        tvheadprofil = (TextView) findViewById(R.id.tvheadprofile);
        imageViewcemra = (ImageView) findViewById(R.id.imageprofilecemra);
        imageView = (ImageView) findViewById(R.id.imageviewprofill);
        textView = (TextView) findViewById(R.id.tvvreified);
        imageViewphone = (ImageView) findViewById(R.id.imageprofilephone);
        btsave = (Button) findViewById(R.id.btsaveprofile);
        pb = (ProgressBar) findViewById(R.id.pbprofile);
        pbupdate = (ProgressBar) findViewById(R.id.pbprofileupdate);
        mRightArrowImageView = (ImageView) findViewById(R.id.right_arrow_image_view);

        btsave.setOnClickListener(this);
        imageViewcemra.setOnClickListener(this);
        imageViewphone.setOnClickListener(this);
        Intent i = getIntent();

        if (i.getExtras() != null) {
            if (i.getExtras().getString("comefrom").equals("register"))
                loadUserInformation();
            imageViewcemra.setVisibility(View.VISIBLE);
            imageViewphone.setVisibility(View.VISIBLE);

        } else {
            retrieveData();
            loadUserInformation();
            btsave.setVisibility(View.GONE);
            textViewdetails.setVisibility(View.VISIBLE);
        }
        navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new Navigation(this));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainLayoutProfile);
        mRightArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });

    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified()) {
            textView.setText(" המייל  מאומת");

        } else {
            textView.setText("לחץ כדי לאמת מייל");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Profile.this, " אימות אימייל נשלח ", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    public void retrieveData() {
        setRefToTables();
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    userprofil = postSnapshot.getValue(User.class);
                    userprofil.setIdkey(mAuth.getCurrentUser().getUid());

                    if (userprofil.getIdkey().equals(mAuth.getCurrentUser().getUid())) {
                        textViewdetails.setText(userprofil.toString());
                        if (!userprofil.getBitmap().equals("none"))
                            loadImage(userprofil.getBitmap());
                        else {
                            textViewpofil.setText("אין לך תמונת פרופיל");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setRefToTables() {
        String uid = mAuth.getCurrentUser().getUid();
        tableusers = General.USER_TABLE_NAME + "/" + uid;
        postRef = FirebaseDatabase.getInstance().getReference(tableusers);
    }

    private void loadImage(String name) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://cookingappofshanir.appspot.com/imagesprofil/").child(name);
        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        //download file as a byte array
        pb.setVisibility(View.VISIBLE);

        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            @Override
            public void onSuccess(byte[] bytes) {
                pb.setVisibility(View.GONE);

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }


    @Override
    public void onClick(View v) {
        if (v == imageViewphone) {

            if (imageView.getDrawable() == null)
                showImageChooser();
            else {
                Toast.makeText(this, "כבר יש תמונה לפרופיל זה", Toast.LENGTH_SHORT).show();
            }
        } else if (v == btsave) {
            if (imageView.getDrawable() == null) {
                Toast.makeText(this, "חסרה תמונה ,צלם שוב בבקשה", Toast.LENGTH_SHORT).show();
            } else if (imageView.getDrawable() != null) {
                Toast.makeText(this, "התמונה נשמרה בהצלחה", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent();
                intent1.putExtra("uri", uriProfileImage.toString());

                setResult(RESULT_OK, intent1);
                finish();
                btsave.setVisibility(View.GONE);
            }


        } else if (v == imageViewcemra) {

            if (imageView.getDrawable() == null) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, codetakepicutre);
            } else {
                Toast.makeText(this, "כבר יש תמונה לפרופיל זה", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uriProfileImage = data.getData();
            Log.d("dd", "onActivityResult: " + uriProfileImage);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == codetakepicutre && resultCode == RESULT_OK) {
            bitmapcemra = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmapcemra);
            FileHelper.saveBitmapToFile(bitmapcemra, Profile.this, PIC_FILE_NAME_PROFIL);
            File tmpFile = new File(getFilesDir() + "/" + PIC_FILE_NAME_PROFIL);
            uriProfileImage = Uri.fromFile(tmpFile);
            Log.d("dd", "onActivityResult: " + uriProfileImage);
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "תבחר תמונה"), CHOOSE_IMAGE);
    }
}

