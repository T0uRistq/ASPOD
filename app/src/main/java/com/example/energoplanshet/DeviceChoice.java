package com.example.energoplanshet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DeviceChoice extends AppCompatActivity {

    Spinner spinnerDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        spinnerDevice = findViewById(R.id.spinnerDevice);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.devices));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDevice.setAdapter(arrayAdapter);
    }

    public void next(View view) {
        MainActivity.addJSON(this, "device", Integer.toString(spinnerDevice.getSelectedItemPosition()));
        Intent i = new Intent(this, MethodChoice.class);
        startActivity(i);
    }

}
