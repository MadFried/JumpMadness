package edu.neu.madcourse.ranchen.jumpmadness.jumpMadness.jumpMadness;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import edu.neu.madcourse.ranchen.jumpmadness.R;

/**
 * Created by cheerhou on 4/10/16.
 */
public class JumpDataReader {
    public static final String TAG = "Reader";

    private long interval;
    private Timer timer;
    private TimerTask timerTask;
    private MediaPlayer jumpSoundPlayer;
    private MediaPlayer otherSoundPlayer;
    private MediaPlayer gameOverPlayer;
    private JumpEventListener jumpEventListener;
    private String rule;

    private ArrayList<Float> jumpXData;
    private ArrayList<Float> jumpYData;
    private ArrayList<Float> jumpZData;
    private ArrayList<Float> filteredJumpXData;
    private ArrayList<Float> filteredJumpYData;
    private ArrayList<Float> filteredJumpZData;
    private ArrayList<Float> totalJumpData = new ArrayList<>();

    private int counter = 0;
    private int enemyCounter = 0;

    protected Context context;
    private Handler handler;


    public JumpDataReader(long interval, JumpEventListener jumpEventListener, MediaPlayer jumpSoundPlayer) {
        this.interval = interval;
        this.jumpEventListener = jumpEventListener;
        this.jumpSoundPlayer = jumpSoundPlayer;
        this.rule = "jump";
    }

    public JumpDataReader(long interval, JumpEventListener jumpEventListener, MediaPlayer jumpSoundPlayer, MediaPlayer otherSoundPlayer ,String rule) {
        this.interval = interval;
        this.jumpEventListener = jumpEventListener;
        this.jumpSoundPlayer = jumpSoundPlayer;
        this.otherSoundPlayer = otherSoundPlayer;
        this.rule = rule;
    }

    public JumpDataReader(long interval, JumpEventListener jumpEventListener, MediaPlayer jumpSoundPlayer, MediaPlayer otherSoundPlayer, MediaPlayer gameOverPlayer, Context context, String rule) {
        this.interval = interval;
        this.jumpEventListener = jumpEventListener;
        this.jumpSoundPlayer = jumpSoundPlayer;
        this.otherSoundPlayer = otherSoundPlayer;
        this.gameOverPlayer = gameOverPlayer;
        this.context = context;
        this.rule = rule;
    }




    public void startTask() {
        timer = new Timer();
        initializeTimerTask();
        //schedule the timer, after the first 0.5s the TimerTask will run every interval
        timer.schedule(timerTask, 800, interval);
    }

    private void initializeTimerTask() {

        timerTask = new TimerTask() {
            @Override
            public void run() {

                if (jumpEventListener.isStarted()) {
                    jumpEventListener.setStarted(false);
                    Log.d(TAG, ">>>>>>>>>>>>>>>>>Stop reading jump data......");

                    //x-axis values
                    jumpXData = jumpEventListener.getXValueList();
                    filteredJumpXData = jumpEventListener.weightedSmoothingFilter(jumpXData);
                    Log.d(TAG, "jumpXData " + jumpXData.toString());
                    Log.d(TAG, "filteredJumpXData " + filteredJumpXData.toString());

                    //y-axis values
                    jumpYData = jumpEventListener.getYValueList();
                    filteredJumpYData = jumpEventListener.weightedSmoothingFilter(jumpYData);
//                    Log.d(TAG, "jumpYData " + jumpYData.toString());
                    Log.d(TAG, "filteredJumpYData " + filteredJumpYData.toString());

                    //z-axis values
                    jumpZData = jumpEventListener.getZValueList();
                    filteredJumpZData = jumpEventListener.weightedSmoothingFilter(jumpZData);
//                    Log.d(TAG, "jumpZData " + jumpZData.toString());
                    Log.d(TAG, "filteredJumpZData " + filteredJumpZData.toString());

                    //total acceleration
                    totalJumpData.clear();
                    for (int i = 0; i < filteredJumpXData.size(); i++) {
                        float x = filteredJumpXData.get(i);
                        float y = filteredJumpYData.get(i);
                        float z = filteredJumpZData.get(i);

                        float total = calculateTotalAcceleration(x, y, z);
                        totalJumpData.add(total);
                    }
                    Log.d(TAG, "totalJumpData " + totalJumpData.toString());

                    jumpDetection(filteredJumpXData, filteredJumpYData, filteredJumpZData, totalJumpData, rule);

                }

                //simulate sound
                jumpSoundPlayer.start();
                //start saving jump data
                Log.d(TAG, ">>>>>>>>>>>>>>>>>Start saving jump data......");
                jumpEventListener.setStarted(true);
            }
        };
    }

    public void stopTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public float calculateTotalAcceleration(float x, float y, float z) {
        float total;
        float orientation = x + y + z;
        float absTotal = (float) Math.sqrt(x * x + y * y + z * z);

        if (orientation >= 0) {
            total = absTotal;
        } else {
            total = -absTotal;
        }
        return total;
    }

    public void jumpDetection(ArrayList<Float> xValue, ArrayList<Float> yValue, ArrayList<Float> zValue, ArrayList<Float> totalValue, String rule) {
        //total
        float maxTotal = Collections.max(totalValue);
//        float minTotal = Collections.min(totalValue);
        //        float total = maxTotal - minTotal;
        Log.d(TAG, "maxTotal " + maxTotal);
//        Log.d(TAG, "minTotal " + minTotal);
        //Log.d(TAG, "total" + total);

        //x value
        float maxXvalue = Collections.max(xValue);
        float minXvalue = Collections.min(xValue);
        Float xDiff = maxXvalue - minXvalue;

        float maxYvalue = Collections.max(yValue);
        float minYvalue = Collections.min(yValue);
        float yDiff = maxYvalue - minYvalue;
        Log.d(TAG, "maxX " + maxXvalue);
        Log.d(TAG, "minX " + minXvalue);
//        Log.d(TAG, "xDiff " + xDiff);

        //y value
//        float maxYvalue = Collections.max(yValue);
//        float minYvalue = Collections.min(yValue);
//        Float yDiff = maxYvalue - minYvalue;
//        Log.d(TAG, "maxY " + maxYvalue);
//        Log.d(TAG, "minY " + minYvalue);
//        Log.d(TAG, "yDiff" + yDiff);

        float maxZValue = Collections.max(zValue);
        float minZValue = Collections.min(zValue);
        float zDiff = maxZValue - minZValue;
//        Log.d(TAG, "maxZ " + maxZValue);
//        Log.d(TAG, "minZ " + minZValue);
//        Log.d(TAG, "zDiff" + zDiff);


        //different rules to count a successful jump action
        if(rule.equals("jump")) {
            //A successful jump is counted here
            if (maxTotal >= 8.0f) {
                counter++;
            }
        }

        //double score when brick appear
        if(rule.equals("brick")){
            if (maxTotal >= 10.0f) {
                //sound notifies user if he breaks a brick
                otherSoundPlayer.start();
                counter += 2;
            }
        }

        if(rule.equals("fire")){
            float tmp = maxTotal - maxXvalue;
            Log.d(TAG, "tmp " + tmp);
            if(tmp >= 1.2f && tmp <= 4.0f){
                otherSoundPlayer.start();
                counter += 5;
                enemyCounter++;
                Log.d(TAG, "enemyCounter " + enemyCounter);
            }
        }

        if(rule.equals("turtle")) {
            if (zDiff > 2) {
                if (yDiff < 5 && yDiff > 2) { //SOME KIND OF RULE
                    otherSoundPlayer.start();
                    counter++;
                }
            }
            else {
                gameOverPlayer.start();
                stopTask();
                handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        final LevelSixActivity levelSixActivity = new LevelSixActivity();
                        levelSixActivity.backGroundMusicPlayer.stop();
                        levelSixActivity.gameCountDownTimer.cancel();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        TextView titleText = new TextView(context);
                        titleText.setText("Game Over!");
                        titleText.setTextSize(30);
                        titleText.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mario_fonts.ttf"));
                        alertDialog.setCustomTitle(titleText);
                        alertDialog.setMessage("You hit the turtle! ");
                        alertDialog.setIcon(R.mipmap.ic_launcher);

                        alertDialog.setPositiveButton("Return to menu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //start next activity
                                Intent restartActivity = new Intent(context, GameLevelMenuActivity.class);
                                levelSixActivity.finish();
                                context.startActivity(restartActivity);

                            }
                        });
                        AlertDialog alert = alertDialog.create();
                        alert.show();
                        alert.getWindow().getAttributes();

                        TextView textView = (TextView) alert.findViewById(android.R.id.message);
                        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mario_fonts.ttf"));
                        Button btn1 = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        btn1.setTextSize(16);
                        btn1.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mario_fonts.ttf"));
                    }
                });
            }
        }

        if(rule.equals("levelFourTurtle")) {
            if (zDiff > 2) {
                if (yDiff < 5 && yDiff > 2) { //SOME KIND OF RULE
                    otherSoundPlayer.start();
                    counter++;
                }
            }
            else {
                gameOverPlayer.start();
                stopTask();
                handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        final LevelFourActivity levelFourActivity = new LevelFourActivity();
                        levelFourActivity.backGroundMusicPlayer.stop();
                        levelFourActivity.gameCountDownTimer.cancel();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                        TextView titleText = new TextView(context);
                        titleText.setText("Game Over!");
                        titleText.setTextSize(30);
                        titleText.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mario_fonts.ttf"));
                        alertDialog.setCustomTitle(titleText);
                        alertDialog.setMessage("You hit the turtle! ");
                        alertDialog.setIcon(R.mipmap.ic_launcher);

                        alertDialog.setPositiveButton("Return to menu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //start next activity
                                Intent restartActivity = new Intent(context, GameLevelMenuActivity.class);
                                levelFourActivity.finish();
                                context.startActivity(restartActivity);

                            }
                        });
                        AlertDialog alert = alertDialog.create();
                        alert.show();
                        alert.getWindow().getAttributes();

                        TextView textView = (TextView) alert.findViewById(android.R.id.message);
                        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mario_fonts.ttf"));
                        Button btn1 = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        btn1.setTextSize(16);
                        btn1.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mario_fonts.ttf"));
                    }
                });
            }
        }

        if(rule.equals("coins")) {
            if(maxTotal >= 9.0f) {  //jump for coin rule
                otherSoundPlayer.start();
                counter += 2;
            }
        }
    }


    public int getCounter() {
        return counter;
    }

    public int getEnemyCounter() {
        return enemyCounter;
    }
}
