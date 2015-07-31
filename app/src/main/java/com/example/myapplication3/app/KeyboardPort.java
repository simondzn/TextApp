package com.example.myapplication3.app;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.text.Editable;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.*;
import java.util.ArrayList;
import static com.example.myapplication3.app.R.id.keyboardview;


public class KeyboardPort extends Activity implements  SensorEventListener {
    SensorManager sensorManager;
    private ArrayList sensorData;
    private ArrayList rotationData;
    private ArrayList touchData;
    private ArrayList linAccelData, gyroData, magnoData;
    EditText text;
    private ArrayList keyVal;
    CustomKeyboard mCustomKeyboard;
    int id, num=0;
    public Keyboard keyboard;
    KeyboardView keyView;
    long timestamp, timestampUp;
    float x, y;
    String toWrite;
    char[] nowOn= new char[20];
    TextView textTo;
    ArrayList<String> strings;
    public static String Id;
    public int i=0;
    public TextView leftNum;
    Thread thread;
    private Long startingNano;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_port);

        leftNum = (TextView)findViewById(R.id.textView13);
        textTo = (TextView)findViewById(R.id.textView10);
        strings = new ArrayList();
//        strings[0] = getResources().getString(R.string.write1);
//        strings[1] = getResources().getString(R.string.write2);
//        strings[2] = getResources().getString(R.string.write3);
//        strings[3] = getResources().getString(R.string.write4);
//        strings[4] = getResources().getString(R.string.write5);
//        strings[5] = getResources().getString(R.string.write6);
//        strings[6] = getResources().getString(R.string.write7);
//        strings[7] = getResources().getString(R.string.write8);
//        strings[8] = getResources().getString(R.string.write9);
//        strings[9] = getResources().getString(R.string.write10);
//        strings[10] = getResources().getString(R.string.write11);
//        strings[11] = getResources().getString(R.string.write12);
//        strings[12] = getResources().getString(R.string.write13);
//        strings[13] = getResources().getString(R.string.write14);
//        strings[14] = getResources().getString(R.string.write16);
//        strings[15] = getResources().getString(R.string.write17);
//        strings[16] = getResources().getString(R.string.write18);
//        strings[17] = getResources().getString(R.string.write19);
//        strings[18] = getResources().getString(R.string.write20);
//        strings[19] = getResources().getString(R.string.write21);
//        strings[20] = getResources().getString(R.string.write22);
//        strings[21] = "";
//        strings[22] = "";
        File sdcard = Environment.getExternalStorageDirectory();
        InputStream stream =  getResources().openRawResource(R.raw.expiriment);
//Get the text file
        File file = new File(sdcard,"Expiriment1_Text.txt");
        if(file.exists()) {

//Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader buffreader = new BufferedReader(new FileReader(file));
                String line;

                while ((line = buffreader.readLine()) != null) {
//                    text.append(line);
//                    text.append('\n');
                    strings.add(line);

                }
                buffreader.close();
            } catch (IOException e) {
                //You'll need to add proper error handling here
            }

        }else {
            InputStreamReader inputreader = new InputStreamReader(stream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line,line1 = "";
            try
            {
                while ((line = buffreader.readLine()) != null)
                    strings.add(line);
                i++;

            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        Intent extras = getIntent();
        Id = extras.getStringExtra("num");



//        Bundle extras = getIntent().getExtras();
//        num = extras.getInt("num");
//        if (num == 0) {
//            num = 100;
//        }
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
                thread = new Thread(runnable);

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

    @Override
    protected void onStart() {
        super.onStart();
//        toWrite.getChars(0, 19,nowOn, 0);
        textTo.setText(strings.get(0));
        leftNum.setText(Integer.toString(num+1) +  "/" + Integer.toString(strings.size()));
        File dir = new File("/storage/emulated/0/Download/User-" + Id);
        dir.mkdirs();
    }

    public void midWrite() throws IOException {
        String str;
        String csvw = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +  "/User-"+ Id + "/Key_key_port.csv";
        CSVWriter writer_touch = new CSVWriter(new FileWriter(csvw,true));
        for(int j=0;j<keyVal.size(); j++) {
            try {
                writer_touch.writeNext( keyVal.get(j).toString());
            }catch (IndexOutOfBoundsException e){

            }
        }
        writer_touch.close();
        keyVal.clear();

        String csv1 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +  "/User-"+ Id + "/Touch_key_port.csv";
        CSVWriter writer_touch1 = new CSVWriter(new FileWriter(csv1,true));
        for(int j=0;j<touchData.size(); j++) {
            str = touchData.get(j).toString();
            try {
                writer_touch1.writeNext(str);
            }catch (IndexOutOfBoundsException e){

            }
        }
        writer_touch1.close();
        touchData.clear();

//        Write the Accelerometer data
        String csv = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Accel_key_port.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv,true));
        for(int j=0;j<sensorData.size(); j += 1) {
            writer.writeNext(sensorData.get(j).toString());
        }
        writer.close();
        sensorData.clear();

        //      Write the rotation data
        String csv2 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Rotation_key_port.csv";
        CSVWriter writer_rotation = new CSVWriter(new FileWriter(csv2,true));
        for(int j=0;j<rotationData.size(); j += 1) {
            writer_rotation.writeNext(rotationData.get(j).toString());
        }
        writer_rotation.close();
        rotationData.clear();


        // Write the linear accelerometer
        String csv3 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Accel_lin_key_port.csv";
        CSVWriter writer_lin = new CSVWriter(new FileWriter(csv3,true));
        for(int j=0;j<linAccelData.size(); j += 1) {
            writer_lin.writeNext(linAccelData.get(j).toString());
        }
        writer_lin.close();
        linAccelData.clear();

        // Write the Gyroscope
        String csv4 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/User-"+ Id + "/Gyro_key_port.csv";
        CSVWriter writer_gyro = new CSVWriter(new FileWriter(csv4,true));
        for(int j=0;j<gyroData.size(); j += 1) {
            writer_gyro.writeNext(gyroData.get(j).toString());
        }
        writer_gyro.close();
        gyroData.clear();


//              Write the Magnometer
        String csv5 = android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +  "/User-"+ Id + "/Magno_key_port.csv";
        CSVWriter writer_magno = new CSVWriter(new FileWriter(csv5,true));
        for(int j=0;j<magnoData.size(); j += 1) {
            writer_magno.writeNext(magnoData.get(j).toString());
        }
        writer_magno.close();
        magnoData.clear();
    }

    public void stopButtonClick() throws IOException {







        Intent i = new Intent(getApplicationContext(),KeyboardPort2.class);
//        i.putExtra("strings",strings);
        i.putExtra("num", Id);
        startActivity(i);
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        long sensorTimestemp = System.nanoTime();;
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            AcceVals acceVals = new AcceVals(x, y, z, sensorTimestemp);
            sensorData.add(acceVals);
        }
        else if(event.sensor.getType()== Sensor.TYPE_ROTATION_VECTOR){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
//            sensorTimestemp = System.nanoTime();
            AcceVals acceVals = new AcceVals(x, y, z, sensorTimestemp);
            rotationData.add(acceVals);
        }
        else if(event.sensor.getType()== Sensor.TYPE_LINEAR_ACCELERATION) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
//            sensorTimestemp = System.nanoTime();
            AcceVals acceVals = new AcceVals(x, y, z, sensorTimestemp);
            linAccelData.add(acceVals);
        }else if(event.sensor.getType()== Sensor.TYPE_GYROSCOPE){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
//            sensorTimestemp = System.nanoTime();
            AcceVals acceVals = new AcceVals(x, y, z, sensorTimestemp);
            gyroData.add(acceVals);
        }else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            sensorTimestemp = System.nanoTime();
            AcceVals acceVals = new AcceVals(x, y, z, sensorTimestemp);
            magnoData.add(acceVals);
        }
        if(linAccelData.size()>2000||sensorData.size()>2000||gyroData.size()>2000)
            try {
                midWrite();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


}
