package com.example.shanir.cookingappofshanir.activities.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shanir.cookingappofshanir.R;
import com.example.shanir.cookingappofshanir.utils.async.TimerAsyncTask;
import com.example.shanir.cookingappofshanir.utils.TimerUtilities;
import java.sql.Time;

public class TimerActivity extends AppCompatActivity {
    private int mTotalTimeInMinutes;
    private TextView mTimerTextView;
    private ProgressBar mTimerProgressBar;
    private Button mPauseButton, mStartButton;
    private TimerAsyncTask mTimerAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_timer);

        this.initTimerTextView();
        this.initStopButton();
        this.initStartButton();
        this.initCancelButton();
        this.initProgressBar();
    }

    private void initTimerTextView(){
        mTimerTextView = findViewById(R.id.timer_text_view);

        Intent intent = getIntent();
        if (intent.getExtras() != null)
            mTotalTimeInMinutes = intent.getExtras().getInt("totalTimeInMinutes");

        TimerUtilities timerUtilities = new TimerUtilities(mTotalTimeInMinutes);
        Time timertext = new Time(timerUtilities.getHour(),
                timerUtilities.getMinutes(), 0);
        mTimerTextView.setText(timertext.toString());
    }

    private void initProgressBar(){
        mTimerProgressBar = findViewById(R.id.timer_progress_bar);
        mTimerProgressBar.setMax(mTotalTimeInMinutes * 60);
        mTimerProgressBar.setProgress(mTotalTimeInMinutes * 60);
    }

    private void initStartButton() {
        this.mStartButton = findViewById(R.id.timer_start_button);

        this.mStartButton.setOnClickListener(v -> {
            mPauseButton.setEnabled(true);
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
        this.mPauseButton = findViewById(R.id.timer_pause_button);
        this.mPauseButton.setEnabled(false);

        this.mPauseButton.setOnClickListener(v -> {
            mPauseButton.setEnabled(false);
            mTimerAsyncTask.setIsRunning(false);
            mStartButton.setEnabled(true);
        });
    }

    private void initCancelButton() {
        findViewById(R.id.timer_cancel_text_view)
                .setOnClickListener(view -> finish());
    }

    public void letUserRestartTimer() {
        mTimerAsyncTask = null;
        mPauseButton.setEnabled(false);
        mStartButton.setEnabled(true);
    }
}

