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

    // Used to run threads in the background using the Looper service
    private BackgroundThread bgThread;
    private EntryDao entryDao; // Data access object
    private TextView prompt; // The prompt to be displayed to the user
    private TextView date; // The date and time of entry creation
    private TextView locationText; // The formatted location of entry creation
    private EditText name; // The name the user enters for the entry
    private EditText contents; // The contents of the entry
    private final Random randomPrompt = new Random(); // Used to show the user a random prompt
    private LocationManager locationManager; // Used to get the user's location
    private final long MIN_TIME = 5000; // The minimum time between location updates: 5 seconds
    private final float MIN_DISTANCE = 10; // The minimum distance between location updates: 10m
    private double latitude; // The latitude of the user at the time of entry creation
    private double longitude; // The longitude of the user at the time of entry creation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_entry);

        bgThread = new BackgroundThread();
        bgThread.start(); // Starts the looper service and initialises a Handler object

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

        // Displays a random journalling prompt from the array in string.xml
        prompt.setText("\"" + PROMPT_ARRAY[randomPrompt.nextInt(20)] + "\"");
        locationText.setText("Getting Location..."); // Temporary text while location is retrieved
        date.setText(getSystemTime());

        submit.setOnClickListener(v -> {
            String entryName = name.getText().toString();
            String entryContents = contents.getText().toString();
            String entryPrompt = prompt.getText().toString();
            String entryLocation = locationText.getText().toString();
            String entryDate = date.getText().toString();
            double entryLat = latitude;
            double entryLong = longitude;

            // All values must be valid to submit the entry
            if (entryName.length() == 0 ||
                entryContents.length() == 0 ||
                entryPrompt.length() == 0 ||
                entryLocation.equals("Getting Location...") ||
                entryDate.length() == 0) {
                // The user is shown a message if the entry is not ready for submission
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(NewEntryActivity.this,
                            "Please grant location permissions.", Toast.LENGTH_LONG).show();
                    Log.e("NewEntryActivity", "Location permissions not granted");
                } else {
                    Toast.makeText(NewEntryActivity.this, "Please enter all details.",
                            Toast.LENGTH_LONG).show();
                    Log.e("NewEntryActivity", "Entry not ready for submission");
                }
                return;
            }

            // Setting all values for DB insertion
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
                finish();
                Log.i("NewEntryActivity", "Entry submitted successfully");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        /* The system location is retrieved in a thread.
         * Without the thread the app freezes until the location is found.
         * That's because the UI thread is fully utilised through getting the location. */
        Runnable locationThread = () -> {
            Log.i("NewEntryActivity", "Getting system location...");
            getSystemLocation();
            Log.i("NewEntryActivity", "Location thread completed");
        };

        bgThread.addTaskToMessageQueue(locationThread);
    }

    /**
     * Gets the current system time and returns a string in the format 03/12/2023, 4:58 PM
     * @return A string containing the current system time.
     */
    private String getSystemTime() {
        // The following code was referenced from javatpoint.com
        // https://www.javatpoint.com/java-date-to-string
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm a");
        return dateFormat.format(currentDate);
    }

    /**
     * This method is used to get the user's location.
     * It checks for location permissions and requests them if they are not granted.
     * If the permissions are granted, the location is retrieved.
     */
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
            // If permissions have been granted, a location update is requested
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME, MIN_DISTANCE, this);
            getAddressFromCoords(latitude, longitude);
        }
    }

    @Override // Runs after permissions dialog exits.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Location permissions are required.",
                    Toast.LENGTH_SHORT).show();
            Log.w("NewEntryActivity", "Location permissions not granted");
        } else {
            Log.i("NewEntryActivity", "Location permissions granted");
        }
    }

    /**
     * This method is used to request location permissions from the user.
     */
    private void requestLocationPermission() {
        int LOCATION_PERMISSION_CODE = 1;
        ActivityCompat.requestPermissions(NewEntryActivity.this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_CODE);
    }

    public void onLocationChanged(Location location) {
        Log.d("NewEntryActivity", "Location Change Detected, Latitude: "
                + location.getLatitude() + " Longitude: " + location.getLongitude());
        // These values are stored in the DB for future use in the map fragment.
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // This code runs on a separate thread so the UI does not freeze.
        Runnable get_location = () -> {
            Log.i("NewEntryActivity", "Getting address from coordinates...");
            getAddressFromCoords(latitude, longitude);
            Log.i("NewEntryActivity", "Address retrieved from coordinates");
        };
        bgThread.addTaskToMessageQueue(get_location);
    }

    /**
     * This method is used to get a formatted location from the latitude and longitude.
     * @param latitude The latitude of the user at the time of entry creation.
     * @param longitude The longitude of the user at the time of entry creation.
     */
    private void getAddressFromCoords(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(NewEntryActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                int size = addresses.size() - 1;
                String cityName = addresses.get(size).getLocality();
                String countryName = addresses.get(size).getCountryName();

                /* Sometimes a locality can't be found, so the admin area is used instead.
                 * The locality is tried first as it's more specific. */
                if (cityName == null) {
                    Log.w("GetLocation", "City name not found, trying admin area");
                    cityName = addresses.get(size).getAdminArea();
                }

                /* This is used because the use of an anonymous class
                 * requires the variable to be final. */
                final String finalCityName = cityName;

                runOnUiThread(() -> {
                    locationText.setText(finalCityName + ", " + countryName);
                    Log.i("NewEntryActivity",
                            "Address found: " + finalCityName + ", " + countryName);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.w("NewEntryActivity", "Address could not be found from coordinates");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The location manager is stopped to save battery life.
        locationManager.removeUpdates(this);
        Log.i("NewEntryActivity", "Location updates paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The background thread is stopped to prevent memory leaks.
        locationManager.removeUpdates(this);
        Log.i("NewEntryActivity", "Location updates stopped");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // A location update is requested when the activity is resumed.
        getSystemLocation();
        Log.i("NewEntryActivity", "Location updates resumed");
    }
}