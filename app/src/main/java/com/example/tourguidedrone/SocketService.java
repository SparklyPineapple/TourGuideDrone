package com.example.tourguidedrone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SocketService extends Service {
    //socket variables
    public static final String SERVERIP = "10.13.78.162";//TODO insert pi or computer IP
    public static final int SERVERPORT = 8010;
    Socket socket;
    // InetAddress serverAddr; //todo remove?
    private BufferedReader reader = null;
    private OutputStream writer = null;
    private String serverSays;

    //Service Variables
    private IBinder mBinder = new myBinder();
    private Boolean runDone = false;

    //Button Variables
    private Boolean  connectIsPressed, startIsPressed, cancelIsPressed, disconnectIsPressed, emergencyLandIsPressed;
    private Boolean isThreadDestroyable = false;


    //GPS variables
    LocationTrackService locationTrackServe;

    public IBinder onBind(Intent intent) {
        System.out.println("I am in Ibinder onBind method");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        System.out.println("I am in Ibinder onRebind method");

        super.onRebind(intent);
    }

    public class myBinder extends Binder {
        public SocketService getService(){
            return SocketService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStartCommand(intent, flags, startId);//todo is this even needed?
        locationTrackServe = new LocationTrackService(getApplicationContext());
        Log.i("S_update", "onStartCommand");
        Runnable connect = new connectSocket();
        new Thread(connect).start();

        return START_STICKY;
    }

    public String serverSaysWhat(){
        return serverSays;
    }

    class connectSocket implements Runnable {

        @Override
        public void run() {
            try {
                //here you must put your computer's IP address.
                // serverAddr = InetAddress.getByName(SERVERIP);//todo remove?
                Log.i("connectSocket","we are in run()");

                //create a socket to make the connection with the server
                socket = new Socket(SERVERIP, SERVERPORT);

            } catch (Exception e) {
                Log.e("S_Error", "Error from making socket", e);
            }

            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }catch (Exception e) {
                Log.e("S_Error", "Error from making socket reader", e);
            }

            try{
                writer = socket.getOutputStream();
            } catch (Exception e){
                Log.e("S_Error", "Error from making socket writer", e);
            }

            //TODO concept test, delete when used
            serverSays = readMessage();
            double lat = locationTrackServe.getLatitude();
            double lon = locationTrackServe.getLongitude();
            String messageToSend = "Hi I am phone, my lat:"+lat+",  my lon:"+ lon +"\n";
            sendMessage(messageToSend);
            runDone = true;
        }

    }

    //for checking when the socketThread.run() has finished
    public Boolean getRunDone(){
        return runDone;
    }


    public String getLatLonString(){
        Log.i("S_update", "in getLatLon()String");

        if(locationTrackServe != null){
            return "GPS:  Lat-"+locationTrackServe.getLatitude()+",  Lon-"+locationTrackServe.getLongitude()+".\n";
        } else{
            Log.d("S_debug", "LocationTrackServe was null");
            return "location service not available";
        }
    }

    //returns -1111 if error
    public double getLat(){
        Log.i("S_update", "in getLat()");
        if(locationTrackServe != null){
            return locationTrackServe.getLatitude();
        } else{
            Log.d("S_debug", "LocationTrackServe was null");
            return -1111;
        }
    }

    //returns -1111 if error
    public double getLon(){
        Log.i("S_update", "in getLon()");
        if(locationTrackServe != null){
            return locationTrackServe.getLongitude();
        } else{
            Log.d("S_debug", "LocationTrackServe was null");
            return -1111;
        }
    }


    public void sendMessage(String message){
        if (writer != null) {
            Log.i("S_update","sendMessage"+message);
            try {
                writer.write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("S_error","Error writing from client to server", e);
            }

        }else{
            Log.d("S_Error","socketservice.sendMessage(): writer was null");
        }
    }

    public String readMessage(){
        String message = "";
        if (reader != null) {
            Log.i("S_info","in readMessage"+message);
            try {
                message = reader.readLine();
                Log.i("S_info", "message read:"+message);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("S_error","Error reading from server", e);
            }

        }else{
            Log.d("S_Error","socketservice.readMessage(): reader was null");
        }
        return message;
    }

    @Override
    public void onDestroy() {
        Log.i("S_update", "onDestroy()");
        super.onDestroy();
        try {
            socket.close();
        } catch (Exception e) {
            Log.e("S_error","Error closing server", e);
            e.printStackTrace();
        }
        socket = null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    public Boolean getConnectIsPressed() {
        return connectIsPressed;
    }

    public void setConnectIsPressed(Boolean connectIsPressed) {
        this.connectIsPressed = connectIsPressed;
    }

    public Boolean getStartIsPressed() {
        return startIsPressed;
    }

    public void setStartIsPressed(Boolean startIsPressed) {
        this.startIsPressed = startIsPressed;
    }

    public Boolean getCancelIsPressed() {
        return cancelIsPressed;
    }

    public void setCancelIsPressed(Boolean cancelIsPressed) {
        this.cancelIsPressed = cancelIsPressed;
    }

    public Boolean getDisconnectIsPressed() {
        return disconnectIsPressed;
    }

    public void setDisconnectIsPressed(Boolean disconnectIsPressed) {
        this.disconnectIsPressed = disconnectIsPressed;
    }

    public Boolean getEmergencyLandIsPressed() {
        return emergencyLandIsPressed;
    }

    public void setEmergencyLandIsPressed(Boolean emergencyLandIsPressed) {
        this.emergencyLandIsPressed = emergencyLandIsPressed;
    }

    public Boolean getThreadDestroyable() {
        return isThreadDestroyable;
    }

    public void setThreadDestroyable(Boolean threadDestroyable) {
        isThreadDestroyable = threadDestroyable;
    }
}
