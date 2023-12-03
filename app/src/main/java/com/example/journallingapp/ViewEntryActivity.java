package com.example.journallingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;

public class ViewEntryActivity extends AppCompatActivity {

    private EntryDao entryDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_entry);

        TextView viewEntryDate = findViewById(R.id.viewEntryDate);
        TextView viewEntryLocation = findViewById(R.id.viewEntryLocation);
        TextView viewEntryPrompt = findViewById(R.id.viewEntryPrompt);
        TextView viewEntryName = findViewById(R.id.viewEntryTitle);
        TextView viewEntryText = findViewById(R.id.viewEntryText);
        FloatingActionButton deleteEntry = findViewById(R.id.deleteEntry);
        Fragment mapView = new MapFragment();

        try {
            entryDao = Room.databaseBuilder(this, EntryDatabase.class, "entry-db")
                    .allowMainThreadQueries()
                    .build()
                    .getEntryDao();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Entry currentEntry = entryDao.getEntryById(getIntent().getExtras().getInt("entry_id"));
        viewEntryDate.setText(currentEntry.getDate());
        viewEntryLocation.setText(currentEntry.getLocation());
        viewEntryPrompt.setText(currentEntry.getPrompt());
        viewEntryName.setText(currentEntry.getName());
        viewEntryText.setText(currentEntry.getContents());

        /* code referenced from
        https://medium.com/@ahmetbostanciklioglu/how-to-pass-data-from-activity-to-fragment-37c2785b443
        article covers kotlin, but the general idea is the same
         */
        shareWithFragment(mapView, currentEntry.getId());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapFragment, mapView) // Check if YourFragment is instantiated correctly
                .commit();

        deleteEntry.setOnClickListener(v -> showDeleteConfirmationDialog(currentEntry));
    }

    private void showDeleteConfirmationDialog(Entry currentEntry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // User clicked Yes, proceed with delete
                    entryDao.delete(currentEntry);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // User clicked No, do nothing
                    dialog.dismiss();
                })
                .show();
    }

    /* code referenced from
        https://medium.com/@ahmetbostanciklioglu/how-to-pass-data-from-activity-to-fragment-37c2785b443
        article covers kotlin, but the general idea is the same
         */
    private void shareWithFragment(Fragment fragment, int entryID) {
        Bundle bundle = new Bundle();
        bundle.putInt("entry_id", entryID);
        fragment.setArguments(bundle);
    }
}