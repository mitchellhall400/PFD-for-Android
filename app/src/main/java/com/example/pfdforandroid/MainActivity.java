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
import java.net.URL;
import java.net.MalformedURLException;

/* Main Activity Loop */
public class MainActivity extends AppCompatActivity {

    /* Variable Initializations */
    private SensorManager sensorManager;
    private Sensor altitudeSensor;
    private Sensor orientationSensor;
    private SensorEventListener altitudeEventListener;
    private SensorEventListener orientationEventListener;
//    String ICAO = "KCID";
//    String link = "https://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&hoursBeforeNow=2&format=xml&stationString=" + ICAO;
//    TextView weather = (TextView)findViewById(R.id.weather);

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
                ImageView heading = (ImageView)findViewById(R.id.compass);
                View artHorizGround = (View)findViewById(R.id.artHorizGround);
                ImageView attitudeInd = (ImageView)findViewById(R.id.attitudeArrow);

                /* Set Heading */
                double headingVal = Math.pow((Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2)),0.5);
                heading.setRotation(event.values[0]*-1);

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
