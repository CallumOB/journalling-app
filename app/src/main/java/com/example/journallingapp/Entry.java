package com.example.journallingapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "Entry")
public class Entry {
    @PrimaryKey (autoGenerate = true)
    private int id; // Used as the primary key to identify each entry.
    private String name; // The title of the entry.
    private String contents; // The contents of the entry.
    private String prompt; // The prompt that was shown to the user.
    private String location; // The location of the entry as a string.
    private String date; // The formatted date and time of writing.
    private double latitude; // The latitude of the entry's location.
    private double longitude; // The longitude of the entry's location.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String entry_name) {
        this.name = entry_name;
    }

    public String getContents() {return contents;}

    public void setContents(String contents) {this.contents = contents;}

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
