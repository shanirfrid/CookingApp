package com.example.shanir.cookingappofshanir;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.classs.CheakTime;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Time;

public class Timer extends AppCompatActivity {
    Button btopentimer;
    TextView tvhead, tvtime;

    int time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        btopentimer = (Button) findViewById(R.id.btopendialog);
        tvhead = (TextView) findViewById(R.id.tvtimer);
        tvtime = (TextView) findViewById(R.id.tvhowmuchtime);

         Intent i = getIntent();
        if (i.getExtras() != null)
        {



            CheakTime cheakTime=new CheakTime(Integer.parseInt(i.getExtras().getString("timedtailsonrecipe")));
            Time timertext=new Time(cheakTime.gethour(),cheakTime.getmin(),cheakTime.getsec());
            tvtime.setText(tvtime.getText().toString()+timertext.toString());
            time = Integer.parseInt(i.getExtras().getString("timedtailsonrecipe"));
        }

        btopentimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Timerrun.class);
                intent.putExtra("timerrun",time);
                startActivity(intent);
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;

            case R.id.mnItemConsumers:
                Intent intent2 = new Intent(getBaseContext(), Consumers.class);
                startActivity(intent2);
                break;


            case R.id.mnItemListOfRecipes:
                Intent intent5 = new Intent(getBaseContext(), ListOfRecipe.class);
                startActivity(intent5);
                break;
            case R.id.mnItemListofsaverecipes:
                Intent intent6 = new Intent(getBaseContext(), ListOfSaveRecipes.class);
                startActivity(intent6);
                break;
                case R.id.mnItemProfile:
                Intent intent3 = new Intent(getBaseContext(), Profile.class);
                startActivity(intent3);
                break;


        }

        return true;
    }


}

