package com.example.pfdforandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import java.lang.String;
import android.view.ViewGroup.LayoutParams;

import static java.lang.Math.abs;


public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroScopeEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(gyroscopeSensor==null){
            Toast.makeText(this, "This device ain't got no gyroscope",Toast.LENGTH_SHORT).show();
            finish();
        }
        gyroScopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
//                final LayoutParams layoutparams = (RelativeLayout.LayoutParams) Txt1.getLayoutParams();
//
//                if(event.values[2] > 0.5f){
//                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
//                }
//                else if(event.values[2] < -0.5f){
//                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
//
                View artHorizGround = (View)findViewById(R.id.artHorizGround);
                artHorizGround.getLayoutParams().height = ((int)(event.values[2]*100)+800);
                artHorizGround.requestLayout();
                artHorizGround.setRotation(-((event.values[1]-9)*10));
                TextView text =(TextView)findViewById(R.id.textView);
                text.setText(Integer.toString((int) (event.values[1])));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(gyroScopeEventListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(gyroScopeEventListener);
    }
}
