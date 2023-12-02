package com.example.journallingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ViewEntryActivity extends AppCompatActivity {

    private TextView viewEntryDate;
    private TextView viewEntryLocation;
    private TextView viewEntryPrompt;
    private TextView viewEntryName;
    private TextView viewEntryText;
    private FloatingActionButton deleteEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_entry);

        viewEntryDate = findViewById(R.id.viewEntryDate);
        viewEntryLocation = findViewById(R.id.viewEntryLocation);
        viewEntryPrompt = findViewById(R.id.viewEntryPrompt);
//        viewEntryName = findViewById(R.id.viewEntryName);
        viewEntryText = findViewById(R.id.viewEntryText);
//        deleteEntry = findViewById(R.id.deleteEntry);
    }
}