package com.example.journallingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NewEntryActivity extends AppCompatActivity {

    private EntryDao entry_dao;
    private TextView prompt;
    private TextView date;
    private TextView location;
    private EditText name;
    private EditText contents;
    private Button submit;
    private int LOCATION_PERMISSION_CODE = 1;
    private String[] prompt_array;
    private Random random_prompt = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_entry);

        prompt = findViewById(R.id.newEntryPrompt);
        date = findViewById(R.id.newEntryDate);
        location = findViewById(R.id.newEntryLocation);
        name = findViewById(R.id.newEntryTitle);
        contents = findViewById(R.id.newEntryText);
        submit = findViewById(R.id.newEntrySubmit);
        prompt_array = getResources().getStringArray(R.array.journal_prompts);

        entry_dao = Room.databaseBuilder(this, EntryDatabase.class, "entry-db")
                .allowMainThreadQueries()
                .build()
                .getEntryDao();

        // displays a random journalling prompt from the array in string.xml
        prompt.setText("\"" + prompt_array[random_prompt.nextInt(20)] + "\"");
        date.setText(getSystemTime());
        location.setText("Dublin, Ireland");  //getSystemLocation());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry_name = name.getText().toString();
                String entry_contents = contents.getText().toString();
                String entry_prompt = prompt.getText().toString();
                String entry_location = "Dublin, Ireland"; //location.getText().toString();
                String entry_date = date.getText().toString();

                if (entry_name.length() == 0 ||
                    entry_contents.length() == 0 ||
                    entry_prompt.length() == 0 ||
                    entry_location == null ||
                    entry_date.length() == 0) {
                    Toast.makeText(NewEntryActivity.this, "Please enter all details.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Entry new_entry = new Entry();
                new_entry.setName(entry_name);
                new_entry.setContents(entry_contents);
                new_entry.setPrompt(entry_prompt);
                new_entry.setLocation(entry_location);
                new_entry.setDate(entry_date);

                try {
                    entry_dao.insert(new_entry);
                    setResult(RESULT_OK);
                    finish();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getSystemTime() {
        // The following code was referenced from javatpoint.com
        // https://www.javatpoint.com/java-date-to-string
        Date current_date = Calendar.getInstance().getTime();
        SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
        return date_format.format(current_date);
    }

    private String getSystemLocation() {
        /* Code referenced from a few places.
        Checking for granted permissions:
        - https://developer.android.com/training/permissions/requesting
        Asking for permissions during runtime:
        - https://youtu.be/SMrB97JuIoM?si=FmdFO62dxNkD_nx4
         */

        // If the app doesn't have location permissions, they will be requested
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        }

        // TODO get longitude and latitude, pass to getLocationFromCoords();
        // if location can't be found null is returned, used for error checking
        Log.i("Message", "Could not get coordinates");
        return null;
    }

    private void requestLocationPermission() {
        /* Code referenced from https://youtu.be/SMrB97JuIoM?si=FmdFO62dxNkD_nx4 */
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("Your location is needed to set the location of your new entry.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(NewEntryActivity.this, new String[]
                                    {Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    private String getLocationFromCoords(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String current_location = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city_name = addresses.get(0).getLocality();
            String country_name = addresses.get(0).getCountryName();

            return city_name + ", " + country_name;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // if location can't be found null is returned, used for error checking
        Log.i("Message", "Location could not be found from coordinates");
        return null;
    }
}