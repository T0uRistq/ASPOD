package com.example.energoplanshet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StepTools extends AppCompatActivity {

    Spinner spinner;
    TableReaderHelper dbHelper;
    DatabaseReference db;
    TextView zavod, svid, srok, cur;
    ArrayList<String> arrayList;
    Cursor cursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        spinner = findViewById(R.id.you_spin_me);
        zavod = findViewById(R.id.zavod);
        svid = findViewById(R.id.svid);
        srok = findViewById(R.id.srok);
        cur = findViewById(R.id.currentState);
        arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dbHelper = new TableReaderHelper(this);
        assert (MainActivity.user_id != null);
        db = FirebaseDatabase.getInstance().getReference().child("protocol").child(MainActivity.user_id).child("tools");
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        cursor = database.rawQuery("SELECT * FROM TOOLS", null);
        Log.d("olboeb", "cnt is " + cursor.getCount());
        while (cursor.moveToNext()) {
            Log.d("olboeb", cursor.getString(0));
            arrayAdapter.add(cursor.getString(0));
        }
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cursor.moveToFirst();
                cursor.move(i);
                zavod.setText(cursor.getString(1)); svid.setText(cursor.getString(7)); srok.setText(cursor.getString(8));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void next(View view) {
        String json = "";
        for (int i = 0; i < arrayList.size(); i++) {
            json += "\"c" + (i % 4) + "\" : \"" + arrayList.get(i) + "\", ";
            if (i % 4 == 3) {
                db.child("" + i / 4).setValue(json.substring(0, json.length() - 2));
                json = "";
            }
        }
        Intent i = new Intent(this, StepOetolpfse.class);
        startActivity(i);
    }

    public void back(View view) {
        finish();
    }

    public void push(View view) {
        arrayList.add(cursor.getString(0));
        arrayList.add(cursor.getString(1));
        arrayList.add(cursor.getString(7));
        arrayList.add(cursor.getString(8));
        cur.setText(cur.getText() + cursor.getString(0) + "\n\n");
    }

}
