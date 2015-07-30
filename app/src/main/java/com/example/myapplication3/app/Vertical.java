package com.example.myapplication3.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class Vertical extends Activity implements SensorEventListener {
    SensorManager sensorManager;
    private ArrayList sensorData;
    private ArrayList touchData;
    private ArrayList rotationData;
    private ArrayList linAccelData;
    float lastx = 0;
    float lasty = 0;
    int count=0;
    TextView countText;
    int num;
    long timestamp, timestampUp;
    float x, y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorData = new ArrayList();
        linAccelData = new ArrayList();
        touchData = new ArrayList();
        rotationData = new ArrayList();
        Sensor accel = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        Sensor rotation = sensorManager
                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotation,
                SensorManager.SENSOR_DELAY_FASTEST);
        Sensor linAccel = sensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, linAccel,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Bundle extras = getIntent().getExtras();
        num = extras.getInt("num");
        if (num == 0) {
            num = 100;
        }
        countText = (TextView) findViewById(R.id.textView6);
        switch (action){
            case MotionEvent.ACTION_DOWN:{
                timestamp = System.currentTimeMillis();
                x = event.getX();
                y = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP:{
                timestampUp = System.currentTimeMillis();
                TouchVals touchVals = new TouchVals(x, y, timestamp, timestampUp);
                touchData.add(touchVals);
                count++;
                countText.setText(Integer.toString(count));
                break;
            }
        }
        if (count == num) {
            try {
                stopButtonClick();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return super.onTouchEvent(event);

    }


    public void onBackPressed()  {
        try {
            stopButtonClick();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.currentTimeMillis();
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            sensorData.add(acceVals);
        }
        else if(event.sensor.getType()== Sensor.TYPE_ROTATION_VECTOR){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.currentTimeMillis();
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            rotationData.add(acceVals);
        }
        else if(event.sensor.getType()== Sensor.TYPE_LINEAR_ACCELERATION) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.currentTimeMillis();
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            linAccelData.add(acceVals);
        }
    }





    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void stopButtonClick() throws IOException {
        sensorManager.unregisterListener(this);
//        Write the Accelerometer data
        String csv = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Accel_land.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv));
        for(int j=0;j<sensorData.size(); j += 1) {
            writer.writeNext(sensorData.get(j).toString());
        }
        writer.close();
//      Write the touch data
        String csv1 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Touch_land.csv";
        CSVWriter writer_touch = new CSVWriter(new FileWriter(csv1));
        for(int j=0;j<touchData.size(); j += 1) {
            writer_touch.writeNext(touchData.get(j).toString());
        }
        writer_touch.close();
//      Write the rotation data
        String csv2 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Rotation_land.csv";
        CSVWriter writer_rotation = new CSVWriter(new FileWriter(csv2));
        for(int j=0;j<rotationData.size(); j += 1) {
            writer_rotation.writeNext(rotationData.get(j).toString());
        }
        writer_rotation.close();
        // Write the linear accelerometer
        String csv3 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Accel_lin_land.csv";
        CSVWriter writer_lin = new CSVWriter(new FileWriter(csv3));
        for(int j=0;j<linAccelData.size(); j += 1) {
            writer_lin.writeNext(linAccelData.get(j).toString());
        }
        writer_lin.close();


        Intent i = new Intent(getApplicationContext(),KeyboardLand.class);
        i.putExtra("num",num);
        startActivity(i);
    }



}