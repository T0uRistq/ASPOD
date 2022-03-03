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
    CheckBox cb1, cb2, cb3, cb4, cb5, cb6;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method);
        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);
        cb4 = findViewById(R.id.cb4);
        cb5 = findViewById(R.id.cb5);
        cb6 = findViewById(R.id.cb6);
        date = findViewById(R.id.date_picker);
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String pm = "0" + (month + 1);
        String pd = "0" + day;
        int style = AlertDialog.THEME_HOLO_LIGHT;
        date.setText(pd.substring(pd.length() - 2) + "." + pm.substring(pm.length() - 2) + "." + year);
        datePickerDialog = new DatePickerDialog(this, style, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String pm = "0" + (month + 1);
                String pd = "0" + day;
                date.setText(pd.substring(pd.length() - 2) + "." + pm.substring(pm.length() - 2) + "." + year);
            }
        }, year, month, day);
    }

    public void setDate(View view) {
        datePickerDialog.show();
    }

    public void toForm(View view) {
        String val = "[ " + (cb1.isChecked() ? "0, " : "") + (cb2.isChecked() ? "1, " : "") + (cb3.isChecked() ? "2, " : "")
                    + (cb4.isChecked() ? "3, " : "") + (cb5.isChecked() ? "4, " : "") + (cb6.isChecked() ? "5, " : "");
        MainActivity.meth1 = cb1.isChecked();
        MainActivity.meth2 = cb2.isChecked();
        MainActivity.meth3 = cb3.isChecked();
        MainActivity.meth4 = cb4.isChecked();
        MainActivity.meth5 = cb5.isChecked();
        MainActivity.meth6 = cb6.isChecked();
        val = val.substring(0, val.length() - 2) + "]";
        MainActivity.addJSON(this, "methods", val);
        MainActivity.addJSON(this, "date", "\"" + date.getText() + "\"");
        finishAffinity();
    }

}
