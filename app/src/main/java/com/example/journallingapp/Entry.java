package com.example.journallingapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;
@Entity (tableName = "Entry")
public class Entry {
    @PrimaryKey (autoGenerate = true)
    private int id;
    private String entry_name;
    private String prompt;
    private String location;
    private Date time;
}
