package com.example.energoplanshet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    static DatabaseReference db;
    EditText et_login, et_pass;
    static String SQL_USERS;
    Context context;
    TableReaderHelper dbHelper;
    static String user_id;
    static Boolean recover_state = false;
    static JSONObject jsonObject = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_login = findViewById(R.id.login);
        et_pass = findViewById(R.id.pass);
        context = getApplicationContext();
        db = FirebaseDatabase.getInstance().getReference();
        SQL_USERS =  "CREATE TABLE USERS  (ID INTEGER PRIMARY KEY," + "FULLNAME TEXT)";
        dbHelper = new TableReaderHelper(context);
    }

    public void enter(View view) {
        String login = et_login.getText().toString();
        String pass = et_pass.getText().toString();
        user_id = "0";
        Intent i = new Intent(this, MainMenu.class);
        startActivity(i);
//        SQLiteDatabase database = dbHelper.getReadableDatabase();
//        Cursor cursor = database.rawQuery("SELECT * FROM MAINTABLE WHERE FULLNAME LIKE '" + login + "'",null);
//        if (cursor.moveToFirst()) {
//            user_id = cursor.getString(0);
//            Intent i = new Intent(this, BuildDoc.class);
//            startActivity(i);
//        } else {
//            Toast.makeText(this,"Неверный логин или пароль",Toast.LENGTH_LONG).show();
//        }
//        cursor.close();
    }

    public static String getFileContent(Context context, String filename) {
        FileInputStream fis = null;
        String res = "";
        try {
            fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }
            res = sb.toString();
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

    public static void setFileContent(Context context, String filename, String value) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(filename, MODE_PRIVATE);
            fos.write(value.getBytes());
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

    public long getAppVersion() {
        String str = getFileContent(this,"version.txt");
        return str.equals("") ? -1 : Integer.parseInt(str);
    }

    public void setAppVersion(long remote) {
        setFileContent(this, "version.txt", Long.toString(remote));
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
                        database.insert("USERS", null, user_values);
                    }
                    setAppVersion(remoteVersion);
                    Toast.makeText(context, "Обновлено", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public static void addJSON(Context context, String key, String val) {
        String res = getFileContent(context, "JSON.txt");
        if (!res.isEmpty()) res = res + ", ";
        res = res + "\"" + key + "\": " + val;
        setFileContent(context, "JSON.txt", res);
    }
}