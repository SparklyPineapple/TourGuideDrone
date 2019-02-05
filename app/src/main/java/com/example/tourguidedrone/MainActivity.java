package com.example.tourguidedrone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //class variables
    private String destinationString = "you should never see me in this form :) destination string";
    protected Spinner selectDestList;// = findViewById(R.id.selectDestList);

    //socket communcation: phone to pi
    private double destLat = 0;
    private double destLong = 0;
    private double phoneLat = 0;
    private double phoneLong = 0;
    private boolean start = false; //start drone flying etc etc
    private boolean stop = false; //for stop button only. stop button for stopping drone where it is immedately
    private boolean emergencyLand = false; //emergency landing at a location (coordinates to be decided/given later)

    //socket communcation: pi to phone
    private boolean ack = false;
    private double droneLat = 0;
    private double droneLong = 0;
    private int droneAlt = 0;
    private int droneVelocity = 0;
    private int droneHeading = 0;

    //set up text editing for "debugtext
    TextView debugTextView;

    //For GPS
    LocationManager locationManager = null;
    LocationListener locationListener = null;


    //For Future Use.....
    //SPINNER/DROPDOWN MENU: to get selected value from destinationDropDownList
    //use "String text = mySpinner.getSelectedItem().toString();"

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

        //set up txt/debug strings for use
        debugTextView = (TextView) findViewById(R.id.debugTextView);

        //stop/start listeners + Async/thread deployment


        //GPS, request for user's location (per Android 6.0), set up location manager + location listener
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                debugTextView.setText("GPS is turned off. Unable to access location" + "\n");
            }
        };
        //refresh location ever .5 sec, ignore distance crossed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] );



            return;
        }
        locationManager.requestLocationUpdates("gps", 500, 0, locationListener);

    }


    ////////////////////////////try loop timer + access location
    //connected to start button for messing with timer and GPS, delete this later
    public void testingStuff(View view){
        debugTextView.setText("TIMER START:" + "\n");
        int counter = 0;
        //start timer part

        SystemClock.sleep(1000);




    }






}
