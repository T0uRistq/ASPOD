package com.example.energoplanshet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    DatabaseReference db;
    EditText et_login, et_pass;
    static String SQL_USERS, SQL_TOOLS, SQL_T0, SQL_T1, SQL_T2, SQL_T3, SQL_T4;
    Context context;
    TableReaderHelper dbHelper;
    static String user_id;

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
//        TODO: give appropriate names to columns (maybe?)
        String common_part = "ID INTEGER C0 TEXT, C1 TEXT, C2 TEXT," +
                " C3 TEXT, C4 TEXT, C5 TEXT, C6 TEXT, C7 TEXT," +
                " C8 TEXT, C9 TEXT, C10 TEXT, C11 TEXT, C12 TEXT," +
                " C13 TEXT, C14 TEXT, C15 TEXT, C16 TEXT, C17 TEXT," +
                " C18 TEXT, C19 TEXT, C20 TEXT," +
                "FOREIGN KEY (ID) REFERENCES MAINTABLE (ID))";
        SQL_T0 = "CREATE TABLE T0 (" + common_part;
        SQL_T1 = "CREATE TABLE T1 (" + common_part;
        SQL_T2 = "CREATE TABLE T2 (" + common_part;
        SQL_T3 = "CREATE TABLE T3 (" + common_part;
        SQL_T4 = "CREATE TABLE T4 (" + common_part;
        SQL_TOOLS =  "CREATE TABLE TOOLS (" + common_part.substring(11, 159) + ")";
        dbHelper = new TableReaderHelper(context);

    }

    public void enter(View view) {
        String login = et_login.getText().toString();
        String pass = et_pass.getText().toString();
        user_id = "0";
        Intent i = new Intent(this, StepTools.class);
        startActivity(i);
//        SQLiteDatabase database = dbHelper.getReadableDatabase();
//        Cursor cursor = database.rawQuery("SELECT * FROM MAINTABLE WHERE FULLNAME LIKE '" + login + "'",null);
//        if (cursor.moveToFirst()) {
//            user_id = cursor.getString(0);
//            Log.d("olboeb", user_id);
//            Intent i = new Intent(this,BuildDoc.class);
//            startActivity(i);
//        } else {
//            Toast.makeText(this,"Неверный логин или пароль",Toast.LENGTH_LONG).show();
//        }
//        cursor.close();
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
                    ContentValues user_values = new ContentValues();
                    for (DataSnapshot data : dataSnapshot.child("users").getChildren()) {
                        user_values.put("ID", id++);
                        user_values.put("FULLNAME", data.getKey());
                        database.insert("MAINTABLE", null, user_values);
                    }
                    ContentValues tool_values = new ContentValues();
                    for (DataSnapshot data : dataSnapshot.child("tools").getChildren()) {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(data.getValue().toString());
                            Log.d("olboeb", json.getString("0"));
                            for (int i = 0; i < 16; i++) {
                                String col = Integer.toString(i);
                                tool_values.put("C" + col, json.getString(col));
                            }
                            database.insert("TOOLS", null, tool_values);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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