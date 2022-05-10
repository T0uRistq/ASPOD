package com.example.energoplanshet;

import static com.example.energoplanshet.MainActivity.db;
import static com.example.energoplanshet.MainActivity.getFileContent;
import static com.example.energoplanshet.MainActivity.recover_state;
import static com.example.energoplanshet.MainActivity.setFileContent;
import static com.example.energoplanshet.MainActivity.user_id;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

public class MainMenu extends AppCompatActivity {

    Button send_report_button, continue_report_button, start_report_button;
    String json_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        send_report_button = findViewById(R.id.send_report);
        continue_report_button = findViewById(R.id.continue_report);
        start_report_button = findViewById(R.id.start_report);

        json_content = getFileContent(this, "JSON.txt");
        try {
            MainActivity.jsonObject = new JSONObject(('{' + json_content + '}'));
        } catch (Exception ex) {
            Toast.makeText(this, "Файл JSON.txt поврежден", Toast.LENGTH_LONG).show();
            finishAffinity();
        }
        if (MainActivity.jsonObject != null) {
            if (MainActivity.jsonObject.has("date")) send_report_button.setEnabled(true);
            else if (MainActivity.jsonObject.length() > 0) continue_report_button.setEnabled(true);
        }
    }

    public void continueReport(View view) {
        MainActivity.recover_state = true;
        startReport(start_report_button);
    }

    public void sendReport(View view) {
        // TODO: keep the stack of protocols based on time (instead of user id)
        //  in case when one user can push several protocols

        db.child("protocols").child(user_id).setValue('{' + json_content + '}');
        setFileContent(getApplicationContext(), "JSON.txt", "");
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void startReport(View view) {
        if (!json_content.isEmpty() && !recover_state) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Имеются неотправленные исходные данные");
            builder.setMessage("При переходе на следующее окно они будут удалены. Продолжить?");
            builder.setPositiveButton("Отмена", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.setNegativeButton("Продолжить", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    Intent intent = new Intent(getApplicationContext(), OrgInfo.class);
                    finish();
                    startActivity(intent);
                }
            }).create().show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OrgInfo.class);
            finish();
            startActivity(intent);
        }
    }
}