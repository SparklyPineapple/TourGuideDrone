package com.example.tourguidedrone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //class variables
    private int destNum = -1;
    private int startNum = -1;
    protected Spinner selectDestList;
    protected Spinner spinner_startPlace;
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


        //DEST PLACE SPINNER HERE
        //populate and set up destination drop down menu
        selectDestList = (Spinner) findViewById(R.id.selectDestList);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.destListArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        selectDestList.setAdapter(adapter);

        //set up scrolling on debug tet
        debugTextView = findViewById(R.id.debugTextView);
        debugTextView.setMovementMethod(new ScrollingMovementMethod());


        //START PLACE SPINNER HERE

        spinner_startPlace = (Spinner) findViewById(R.id.spinner_startPlace);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterStart = ArrayAdapter.createFromResource(this,
                R.array.destListArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner_startPlace.setAdapter(adapterStart);


        //DEBUG TEXT SCROLLER HERE
        //set up scrolling on debug tet
        debugTextView = findViewById(R.id.debugTextView);
        debugTextView.setMovementMethod(new ScrollingMovementMethod());


        //stop/start listeners + Async/thread deployment
        startBtn = findViewById(R.id.startButton);
        stopBtn = findViewById(R.id.disconnectButton);
        gpsTextView = findViewById(R.id.gpsTextViewStatus);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get string from spinner to be sent to socket client
                String destName = selectDestList.getSelectedItem().toString();
                if (destName.equals("(SSC) Stevens Student Center")){
                    destNum = 1;
                    //debugTextView.append("\n SSC");
                }else if (destName.equals("(DMC) Dixon Ministry Center")){
                    destNum = 10;
                    //debugTextView.append("\n DMC");
                }else if (destName.equals("(BTS) Center for Biblical and Theological Studies")){
                    destNum = 12;
                    //debugTextView.append("\n BTS");
                }else if (destName.equals("(ENS) Engineering and Science Center")){
                    destNum = 23;
                    //debugTextView.append("\n ENS");
                }else if (destName.equals("(HSC) Health and Science Center")){
                    destNum = 29;
                    //debugTextView.append("\n HSC");
                }

                //now to start place spinner selection
                String startName = spinner_startPlace.getSelectedItem().toString();
                if (startName.equals("(SSC) Stevens Student Center")){
                    startNum = 1;
                    //debugTextView.append("\n SSC");
                }else if (startName.equals("(DMC) Dixon Ministry Center")){
                    startNum = 10;
                    //debugTextView.append("\n DMC");
                }else if (startName.equals("(BTS) Center for Biblical and Theological Studies")){
                    startNum = 12;
                    //debugTextView.append("\n BTS");
                }else if (startName.equals("(ENS) Engineering and Science Center")){
                    startNum = 23;
                    //debugTextView.append("\n ENS");
                }else if (startName.equals("(HSC) Health and Science Center")){
                    startNum = 29;
                    //debugTextView.append("\n HSC");
                }

                //pi wifi details:
                    //IP: "192.168.4.1"
                    //PORT: 8000
                //kirby's wifi details
                    //IP = "10.13.78.162"
                    //PORT: 8080
                phoneClient = new asyncClient(gpsTextView, "192.168.4.1", 8000, startNum, destNum, debugTextView );
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
