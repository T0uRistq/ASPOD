package com.example.energoplanshet;

import static com.example.energoplanshet.MainActivity.db;
import static com.example.energoplanshet.MainActivity.getFileContent;
import static com.example.energoplanshet.MainActivity.setFileContent;
import static com.example.energoplanshet.MainActivity.user_id;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        String json_content = getFileContent(this, "JSON.txt");
        if (!json_content.isEmpty()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Имеются неотправленные исходные данные");
            builder.setMessage("При переходе на следующее окно они будут удалены. Продолжить?");
            builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String res = getFileContent(getApplicationContext(), "JSON.txt");
                    db.child("protocols").child(user_id).setValue(res);
                    setFileContent(getApplicationContext(), "JSON.txt", "");
                }
            });
            builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.create().show();
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
        startActivity(i);
    }
}
