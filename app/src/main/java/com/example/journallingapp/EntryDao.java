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
    void delete(Entry targetEntry);

    // Returns all entries in the database. Used for the recycler view.
    @Query("SELECT * FROM ENTRY ORDER BY id DESC")
    List<Entry> getEntries();

    // Returns a single entry from the database. Used for ViewEntryActivity.
    @Query("SELECT * FROM ENTRY WHERE id = :id")
    Entry getEntryById(int id);
}
