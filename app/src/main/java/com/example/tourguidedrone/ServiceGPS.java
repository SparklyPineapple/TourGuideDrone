package com.example.tourguidedrone;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

//to start service use ()in mainactivity class)
//startService(new Intent(this, ServiceGPS.class));


public class ServiceGPS extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }





}
