package com.example.journallingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NewEntryActivity extends AppCompatActivity implements LocationListener {

    private BackgroundThread bg_thread;
    private EntryDao entry_dao;
    private TextView prompt;
    private TextView date;
    private TextView location_text;
    private EditText name;
    private EditText contents;
    private Button submit;
    private int LOCATION_PERMISSION_CODE = 1;
    private String[] prompt_array;
    private Random random_prompt = new Random();
    private LocationManager locationManager;
    private long minTime = 500;
    private float minDistance = 1;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_entry);

        bg_thread = new BackgroundThread();
        bg_thread.start();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        prompt = findViewById(R.id.newEntryPrompt);
        date = findViewById(R.id.newEntryDate);
        location_text = findViewById(R.id.newEntryLocation);
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
        location_text.setText("Getting Location...");
        date.setText(getSystemTime());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry_name = name.getText().toString();
                String entry_contents = contents.getText().toString();
                String entry_prompt = prompt.getText().toString();
                String entry_location = location_text.getText().toString();
                String entry_date = date.getText().toString();
                double entry_lat = latitude;
                double entry_long = longitude;

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
                new_entry.setLatitude(entry_lat);
                new_entry.setLongitude(entry_long);

                try {
                    entry_dao.insert(new_entry);
                    setResult(RESULT_OK);
                    finish();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        Runnable location_thread = () -> {
            Log.i("location_thread", "Checking for location permissions...");
            getSystemLocation();
            Log.i("location_thread", "Thread completed");
        };

        bg_thread.addTaskToMessageQueue(location_thread);
    }

    private String getSystemTime() {
        // The following code was referenced from javatpoint.com
        // https://www.javatpoint.com/java-date-to-string
        Date current_date = Calendar.getInstance().getTime();
        SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
        return date_format.format(current_date);
    }

    private void getSystemLocation() {
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
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
        }
    }

    @Override // runs after permissions dialog exits
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Location permissions are required.", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestLocationPermission() {
        /* Code referenced from Lab 9 */
        if (ActivityCompat.checkSelfPermission(NewEntryActivity.this, // if permissions are not granted ...
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(NewEntryActivity.this, // ... a permission request will be sent
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        }else{
            // if location permissions have been granted, a location update is requested
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
        }
    }

    public void onLocationChanged(Location location) {
        Log.d("new_location", "Location Change Detected, Latitude: "
                + location.getLatitude() + " Longitude: " + location.getLongitude());
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Runnable get_location = () -> {
                Log.i("get_location", "Getting location from coordinates...");
                getLocationFromCoords(latitude, longitude);
                Log.i("get_location", "Thread completed");
            };
//            getLocationFromCoords(latitude, longitude);
            bg_thread.addTaskToMessageQueue(get_location);
        }
    }

    private void getLocationFromCoords(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(NewEntryActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.i("get_location", ("Latitude: " + latitude + " Longitude: " + longitude));

            if (!addresses.isEmpty() && addresses.size() > 0) {
                int size = addresses.size() - 1;
                Log.i("get_location", ("Size of address list: " + size));
                String city_name = addresses.get(size).getLocality();
                String country_name = addresses.get(size).getCountryName();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        location_text.setText(city_name + ", " + country_name);
                    }
                });
            } else {
                Log.i("get_location", "No address found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // if location can't be found null is returned, used for error checking
            Log.i("Message", "Location could not be found from coordinates");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}