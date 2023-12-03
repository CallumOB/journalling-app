package com.example.journallingapp;

import androidx.annotation.NonNull;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NewEntryActivity extends AppCompatActivity implements LocationListener {

    private BackgroundThread bgThread;
    private EntryDao entryDao;
    private TextView prompt;
    private TextView date;
    private TextView locationText;
    private EditText name;
    private EditText contents;
    private final Random randomPrompt = new Random();
    private LocationManager locationManager;
    private final long MIN_TIME = 5000;
    private final float MIN_DISTANCE = 10;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_entry);

        bgThread = new BackgroundThread();
        bgThread.start();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        prompt = findViewById(R.id.newEntryPrompt);
        date = findViewById(R.id.newEntryDate);
        locationText = findViewById(R.id.newEntryLocation);
        name = findViewById(R.id.newEntryTitle);
        contents = findViewById(R.id.newEntryText);
        Button submit = findViewById(R.id.newEntrySubmit);
        String[] PROMPT_ARRAY = getResources().getStringArray(R.array.journal_prompts);

        entryDao = Room.databaseBuilder(this, EntryDatabase.class, "entry-db")
                .allowMainThreadQueries()
                .build()
                .getEntryDao();

        // displays a random journalling prompt from the array in string.xml
        prompt.setText("\"" + PROMPT_ARRAY[randomPrompt.nextInt(20)] + "\"");
        locationText.setText("Getting Location...");
        date.setText(getSystemTime());

        submit.setOnClickListener(v -> {
            String entryName = name.getText().toString();
            String entryContents = contents.getText().toString();
            String entryPrompt = prompt.getText().toString();
            String entryLocation = locationText.getText().toString();
            String entryDate = date.getText().toString();
            double entryLat = latitude;
            double entryLong = longitude;

            if (entryName.length() == 0 ||
                entryContents.length() == 0 ||
                entryPrompt.length() == 0 ||
                !entryLocation.equals("Getting Location...") ||
                entryDate.length() == 0) {
                Toast.makeText(NewEntryActivity.this, "Please enter all details.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Entry newEntry = new Entry();
            newEntry.setName(entryName);
            newEntry.setContents(entryContents);
            newEntry.setPrompt(entryPrompt);
            newEntry.setLocation(entryLocation);
            newEntry.setDate(entryDate);
            newEntry.setLatitude(entryLat);
            newEntry.setLongitude(entryLong);

            try {
                entryDao.insert(newEntry);
                setResult(RESULT_OK);
                finish();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Runnable locationThread = () -> {
            Log.i("PermissionCheck", "Checking for location permissions...");
            getSystemLocation();
            Log.i("PermissionCheck", "Thread completed");
        };

        bgThread.addTaskToMessageQueue(locationThread);
    }

    private String getSystemTime() {
        // The following code was referenced from javatpoint.com
        // https://www.javatpoint.com/java-date-to-string
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
        return dateFormat.format(currentDate);
    }

    private void getSystemLocation() {
        /* Code referenced from a few places.
        Checking for granted permissions:
        - https://developer.android.com/training/permissions/requesting
        Asking for permissions during runtime:
        - https://youtu.be/SMrB97JuIoM?si=FmdFO62dxNkD_nx4
         */

        // If the app doesn't have location permissions, they will be requested
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME, MIN_DISTANCE, this);
            getLocationFromCoords(latitude, longitude);
        }
    }

    @Override // runs after permissions dialog exits
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Location permissions are required.",
                    Toast.LENGTH_SHORT).show();
            Log.w("PermissionCheck", "Location permissions not granted");
        } else {
            Log.i("PermissionCheck", "Location permissions granted");
        }
    }

    private void requestLocationPermission() {
        /* Code referenced from Lab 9 */
        if (ActivityCompat.checkSelfPermission(NewEntryActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            int LOCATION_PERMISSION_CODE = 1;
            ActivityCompat.requestPermissions(NewEntryActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        }else{
            // if location permissions have been granted, a location update is requested
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME, MIN_DISTANCE, this);
            Log.i("LocationManager", "Location updates requested");
        }
    }

    public void onLocationChanged(Location location) {
        Log.d("new_location", "Location Change Detected, Latitude: "
                + location.getLatitude() + " Longitude: " + location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Runnable get_location = () -> {
            Log.i("LocationChange", "Getting location from coordinates...");
            getLocationFromCoords(latitude, longitude);
            Log.i("LocationChange", "Thread completed");
        };
        bgThread.addTaskToMessageQueue(get_location);
    }

    private void getLocationFromCoords(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(NewEntryActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.d("GetLocation", ("Latitude: " + latitude + " Longitude: " + longitude));

            if (addresses != null && !addresses.isEmpty()) {
                int size = addresses.size() - 1;
                Log.d("GetLocation", ("Size of address list: " + size));
                String cityName = addresses.get(size).getLocality();
                String countryName = addresses.get(size).getCountryName();

                /* sometimes a locality can't be found, so the admin area is used instead.
                   The locality is tried first as it's more accurate. */
                if (cityName == null) {
                    Log.w("GetLocation", "City name not found, trying admin area");
                    cityName = addresses.get(size).getAdminArea();
                }

                /* this is used because the use of an anonymous class (such as new Runnable())
                   requires the variable to be final. */
                final String finalCityName = cityName;

                runOnUiThread(() -> {
                    locationText.setText(finalCityName + ", " + countryName);
                    Log.i("GetLocation",
                            "Location found: " + finalCityName + ", " + countryName);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.w("GetLocation", "Location could not be found from coordinates");
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