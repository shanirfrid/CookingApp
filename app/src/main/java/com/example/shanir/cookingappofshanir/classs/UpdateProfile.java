package com.example.shanir.cookingappofshanir.classs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.shanir.cookingappofshanir.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class UpdateProfile extends AppCompatActivity {

    EditText etname;
    Button button;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    DocumentReference documentReference;
    FirebaseFirestore
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}