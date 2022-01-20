package com.example.shanir.cookingappofshanir;

import android.content.Context;
import android.os.Vibrator;
import android.os.AsyncTask;
import android.os.Build;
import android.os.VibrationEffect;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shanir.cookingappofshanir.classs.CheakTime;

import java.sql.Time;

/**
 * Created by Shanir on 03/03/2018.
 */

public class AsTsk1 extends AsyncTask<Integer, Integer, String> {
    TextView tv;
    ProgressBar progressBar;
    Context context;
    private Time timetext;
    private CheakTime cheakTime;
    boolean isRun = true;
    private TimerActivity mTimerRunActivity;
    int num;

    public AsTsk1(TextView tv,
                  ProgressBar progressBar,Context context) {
        this.tv = tv;
        this.progressBar = progressBar;

        this.context=context;
        this.mTimerRunActivity = (TimerActivity) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Integer... integers) {
        num = integers[0];
        cheakTime=new CheakTime(num);
        timetext=new Time(cheakTime.gethour(),cheakTime.getmin(),cheakTime.getsec());
        int sec=num*60;

        while (sec > 0)
        {
            if (isRun) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sec--;
                timetext.setSeconds(timetext.getSeconds()-1);
                publishProgress(sec);
            }
        }
        return "time is up!";
    }

    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        tv.setText(timetext.toString());

        progressBar.setProgress(values[0]);
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        this.mTimerRunActivity.letUserRestartTimer();
        tv.setText(s);
        Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else
            {
            //deprecated in API 26
            v.vibrate(500);
        }


    }
}


