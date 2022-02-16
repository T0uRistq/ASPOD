package com.example.energoplanshet;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MethodChoice extends AppCompatActivity {

    TextView date;
    DatePickerDialog datePickerDialog;
    CheckBox cb1, cb2, cb3, cb4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method);
        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);
        cb4 = findViewById(R.id.cb4);
        cb2.setEnabled(false);
        cb3.setEnabled(false);
        cb4.setEnabled(false);
        date = findViewById(R.id.date_picker);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        String pm = "0" + (month + 1);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        date.setText(day + "." + pm.substring(pm.length() - 2) + "." + year);
        datePickerDialog = new DatePickerDialog(this, style, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String pm = "0" + month;
                date.setText(day + "." + pm.substring(pm.length() - 2) + "." + year);
            }
        }, year, month, day);
    }

    public void setDate(View view) {
        datePickerDialog.show();
    }

    public void toForm(View view) {
        String val = "[ " + (cb1.isChecked() ? "0, " : "") + (cb2.isChecked() ? "1, " : "") + (cb3.isChecked() ? "2, " : "")
                    + (cb4.isChecked() ? "3, " : "");
        val = val.substring(0, val.length() - 2) + "]";
        MainActivity.addJSON(this, "methods", val);
        MainActivity.addJSON(this, "date", "\"" + date.getText() + "\"");
        MainActivity.sendJSON(this);
        finishAffinity();
    }

}
