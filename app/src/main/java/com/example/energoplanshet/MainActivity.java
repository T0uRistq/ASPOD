package com.example.energoplanshet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    DatabaseReference db;
    EditText et_login, et_pass;
    static String SQL_USERS, SQL_TOOLS, SQL_LEVEL;
    Context context;
    TableReaderHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_login = findViewById(R.id.login);
        et_pass = findViewById(R.id.pass);
        context = getApplicationContext();
        db = FirebaseDatabase.getInstance().getReference();
        SQL_USERS =  "CREATE TABLE MAINTABLE  (ID INTEGER PRIMARY KEY," +
                "FULLNAME TEXT)";
//        TODO: add tools database
//        SQL_TOOLS =  "CREATE TABLE TOOLS (TOOL_NAME TEXT)";
        SQL_LEVEL = "CREATE TABLE LEVEL (" +
                "ID INTEGER VIK TEXT UK TEXT, MK TEXT," +
                " PVK TEXT, PVT TEXT, VD TEXT, VK TEXT, RK TEXT," +
                " AU TEXT, KI,HZ TEXT, MKNDS TEXT, VIKNDS TEXT, VIK TEXT," +
                " FIRMNESS TEXT, GEODEZY TEXT, OPTI TEXT, PTM TEXT, OT TEXT," +
                " AB TEXT, PB TEXT, HEIGHTWORK TEXT," +
                "FOREIGN KEY (ID) REFERENCES MAINTABLE (ID))";
        dbHelper = new TableReaderHelper(context);

    }

    public void enter(View view) {
        String login = et_login.getText().toString();
        String pass = et_pass.getText().toString();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT FULLNAME FROM MAINTABLE WHERE FULLNAME LIKE '" + login + "'",null);
        if (cursor.moveToFirst()) {
            Intent i = new Intent(this,BuildDoc.class);
            startActivity(i);
        } else {
            Toast.makeText(this,"Неверный логин или пароль",Toast.LENGTH_LONG).show();
        }
    }

    public long getAppVersion() {
        FileInputStream fis = null;
        long res = -1;
        try {
            fis = openFileInput("version.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }
            res = Integer.parseInt(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    public void setAppVersion(long remote) {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("version.txt", MODE_PRIVATE);
            fos.write(Long.toString(remote).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void update(View view) {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long remoteVersion = (long) dataSnapshot.child("version").getValue();
                if (remoteVersion == getAppVersion()) {
                    Toast.makeText(context, "У вас уже последняя версия БД", Toast.LENGTH_SHORT).show();
                } else {
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    int id = 0;
                    ContentValues values = new ContentValues();
                    for (DataSnapshot data : dataSnapshot.child("users").getChildren()) {
                        values.put("ID", id++);
                        values.put("FULLNAME", data.getKey());
                        database.insert("MAINTABLE", null, values);
                    }
                    setAppVersion(remoteVersion);
                    Toast.makeText(context, "Обновлено", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}