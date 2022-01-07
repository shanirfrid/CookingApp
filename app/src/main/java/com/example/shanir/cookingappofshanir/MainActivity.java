package com.example.shanir.cookingappofshanir;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shanir.cookingappofshanir.Admin.AddDetailsOnRecipeAdmin;
import com.example.shanir.cookingappofshanir.Admin.AdminListOfRecipes;
import com.example.shanir.cookingappofshanir.Admin.Adminchose;
import com.example.shanir.cookingappofshanir.Admin.General;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btlogin, btsignin;
    TextView tvhead;
    EditText  etpass, etemail;
    ProgressBar pb;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        btlogin = (Button) findViewById(R.id.btLogin);
        btsignin = (Button) findViewById(R.id.btSignInlogin);
        etemail = (EditText) findViewById(R.id.etemaillogin);
        etpass = (EditText) findViewById(R.id.etPasswordlogin);
        tvhead = (TextView) findViewById(R.id.tvheadlogin);

        pb = (ProgressBar) findViewById(R.id.progressbarlogin);
        btlogin.setOnClickListener(this);
        btsignin.setOnClickListener(this);
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
                         intent=new Intent(getApplicationContext(),Adminchose.class);
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
                intent=new Intent(getApplicationContext(),Adminchose.class);
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
        if (v == btlogin) {
            userLogin();

        } else if (v == btsignin) {
            i = new Intent(getApplicationContext(), Register.class);
            startActivity(i);
            finish();
        }

    }


}

