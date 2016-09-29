package edu.neu.madcourse.ranchen.jumpmadness.jumpMadness.jumpMadness;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import edu.neu.madcourse.ranchen.jumpmadness.R;


public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";

    private MediaPlayer mediaPlayer;
    private SensorManager sensorManager;
    private JumpEventListener jumpEventListener;
    private JumpDataReader jumpDataReader;
    private long interval;
    private boolean isStopped = false;

    TextView jumpCounterTextView;
    EditText intervalTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jump_activity_main);

        jumpCounterTextView = (TextView) findViewById(R.id.jump_text_view);
        intervalTextView = (EditText) findViewById(R.id.interval_text_view);

        mediaPlayer = MediaPlayer.create(this, R.raw.swing_sound);
        mediaPlayer.setLooping(false);
        mediaPlayer.setVolume(10.0f, 10.0f);

        //get sensor service
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        jumpEventListener = new JumpEventListener(false);
        sensorManager.registerListener(jumpEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    //start button event handler
    public void startSensor(View view) {
        if (isStopped) {
            isStopped = false;
            onResume();
        }

        String intervalStr = intervalTextView.getText().toString();
        interval = Float.valueOf(intervalStr).longValue() * 1000;
        Log.d(TAG, "interval " + interval);

        jumpDataReader = new JumpDataReader(interval, jumpEventListener, mediaPlayer);
        jumpDataReader.startTask();
    }

    //stop button event handler
    public void stopSensor(View view) {
        isStopped = true;
        jumpDataReader.stopTask();
        sensorManager.unregisterListener(jumpEventListener);

        int counter = jumpDataReader.getCounter();
        jumpCounterTextView.setText("" + counter);
    }


    @Override
    protected void onStop() {
        super.onStop();

        jumpDataReader.stopTask();
        sensorManager.unregisterListener(jumpEventListener);

    }

    public void cleanTextView(View view) {
        jumpCounterTextView.setText("");
    }

}
