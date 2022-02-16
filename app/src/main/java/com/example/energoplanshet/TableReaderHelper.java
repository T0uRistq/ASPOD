package com.example.energoplanshet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TableReaderHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ENERGO.db";
    public TableReaderHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MainActivity.SQL_USERS);
        db.execSQL(MainActivity.SQL_TOOLS);
//        db.execSQL(MainActivity.SQL_LEVEL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL();
        onCreate(db);
    }
}
