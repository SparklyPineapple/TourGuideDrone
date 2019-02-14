package com.example.tourguidedrone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //class variables
    private String destinationString = "you should never see me in this form :) destination string";
    protected Spinner selectDestList;
    private asyncClient phoneClient;

    //set up text editing for "debugtext
    TextView debugTextView;
    Button startBtn;
    Button stopBtn;
    TextView gpsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up the choices for the dropdown destination list
        selectDestList = findViewById(R.id.selectDestList);
        String[] destListString = new String[]{"Stevens Student Center", "Dixon Ministry Center",
                "Center for Biblical and Theological Studies", "Engineering and Science Center",
                "Health and Science Center"};
        ArrayAdapter<String> selectDestListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, destListString);
        selectDestList.setAdapter(selectDestListAdapter);

        //For Future Use.....
        //SPINNER/DROPDOWN MENU: to get selected value from destinationDropDownList
        //use "String text = mySpinner.getSelectedItem().toString();"



        //set up txt/debug strings for use
        debugTextView = findViewById(R.id.debugTextView);

        //stop/start listeners + Async/thread deployment
        startBtn = findViewById(R.id.startButton);
        stopBtn = findViewById(R.id.stopButton);
        gpsTextView = findViewById(R.id.textView4); //todo change id of this textview?
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destString = ""; //todo extract destination from list
                phoneClient = new asyncClient(gpsTextView, "192.168.4.1", 8080, destString, debugTextView );
                phoneClient.execute();
                stopBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        phoneClient.cancel(true);
                    }
                });

            }
        });


    }


}
