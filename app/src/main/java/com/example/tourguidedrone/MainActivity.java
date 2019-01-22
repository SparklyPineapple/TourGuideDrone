package com.example.tourguidedrone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    //class variables
    private boolean isTraveling = false;
    private String destinationString = "you should never see me";
    protected Spinner selectDestList;// = findViewById(R.id.selectDestList);

    //socket communcation: phone to pi
    private double destLat = 0;
    private double destLong = 0;
    private double phoneLat = 0;
    private double phoneLong = 0;
    private boolean start = false; //start drone flying etc etc
    private boolean stop = false; //for stop button only. stop button for stopping drone where it is immedately
    private boolean emergencyLand = false ; //emergency landing at a location (coordinates to be decided/given later)

    //socket communcation: pi to phone
    private boolean ack = false;
    private double droneLat = 0;
    private double droneLong = 0;
    private int droneAlt =0;
    private int droneVelocity = 0;
    private int droneHeading = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up the choices for the dropdown destination list
        selectDestList = findViewById(R.id.selectDestList);
        String[] destListString = new String[] {"Stevens Student Center", "Dixon Ministry Center",
                "Center for Biblical and Theological Studies", "Engineering and Science Center",
                "Health and Science Center"};
        ArrayAdapter<String> selectDestListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, destListString);
        selectDestList.setAdapter(selectDestListAdapter);

    }

    protected void selectStartButton(){

        //1.freeze dropdown list so that destination cannot be changed, freeze start button two --TODO



        //2.check to see what value is chosen in dropdown list & set destination variable
        destinationString = selectDestList.getSelectedItem().toString();
        //3.set isTraveling to true
        isTraveling = true;
        //4.send socket the start code. make sure emergency + stop are false so it doesnt set off landing etc early
        start = true;
        stop = false;
        emergencyLand = false;
        //5. unfreeze emergency landing and stop button --TODO

    }



    //For Future Use.....
        //SPINNER/DROPDOWN MENU: to get selected value from destinationDropDownList use "String text = mySpinner.getSelectedItem().toString();"




}
