package com.example.tourguidedrone;

import android.location.GpsStatus;
import android.os.AsyncTask;
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
    private double destLat = 0;
    private double destLong = 0;


    //thread functions-----------------------------------------------------------------------------------
    //gui objects that async reads or writes from must be in constructor parameters
    //TODO pass in object that can update current phone GPS location
    asyncClient(TextView gpsTextView, GpsStatus gpsStatus, String IP, int portN, double destLat, double destLong, TextView debugTextView ){

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void...arg0){
        openSocketClient(ipOfServer,portNum);

        String printMessage = readFromSocket();

        sendSocketData(" "); //TODO set up timer loop

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


     return printMessage;
    }

    @Override
    protected void onProgressUpdate(String...progress){
        //this method may take several seconds to complete

    }

    @Override
    protected void onPostExecute(String printMessage){
    }

    @Override
    protected void onCancelled(){

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