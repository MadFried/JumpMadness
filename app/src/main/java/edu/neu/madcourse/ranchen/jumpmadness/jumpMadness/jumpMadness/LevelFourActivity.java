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
 *enemies appear in this level
 */
public class LevelFourActivity extends Activity {
    public static final String TAG = "LevelFour";
    public static final String PREFS_NAME = "MyPrefsFile";
    private static final long JUMP_INTERVAL = 1000;
    private static final long GAME_TOTAL_TIME = 40000;
    private static final long GAME_COUNT_DOWN = 1000;

    public static MediaPlayer backGroundMusicPlayer;
    private MediaPlayer jumpSoundPlayer;
    private MediaPlayer enemyIntroPlayer;
    private MediaPlayer nextLevelPlayer;
    private MediaPlayer fireSoundPlayer;
    private MediaPlayer fireNotificationPlayer;
    private MediaPlayer kickTurtlePlayer;
    private MediaPlayer turtleNotificationPlayer;
    private MediaPlayer normalJumpNotificationPlayer;
    private MediaPlayer gameOverPlayer;

    private Context mContext;
    private SensorManager sensorManager;
    private JumpEventListener jumpEventListener;
    private JumpDataReader jumpDataReader;
    public static GameCountDownTimer gameCountDownTimer;

    private long remainMilli;
    private int jumpCount = 0;
    private int enemyCount = 0;

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
        mContext = LevelFourActivity.this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/mario_fonts.ttf");


        backGroundMusicPlayer = MediaPlayer.create(this, R.raw.background_music);
        backGroundMusicPlayer.setLooping(true);
        backGroundMusicPlayer.setVolume(0.4f, 0.4f);

        jumpSoundPlayer = MediaPlayer.create(this, R.raw.jump_sound);
        jumpSoundPlayer.setLooping(false);
        jumpSoundPlayer.setVolume(1.0f, 1.0f);

        enemyIntroPlayer = MediaPlayer.create(this, R.raw.enemy_intro);
        enemyIntroPlayer.setLooping(false);
        enemyIntroPlayer.setVolume(1.0f, 1.0f);

        fireSoundPlayer = MediaPlayer.create(this, R.raw.gun_fire);
        fireSoundPlayer.setLooping(false);
        fireSoundPlayer.setVolume(1.0f, 1.0f);

        fireNotificationPlayer = MediaPlayer.create(this, R.raw.fire_ntf);
        fireNotificationPlayer.setLooping(false);
        fireNotificationPlayer.setVolume(1.0f, 1.0f);

        kickTurtlePlayer = MediaPlayer.create(this, R.raw.smb_kick);
        kickTurtlePlayer.setLooping(false);
        kickTurtlePlayer.setVolume(1.0f, 1.0f);

        turtleNotificationPlayer = MediaPlayer.create(this, R.raw.koopa_ntf);
        turtleNotificationPlayer.setLooping(false);
        turtleNotificationPlayer.setVolume(1.0f, 1.0f);

        normalJumpNotificationPlayer = MediaPlayer.create(this, R.raw.normal_jump_tutorial);
        normalJumpNotificationPlayer.setLooping(false);
        normalJumpNotificationPlayer.setVolume(1.0f, 1.0f);

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
        tryAgainBtn = (Button) findViewById(R.id.try_again_btn);
        nextLevelBtn.setTypeface(custom_font);
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

        enemyIntroPlayer.start();
        enemyIntroPlayer.setOnCompletionListener(
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

        enemyIntroPlayer.stop();
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
        Intent intent = new Intent(this, LevelFiveActivity.class);
        startActivity(intent);
    }

    public void startThisLevel(View view) {
        finish();
        Intent intent = new Intent(this, LevelFourActivity.class);
        startActivity(intent);
    }

    public void enemyAppear() {
        jumpDataReader.stopTask();
        jumpCount += jumpDataReader.getCounter();

        fireNotificationPlayer.start();
        jumpDataReader = new JumpDataReader(JUMP_INTERVAL, jumpEventListener, jumpSoundPlayer, fireSoundPlayer, "fire");

        fireNotificationPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        jumpDataReader.startTask();
                    }
                }

        );
    }

    /**
     * readEnemyCount is a flag to indicate if the counter from jump data reader needs to save to other variable
     *
     * @param readEnemyCount
     */
    public void normalJump(boolean readEnemyCount) {
        jumpDataReader.stopTask();
        if (readEnemyCount) {
            enemyCount = jumpDataReader.getEnemyCounter();
        }
        jumpCount += jumpDataReader.getCounter();
        Log.d(TAG, "jumpCount of first normal and brick jump" + jumpCount);

        normalJumpNotificationPlayer.start();
        jumpDataReader = new JumpDataReader(JUMP_INTERVAL, jumpEventListener, jumpSoundPlayer);

        normalJumpNotificationPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        jumpDataReader.startTask();
                    }
                }
        );
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

            if (remainMilli < (GAME_TOTAL_TIME - 10000) && remainMilli > (GAME_TOTAL_TIME - GAME_COUNT_DOWN - 10000)) {
                Log.d(TAG, ">>>>>>>>>>>>>>>>>Start fire......");
                enemyAppear();
            }

            //back to normal after enemies appear for 10s
            if (remainMilli < (GAME_TOTAL_TIME - 25000) && remainMilli > (GAME_TOTAL_TIME - GAME_COUNT_DOWN - 25000)) {
                Log.d(TAG, ">>>>>>>>>>>>>>>>>Start normal jump......");
                normalJump(true);
            }

            //Koopa Troopa shows up
            if (remainMilli < (GAME_TOTAL_TIME - 30000) && remainMilli > (GAME_TOTAL_TIME - GAME_COUNT_DOWN) - 30000) {
                Log.d(TAG, ">>>>>>>>>>>>>>>start to kick turtle");
                kickTurtle();
            }
            //TODO: Koopa appear here.....

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

            enemyTextView.setText("" + enemyCount);

            //forward to next level
            if (jumpCount > 10) {
                nextLevelPlayer.start();

                //set This Level pass is true
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putBoolean("levelFourPass", true).commit();


                //show next level button
                nextLevelBtn.setVisibility(View.VISIBLE);
            } else {
                gameOverPlayer.start();
                tryAgainBtn.setVisibility(View.VISIBLE);
            }
        }
    }


    public void kickTurtle() {
        jumpDataReader.stopTask();
        jumpCount += jumpDataReader.getCounter();

        turtleNotificationPlayer.start();
        jumpDataReader = new JumpDataReader(JUMP_INTERVAL, jumpEventListener, jumpSoundPlayer, kickTurtlePlayer, gameOverPlayer, mContext, "levelFourTurtle");

        turtleNotificationPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                jumpDataReader.startTask();
            }
        });
    }
}
