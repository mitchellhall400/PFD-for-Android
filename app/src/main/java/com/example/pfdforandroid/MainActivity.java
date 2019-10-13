package com.example.pfdforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.Space;
import java.lang.String;
import android.view.ViewGroup.LayoutParams;


import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor magneticSensor;
    private Sensor altitudeSensor;
    private SensorEventListener accelerometerEventListener;
    private SensorEventListener magneticEventListener;
    private SensorEventListener altitudeEventListener;
    private Compass compass;


    private float[] RollHistory = new float[100];
    private float[] PitchHistory = new float[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        altitudeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);


        /* Accelerometer Loop */
        accelerometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                View artHorizGround = (View)findViewById(R.id.artHorizGround);
                Space space = (Space)findViewById(R.id.space);

                /* Set Pitch */
                if(event.values[0] > 0) {
                    addToArray(PitchHistory, ((event.values[2] * 100) + 1400) - (event.values[0] * 200));
                    artHorizGround.getLayoutParams().height = (int) (filter(PitchHistory));
                    space.getLayoutParams().height = (int) (filter(PitchHistory)/100);
                    space.requestLayout();
                    artHorizGround.requestLayout();
                }
                else {
                    addToArray(PitchHistory, ((event.values[2] * 100) + 1400) + (event.values[0] * 40));
                    artHorizGround.getLayoutParams().height = (int) (filter(PitchHistory));
                    artHorizGround.requestLayout();
                }

                /* Set Roll */
                if(event.values[0] > 0) {
                    addToArray(RollHistory, (float) (event.values[0] * 9.5));
                    artHorizGround.setRotation((int) (filter(RollHistory)));
                    space.setRotation((int) (filter(RollHistory)));
                }
                else {
                    addToArray(RollHistory, (float) (event.values[0] * 9.5));
                    artHorizGround.setRotation((int) (filter(RollHistory)));
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        /* Magnet Heading Loop */
        magneticEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                ImageView heading = (ImageView)findViewById(R.id.compass);

                /* Set Heading */
                double headingVal = Math.pow((Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2)),0.5);
                heading.setRotation(event.values[1]);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        /* Altitude  Loop */
        altitudeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                TextView alt = (TextView)findViewById(R.id.altitude);
                float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, event.values[0]);
                alt.setText(Integer.toString((int)((altitude)*0.0328084)));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    public static void addToArray(float[] history, float element){
        for(int i = history.length-1; i > 0; i--){
            history[i] = history[i-1];
        }
        history[0] = element;
    }

    public static float filter(float[] history){
        float average = 0;
        for(int i = 0; i < history.length-1;i++) {
            average += history[i];
        }
        return (average/history.length);
    }



    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(accelerometerEventListener,accelerometerSensor,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(magneticEventListener,magneticSensor,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(altitudeEventListener,magneticSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(accelerometerEventListener);
        sensorManager.unregisterListener(magneticEventListener);
        sensorManager.unregisterListener(altitudeEventListener);
    }
}
