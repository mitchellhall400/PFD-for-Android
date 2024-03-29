package com.example.pfdforandroid;

/* Imports */
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import java.lang.String;

/* Definition of MainActivity Class */
public class MainActivity extends AppCompatActivity {

    /* Variable Initializations */
    private SensorManager sensorManager;
    private Sensor altitudeSensor;
    private Sensor orientationSensor;
    private SensorEventListener altitudeEventListener;
    private SensorEventListener orientationEventListener;

    /* onCreate Loop */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* Initial Setup */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        altitudeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        /* Magnet Heading Loop */
        orientationEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                /* Initialize Variables for Heading*/
                ImageView heading = findViewById(R.id.compass);
                ImageView attitudeInd = findViewById(R.id.attitudeArrow);
                View artHorizGround = findViewById(R.id.artHorizGround);

                /* Set Heading */
                heading.setRotation(event.values[0] * -1);

                /* Set Pitch */
                artHorizGround.getLayoutParams().height = (int)(event.values[1] * 30) + 4200;

                /* Set Roll */
                artHorizGround.setRotation(event.values[2]);
                artHorizGround.requestLayout();

                /* Set Attitude */
                attitudeInd.setPivotX(30);
                attitudeInd.setPivotY(430);
                attitudeInd.setRotation((float)(-1.3 * event.values[2]));

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}       //Nothing needed
        };

        /* Altitude  Loop */
        altitudeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                /* Initialize Variables for Altitude */
                TextView alt = findViewById(R.id.altitudeVal);
                String altitudeString= "";
                ImageView altScroll = findViewById(R.id.altitudeScroll);

                /* Get Altitude */
                float altitude = SensorManager.getAltitude((float)1011.6, event.values[0]) * (float)3.281;

                /* Set String to Fill Zeros Up To Four Digits */
                if((int)altitude < 1000) {
                    altitudeString = "0" + altitude;
                }

                /* Set Altitude */
                alt.setText(altitudeString);
                altScroll.setScrollY((int)(2500 - (altitude *1.42)));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}       //Nothing needed


        };

    }

    /* User Defined Functions Outside of MainActivity Class*/

    /* onResume - register event listeners for sensors */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(altitudeEventListener,altitudeSensor,SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(orientationEventListener,orientationSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    /* onPause - unregister event listeners for sensors */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(altitudeEventListener);
        sensorManager.unregisterListener(orientationEventListener);
    }
}
