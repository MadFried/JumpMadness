package edu.neu.madcourse.ranchen.jumpmadness.jumpMadness.jumpMadness;

import android.app.Activity;
import android.app.AlertDialog;
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
 * Created by cheerhou on 4/16/16.
 */
public class LevelSixActivity extends Activity {
    public static final String TAG = "LevelSixActivity";
    public static final String PREFS_NAME = "MyPrefsFile";

    public static final long JUMP_INTERVAL = 1000;
    public static final long COIN_JUMP_INTERVAL = 500;
    private final long GAME_TOTAL_TIME = 90000;
    private final long GAME_COUNT_DOWN = 1000;

    public static MediaPlayer backGroundMusicPlayer;
    private MediaPlayer countOffBeginPlayer;
    private MediaPlayer jumpSoundPlayer;
    private MediaPlayer brickNotificationPlayer;
    private MediaPlayer brickSoundPlayer;
    private MediaPlayer normalJumpNotificationPlayer;
    private MediaPlayer kickTurtlePlayer;
    private MediaPlayer turtleNotificationPlayer;
    private MediaPlayer coinPlayer;
    private MediaPlayer coinNotificationPlayer;
    private MediaPlayer gameOverPlayer;
    private MediaPlayer warningPlayer;
    private MediaPlayer stageClearPlayer;

    private SensorManager sensorManager;
    private JumpEventListener jumpEventListener;
    private JumpDataReader jumpDataReader;
    public static GameCountDownTimer gameCountDownTimer;

    private long remainMilli;
    private int jumpCount = 0;

    TextView timerCounterTextView;
    TextView jumpCounterTextView;
    TextView timerlabel;
    TextView jumplabel;
    Typeface custom_font;
    TextView enemyLabel;
    TextView enemyTextView;

    Button backHomeBtn;
    Button nextLevelBtn;
    Button tryAgainBtn;


    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_two);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/mario_fonts.ttf");


        mContext = LevelSixActivity.this;

        backGroundMusicPlayer = MediaPlayer.create(this, R.raw.level_two_bgm);
        backGroundMusicPlayer.setLooping(true);
        backGroundMusicPlayer.setVolume(1.0f, 1.0f);

        countOffBeginPlayer = MediaPlayer.create(this, R.raw.count_off_begin_game);
        countOffBeginPlayer.setLooping(false);
        countOffBeginPlayer.setVolume(1.0f, 1.0f);

        jumpSoundPlayer = MediaPlayer.create(this, R.raw.jump_sound);
        jumpSoundPlayer.setLooping(false);
        jumpSoundPlayer.setVolume(1.0f, 1.0f);

        brickNotificationPlayer = MediaPlayer.create(this, R.raw.break_brick_ntf);
        brickNotificationPlayer.setLooping(false);
        brickNotificationPlayer.setVolume(1.0f, 1.0f);

        brickSoundPlayer = MediaPlayer.create(this, R.raw.break_brick);
        brickSoundPlayer.setLooping(false);
        brickSoundPlayer.setVolume(1.0f, 1.0f);

        normalJumpNotificationPlayer = MediaPlayer.create(this, R.raw.normal_jump_tutorial);
        normalJumpNotificationPlayer.setLooping(false);
        normalJumpNotificationPlayer.setVolume(1.0f, 1.0f);

        kickTurtlePlayer = MediaPlayer.create(this, R.raw.smb_kick);
        kickTurtlePlayer.setLooping(false);
        kickTurtlePlayer.setVolume(1.0f, 1.0f);

        turtleNotificationPlayer = MediaPlayer.create(this, R.raw.koopa_ntf);
        turtleNotificationPlayer.setLooping(false);
        turtleNotificationPlayer.setVolume(1.0f, 1.0f);

        coinPlayer = MediaPlayer.create(this, R.raw.smb_coin);
        coinPlayer.setVolume(1.0f, 1.0f);
        coinPlayer.setLooping(false);

        coinNotificationPlayer = MediaPlayer.create(this, R.raw.coins_ntf);
        coinNotificationPlayer.setLooping(false);
        coinNotificationPlayer.setVolume(1.0f, 1.0f);

        gameOverPlayer = MediaPlayer.create(this, R.raw.smb_gameover);
        gameOverPlayer.setLooping(false);
        gameOverPlayer.setVolume(1.0f, 1.0f);

        stageClearPlayer = MediaPlayer.create(this,R.raw.smb_stage_clear);
        stageClearPlayer.setLooping(false);
        stageClearPlayer.setVolume(1.0f, 1.0f);

        warningPlayer = MediaPlayer.create(this, R.raw.smb_warning);
        warningPlayer.setLooping(true);
        warningPlayer.setVolume(1.0f, 1.0f);


        timerCounterTextView = (TextView) findViewById(R.id.timer_text);
        timerCounterTextView.setTypeface(custom_font);
        jumpCounterTextView = (TextView) findViewById(R.id.jump_text_view);
        jumpCounterTextView.setTypeface(custom_font);
        jumplabel = (TextView) findViewById(R.id.jump_lable);
        jumplabel.setTypeface(custom_font);
        timerlabel = (TextView) findViewById(R.id.timer_label);
        timerlabel.setTypeface(custom_font);
        enemyLabel = (TextView) findViewById(R.id.enemy_lable);
        enemyLabel.setTypeface(custom_font);
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

        //start count off
        countOffBeginPlayer.start();
        countOffBeginPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        backGroundMusicPlayer.start();
                        gameCountDownTimer.start();
                        jumpDataReader.startTask();
                    }
                }
        );
    }

    @Override
    protected void onStop() {
        super.onStop();

        jumpDataReader.stopTask();
        countOffBeginPlayer.stop();
        sensorManager.unregisterListener(jumpEventListener);
        backGroundMusicPlayer.stop();
        gameCountDownTimer.cancel();
        warningPlayer.stop();
        gameOverPlayer.stop();
        stageClearPlayer.stop();

        jumpCount += jumpDataReader.getCounter();
        Log.d(TAG, "jumpCount " + jumpCount);
        jumpCounterTextView.setText("" + jumpCount);
    }

    public void backHome(View view) {
        finish();
    }

    public void startThisLevel(View view) {
        finish();
        Intent intent = new Intent(this, LevelSixActivity.class);
        startActivity(intent);
    }

    public void normalJump() {
        jumpDataReader.stopTask();
        jumpCount += jumpDataReader.getCounter();

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

    public void brickAppear() {
        jumpDataReader.stopTask();
        jumpCount = jumpDataReader.getCounter();


        brickNotificationPlayer.start();
        jumpDataReader = new JumpDataReader(JUMP_INTERVAL, jumpEventListener, jumpSoundPlayer, brickSoundPlayer, "brick");

        brickNotificationPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        jumpDataReader.startTask();
                    }
                }

        );
    }

    public void kickTurtle() {
        jumpDataReader.stopTask();
        jumpCount += jumpDataReader.getCounter();

        turtleNotificationPlayer.start();
        jumpDataReader = new JumpDataReader(JUMP_INTERVAL, jumpEventListener, jumpSoundPlayer, kickTurtlePlayer, gameOverPlayer, mContext, "turtle");

        turtleNotificationPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                jumpDataReader.startTask();
            }
        });
    }

    public void jumpForCoins() {
        jumpDataReader.stopTask();
        jumpCount += jumpDataReader.getCounter();

        coinNotificationPlayer.start();
        jumpDataReader = new JumpDataReader(COIN_JUMP_INTERVAL, jumpEventListener, jumpSoundPlayer, coinPlayer, "coins");

        coinNotificationPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                jumpDataReader.startTask();
            }
        });

    }

    /**
     * Game count down timer
     */
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

            //coin shows up after 20s
            if (remainMilli < (GAME_TOTAL_TIME - 20000) && remainMilli > GAME_TOTAL_TIME - GAME_COUNT_DOWN - 20000) {
                Log.d(TAG, ">>>>>>>>>>>>>>>>start jump for coins");
                jumpForCoins();
            }

            //back to normal jump after collecting coins for 10s
            if (remainMilli < (GAME_TOTAL_TIME - 30000) && remainMilli > GAME_TOTAL_TIME - GAME_COUNT_DOWN - 30000) {
                Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>back to normal");
                normalJump();
            }

            //turtle comes up after normal jump for 30s
            if (remainMilli < (GAME_TOTAL_TIME - 60000) && remainMilli > (GAME_TOTAL_TIME - GAME_COUNT_DOWN) - 60000) {
                Log.d(TAG, ">>>>>>>>>>>>>>>start to kick turtle");
                kickTurtle();
            }

            //back to normal after turtle appears for 5s
            if (remainMilli < (GAME_TOTAL_TIME - 65000) && remainMilli > (GAME_TOTAL_TIME - GAME_COUNT_DOWN) - 65000) {
                Log.d(TAG, ">>>>>>>>>>>>>>>>>Start normal jump......");
                normalJump();
            }

            if (remainMilli < 10000 && remainMilli > 9000) {
                warningPlayer.start();
            }
        }

        @Override
        public void onFinish() {
            this.cancel();
            timerCounterTextView.setText("Time's up!");
            jumpDataReader.stopTask();
            sensorManager.unregisterListener(jumpEventListener);

            backGroundMusicPlayer.stop();
            warningPlayer.stop();

            //store jumps
            int oldJumps = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getInt("LevelSixActivity", 0);
            if (jumpCount > oldJumps) {
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putInt("LevelSixActivity", jumpCount).commit();
            }

            //success
            if (jumpCount >= 60) {
                stageClearPlayer.start();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LevelSixActivity.this);
                AlertDialog alert = alertDialog.create();
                alert.show();
                alert.getWindow().getAttributes();
                alert.setContentView(R.layout.alterdialog);

                TextView message = (TextView) alert.findViewById(R.id.dialog_message);
                message.setTypeface(custom_font);
                message.setText("Congratulations! Your score is :" + jumpCount + "\n" + "You have finished all the levels!!");

   /*             Button dialogButton = (Button) alert.findViewById(R.id.play_button);
                dialogButton.setText("Play!");
                dialogButton.setTypeface(custom_font);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent goNextLevel = new Intent(LevelSixActivity.this, LevelSixActivity.class);
                        startActivity(goNextLevel);
                    }
                });*/
                Button returnMenu = (Button) alert.findViewById(R.id.return_menu_button);
                returnMenu.setText("Return to menu");
                returnMenu.setTypeface(custom_font);
                returnMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent goMenu = new Intent(LevelSixActivity.this, GameLevelMenuActivity.class);
                        startActivity(goMenu);
                    }
                });
            }
            //failed
            if (jumpCount < 60) {
                gameOverPlayer.start();
                tryAgainBtn.setVisibility(View.VISIBLE);

/*                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LevelSixActivity.this);
            AlertDialog alert = alertDialog.create();
            alert.show();
            alert.getWindow().getAttributes();
            alert.setContentView(R.layout.alterdialog);

            TextView message = (TextView) alert.findViewById(R.id.dialog_message);
            message.setTypeface(custom_font);
            message.setText("Game Over! Your score is :" + jumpCount + "\n");

            Button dialogButton = (Button) alert.findViewById(R.id.play_button);
            dialogButton.setText("RePlay!");
            dialogButton.setTypeface(custom_font);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent replayLevel = new Intent(LevelSixActivity.this, LevelSixActivity.class);
                    startActivity(replayLevel);
                }
            });*/
        }

            jumpCount += jumpDataReader.getCounter();
            Log.d(TAG, "jumpCount " + jumpCount);
            jumpCounterTextView.setText("" + jumpCount);
        }
    }
}
