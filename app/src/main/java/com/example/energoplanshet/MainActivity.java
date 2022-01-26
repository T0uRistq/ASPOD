package com.example.energoplanshet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    int appVersion;
    DatabaseReference db;
    EditText et_login, et_pass;
    String users_key = "users";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_login = findViewById(R.id.login);
        et_pass = findViewById(R.id.pass);
        db = FirebaseDatabase.getInstance().getReference();
    }

    public void enter(View view) {
        String login = et_login.getText().toString();
        String pass = et_pass.getText().toString();
    }

    public void update(View view) {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int remoteVersion = (int) dataSnapshot.child("version").getValue();
                if (remoteVersion == appVersion) {
                    Toast.makeText(getApplicationContext(), "У вас уже последняя версия БД", Toast.LENGTH_SHORT).show();
                } else {
//                    TODO: update local json files
                    appVersion = remoteVersion;
                    Toast.makeText(getApplicationContext(), "Обновлено", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}