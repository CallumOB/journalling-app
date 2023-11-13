package com.example.journallingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewEntryActivity extends AppCompatActivity {

    private TextView prompt;
    private TextView date;
    private TextView location;
    private EditText contents;
    private Button submit;
    int LOCATION_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_entry);

        prompt = findViewById(R.id.newEntryPrompt);
        date = findViewById(R.id.newEntryDate);
        location = findViewById(R.id.newEntryLocation);
        contents = findViewById(R.id.newEntryText);
        submit = findViewById(R.id.newEntrySubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    private String getSystemLocation(Context context, String location_name) {
        /* Code referenced from a few places.
        Getting Longitude and Latitude:
        - https://www.tutorialspoint.com/how-to-get-current-location-latitude-and-longitude-in-android
        Checking for granted permissions:
        - https://developer.android.com/training/permissions/requesting
        Asking for permissions during runtime:
        - https://youtu.be/SMrB97JuIoM?si=FmdFO62dxNkD_nx4
         */

        LocationManager location_manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // If the app doesn't have location permissions, they will be requested
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        }
        Location current_location = location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (current_location != null) {
            double latitude = current_location.getLatitude();
            double longitude = current_location.getLongitude();

            return getLocationFromCoords(latitude, longitude);
        }

        // if location can't be found null is returned, used for error checking
        return null;
    }

    private void requestLocationPermission() {
        /* Code referenced from https://youtu.be/SMrB97JuIoM?si=FmdFO62dxNkD_nx4 */
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("Your location is needed to set the location of your new entry.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(NewEntryActivity.this, new String[]
                                    {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
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
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
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
        return null;
    }
}