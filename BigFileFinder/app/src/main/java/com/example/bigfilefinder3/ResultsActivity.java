package com.example.bigfilefinder3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public final class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ListView lvResultFiles = findViewById(R.id.listBigFiles_LV);
        ArrayList<String> resultFilePaths = getIntent().getExtras().getStringArrayList("com.example.bigfilefinder3.ResultFilePaths");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resultFilePaths);
        lvResultFiles.setAdapter(adapter);
    }
}