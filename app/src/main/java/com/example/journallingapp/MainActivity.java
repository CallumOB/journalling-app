package com.example.journallingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView entryView;
    private EntryDao entryDao; // Data access object
    private EntryRecyclerAdapter entryAdapter; // Adapter for recycler view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entryView = findViewById(R.id.journalEntries);
        entryView.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton addEntry = findViewById(R.id.addEntry);

        entryDao = Room.databaseBuilder(this, EntryDatabase.class, "entry-db")
                .allowMainThreadQueries()
                .build()
                .getEntryDao();

        entryAdapter = new EntryRecyclerAdapter(this, new ArrayList<>());
        entryAdapter.setOnClickListener(entry -> {
            // Create new intent for displaying existing entry
            Intent intent = new Intent(MainActivity.this, ViewEntryActivity.class);
            Bundle b = new Bundle();
            b.putInt("entry_id", entry.getId()); // Pass the id of the entry of be viewed
            intent.putExtras(b);
            Log.i("EntryAdapter", "Starting ViewEntryActivity, entry_id = " + entry.getId());
            startActivity(intent);
        });

        entryView.setAdapter(entryAdapter);

        addEntry.setOnClickListener(v -> {
            // create new intent for creating a new entry
            Log.i("NewEntry", "Starting NewEntryActivity");
            Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
            startActivity(intent);
        });

        loadEntries();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures the list of entries is updated when the activity is resumed
        loadEntries();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Ensures the list of entries is updated when the activity is started
        loadEntries();
    }

    /**
     * Loads the entries from the database and updates the recycler view.
     */
    public void loadEntries() {
        Log.i("MainActivity", "Loading entries");
        entryAdapter.updateData(entryDao.getEntries());
    }
}