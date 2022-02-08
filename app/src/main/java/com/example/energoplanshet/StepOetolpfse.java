package com.example.energoplanshet;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StepOetolpfse extends AppCompatActivity {

    EditText inspectionType, controlResult;
    ArrayList<String> arrayList;
    TextView cur;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oetolpfse);
        inspectionType = findViewById(R.id.inspectionType);
        controlResult = findViewById(R.id.controlResult);
        cur = findViewById(R.id.currentState);
        arrayList = new ArrayList<>();
    }

    public void next(View view) {
        String json = "";
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference().child("protocol").child(MainActivity.user_id).child("oetolpfse");
        for (int i = 0; i < arrayList.size(); i++) {
            json += "\"c" + (i % 2) + "\" : \"" + arrayList.get(i) + "\", ";
            if (i % 2 == 1) {
                db.child("" + i / 2).setValue(json.substring(0, json.length() - 2));
                json = "";
            }
        }
        Intent i = new Intent(this, StepEto.class);
        startActivity(i);
    }

    public void back(View view) {
        finish();
    }

    // cuts EditText content and pastes it to arrayList
    public String cutEditText(EditText btn) {
        String tmp = btn.getText().toString();
        btn.setText("");
        arrayList.add(tmp);
        return tmp;
    }

    public void push(View view) {
        String to_print = cur.getText().toString();
        to_print += cutEditText(inspectionType);
        to_print += cutEditText(controlResult);
        cur.setText(to_print + "\n\n");
    }

}
