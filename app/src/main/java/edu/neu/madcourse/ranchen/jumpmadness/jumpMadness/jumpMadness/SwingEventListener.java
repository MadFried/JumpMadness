package edu.neu.madcourse.ranchen.jumpmadness.jumpMadness.jumpMadness;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;


public class SwingEventListener implements SensorEventListener {
    public static final String TAG = "Swing";
    public static final int SHAKE_THRESHOLD = 30;

    private float x;
    private float y;
    private float z;
    private float oldX;
    private float oldY;
    private float oldZ;
    private long swingLastTime;
    private long swingCurrentTime;
    private long lastTime;
    private long currentTime;

    private JumpEventListener jumpEventListener;
    private MediaPlayer mediaPlayer;
    private ArrayList<Float> jumpXData;
    private ArrayList<Float> filteredJumpXData;
    private ArrayList<Float> jumpYData;

    private ArrayList<Float> jumpZData;
    private float maxXValue;
    private float maxYValue;
    private float maxZValue;
    private TextView xTextView;
    private TextView yTextView;
    private TextView zTextView;

    String xStr = "";
    String yStr = "";
    String zStr = "";

    public SwingEventListener(MediaPlayer mediaPlayer, JumpEventListener jumpEventListener) {
        this.mediaPlayer = mediaPlayer;
        this.jumpEventListener = jumpEventListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        swingCurrentTime = System.currentTimeMillis();
        if (swingCurrentTime - swingLastTime > 100) {
            long diffTime = swingCurrentTime - swingLastTime;
            swingLastTime = swingCurrentTime;

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            float diffX = Math.abs(x - oldX);
            float diffY = Math.abs(y - oldY);
            float diffZ = Math.abs(z - oldZ);

            float speed = (diffX + diffY + diffZ) / diffTime * 100;
//            Log.d(TAG, "speed " + speed);

            if (speed > SHAKE_THRESHOLD) {

                if (jumpEventListener.isStarted()) {
//                    TODO read jump data
//                    Log.d(TAG, ">>>>>>>>>>>>>>>>>Stop detecting jump......");
                    jumpEventListener.setStarted(false);

                    jumpXData = jumpEventListener.getXValueList();
                    filteredJumpXData = jumpEventListener.weightedSmoothingFilter(jumpXData);
//                    Log.d(TAG, "jumpXData " + jumpXData.toString());
//                    Log.d(TAG, "filteredJumpXData " + filteredJumpXData.toString());


                    jumpYData = jumpEventListener.getYValueList();
                    jumpZData = jumpEventListener.getZValueList();


//                    if (!jumpXData.isEmpty()) {
//                        maxXValue = Collections.max(jumpXData);
//                        Log.d(TAG, "Max X " + maxXValue);
//
//                        xStr = xStr + Float.toString(maxXValue) + " ";
//                        xTextView.setText(xStr);
//                    }
//                    if (!jumpYData.isEmpty()) {
//                        maxYValue = Collections.max(jumpYData);
//                        Log.d(TAG, "Max Y " + maxYValue);
//
//                        yStr = yStr + Float.toString(maxYValue) + " ";
//                        yTextView.setText(yStr);
//                    }
//                    if (!jumpZData.isEmpty()) {
//                        maxZValue = Collections.max(jumpZData);
//                        Log.d(TAG, "Max Z " + maxZValue);
//
//                        zStr = zStr + Float.toString(maxZValue) + " ";
//                        zTextView.setText(zStr);
//                    }

                }

                //TODO: start detect jump
                mediaPlayer.start();
                Log.d(TAG, ">>>>>>>>>>>>>>>>>Start detecting jump......");
                jumpEventListener.setStarted(true);

            }
        }

        //save x, y, z
        oldX = x;
        oldY = y;
        oldZ = z;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
