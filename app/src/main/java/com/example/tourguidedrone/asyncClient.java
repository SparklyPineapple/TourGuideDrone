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
    private String receivedMessage = "string_receivedMessage";
    //socket communication: phone to pi
    private int destWaypointNum; //waypoint index num of
    private int startWaypointNum;
    private double phoneLat = 0;
    private double phoneLong = 0;
    private boolean phoneStartTriggered = true;
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
    //timer variables
    boolean ackTimerDone = false;


    //thread functions-----------------------------------------------------------------------------------
    asyncClient(TextView gpsTextView, String ipOfServer, int portNum, int startNum, int destNum, TextView debugTextView ){
        this.gpsTextView = gpsTextView;
        this.ipOfServer = ipOfServer;
        this.portNum = portNum;
        this.debugTextView = debugTextView;
        this.destWaypointNum = destNum;
        this.startWaypointNum = startNum;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //This should be the only time we call .setText on debugTextView in AsyncClient
        String message = "Starting Thread";
        debugTextView.setText("Debug Data \n" + message);
    }

    @Override
    protected String doInBackground(Void...arg0){

        //connect faded
        //start, disconnect visable


        boolean loop = false;
        drone_arrived = false;
        emergencyLand = false;
        phoneStopTriggered = false;

        //Connection! -State 1: everyone connects through the socket
        publishProgress("", " ready to open client");
        openSocketClient(ipOfServer,portNum);

        //Update! -State 2: pi + app update each other with all initial values
        //droneLat
        String receivedMessage = readFromSocketAndSendAck();
        droneLat = Double.valueOf(receivedMessage);
        publishProgress("", receivedMessage + "\n"); //for debugging
        //droneLong
        receivedMessage = readFromSocketAndSendAck();
        droneLong = Double.valueOf(receivedMessage);
        publishProgress("", receivedMessage + "\n");//for debugging
        //droneAlt
        receivedMessage = readFromSocketAndSendAck();
        droneAlt = Integer.valueOf(receivedMessage);
        publishProgress("", receivedMessage + "\n");//for debugging
        //droneVel
        receivedMessage = readFromSocketAndSendAck();
        droneVelocity = Integer.valueOf(receivedMessage);
        publishProgress("", receivedMessage + "\n");//for debugging
        //droneHeading
        receivedMessage = readFromSocketAndSendAck();
        droneHeading = Integer.valueOf(receivedMessage);
        publishProgress("", receivedMessage + "\n");//for debugging


        //SEND SOCKET ALL COMM DATA
        sendSocketDataAndReceiveAck(String.valueOf(destWaypointNum));
        sendSocketDataAndReceiveAck(String.valueOf(phoneLat));
        sendSocketDataAndReceiveAck(String.valueOf(phoneLong));
        sendSocketDataAndReceiveAck(String.valueOf(phoneStartTriggered));
        sendSocketDataAndReceiveAck(String.valueOf(phoneStopTriggered));
        sendSocketDataAndReceiveAck(String.valueOf(false)); //emergency stop
        sendSocketDataAndReceiveAck(String.valueOf(false)); //exit buttons

        //Wait for start! -State 3: act according to stop/start button presses
        //start has been pressed so...
        //cancel visable
        //start invisable


        //START BUTTON NEEDS TO BE PRESSED TO START THIS THREAD. FOR NOW ASSUME START IS ALREADY TRUE HERE
        //Needs to work like ^ for final app. can auto start for async
        //Drone waiting for start command! -State3: waiting for start to be pressed

        boolean start = true; //will be value connected to UI start button in service client
        boolean stop = false; //will be value connected to UI (aborts current mission values and waits for new mission. DO NOT stop socket


        //only do one loop since we have no functionality for stop, as stop is defined for the new stop button
        //while (loop) {

            //send start boolean
            sendSocketDataAndReceiveAck(String.valueOf(true));
            //FUTURE: send exit button boolean - NOT IMPLEMENTED YET SO DONT. current stop button is more like the exit button should be
            //send false bc exit will be false rm
            sendSocketDataAndReceiveAck(String.valueOf(false));

            //FUTURE: if exit=true then exit the loop and stop socket, only coonect visable

            if (start) {
                //send destWaypointNumber and phone lat and long
                sendSocketDataAndReceiveAck(String.valueOf(destWaypointNum));
                sendSocketDataAndReceiveAck(String.valueOf(phoneLat));
                sendSocketDataAndReceiveAck(String.valueOf(phoneLong));

                //Take off! -State 4:
                //check if mission aborted (stop button)
                sendSocketDataAndReceiveAck(String.valueOf(stop));
                //if stop == false break from loop and send ^

                //else go through with take off
                receivedMessage = "";
                boolean takingOff = true;
                while (takingOff){
                    //check for string "adv" then end loop, otherwise get drone altitude
                    receivedMessage = readFromSocketAndSendAck();
                    if (receivedMessage.equals("adv")){
                        takingOff = false;
                    } else {
                        droneAlt = Integer.valueOf(receivedMessage);
                    }
                }

                //while in flight + landing
                String message = "";
                boolean inFlight = true;
                receivedMessage = "";
                while (inFlight) {
                    receivedMessage = readFromSocketAndSendAck();
                    //if first message = done then flight is done. skip rest
                    if (receivedMessage.equals("done")) {
                        inFlight = false;
                    } else {
                        //populate and send nessarcy data
                        //droneLat
                        droneLat = Double.valueOf(receivedMessage);
                        publishProgress("", receivedMessage + "\n"); //for debugging
                        //droneLong
                        receivedMessage = readFromSocketAndSendAck();
                        droneLong = Double.valueOf(receivedMessage);
                        publishProgress("", receivedMessage + "\n");//for debugging
                        //droneAlt
                        receivedMessage = readFromSocketAndSendAck();
                        droneAlt = Integer.valueOf(receivedMessage);
                        publishProgress("", receivedMessage + "\n");//for debugging
                        //droneVel
                        receivedMessage = readFromSocketAndSendAck();
                        droneVelocity = Integer.valueOf(receivedMessage);
                        publishProgress("", receivedMessage + "\n");//for debugging
                        //droneHeading
                        receivedMessage = readFromSocketAndSendAck();
                        droneHeading = Integer.valueOf(receivedMessage);
                        publishProgress("", receivedMessage + "\n");//for debugging


                        //SEND SOCKET ALL COMM DATA
//                        sendSocketDataAndReceiveAck(String.valueOf(destWaypointNum));
//                        sendSocketDataAndReceiveAck(String.valueOf(phoneLat));
//                        sendSocketDataAndReceiveAck(String.valueOf(phoneLong));
//                        sendSocketDataAndReceiveAck(String.valueOf(phoneStartTriggered));
//                        sendSocketDataAndReceiveAck(String.valueOf(phoneStopTriggered));
                        //sendSocketDataAndReceiveAck(String.valueOf(emergencyLand));
                        //send future exit button info here
                    }
                }


            }


        //}
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


     return "\n socket comm stopped";
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
        debugTextView.append("\n communication cancelled");

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

    private String readFromSocketAndSendAck(){
        //get message from pi
        System.out.println("readFromSocketAndSendAck()");
        String message = readFromSocket();
        //send ack to pi that phone has received message
        sendSocketData(String.valueOf(true));

        return message;
    }

    private void update_GPS_UI_Info() {

        //RECEIVE ALL DATA FROM DRONE
        //droneLat
        String receivedMessage = readFromSocketAndSendAck();
        droneLat = Double.valueOf(receivedMessage);
        publishProgress("", receivedMessage + "\n"); //for debugging
        //droneLong
        receivedMessage = readFromSocketAndSendAck();
        droneLong = Double.valueOf(receivedMessage);
        publishProgress("", receivedMessage + "\n");//for debugging
        //droneAlt
        receivedMessage = readFromSocketAndSendAck();
        droneAlt = Integer.valueOf(receivedMessage);
        publishProgress("", receivedMessage + "\n");//for debugging
        //droneVel
        receivedMessage = readFromSocketAndSendAck();
        droneVelocity = Integer.valueOf(receivedMessage);
        publishProgress("", receivedMessage + "\n");//for debugging
        //droneHeading
        receivedMessage = readFromSocketAndSendAck();
        droneHeading = Integer.valueOf(receivedMessage);
        publishProgress("", receivedMessage + "\n");//for debugging


        //SEND SOCKET ALL COMM DATA
        sendSocketDataAndReceiveAck(String.valueOf(destWaypointNum));
        sendSocketDataAndReceiveAck(String.valueOf(phoneLat));
        sendSocketDataAndReceiveAck(String.valueOf(phoneLong));
        sendSocketDataAndReceiveAck(String.valueOf(phoneStartTriggered));
        sendSocketDataAndReceiveAck(String.valueOf(phoneStopTriggered));
        //sendSocketDataAndReceiveAck(String.valueOf(emergencyLand));
        //send future exit button info here
    }


}