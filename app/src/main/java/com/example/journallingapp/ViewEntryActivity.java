package com.example.journallingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.text.method.ScrollingMovementMethod;

public class ViewEntryActivity extends AppCompatActivity {

    private EntryDao entryDao; // Data access object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_entry);

        TextView viewEntryDate = findViewById(R.id.viewEntryDate);
        TextView viewEntryLocation = findViewById(R.id.viewEntryLocation);
        TextView viewEntryPrompt = findViewById(R.id.viewEntryPrompt);
        TextView viewEntryName = findViewById(R.id.viewEntryTitle);
        TextView viewEntryText = findViewById(R.id.viewEntryText);

        /* referenced from:
         * https://www.geeksforgeeks.org/how-to-make-textview-scrollable-in-android/
         * allows the TextView to be scrollable once it reaches its maximum height
         */
        viewEntryText.setMovementMethod(new ScrollingMovementMethod());

        FloatingActionButton deleteEntry = findViewById(R.id.deleteEntry);
        Fragment mapView = new MapFragment(); // The fragment that will display the map.

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

        /* Code referenced from
         * https://medium.com/@ahmetbostanciklioglu/how-to-pass-data-from-activity-to-fragment-37c2785b443
         * Article covers kotlin, but the general idea is the same
         */
        shareWithFragment(mapView, currentEntry.getId());

        /* Used to ensure the fragment displayed is the same one initialised above, so the correct
         * arguments can be passed */
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mapFragment, mapView)
                .commit();

        deleteEntry.setOnClickListener(v -> showDeleteConfirmationDialog(currentEntry));
    }

    /**
     * This method is used to display a confirmation dialog when the user attempts to delete an entry.
     * @param currentEntry The entry to be deleted.
     */
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

    /**
     * This method is used to pass data from the activity to the fragment.
     * @param fragment The fragment to pass data to.
     * @param entryID The id of the entry to be passed to the fragment.
     */
    private void shareWithFragment(Fragment fragment, int entryID) {
        /* Code referenced from
         * https://medium.com/@ahmetbostanciklioglu/how-to-pass-data-from-activity-to-fragment-37c2785b443
         * Article covers kotlin, but the general idea is the same
         */
        Bundle bundle = new Bundle();
        bundle.putInt("entry_id", entryID);
        fragment.setArguments(bundle);
    }
}