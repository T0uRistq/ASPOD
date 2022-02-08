package com.example.energoplanshet;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StepEto extends AppCompatActivity {

    EditText elementNum, element, diameter, elementLength, defectsRevealed, defectsDescription, conclusion;
    ArrayList<String> arrayList;
    TextView cur;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eto);
        elementNum = findViewById(R.id.elementNum);
        element = findViewById(R.id.element);
        diameter = findViewById(R.id.diameter);
        elementLength = findViewById(R.id.elementLength);
        defectsRevealed = findViewById(R.id.defectsRevealed);
        defectsDescription = findViewById(R.id.defectsDescription);
        conclusion = findViewById(R.id.conclusion);
        cur = findViewById(R.id.currentState);
        arrayList = new ArrayList<>();
    }

    public void next(View view) {
        String json = "";
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference().child("protocol").child(MainActivity.user_id).child("eto");
        for (int i = 0; i < arrayList.size(); i++) {
            json += "\"c" + (i % 7) + "\" : \"" + arrayList.get(i) + "\", ";
            if (i % 7 == 6) {
                db.child("" + i / 7).setValue(json.substring(0, json.length() - 2));
                json = "";
            }
        }
        Intent i = new Intent(this, StepSseto.class);
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
        to_print += cutEditText(elementNum);
        to_print += cutEditText(element);
        to_print += cutEditText(diameter);
        to_print += cutEditText(elementLength);
        to_print += cutEditText(defectsRevealed);
        to_print += cutEditText(defectsDescription);
        to_print += cutEditText(conclusion);
        cur.setText(to_print + "\n\n");
    }

}