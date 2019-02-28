package com.example.tourguidedrone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //class variables
    private int destNum = -1;
    protected Spinner selectDestList;
    private asyncClient phoneClient;

    //set up text editing for "debugtext
    TextView debugTextView;
    Button startBtn, connectBtn, cancelBtn, disconBtn, emergLandBtn;
    TextView gpsTextView;

    // variables for connecting so SocketService;
    private SocketService socketService;
    private Boolean isServiceBound = false;
    private ServiceConnection socketServiceConnection;
    private Intent socketServiceIntent;
    private Boolean runDone;
    private Boolean isFlightStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //populate and set up destination drop down menu
        selectDestList = (Spinner) findViewById(R.id.selectDestList);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.destListArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        selectDestList.setAdapter(adapter);

        //set up scrolling on debug text
        debugTextView = findViewById(R.id.debugTextView);
        debugTextView.setMovementMethod(new ScrollingMovementMethod());

        //set up and send all button listening to MainActivity.OnClick
        connectBtn = findViewById(R.id.connectButton);
        startBtn = findViewById(R.id.startButton);
        cancelBtn = findViewById(R.id.cancelButton);
        disconBtn = findViewById(R.id.disconnectButton);
        emergLandBtn = findViewById(R.id.emergancyLandButton);

        connectBtn.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        disconBtn.setOnClickListener(this);
        emergLandBtn.setOnClickListener(this);

        gpsTextView = findViewById(R.id.gpsTextViewStatus);

        //used for making a socketService connection later
        socketServiceIntent = new Intent(this, SocketService.class);

//        startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //get string from spinner to be sent to socket client
//                String destName = selectDestList.getSelectedItem().toString();
//                if (destName.equals("(SSC) Stevens Student Center")){
//                    destNum = 1;
//                    //debugTextView.append("\n SSC");
//                }else if (destName.equals("(DMC) Dixon Ministry Center")){
//                    destNum = 10;
//                    //debugTextView.append("\n DMC");
//                }else if (destName.equals("(BTS) Center for Biblical and Theological Studies")){
//                    destNum = 12;
//                    //debugTextView.append("\n BTS");
//                }else if (destName.equals("(ENS) Engineering and Science Center")){
//                    destNum = 23;
//                    //debugTextView.append("\n ENS");
//                }else if (destName.equals("(HSC) Health and Science Center")){
//                    destNum = 29;
//                    //debugTextView.append("\n HSC");
//                }
//                //pi wifi details:
//                    //IP: "192.168.4.1"
//                    //PORT: 8000
//                //kirby's wifi details
//                    //IP = "10.13.78.162"
//                    //PORT: 8080
//                phoneClient = new asyncClient(gpsTextView, "192.168.4.1", 8000, destNum, debugTextView );
//                phoneClient.execute();
////                stopBtn.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        phoneClient.cancel(true);
////                    }
////                });
//
//            }
//        });

        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateGpsTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();

    }

    private void updateGpsTextView(){
        if(isServiceBound){
            gpsTextView.setText(socketService.getLatLonString());
        }else{
            gpsTextView.setText("GPS: None Available");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connectButton:

                if(!isServiceBound){
                    startService(socketServiceIntent);
                    bindService();
                    socketService.setConnectIsPressed(true);
                    debugTextView.setText("SocketService started and bound: you can now call SocketService Functions");
                }else{
                    debugTextView.setText("SocketService already Bound");
                }
                break;
            case R.id.startButton:
                setDestNumFromScroll();
                if(isServiceBound){
                    socketService.setStartIsPressed(true);
                }
                break;
            case R.id.cancelButton:
                if(isServiceBound){
                    socketService.setCancelIsPressed(true);
                }
                break;
            case R.id.disconnectButton:
                if(isServiceBound){
                    socketService.setDisconnectIsPressed(true);

                    if(socketService.getThreadDestroyable()) {//todo: alternatively write in SocketService function LastMessage(){this.stopself()}
                        unbindService();
                    }
                    debugTextView.setText("SocketService Destroyed");
                }else{
                    debugTextView.setText("SocketService already Destroyed or never stated");
                }
                break;
            case R.id.emergancyLandButton:
                break;
        }


    }

    //TODO once a second update things function


    private void setDestNumFromScroll(){
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
    }



    private void bindService(){
        Log.i("MA_inf", "BindService()");
        if(socketServiceConnection == null){
            socketServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
                    SocketService.myBinder myBinder = (SocketService.myBinder)serviceBinder;
                    socketService = ((SocketService.myBinder) serviceBinder).getService();
                    isServiceBound = true;
                    Log.i("MA_inf", "bindservice.onServiceConnected");
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Log.i("MA_inf", "bindservice.onServiceDisconnected");
                    isServiceBound = false;
                }
            };
        }
        bindService(socketServiceIntent, socketServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService(){
        if(isServiceBound){
            unbindService(socketServiceConnection);
            isServiceBound=false;
        }
    }

}