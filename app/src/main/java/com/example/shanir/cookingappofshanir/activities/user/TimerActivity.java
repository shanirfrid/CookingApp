package com.example.shanir.cookingappofshanir.activities.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.utils.TimerAsyncTask;
import com.example.shanir.cookingappofshanir.utils.TimerUtilities;
import java.sql.Time;

public class TimerActivity extends AppCompatActivity {
    private int mTotalTimeInMinutes;
    private TextView mTimerTextView;
    private ProgressBar mTimerProgressBar;
    private Button mStopButton, mStartButton;
    private TimerAsyncTask mTimerAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_activity);

        this.initTimerTextView();
        this.initStopButton();
        this.initStartButton();
        this.initCancelButton();
        this.initProgressBar();
    }

    private void initTimerTextView(){
        mTimerTextView = findViewById(R.id.timer_textview);

        Intent intent = getIntent();
        if (intent.getExtras() != null)
            mTotalTimeInMinutes = intent.getExtras().getInt("totalTimeInMinutes");

        TimerUtilities timerUtilities = new TimerUtilities(mTotalTimeInMinutes);
        Time timertext = new Time(timerUtilities.getHour(),
                timerUtilities.getMinutes(), 0);
        mTimerTextView.setText(timertext.toString());
    }

    private void initProgressBar(){
        mTimerProgressBar = findViewById(R.id.timer_progressbar);
        mTimerProgressBar.setMax(mTotalTimeInMinutes * 60);
        mTimerProgressBar.setProgress(mTotalTimeInMinutes * 60);
    }

    private void initStartButton() {
        this.mStartButton = findViewById(R.id.start_button);

        this.mStartButton.setOnClickListener(v -> {
            mStopButton.setEnabled(true);
            mStartButton.setEnabled(false);

            if (mTimerAsyncTask == null) {
                mTimerAsyncTask = new TimerAsyncTask(mTimerTextView,
                        mTimerProgressBar, TimerActivity.this);
                mTimerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        mTotalTimeInMinutes);
            } else
                mTimerAsyncTask.setIsRunning(true);
        });
    }

    private void initStopButton() {
        this.mStopButton = findViewById(R.id.stop_button);
        this.mStopButton.setEnabled(false);

        this.mStopButton.setOnClickListener(v -> {
            mStopButton.setEnabled(false);
            mTimerAsyncTask.setIsRunning(false);
            mStartButton.setEnabled(true);
        });
    }

    private void initCancelButton() {
        findViewById(R.id.cancel_timer_textview)
                .setOnClickListener(view -> finish());
    }

    public void letUserRestartTimer() {
        mTimerAsyncTask = null;
        mStopButton.setEnabled(false);
        mStartButton.setEnabled(true);
    }
}

