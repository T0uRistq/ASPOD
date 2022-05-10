package com.example.energoplanshet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class OrgInfo extends AppCompatActivity {

    Spinner spinnerExecutor, spinnerClient, spinnerFIO;
    TableReaderHelper dbHelper;
    Cursor cursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orginfo);
        spinnerExecutor = findViewById(R.id.spinnerExecutor);
        spinnerClient = findViewById(R.id.spinnerClient);
        spinnerFIO = findViewById(R.id.spinnerFIO);
        inflateSpinner(spinnerExecutor, "EXECUTORS", R.array.executors);
        inflateSpinner(spinnerClient, "CLIENTS", R.array.clients);
        inflateSpinner(spinnerFIO, "USERS", -1);
        MainActivity.setFileContent(this, "JSON.txt", "");
        // recover state
        if (MainActivity.recover_state) {
            try {
                spinnerExecutor.setSelection(Integer.parseInt(MainActivity.jsonObject.get("executor").toString()));
                spinnerClient.setSelection(Integer.parseInt(MainActivity.jsonObject.get("client").toString()));
                spinnerFIO.setSelection(Integer.parseInt(MainActivity.jsonObject.get("FIO").toString()));
                spinnerExecutor.setBackgroundColor(0xFFFFFF00);
                spinnerClient.setBackgroundColor(0xFFFFFF00);
                spinnerFIO.setBackgroundColor(0xFFFFFF00);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void inflateSpinner(Spinner spinner, String tableName, int res) {
        ArrayAdapter<String> arrayAdapter;
        if (res != -1) arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(res));
        else {
            arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
            dbHelper = new TableReaderHelper(this);
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            cursor = database.rawQuery("SELECT * FROM " + tableName, null);
            while (cursor.moveToNext()) arrayAdapter.add(cursor.getString(1));
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    public void next(View view) {
        MainActivity.addJSON(getApplicationContext(), "executor", Integer.toString(spinnerExecutor.getSelectedItemPosition()));
        MainActivity.addJSON(getApplicationContext(), "client", Integer.toString(spinnerClient.getSelectedItemPosition()));
        MainActivity.addJSON(getApplicationContext(), "FIO", Integer.toString(spinnerFIO.getSelectedItemPosition()));
        Intent i = new Intent(this, DeviceChoice.class);
        finish();
        startActivity(i);
    }
}
