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
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;
import android.view.View;
import android.widget.Space;
import java.lang.String;
import android.view.ViewGroup.LayoutParams;


import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor altitudeSensor;
    private Sensor orientationSensor;
    private SensorEventListener altitudeEventListener;
    private SensorEventListener orientationEventListener;


    private float[] AltHistory = new float[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        altitudeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        /* Magnet Heading Loop */
        orientationEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                ImageView heading = (ImageView)findViewById(R.id.compass);
                View artHorizGround = (View)findViewById(R.id.artHorizGround);

                /* Set Heading */
                double headingVal = Math.pow((Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2)),0.5);
                heading.setRotation(event.values[0]*-1);

                /* Set Pitch */
                artHorizGround.getLayoutParams().height = (int)(event.values[1] * 30) + 4100;

                /* Set Roll */
                artHorizGround.setRotation(event.values[2]);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        /* Altitude  Loop */
        altitudeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                TextView alt = (TextView)findViewById(R.id.altitudeVal);
                String altitudeString="";
                float altitude = SensorManager.getAltitude((float)1011.6, event.values[0]) * (float)3.281;
                if((int)altitude-1000<0){
                    altitudeString = "0"+Integer.toString((int)altitude);
                }
                alt.setText(altitudeString);
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
        sensorManager.registerListener(altitudeEventListener,altitudeSensor,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(orientationEventListener,orientationSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(altitudeEventListener);
        sensorManager.unregisterListener(orientationEventListener);
    }
}
