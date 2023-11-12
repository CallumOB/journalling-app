package com.example.journallingapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntryDao {
    @Insert
    void insert(Entry newEntry);

    @Update
    void update(Entry targetEntry);

    @Delete
    void delete (Entry targetEntry);

    @Query("SELECT * FROM ENTRY ORDER BY DATE DESC")
    List<Entry> getEntries();
}
