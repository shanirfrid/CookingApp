package com.example.shanir.cookingappofshanir.Admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.shanir.cookingappofshanir.MainActivity;
import com.example.shanir.cookingappofshanir.R;
import com.google.firebase.auth.FirebaseAuth;

public class Adminchose extends AppCompatActivity implements View.OnClickListener {
    Button btaddrecipe;
    private FirebaseAuth mAuth;
    Button btlistofmyrecipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminchose);
        btaddrecipe=(Button)findViewById(R.id.btmoveaddrecipeadmin);
        btlistofmyrecipe=(Button)findViewById(R.id.btmovetolistofrecipeadmin);
        mAuth= FirebaseAuth.getInstance();
        btaddrecipe.setOnClickListener(this);
        btlistofmyrecipe.setOnClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;

        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        if (v==btaddrecipe)
        {
            intent=new Intent(this,AddDetailsOnRecipeAdmin.class);
            startActivity(intent);

        }
        else if (v==btlistofmyrecipe)
        {
            intent=new Intent(this,AdminListOfRecipes.class);
            startActivity(intent);

        }


    }
}
