package com.example.tourguidedrone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up the choices for the dropdown destination list

        Spinner selectDestList = findViewById(R.id.selectDestList);
        String[] destListString = new String[] {"Stevens Student Center", "Dixon Ministry Center",
                "Center for Biblical and Theological Studies", "Engineering and Science Center",
                "Health and Science Center"};
        ArrayAdapter<String> selectDestListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, destListString);
        selectDestList.setAdapter(selectDestListAdapter);

    }
}
