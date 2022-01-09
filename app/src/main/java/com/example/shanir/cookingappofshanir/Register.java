package com.example.shanir.cookingappofshanir;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shanir.cookingappofshanir.Admin.General;
import com.example.shanir.cookingappofshanir.classs.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Register extends AppCompatActivity implements View.OnClickListener {
    EditText etpass, etfirstname, etemail, etlastname, etphone, etid, etconfirmpass;
    TextView tvBack;
    Button btsave;
    Uri imageUri;
    String namebitmap;

    ProgressBar pb;
    public static User user;
    private FirebaseAuth mAuth;
    Context context = this;
    int CODEREGISTER = 43806;
    public static final String TAG = "myapp";
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etpass = (EditText) findViewById(R.id.etpasswardregister);
        etconfirmpass = (EditText) findViewById(R.id.etconfirmPassword);
        etfirstname = (EditText) findViewById(R.id.etfirstname);
        etemail = (EditText) findViewById(R.id.etEmail);
        etid = (EditText) findViewById(R.id.etid);
        etphone = (EditText) findViewById(R.id.etPhone);
        etlastname = (EditText) findViewById(R.id.etlastname);
        pb = (ProgressBar) findViewById(R.id.progressbarregister);
        mAuth = FirebaseAuth.getInstance();
        btsave = (Button) findViewById(R.id.btSaveregister);
        tvBack = (TextView) findViewById(R.id.tv_back);
        btsave.setOnClickListener(this);
        tvBack.setOnClickListener(this);

    }

    public void adduser() {
        String firstname = etfirstname.getText().toString();
        String lastname = etlastname.getText().toString();
        String pass = etpass.getText().toString();
        String email = etemail.getText().toString();
        String phone = etphone.getText().toString();
        String id = etid.getText().toString();
        if (namebitmap == null)
            user = new User(firstname, lastname, id, pass, phone, email, null, "none");
        else {
            user = new User(firstname, lastname, id, pass, phone, email, null, namebitmap);
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user.setIdkey(uid);
        firebaseDatabase.getReference(General.USER_TABLE_NAME).child(uid).push().setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReferfinence) {
                if (databaseError == null) {
                    General.user = user;
                    General.userKey = user.getIdkey();
                    General.userEmail = user.getEmail();
                    Toast.makeText(Register.this, "פרטי משתמש הוכנסו למערכת ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Consumers.class);
                    startActivity(intent);
                    finish();

                } else
                    Toast.makeText(Register.this, "בעייה בקישור לאינטרנט - פרטי משתמש לא נשמרו", Toast.LENGTH_SHORT).show();
            }

        });


    }

    private void registerUser() {
        String password = etpass.getText().toString().trim();
        String email = etemail.getText().toString().trim();
        String confirmpass = etconfirmpass.getText().toString().trim();
        if (!confirmpass.equals(password)) {
            etpass.setError("סיסמא לא תואמת לאישור סיסמא");
            etpass.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etemail.setError("צריך מייל");
            etemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.setError("בבקשה תכניס מייל אמיתי");
            etemail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etpass.setError("צריך סיסמא");
            etpass.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etpass.setError("האורך המינימאלי של הסיסמא צריך להיות 6 ");
            etpass.requestFocus();
            return;
        }
        pb.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pb.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("צילום תמונת פרופיל");
                            builder.setMessage("האם תרצה לצלם תמונת פרופיל?" + "\n" + "בחר פה את תשובתך");
                            builder.setCancelable(true);
                            builder.setNegativeButton("לא מסכים", new Register.DialogListener());
                            builder.setPositiveButton("מסכים", new Register.DialogListener());
                            AlertDialog dialog = builder.create();
                            dialog.show();


                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(Register.this, "אתה כבר נרשמת",
                                        Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(Register.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                                Toast.makeText(Register.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            }


                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.

                        }

                        // ...
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (v == btsave) {
            registerUser();
        } else if (v == tvBack) {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public class DialogListener implements DialogInterface.OnClickListener {

        public DialogListener() {
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == AlertDialog.BUTTON_POSITIVE) {


                Intent i = new Intent(getApplicationContext(), Profile.class);
                i.putExtra("comefrom", "register");
                startActivityForResult(i, CODEREGISTER);


            } else if (which == AlertDialog.BUTTON_NEGATIVE) {
                Toast.makeText(context, "לא הסכמת לצלם תמונה",
                        Toast.LENGTH_SHORT).show();
                adduser();


            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODEREGISTER && resultCode == RESULT_OK) {
            String s = data.getStringExtra("uri");
            imageUri = Uri.parse(s);
            String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());

            namebitmap = timeStamp;
            uploadImage();
            adduser();


        }
    }

    private void uploadImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("imagesprofil/").child(namebitmap);
        storageReference.putFile(imageUri);
    }


}









