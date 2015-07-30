package com.example.myapplication3.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void onStartButtonClicked(View view){
        EditText editText = (EditText)findViewById(R.id.numSamp);
        int num=100;
        if(!isStringEmpty(editText.getText())) num = Integer.parseInt(editText.getText().toString());
        Intent i = new Intent(getApplicationContext(),KeyboardPort.class);
        i.putExtra("num",Integer.toString(num));
        startActivity(i);


    }
    public boolean isStringEmpty(Editable string){
        if(string != null){
            if(!string.toString().isEmpty()){
                return false;
            }
        }
         return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
