package com.example.myapplication3.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.myapplication3.app.R.id.keyboardview;


public class KeyboardLand extends Activity implements SensorEventListener {
    SensorManager sensorManager;
    private ArrayList sensorData;
    private ArrayList rotationData;
    private ArrayList touchData;
    private ArrayList linAccelData,gyroData, magnoData;
    EditText text;
    private ArrayList keyVal;
    CustomKeyboard mCustomKeyboard;
    int num;
    float lastx = 0;
    float lasty = 0;
    public Keyboard keyboard;
    KeyboardView keyView;
    long timestamp, timestampUp;
    float x, y;
    char[] nowOn= new char[6];
    TextView textTo, leftNum;
    ArrayList<String> strings;
    String Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_land);

        textTo = (TextView)findViewById(R.id.textView12);
//        toWrite = getResources().getString(R.string.towrite);
//        toWrite.getChars(0, 5,nowOn, 0);

        Bundle extras = getIntent().getExtras();
        Id = extras.getString("num");
        strings = extras.getStringArrayList("strings");
        textTo.setText(strings.get(0));
        leftNum = (TextView)findViewById(R.id.textView14);
        leftNum.setText(Integer.toString(num+1) + "/" + Integer.toString(strings.size()));

        touchData = new ArrayList();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorData = new ArrayList();
        linAccelData = new ArrayList();
        rotationData = new ArrayList();
        gyroData = new ArrayList();
        magnoData = new ArrayList();

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
        Sensor gyro = sensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyro,
                SensorManager.SENSOR_DELAY_FASTEST);
        Sensor magno = sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magno,
                SensorManager.SENSOR_DELAY_FASTEST);

        keyVal = new ArrayList();
        text = (EditText) findViewById(R.id.editText5);
        mCustomKeyboard = new CustomKeyboard(this, keyboardview, R.xml.hexkbd);
        mCustomKeyboard.registerEditText(R.id.editText5);
        keyView = (KeyboardView) findViewById(keyboardview);
        keyboard = keyView.getKeyboard();


        keyView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int primaryCode) {

            }

            @Override
            public void onRelease(int primaryCode) {

            }


            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
//                Write to csv thread

                Runnable runnable = new Runnable() {
                    public void run() {
                        try {
                            midWrite();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(runnable);


                text.setText(text.getText() + String.valueOf((char) primaryCode));
                String keyS;
                keyS = String.valueOf((char) primaryCode);
                KeyVals keyVals = new KeyVals(keyS);
                keyVal.add(keyVals);

                 if (primaryCode == -5) {
                    int length = text.getText().length();
                    text.getText().delete(length-2,length);
                    String s = text.getText().toString();
                    text.setText(s);

                }else if( primaryCode==-3 ) {
                    thread.start();
                     try {
                        stopButtonClick();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(primaryCode == 46) {

                     num++;
                     if(num < strings.size()) {

                         leftNum.setText(Integer.toString(num + 1) + "/" + Integer.toString(strings.size()));
                         textTo.setText(strings.get(num));
                         text.setText("");
                     }
                 }
                if(num==strings.size()) {
                    try {
                        stopButtonClick();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onText(CharSequence text) {

            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }

        });


        keyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN: {
                        timestamp = System.nanoTime();
                        x = event.getX();
                        y = event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        timestampUp = System.nanoTime();
                        TouchVals touchVals = new TouchVals(x, y, timestamp, timestampUp);
                        touchData.add(touchVals);
                        break;
                    }
                }


                return false;
            }
        });
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    public void midWrite() throws IOException {

//        Write the Accelerometer data
        String csv = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Accel_key_land.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv,true));
        for(int j=0;j<sensorData.size(); j += 1) {
            writer.writeNext(sensorData.get(j).toString());
        }
        writer.close();
        sensorData.clear();

        //      Write the rotation data
        String csv2 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Rotation_key_land.csv";
        CSVWriter writer_rotation = new CSVWriter(new FileWriter(csv2,true));
        for(int j=0;j<rotationData.size(); j += 1) {
            writer_rotation.writeNext(rotationData.get(j).toString());
        }
        writer_rotation.close();
        rotationData.clear();


        // Write the linear accelerometer
        String csv3 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Accel_lin_key_land.csv";
        CSVWriter writer_lin = new CSVWriter(new FileWriter(csv3,true));
        for(int j=0;j<linAccelData.size(); j += 1) {
            writer_lin.writeNext(linAccelData.get(j).toString());
        }
        writer_lin.close();
        linAccelData.clear();


        //      Write the touch data
        String csv0 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Key_key_land.csv";
        CSVWriter writer_touch0 = new CSVWriter(new FileWriter(csv0,true));
        for(int j=0;j<keyVal.size(); j += 1) {
            try {
                writer_touch0.writeNext(keyVal.get(j).toString());
            }catch (IndexOutOfBoundsException e){
            }
        }
        writer_touch0.close();
        keyVal.clear();

        String csv1 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Touch_key_land.csv";
        CSVWriter writer_touch1 = new CSVWriter(new FileWriter(csv1,true));
        for(int j=0;j<touchData.size(); j += 1) {
            try {
                writer_touch1.writeNext(touchData.get(j).toString());
            }catch (IndexOutOfBoundsException e){
            }
        }
        writer_touch1.close();
        touchData.clear();

        // Write the Gyroscope
        String csv4 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Gyro_key_land.csv";
        CSVWriter writer_gyro = new CSVWriter(new FileWriter(csv4,true));
        for(int j=0;j<gyroData.size(); j += 1) {
            writer_gyro.writeNext(gyroData.get(j).toString());
        }
        writer_gyro.close();
        gyroData.clear();


//              Write the Magnometer
        String csv5 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +  "/User-"+ Id + "/Magno_key_land.csv";
        CSVWriter writer_magno = new CSVWriter(new FileWriter(csv5,true));
        for(int j=0;j<magnoData.size(); j += 1) {
            writer_magno.writeNext(magnoData.get(j).toString());
        }
        writer_magno.close();
        magnoData.clear();
    }

    public void stopButtonClick() throws IOException {


//        //      Write the touch data
//        String csv0 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Touch_key_land.csv";
//        CSVWriter writer_touch0 = new CSVWriter(new FileWriter(csv0));
//        for(int j=0;j<touchData.size(); j += 1) {
//            writer_touch0.writeNext(touchData.get(j).toString() + ", " + keyVal.get(j).toString());
//        }
//        writer_touch0.close();
//
////      Write the rotation data
//        String csv2 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Rotation_key_land.csv";
//        CSVWriter writer_rotation = new CSVWriter(new FileWriter(csv2));
//        for(int j=0;j<rotationData.size(); j += 1) {
//            writer_rotation.writeNext(rotationData.get(j).toString());
//        }
//        writer_rotation.close();
//        // Write the linear accelerometer
//        String csv3 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Accel_lin_key_land.csv";
//        CSVWriter writer_lin = new CSVWriter(new FileWriter(csv3));
//        for(int j=0;j<linAccelData.size(); j += 1) {
//            writer_lin.writeNext(linAccelData.get(j).toString());
//        }
//        writer_lin.close();
        midWrite();

        Intent i = new Intent(getApplicationContext(),ThankYou.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.nanoTime();
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            sensorData.add(acceVals);
        }
        else if(event.sensor.getType()== Sensor.TYPE_ROTATION_VECTOR){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.nanoTime();
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            rotationData.add(acceVals);
        }
        else if(event.sensor.getType()== Sensor.TYPE_LINEAR_ACCELERATION) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.nanoTime();
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            linAccelData.add(acceVals);
        }else if(event.sensor.getType()== Sensor.TYPE_GYROSCOPE){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.nanoTime();
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            gyroData.add(acceVals);
        }else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.nanoTime();
            AcceVals acceVals = new AcceVals(x, y, z, timestamp);
            magnoData.add(acceVals);
        }
        if(linAccelData.size()>2000||sensorData.size()>2000||rotationData.size()>2000)
            try {
                midWrite();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
