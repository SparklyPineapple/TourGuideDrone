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
    private TextView gpsTextView;
    private String receivedMessage = "string receivedMessage";
    //socket communication: phone to pi
    private int destWaypointNum; //waypoint index num of
    private double phoneLat = 0;
    private double phoneLong = 0;
    private boolean phoneStartTriggered = false;
    private boolean phoneStopTriggered = false;
    private boolean emergencyLand = false;
    //socket communication: pi to phone
    private boolean ack = false;
    private double droneLat = 0;
    private double droneLong = 0;
    private int droneAlt =0;
    private int droneVelocity = 0;
    private int droneHeading = 0;
    private boolean drone_arrived = false;


    //thread functions-----------------------------------------------------------------------------------
    asyncClient(TextView gpsTextView, String ipOfServer, int portNum, int destNum, TextView debugTextView ){
        this.gpsTextView = gpsTextView;
        this.ipOfServer = ipOfServer;
        this.portNum = portNum;
        this.debugTextView = debugTextView;
        this.destWaypointNum = destNum;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //This should be the only time we call .setText on debugTextView in AsyncClient
        String message = "Starting Thread";
        debugTextView.setText(message);
    }

    @Override
    protected String doInBackground(Void...arg0){
        boolean stopSocket = false;
        drone_arrived = false;
        emergencyLand = false;
        phoneStopTriggered = false;

        openSocketClient(ipOfServer,portNum);

//        while (!stopSocket) {
//            SystemClock.sleep(1000); //wait for 1 sec

            //Receive data from pi
//            receivedMessage = readFromSocket();
//            ack = Boolean.valueOf(receivedMessage);
//            publishProgress("", receivedMessage);
//            //droneLat
//            String receivedMessage = readFromSocket();
//            droneLat = Double.valueOf(receivedMessage);
//            publishProgress("", receivedMessage);
//            //droneLong
//            receivedMessage = readFromSocket();
//            droneLong = Double.valueOf(receivedMessage);
//            publishProgress("", receivedMessage);
//            //droneAlt
//            receivedMessage = readFromSocket();
//            droneAlt = Integer.valueOf(receivedMessage);
//            publishProgress("", receivedMessage);
//            //droneVel
//            receivedMessage = readFromSocket();
//            droneVelocity = Integer.valueOf(receivedMessage);
//            publishProgress("", receivedMessage);
//            //droneHeading
//            receivedMessage = readFromSocket();
//            droneHeading = Integer.valueOf(receivedMessage);
//            publishProgress("", receivedMessage);


            //Send phone data to pi
//            sendSocketData(String.valueOf(destLat));
//            sendSocketData(String.valueOf(destLong));
//            sendSocketData(String.valueOf(phoneLat));
//            sendSocketData(String.valueOf(phoneLong));
//            sendSocketData(String.valueOf(phoneStartTriggered));
//            sendSocketData(String.valueOf(phoneStopTriggered));
//            sendSocketData(String.valueOf(emergencyLand));





//            //if drone arrived then stop communication and exit
//            if (drone_arrived == true){
//                stopSocket = true;
//                publishProgress("", "Socket communcation stoped because: drone arrived");
//            }
//        }


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
        //progress[0] == string for gps
        //progress[1] == String that is received Socket message
        gpsTextView.setText(progress[0]);
        debugTextView.append(progress[1]);

    }

    @Override
    protected void onPostExecute(String printMessage){
        //not used for now, thread will be cancelled
        debugTextView.append("onPostExecute():"+printMessage);
    }

    @Override
    protected void onCancelled(){
        //todo update debugTV
        debugTextView.append("communication cancelled");

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