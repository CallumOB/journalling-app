package com.example.journallingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ViewEntryActivity extends AppCompatActivity {

    private TextView viewEntryDate;
    private TextView viewEntryLocation;
    private TextView viewEntryPrompt;
    private TextView viewEntryName;
    private TextView viewEntryText;
    private FloatingActionButton deleteEntry;
    private EntryDao entryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_entry);

        viewEntryDate = findViewById(R.id.viewEntryDate);
        viewEntryLocation = findViewById(R.id.viewEntryLocation);
        viewEntryPrompt = findViewById(R.id.viewEntryPrompt);
        viewEntryName = findViewById(R.id.viewEntryTitle);
        viewEntryText = findViewById(R.id.viewEntryText);
        deleteEntry = findViewById(R.id.deleteEntry);

        try {
            entryDao = Room.databaseBuilder(this, EntryDatabase.class, "entry-db")
                    .allowMainThreadQueries()
                    .build()
                    .getEntryDao();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Entry currentEntry = entryDao.getEntries()
                .get(findEntryIndex(getIntent().getExtras().getInt("entry_id")));
        viewEntryDate.setText(currentEntry.getDate());
        viewEntryLocation.setText(currentEntry.getLocation());
        viewEntryPrompt.setText(currentEntry.getPrompt());
        viewEntryName.setText(currentEntry.getName());
        viewEntryText.setText(currentEntry.getContents());

        deleteEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(currentEntry);
            }
        });
    }

    private void showDeleteConfirmationDialog(Entry currentEntry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked Yes, proceed with delete
                        entryDao.delete(currentEntry);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No, do nothing
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private int findEntryIndex(int entryId) {
        for (int i = 0; i < entryDao.getEntries().size(); i++) {
            if (entryDao.getEntries().get(i).getId() == entryId) {
                return i;
            }
        }
        return -1;
    }
}