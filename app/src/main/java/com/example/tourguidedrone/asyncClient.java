package com.example.tourguidedrone;

import android.location.GpsStatus;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class asyncClient extends AsyncTask<Void, String, String> {

    //Class Variables--------------------------------------------------------------------------------
    private Socket socket = null;
    private String ipOfServer = "";
    private int portNum = -1;
    private TextView debugTextView;
   // private gps object ? //TODO see to-do near constructor
    private TextView gpsTextView;
    private String receivedMessage = "string receivedMessage";
    //socket communication: phone to pi
    private double destLat = 0; //a table of mapping coord to dest is in phone if we do it this way. do we care? or do we want to do it all in the drone?
    private double destLong = 0;
    private double phoneLat = 0;
    private double phoneLong = 0;
    private boolean phoneStartTriggered = false; //referred to as start in the spreadsheet
    private boolean phoneStopTriggered = false; //referred to as stop in the spreadsheet. renamed for clarification
    private boolean emergencyLand = false;
    //socket communication: pi to phone
    private boolean ack = false;
    private double droneLat = 0;
    private double droneLong = 0;
    private int droneAlt =0;
    private int droneVelocity = 0;
    private int droneHeading = 0;

    //should get this from debugging from the drone????
    private boolean drone_arrived = false; //kirby added since you want to stop when drone stops and drone GPS + phone GPS are different


    //thread functions-----------------------------------------------------------------------------------
    //gui objects that async reads or writes from must be in constructor parameters
    //TODO pass in object that can update current phone GPS location, this could actually be checked
    asyncClient(TextView gpsTextView, GpsStatus gpsStatus, String IP, int portN, String destString, TextView debugTextView ){

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //todo parse destString and use to initialize destLat and destLon <-- could also be done in constructor or main activity
    }

    @Override
    protected String doInBackground(Void...arg0){
        boolean stopSocket = false;
        drone_arrived = false;
        emergencyLand = false;
        phoneStopTriggered = false;

        openSocketClient(ipOfServer,portNum);

        //TODO set up timer loop, make sure to receive an acknowledge before sending next value
        //TODO - what happens if no ack received? does it just wait. Do we need to send ack for sending????
        //TODO - connect stop button to the boolean phoneStopTriggered
        //TODO-getting GPS

/*        while (!stopSocket) {
            SystemClock.sleep(1000); //wait for 1 sec


            //TODO get phones current GPS coordinates here and set values for class variables. output to debug txt
            publishProgress("String for GPS", "");








            //server sends ack after it reads something. wait for next ack before a new send
            //send ack to pi the same way


            //TODO check for ack before read. ASK AILIN HOW SHE HAS ACK SET UP + WHAT IT LOOKS LIKEs
            //if ack then ....
            //else wait for ack -------put this in read function? have a timer if ack not received after 5 sec then error
            //do all acks in read?????

            //ack
            receivedMessage = readFromSocket();
            ack = Boolean.valueOf(receivedMessage);
            publishProgress("", receivedMessage);
            //droneLat
            String receivedMessage = readFromSocket();
            droneLat = Double.valueOf(receivedMessage);
            publishProgress("", receivedMessage);
            //droneLong
            receivedMessage = readFromSocket();
            droneLong = Double.valueOf(receivedMessage);
            publishProgress("", receivedMessage);
            //droneAlt
            receivedMessage = readFromSocket();
            droneAlt = Integer.valueOf(receivedMessage);
            publishProgress("", receivedMessage);
            //droneVel
            receivedMessage = readFromSocket();
            droneVelocity = Integer.valueOf(receivedMessage);
            publishProgress("", receivedMessage);
            //droneHeading
            receivedMessage = readFromSocket();
            droneHeading = Integer.valueOf(receivedMessage);
            publishProgress("", receivedMessage);


            //TODO - possibly stuff w/ ack here to??????. send an ack?????
            sendSocketData(String.valueOf(destLat));
            sendSocketData(String.valueOf(destLong));
            sendSocketData(String.valueOf(phoneLat));
            sendSocketData(String.valueOf(phoneLong));
            sendSocketData(String.valueOf(phoneStartTriggered));
            sendSocketData(String.valueOf(phoneStopTriggered));
            sendSocketData(String.valueOf(emergencyLand));




            //publishProgress("String for GPS", "String for received socket Messages");
            //if its a debug message then print to the debug txt in the UI

            //TODO - check and see of the stop button has been pressed

            //if button stop, emerg, stop or drone arrived then stop comm
            if (phoneStopTriggered == true){
                stopSocket = true;
                publishProgress("", "Socket communcation stoped because: stop button pressed");
            }else if (emergencyLand == true){
                stopSocket = true;
                publishProgress("", "Socket communcation stoped because: emergancy land activated");
            } else if (drone_arrived == true){
                stopSocket = true;
                publishProgress("", "Socket communcation stoped because: drone arrived");
            }
        }
        */

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


     return "socket comm stopped";
    }

    @Override
    protected void onProgressUpdate(String...progress){
        //this method may take several seconds to complete <--Android Studios
        String gpsString = progress[0];
        String receivedSocketMessage = progress[1];
    }

    @Override
    protected void onPostExecute(String printMessage){
        //not used for now, thread will be cancelled
    }

    @Override
    protected void onCancelled(){
        //todo update debugTV

    }


    //socket functions-------------------------------------------------------------------------------
    private void openSocketClient(String IP, Integer portNum) {
        System.out.println("openSockClient("+IP+", "+portNum+")");
        try{
            socket = new Socket(IP, portNum);
            System.out.println("SocketOpened, reader ready");
        }catch(UnknownHostException e){
            e.printStackTrace();
            System.out.println("UnknownHostException: " + e.toString());
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("OpenSocketClient() -- IOException: " + e.toString());
        }

    }

    private void sendSocketData(String clientMessage){
        try {
            System.out.println("Sending Client Message");
            OutputStream output = socket.getOutputStream();
            byte[] clientMessBytes = clientMessage.getBytes();
            output.write(clientMessBytes);
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("sendSocketData -- IOException: " + e.toString());
        }
    }


    private String readFromSocket()  {
        System.out.println("readFromSocket()");
        String Mess = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Mess = reader.readLine();
            if (Mess != null) {
                System.out.println(Mess + "<------ read from socket");
            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("readFromSocket() -- IOException: " + e.toString());
        }

        return Mess;
    }



}