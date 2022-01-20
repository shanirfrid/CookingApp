package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.classs.Navigation;
import com.example.shanir.cookingappofshanir.classs.UpdateProfile;
import com.example.shanir.cookingappofshanir.classs.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView ivAccountProfile, mRightArrowImageView;
    TextView textView, textViewdetails, tvHeaderVerficationEmail, tvVerficationEmail, tvIconEmail;
    Button btsave;
    String tableusers;
    private DatabaseReference postRef;
    ProgressBar pb;
    User userprofil;
    FirebaseAuth mAuth;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        textViewdetails = (TextView) findViewById(R.id.tvdetailsprofil);
        ivAccountProfile = (ImageView) findViewById(R.id.profile_iv_account_profile);
        textView = (TextView) findViewById(R.id.tvvreified);
        tvHeaderVerficationEmail = (TextView) findViewById(R.id.tv_header_verify_email);
        tvVerficationEmail = (TextView) findViewById(R.id.tv_verigy_email);
        tvIconEmail = (TextView) findViewById(R.id.tv_icon_email);
        btsave = (Button) findViewById(R.id.btsaveprofile);
        pb = (ProgressBar) findViewById(R.id.pbprofile);
        mRightArrowImageView = (ImageView) findViewById(R.id.right_arrow_image_view);
        btsave.setOnClickListener(this);
        ivAccountProfile.setOnClickListener(this);

        retrieveData();
        loadUserInformation();
        textViewdetails.setVisibility(View.VISIBLE);

        navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new Navigation(this));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainLayoutProfile);
        mRightArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveData();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user.isEmailVerified()) {
            changeEmailToVerified();
        } else {
            textView.setText("Verify Email");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(UserProfileActivity.this, "Email verification has been sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }
    }

    public void changeEmailToVerified() {
        textView.setVisibility(View.GONE);
        tvHeaderVerficationEmail.setText("Verification Success");
        tvVerficationEmail.setText("Thank you for your support, we have successfully verfied your email");
        tvIconEmail.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.email_verified_successfully));
    }


    public void retrieveData() {
        setRefToTables();

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userprofil = dataSnapshot.getValue(User.class);
                userprofil.setIdkey(mAuth.getCurrentUser().getUid());
                if (userprofil.getIdkey().equals(mAuth.getCurrentUser().getUid())) {
                    textViewdetails.setText(userprofil.toString());

                    if (!userprofil.getBitmap().equals("none"))
                        loadImage(userprofil.getBitmap());
                    else {
                        Toast.makeText(getApplicationContext(),
                                "You dont have a profile image", Toast.LENGTH_SHORT).show();
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
                ivAccountProfile.setImageBitmap(bitmap);
            }

        });
        storageRef.getBytes(ONE_MEGABYTE).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == btsave) {
            Intent intent = new Intent(this, UpdateProfile.class);
            intent.putExtra("bitmap", userprofil.getBitmap());
            startActivity(intent);
        }
    }


}



