package com.example.journallingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView entryView;
    private FloatingActionButton addEntry;
    private EntryDao entryDao;
    private EntryRecyclerAdapter entryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entryView = findViewById(R.id.journalEntries);
        entryView.setLayoutManager(new LinearLayoutManager(this));
        addEntry = findViewById(R.id.addEntry);

        entryDao = Room.databaseBuilder(this, EntryDatabase.class, "entry-db")
                .allowMainThreadQueries()
                .build()
                .getEntryDao();

        entryAdapter = new EntryRecyclerAdapter(this, new ArrayList<Entry>());
        entryAdapter.setOnClickListener(new EntryRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(Entry entry) {
                // create new intent for displaying existing entry
                Intent intent = new Intent(MainActivity.this, ViewEntryActivity.class);
                Bundle b = new Bundle();
                b.putInt("entry_id", entry.getId());
                intent.putExtras(b);
                Log.i("EntryAdapter", "Starting ViewEntryActivity, entry_id = " + entry.getId());
                startActivity(intent);
            }
        });

        entryView.setAdapter(entryAdapter);

        addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create new intent for creating a new entry
                Log.i("NewEntry", "Starting NewEntryActivity");
                Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
                startActivity(intent);
            }
        });

        Log.i("MainActivity", "Loading entries");
        loadEntries();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEntries();
    }

    public void loadEntries() {
        entryAdapter.updateData(entryDao.getEntries());
    }
}