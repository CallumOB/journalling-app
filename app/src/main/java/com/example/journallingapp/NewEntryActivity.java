package com.example.journallingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewEntryActivity extends AppCompatActivity {

    private TextView prompt;
    private TextView date;
    private TextView location;
    private EditText contents;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_entry);

        prompt = findViewById(R.id.newEntryPrompt);
        date = findViewById(R.id.newEntryDate);
        location = findViewById(R.id.newEntryLocation);
        contents = findViewById(R.id.newEntryText);
        submit = findViewById(R.id.newEntrySubmit);
    }
}