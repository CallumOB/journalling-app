package com.example.journallingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView entry_view;
    private FloatingActionButton add_entry;
    private EntryDao entry_dao;
    private EntryRecyclerAdapter entry_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entry_view = findViewById(R.id.journalEntries);
        entry_view.setLayoutManager(new LinearLayoutManager(this));
        add_entry = findViewById(R.id.addEntry);

        entry_dao = Room.databaseBuilder(this, EntryDatabase.class, "entry-db")
                .allowMainThreadQueries()
                .build()
                .getEntryDao();

        entry_adapter = new EntryRecyclerAdapter(this, new ArrayList<Entry>());
        entry_view.setAdapter(entry_adapter);
        entry_adapter.addOnClickListener(new EntryRecyclerAdapter.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create new intent for displaying existing entry
                Intent intent = new Intent(MainActivity.this, ViewEntryActivity.class);
                Bundle b = new Bundle();
                b.putInt("entry_id", v.getId());
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        add_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create new intent for creating a new entry
                Intent intent = new Intent(MainActivity.this, NewEntryActivity.class);
                startActivity(intent);
            }
        });

        loadEntries();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEntries();
    }

    public void loadEntries() {entry_adapter.updateData(entry_dao.getEntries());}
}