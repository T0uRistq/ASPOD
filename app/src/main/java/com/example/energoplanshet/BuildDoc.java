package com.example.energoplanshet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BuildDoc extends AppCompatActivity {

    Spinner spinner;
    DatabaseReference db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);
        spinner = findViewById(R.id.you_spin_me);
        db = FirebaseDatabase.getInstance().getReference();
        List<String> tools = getData();
//        TODO: build arrayAdapter by iteration children in users folder
        assert(!tools.isEmpty());
//        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.activity_doc, tools);
//        spinner.setAdapter(arrayAdapter);
    }

    public List<String> getData() {
        List<String> list = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                TODO: make a correct query for immediate children of root (users folder in future)
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    list.add(ds.getKey());
                    Log.d("deb", ds.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        return list;
    }

}
