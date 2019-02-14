package com.example.tourguidedrone;

import android.location.GpsStatus;
import android.os.AsyncTask;
import android.os.CountDownTimer;
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
    //private String receivedMessage = "string receivedMessage";
    //socket communication: phone to pi
    //private double destLat = 0; //a table of mapping coord to dest is in phone if we do it this way. do we care? or do we want to do it all in the drone?
    //private double destLong = 0;
    private int destWayPoint = -1;
    private double phoneLat = 0;
    private double phoneLong = 0;
    private boolean phoneStartTriggered = false; //referred to as start in the spreadsheet
    private boolean phoneStopTriggered = false; //referred to as stop in the spreadsheet. renamed for clarification
    private boolean emergencyLand = false;
    private boolean ackFromPhone = false;
    //socket communication: pi to phone
    private double droneLat = 0;
    private double droneLong = 0;
    private int droneAlt =0;
    private int droneVelocity = 0;
    private int droneHeading = 0;

    //should get this from debugging from the drone????
    private boolean drone_arrived = false; //kirby added since you want to stop when drone stops and drone GPS + phone GPS are different

    //timer for timeout error if ack is not received
    private CountDownTimer ackTimer = null;
    private boolean ackTimerDone = false;




    //thread functions-----------------------------------------------------------------------------------
    //gui objects that async reads or writes from must be in constructor parameters
    //TODO pass in object that can update current phone GPS location, this could actually be checked
    asyncClient(TextView gpsTextView, /*GpsStatus gpsStatus,*/ String ipOfServer, int portNum, int destNum, TextView debugTextView ){
        this.gpsTextView = gpsTextView;
        this.ipOfServer = ipOfServer;
        this.portNum = portNum;
        this.debugTextView = debugTextView;
        this.destWayPoint = destNum;
        //todo parse destString and use to initialize destLat and destLon <-- could also be done in constructor or main activity

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //This should be the only time we call .setText on debugTextView in AsyncClient
        String message = "\n" + "Starting Thread";
        debugTextView.append(message);
    }

    @Override
    protected String doInBackground(Void...arg0){
        boolean stopSocket = false;
        drone_arrived = false;
        emergencyLand = false;
        phoneStopTriggered = false;

        String message = "insert message here";
        openSocketClient(ipOfServer,portNum);

        //TODO set up timer loop, make sure to receive an acknowledge before sending next value
        //TODO - what happens if no ack received? does it just wait. Do we need to send ack for sending????
        //TODO - connect stop button to the boolean phoneStopTriggered
        //TODO-getting GPS

//       while (!stopSocket) {
            SystemClock.sleep(1000); //wait for 1 sec

            //sendSocketData("this is the app speaking");
            sendSocketDataAndReceiveAck(String.valueOf(true));//--WORKS WITH EXCEPTION OF TIMER STUFF
            //message = readFromSocketAndSendAck(); //


            //TODO get phones current GPS coordinates here
            //publishProgress("String for GPS", "");


            //RECEIVE ALL DATA FROM DRONE
            //droneLat
            //String receivedMessage = readFromSocketAndSendAck();
            //droneLat = Double.valueOf(receivedMessage);
            //publishProgress("", receivedMessage); //for debugging
//            //droneLong
//            receivedMessage = readFromSocketAndSendAck();
//            droneLong = Double.valueOf(receivedMessage);
//            publishProgress("", receivedMessage);//for debugging
//            //droneAlt
//            receivedMessage = readFromSocketAndSendAck();
//            droneAlt = Integer.valueOf(receivedMessage);
//            publishProgress("", receivedMessage);//for debugging
//            //droneVel
//            receivedMessage = readFromSocketAndSendAck();
//            droneVelocity = Integer.valueOf(receivedMessage);
//            publishProgress("", receivedMessage);//for debugging
//            //droneHeading
//            receivedMessage = readFromSocketAndSendAck();
//            droneHeading = Integer.valueOf(receivedMessage);
//            publishProgress("", receivedMessage);//for debugging



            //fake data for testing
            destWayPoint = 1; //
            phoneLat = 2;
            phoneLong = 3;
            phoneStartTriggered = true;
            phoneStopTriggered = false;
            emergencyLand = false;
            //SEND SOCKET ALL COMM DATA
//            sendSocketDataAndReceiveAck(String.valueOf(destWayPoint));
//            sendSocketDataAndReceiveAck(String.valueOf(phoneLat));
//            sendSocketDataAndReceiveAck(String.valueOf(phoneLong));
//            sendSocketDataAndReceiveAck(String.valueOf(phoneStartTriggered));
//            sendSocketDataAndReceiveAck(String.valueOf(phoneStopTriggered));
//            sendSocketDataAndReceiveAck(String.valueOf(emergencyLand));



            //Check and see if drone arrived at dest. if so stop comm
            if (drone_arrived == true){
                //stopSocket =  //for while loop to say when stop
                publishProgress("", "Socket communcation stopped because: drone arrived");
            }
//        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


     return "\n"+"socket comm stopped";
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

    private void sendSocketDataAndReceiveAck(String clientMessage){
        //send pi message
        sendSocketData(clientMessage);
        //wait for pi to send back ack
        String message = "";
        ackTimerDone = false;
        //startTimer();
        while(true){
            message = readFromSocket();
            System.out.println("in while");

            if (message.equals(String.valueOf(true))){
                System.out.println("message = true");
                //sendSocketData("got ack");
                System.out.println("message sent and ack received");
                //cancelTimer();
                return;
            } else if (ackTimerDone == true){
                System.out.println("message != true");
                //sendSocketData("did not get ack");
                System.out.println("ERROR ACK TIMEOUT: NO ACK RECEIVED AFTER DATA SENT");
                publishProgress("", "ERROR ACK TIMEOUT: NO ACK RECEIVED AFTER DATA SENT");
                //TODO - get a string or boolean or something returned to the UI so we know what didnt sent an ack. Also possibly stop functionality of app or trigger emergany land or something
                return;
            }
        }
    }




    private String readFromSocket()  {
        System.out.println("readFromSocket()");
        String yolo = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            yolo = reader.readLine(); //DIES HERE :/
            System.out.println(yolo);
            if (yolo != null) {
                System.out.println(yolo + "<------ read from socket");
            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("readFromSocket() -- IOException: " + e.toString());
        }

        return yolo;
    }

    private String readFromSocketAndSendAck(){
        //get message from pi
        System.out.println("readFromSocketAndSendAck()");
        String message = readFromSocket();
        //send ack to pi that phone has received message
        sendSocketData(String.valueOf(true));

        return message;
    }

    //start 5 second timer
    private void startTimer() {
        ackTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                ackTimerDone = true;
            }
        };
        ackTimer.start();
    }


    //cancel timer
    private void cancelTimer() {
        if(ackTimer!=null)
            ackTimer.cancel();
    }


}