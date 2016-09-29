package edu.neu.madcourse.ranchen.jumpmadness.jumpMadness.jumpMadness;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import edu.neu.madcourse.ranchen.jumpmadness.R;

/**
 * Created by cheerhou on 4/18/16.
 */
public class LevelOneActivity extends Activity {
    public static final String TAG = "LevelOne";
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final long JUMP_INTERVAL = 1000;
    private static final long GAME_TOTAL_TIME = 10000;
    private static final long GAME_COUNT_DOWN = 1000;

    private MediaPlayer backGroundMusicPlayer;
    private MediaPlayer jumpSoundPlayer;
    private MediaPlayer jumpIntroPlayer;
    private MediaPlayer nextLevelPlayer;
    private MediaPlayer gameOverPlayer;

    private SensorManager sensorManager;
    private JumpEventListener jumpEventListener;
    private JumpDataReader jumpDataReader;
    private GameCountDownTimer gameCountDownTimer;

    private long remainMilli;
    private int jumpCount = 0;

    TextView timerLabel;
    TextView jumpLabel;
    TextView enemyLabel;
    TextView timerCounterTextView;
    TextView jumpCounterTextView;
    TextView enemyTextView;
    Button backHomeBtn;
    Button nextLevelBtn;
    Button tryAgainBtn;
    Typeface custom_font;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jump_game_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/mario_fonts.ttf");


        backGroundMusicPlayer = MediaPlayer.create(this, R.raw.background_music);
        backGroundMusicPlayer.setLooping(true);
        backGroundMusicPlayer.setVolume(0.4f, 0.4f);

        jumpSoundPlayer = MediaPlayer.create(this, R.raw.jump_sound);
        jumpSoundPlayer.setLooping(false);
        jumpSoundPlayer.setVolume(1.0f, 1.0f);

        jumpIntroPlayer = MediaPlayer.create(this, R.raw.jump_intro);
        jumpIntroPlayer.setLooping(false);
        jumpIntroPlayer.setVolume(1.0f, 1.0f);

        nextLevelPlayer = MediaPlayer.create(this, R.raw.good_job);
        nextLevelPlayer.setLooping(false);
        nextLevelPlayer.setVolume(1.0f, 1.0f);

        gameOverPlayer = MediaPlayer.create(this, R.raw.smb_gameover);
        gameOverPlayer.setLooping(false);
        gameOverPlayer.setVolume(1.0f, 1.0f);


        timerLabel = (TextView) findViewById(R.id.timer_label);
        timerLabel.setTypeface(custom_font);
        jumpLabel = (TextView) findViewById(R.id.jump_lable);
        jumpLabel.setTypeface(custom_font);
        enemyLabel = (TextView) findViewById(R.id.enemy_lable);
        enemyLabel.setTypeface(custom_font);
        timerCounterTextView = (TextView) findViewById(R.id.timer_text);
        timerCounterTextView.setTypeface(custom_font);
        jumpCounterTextView = (TextView) findViewById(R.id.jump_text_view);
        jumpCounterTextView.setTypeface(custom_font);
        enemyTextView = (TextView) findViewById(R.id.enemy_text_view);
        enemyTextView.setTypeface(custom_font);

        backHomeBtn = (Button) findViewById(R.id.back_home_btn);
        backHomeBtn.setTypeface(custom_font);
        nextLevelBtn = (Button) findViewById(R.id.next_level_btn);
        nextLevelBtn.setTypeface(custom_font);
        tryAgainBtn = (Button) findViewById(R.id.try_again_btn);
        tryAgainBtn.setTypeface(custom_font);

        gameCountDownTimer = new GameCountDownTimer(GAME_TOTAL_TIME, GAME_COUNT_DOWN);

        //get sensor service
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //register sensor
        jumpEventListener = new JumpEventListener(true);
        sensorManager.registerListener(jumpEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        //reading jump data during the entire game level one
        jumpDataReader = new JumpDataReader(JUMP_INTERVAL, jumpEventListener, jumpSoundPlayer);
    }

    @Override
    protected void onStart() {
        super.onStart();

        backGroundMusicPlayer.start();

        jumpIntroPlayer.start();
        jumpIntroPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        gameCountDownTimer.start();
                        jumpDataReader.startTask();
                    }
                }
        );

    }

    @Override
    protected void onStop() {
        super.onStop();

        jumpIntroPlayer.stop();
        jumpDataReader.stopTask();
        sensorManager.unregisterListener(jumpEventListener);
        backGroundMusicPlayer.stop();
        gameCountDownTimer.cancel();
        gameOverPlayer.stop();
        nextLevelPlayer.stop();


        jumpCount += jumpDataReader.getCounter();
        Log.d(TAG, "jumpCount " + jumpCount);
        jumpCounterTextView.setText("" + jumpCount);
    }

    public void backHome(View view) {
        finish();
    }

    public void startNextLevel(View view) {
        finish();
        Intent intent = new Intent(this, LevelTwoActivity.class);
        startActivity(intent);
    }

    public void startThisLevel(View view) {
        finish();
        Intent intent = new Intent(this, LevelOneActivity.class);
        startActivity(intent);
    }

    public class GameCountDownTimer extends CountDownTimer {

        public GameCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            remainMilli = millisUntilFinished;

            //calculate minutes and seconds from milliseconds
            String minute = "" + (millisUntilFinished / 1000) / 60;
            String second = "" + (millisUntilFinished / 1000) % 60;

            //apply style to minute and second
            if ((millisUntilFinished / 1000) / 60 < 10)
                minute = "0" + (millisUntilFinished / 1000) / 60;
            if ((millisUntilFinished / 1000) % 60 < 10)
                second = "0" + (millisUntilFinished / 1000) % 60;

            // update textview with remaining time
            timerCounterTextView.setText(minute + ":" + second);

        }

        @Override
        public void onFinish() {
            this.cancel();
            timerCounterTextView.setText("00:00");
            jumpDataReader.stopTask();
            sensorManager.unregisterListener(jumpEventListener);
            backGroundMusicPlayer.stop();

            jumpCount += jumpDataReader.getCounter();
            Log.d(TAG, "last jumpCount " + jumpCount);
            jumpCounterTextView.setText("" + jumpCount);

            //forward to next level
            if(jumpCount > 5) {
                nextLevelPlayer.start();
                //
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putBoolean("levelOnePass", true).commit();
                //show next button
                nextLevelBtn.setVisibility(View.VISIBLE);
            } else {
                gameOverPlayer.start();
                tryAgainBtn.setVisibility(View.VISIBLE);
            }

        }

    }
}
