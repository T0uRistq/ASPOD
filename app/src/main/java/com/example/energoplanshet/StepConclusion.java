package com.example.energoplanshet;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class StepConclusion extends AppCompatActivity {

    EditText conclusion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conclusion);
        conclusion = findViewById(R.id.conclusion);
    }

    public void back(View view) {
        finish();
    }

    public void send(View view) {
        DatabaseReference db;
        db = FirebaseDatabase.getInstance().getReference().child("protocol").child(MainActivity.user_id).child("conclusion");
        db.setValue(conclusion.getText().toString());
        finish();
    }
}
