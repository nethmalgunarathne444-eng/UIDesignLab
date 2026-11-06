package com.example.uidesignlab;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import android.content.Intent;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.CheckBox;



public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LifecycleTracker";
    static final int COLOR_NORMAL = Color.parseColor("#6200EE");
    private static final int COLOR_PRESSED = Color.parseColor("#FF9800");
    private ToneGenerator toneGenerator;
    public static final String PREFS_NAME = "UserPrefs";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_REMEMBER = "remember";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //adding sound track
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        Log.d(TAG, "onCreate: Activity is being created");
        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        Button btnSave = findViewById(R.id.btnSave);
        EditText editUsername = findViewById(R.id.editUsername);
        CheckBox chkRemember = findViewById(R.id.chkRemember);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        boolean remember = prefs.getBoolean(KEY_REMEMBER, false);
        chkRemember.setChecked(remember);
        if (remember) {
            editUsername.setText(prefs.getString(KEY_USERNAME, ""));
        }

        btnSave.setBackgroundColor(COLOR_NORMAL);

        btnSave.setOnClickListener(v -> {

            Toast.makeText(this, "Processing...", Toast.LENGTH_SHORT).show();
            btnSave.setBackgroundColor(COLOR_PRESSED);
            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> btnSave.setBackgroundColor(COLOR_NORMAL), 1000);
            //2nd sound code lines
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 200);

            TextView result = new TextView(this);
            result.setId(View.generateViewId());
            result.setText("Profile Saved Successfully!");
            result.setTextColor(Color.parseColor("#2E7D32"));
            result.setTextSize(18);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT);
            params.topToBottom = R.id.editUsername;
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            params.topMargin = 48;
            mainLayout.addView(result, params);

            String userName = editUsername.getText().toString().trim();
            if (userName.isEmpty()) {
                userName = "Student";
            }

            SharedPreferences.Editor editor = prefs.edit();
            if (chkRemember.isChecked()) {
                editor.putString(KEY_USERNAME, userName);
                editor.putBoolean(KEY_REMEMBER, true);
            } else {
                editor.remove(KEY_USERNAME);
                editor.putBoolean(KEY_REMEMBER, false);
            }
            editor.apply();

            // 1. Make a final copy of the username for the background thread
            final String nameToSave = userName;

            // 2. Run the database insert on a background thread using ExecutorService
            AppDatabase.databaseExecutor.execute(() -> {
                long newId = AppDatabase.getInstance(getApplicationContext())
                        .userDao()
                        .insert(new UserRecord(nameToSave));

                Log.d("RoomLab", "Inserted " + nameToSave + ", id = " + newId);
            });

            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra("USER_NAME", userName); // data for the next screen
            startActivity(intent);

        });
    }


        @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity is becoming visible");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity is in the foreground");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity is losing focus");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity is no longer visible");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity is being destroyed");
        if (toneGenerator != null) {
            toneGenerator.release();
        }
    }

}