package com.example.sqlitecrudoperation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.example.sqlitecrudoperation.R;
import com.example.sqlitecrudoperation.adapter.Adapter;
import com.example.sqlitecrudoperation.sqlite.Constants;
import com.example.sqlitecrudoperation.sqlite.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;

    ActionBar actionBar;
    DatabaseHelper databaseHelper;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("All Record's");
        databaseHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerView);

        showRecord();

        fab = findViewById(R.id.addFabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
                intent.putExtra("editMode", false);
                startActivity(intent);
            }
        });
    }

    private void showRecord() {
        Adapter adapter = new Adapter(MainActivity.this, databaseHelper.getAllData(Constants.C_ADD_TIMESTAMP + " DESC"));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        showRecord();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }
}