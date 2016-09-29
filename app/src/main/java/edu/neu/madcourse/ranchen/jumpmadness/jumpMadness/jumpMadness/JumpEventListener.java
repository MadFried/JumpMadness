package edu.neu.madcourse.ranchen.jumpmadness.jumpMadness.jumpMadness;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;

public class JumpEventListener implements SensorEventListener {
    public static final String TAG = "Jump";
    private static final float ALPHA = 0.8f;
    private static final float A = 0.4f;

    private float[] gravity;
    private boolean useHighPassFilter;
    private ArrayList<Float> xValueList;
    private ArrayList<Float> yValueList;
    private ArrayList<Float> zValueList;
    private boolean started = false;


    public JumpEventListener(boolean useHighPassFilter) {
        this.gravity = new float[3];
        this.useHighPassFilter = useHighPassFilter;
        this.xValueList = new ArrayList<>();
        this.yValueList = new ArrayList<>();
        this.zValueList = new ArrayList<>();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values.clone();

            if (useHighPassFilter) {
                values = highPass(values[0], values[1], values[2]);
            }
            //start saving values
            if (started) {
//            Log.d(TAG, "" + values[0]);
                xValueList.add(values[0]);
                yValueList.add(values[1]);
                zValueList.add(values[2]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * This method derived from the Android documentation and is available under
     * the Apache 2.0 license.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    private float[] highPass(float x, float y, float z) {
        float[] filteredValues = new float[3];

        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

        filteredValues[0] = x - gravity[0];
        filteredValues[1] = y - gravity[1];
        filteredValues[2] = z - gravity[2];

        return filteredValues;
    }


    public ArrayList<Float> weightedSmoothingFilter(ArrayList<Float> xValueList) {
        ArrayList<Float> filteredXValues = new ArrayList<Float>();
        if (!xValueList.isEmpty()) {
            for (int i = 1; i < xValueList.size(); i++) {
                float filteredX = lowPass(xValueList.get(i), xValueList.get(i - 1));
                filteredXValues.add(filteredX);
            }
        }
        return filteredXValues;
    }

    // simple low-pass filter
    float lowPass(float current, float last) {
        return last * (1.0f - A) + current * A;
    }


    public ArrayList<Float> getXValueList() {
        return xValueList;
    }

    public ArrayList<Float> getYValueList() {
        return yValueList;
    }

    public ArrayList<Float> getZValueList() {
        return zValueList;
    }

    public void setStarted(boolean started) {
        if (started) {
            xValueList.clear();
            yValueList.clear();
            zValueList.clear();
        }
        this.started = started;
    }

    public boolean isStarted() {
        return started;
    }


}
