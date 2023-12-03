package com.example.journallingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapFragment extends Fragment {

    private Entry entry;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            float zoomLevel = 10.0f;
            LatLng entryLocation = null;

            if (entry != null) {
                entryLocation = new LatLng(entry.getLatitude(), entry.getLongitude());
                addMarker(googleMap, entryLocation, zoomLevel);
                Log.i("MapFragment", "Map marker created for " + entry.getLocation());
            } else {
                Log.e("MapFragment", "Entry is null");
            }

            /* Anonymous classes such as that used for the OnClickListener
             * require variables to be final. */
            final LatLng finalEntryLocation = entryLocation;

            FloatingActionButton focusMap = requireActivity().findViewById(R.id.focusMap);
            if (entry != null) {
                focusMap.setOnClickListener(v -> {

                    /* finalEntryLocation will never be null in this point of execution,
                     * so I assert it to be non-null to prevent warnings.
                     * finalEntryLocation will only be null if entry is also null, which I've
                     * already checked for. */
                    assert finalEntryLocation != null;
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(finalEntryLocation, zoomLevel));
                    Log.i("MapFragment", "Map focused on " + entry.getLocation());
                });
            } else {
                Log.e("MapFragment", "Entry is null");
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EntryDao entryDao = Room.databaseBuilder(requireActivity(),
                        EntryDatabase.class, "entry-db")
                .allowMainThreadQueries()
                .build()
                .getEntryDao();

        /* The entry is retrieved from the database
         * so the fragment can access the stored latitude and longitude.*/
        entry = entryDao.getEntryById(getEntryId());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    /**
     * This method is used to access data passed from the parent activity to the fragment.
     * @return The entry id passed from the parent activity.
     */
    private int getEntryId() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            return bundle.getInt("entry_id");
        }

        return -1;
    }

    /**
     * This method adds a marker to the map, moves the camera to the marker,
     * and ensures the map is interactive through gestures.
     * @param googleMap The map the marker is being added to.
     * @param entryLocation The latitude and longitude of the marker.
     * @param zoomLevel The zoom level of the map.
     */
    private void addMarker(GoogleMap googleMap, LatLng entryLocation, float zoomLevel) {
        /* The following code with the exception of the zoom level is already given upon creation of
           a google map fragment. */
        googleMap.addMarker(new MarkerOptions().position(entryLocation));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(entryLocation, zoomLevel));

        /* The following code is referenced from this link:
           https://developers.google.com/android/reference/com/google/android/gms/maps/UiSettings */
        UiSettings uiSettings = googleMap.getUiSettings(); // Retrieves the UI Settings of the map
        uiSettings.setZoomGesturesEnabled(true); // Allows the user to use zoom gestures
        uiSettings.setScrollGesturesEnabled(true); // Allows the user to scroll around the map
    }
}