package com.example.journallingapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;
@Entity (tableName = "Entry")
public class Entry {
    @PrimaryKey (autoGenerate = true)
    private int id;
    private String name;
    private String prompt;
    private String location;
    private String date;

    public String getName() {
        return name;
    }

    public void setName(String entry_name) {
        this.name = entry_name;
    }

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

}