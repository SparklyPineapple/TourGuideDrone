package com.example.tourguidedrone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //class variables
    private boolean isTraveling = false;
    private String destinationString = "you should never see me in this form :) destination string";
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

    //set up text editing for "debugtext
    TextView debugTextView;

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

    public void selectStartButton(){

        //1.freeze dropdown list so that destination cannot be changed, freeze start button two
        //TODO - lesser priority
        //2.check to see what value is chosen in dropdown list & set destination variable
        destinationString = selectDestList.getSelectedItem().toString();
        //3.set isTraveling to true
        isTraveling = true;
        //4.send socket the start code. make sure emergency + stop are false so it doesnt set off landing etc early
        start = true;
        stop = false;
        emergencyLand = false;
        //5. unfreeze emergency landing and stop button (change color to show unusable)
        //TODO - lesser priority
        //6. start the socket and attempt to connect with server
        //TODO - cant be done until ailin is ready :)

        //clear out debug stuff
        debugTextView.setText("START BUTTON PRESSED" + "\n" + "DESTINATION = ....");
    }

    public void selectStopButton(){
        //1. send stocket stop = true
        stop = true;
        //2. say "stopped" on a status bar or something (for debugging)
        debugTextView.append("\n" + "STOP PRESSED. app functionality halted");
        //3. set isTraveling = false
        isTraveling = false;
        //4. unfreeze dropdown (fix back color)
        //TODO - lesser priority
        //5. close connection with socket
        //TODO - cant be done until ailin is ready :)
    }

    ////////////////////////////try loop timer stuff :)


    public void countDownTimer(){





    }


    //For Future Use.....
        //SPINNER/DROPDOWN MENU: to get selected value from destinationDropDownList use "String text = mySpinner.getSelectedItem().toString();"




}
