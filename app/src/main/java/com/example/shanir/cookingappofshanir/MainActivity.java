package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.Admin.AdminListOfRecipes;
import com.example.shanir.cookingappofshanir.Admin.General;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btSignIn;
    TextView tvhead, tvsignup;
    EditText  etpass, etemail;
    ProgressBar pb;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        btSignIn = (Button) findViewById(R.id.btSignIn);
        tvsignup = (TextView) findViewById(R.id.tvSignUp);
        etemail = (EditText) findViewById(R.id.etemaillogin);
        etpass = (EditText) findViewById(R.id.etPasswordlogin);
        tvhead = (TextView) findViewById(R.id.tvheadlogin);

        pb = (ProgressBar) findViewById(R.id.progressbarlogin);
        btSignIn.setOnClickListener(this);
        tvsignup.setOnClickListener(this);
    }



    private void userLogin() {
        String password = etpass.getText().toString().trim();
        String email = etemail.getText().toString().trim();


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
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pb.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    General.userKey = mAuth.getCurrentUser().getUid();
                    General.userEmail = mAuth.getCurrentUser().getEmail();
                    Intent intent=null;
                    if (General.userEmail.equals(General.Admin_Email))
                    {
                         intent=new Intent(getApplicationContext(), AdminListOfRecipes.class);
                    }
                    else
                    {
                         intent = new Intent(getApplicationContext(), Consumers.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null)
        {
            General.userKey = mAuth.getCurrentUser().getUid();
            General.userEmail = mAuth.getCurrentUser().getEmail();
            Intent intent=null;
            if (General.userEmail.equals(General.Admin_Email))
            {
                intent=new Intent(getApplicationContext(),AdminListOfRecipes.class);
            }
            else
            {
                intent = new Intent(getApplicationContext(), Consumers.class);
            }
            startActivity(intent);
            finish();
        }
    }




    @Override
    public void onClick(View v) {
        Intent i = null;
        if (v == btSignIn) {
            userLogin();

        } else if (v == tvsignup) {
            i = new Intent(getApplicationContext(), Register.class);
            startActivity(i);
            finish();
        }

    }


}

