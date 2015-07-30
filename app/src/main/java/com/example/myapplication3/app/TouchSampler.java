package com.example.myapplication3.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVWriter;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;



public class TouchSampler extends Activity implements SensorEventListener {
    SensorManager sensorManager;
    private ArrayList sensorData;
    private ArrayList touchData;
    private ArrayList rotationData;
    private ArrayList linAccelData;
    float lastx = 0;
    float lasty = 0;
    static final float ALPHA = 0.25f;
    float[] gravSensorVals;
    int count=0;
    TextView countText;
    int num ;
    long timestamp, timestampUp;
    float x, y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_sampler);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorData = new ArrayList();
        linAccelData = new ArrayList();
        touchData = new ArrayList();
        rotationData = new ArrayList();
        Sensor accel = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accel,
                SensorManager.SENSOR_DELAY_FASTEST);
        Sensor rotation = sensorManager
                .getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotation,
                SensorManager.SENSOR_DELAY_FASTEST);
        Sensor linAccel = sensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, linAccel,
                SensorManager.SENSOR_DELAY_FASTEST);


    }
    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }
@Override
    public boolean onTouchEvent(MotionEvent event) {
    int action = event.getAction();
    Bundle extras = getIntent().getExtras();
    num = extras.getInt("num");
    if (num == 0) {
        num = 100;
    }
    countText = (TextView) findViewById(R.id.textView3);
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
        super.onBackPressed();
        try {
            stopButtonClick();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        long futureTime = System.currentTimeMillis() + 4000;
//        while(System.currentTimeMillis()<futureTime) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
           // gravSensorVals = lowPass(event.values.clone(), gravSensorVals);
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
        String csv = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Accel.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv));
        for(int j=0;j<sensorData.size(); j += 1) {
            writer.writeNext(new String[]{sensorData.get(j).toString()});
        }
        writer.close();
//      Write the touch data
        String csv1 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Touch.csv";
        CSVWriter writer_touch = new CSVWriter(new FileWriter(csv1));
        for(int j=0;j<touchData.size(); j += 1) {
            writer_touch.writeNext(new String[]{touchData.get(j).toString()});
        }
        writer_touch.close();
//      Write the rotation data
        String csv2 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Rotation.csv";
        CSVWriter writer_rotation = new CSVWriter(new FileWriter(csv2));
        for(int j=0;j<rotationData.size(); j += 1) {
            writer_rotation.writeNext(new String[]{rotationData.get(j).toString()});
        }
        writer_rotation.close();
        // Write the linear accelerometer
        String csv3 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Accel_lin.csv";
        CSVWriter writer_lin = new CSVWriter(new FileWriter(csv3));
        for(int j=0;j<linAccelData.size(); j += 1) {
            writer_lin.writeNext(new String[]{linAccelData.get(j).toString()});
        }
        writer_lin.close();


        Intent i = new Intent(getApplicationContext(),Vertical.class);
        i.putExtra("num",num);
        startActivity(i);
    }


}
