package com.example.shanir.cookingappofshanir;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shanir.cookingappofshanir.classs.CheakTime;

import java.sql.Time;

public class Timerrun extends AppCompatActivity implements View.OnClickListener {
    int time ;
    TextView tv;
    ProgressBar progress;
    Context context=this;
    Button btstop,btstart;
    AsTsk1 asTsk1=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timerrun);
        progress=(ProgressBar)findViewById(R.id.progressBar1);
        btstart=(Button)findViewById(R.id.btstart1);
        btstop=(Button)findViewById(R.id.btstop1) ;
        btstop.setOnClickListener(this);
        btstop.setEnabled(false);
        btstart.setOnClickListener(this);

        Intent intent=getIntent();
        if (intent.getExtras()!=null)
        {
            time=intent.getExtras().getInt("timerrun");

        }
        progress.setMax(time*60);
        progress.setProgress(time*60);
        tv=(TextView)findViewById(R.id.tvtime1);

        CheakTime cheakTime=new CheakTime(time);

        Time timertext=new Time(cheakTime.gethour(),cheakTime.getmin(),cheakTime.getsec());
        tv.setText(timertext.toString());

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_back:

                asTsk1=null;
                Intent intent = new Intent(getApplicationContext(), Consumers.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v==btstart)
        {
            btstop.setEnabled(true);
            btstart.setEnabled(false);
            if (asTsk1==null )
            {
                asTsk1 =new AsTsk1(tv,progress,context);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    asTsk1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            time);
                else
                    asTsk1.execute(time);

            }
            else
                asTsk1.isRun=true;
        }
        else if(v==btstop)
        {
            btstop.setEnabled(false);
            asTsk1.isRun=false;
            btstart.setEnabled(true);

        }


    }

    public void letUserRestartTimer() {
        asTsk1 = null;
        btstop.setEnabled(false);
        btstart.setEnabled(true);
    }
}

