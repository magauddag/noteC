package com.uninsubria.notec.data

import androidx.lifecycle.LiveData
import androidx.room.*

// TODO: Create Dao for each other entity (Folder)

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert (note: Note)

    @Update
    suspend fun update (note: Note)

    @Delete
    suspend fun delete (note: Note)

    @Query("DELETE FROM note_table")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM note_table ORDER BY id ASC")
    fun getAllNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE title LIKE :filterQuery OR body LIKE :filterQuery OR category LIKE :filterQuery")
    fun searchDatabase(filterQuery: String) : LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE category = :filterQuery")
    fun getFilteredNotes(filterQuery: String) : LiveData<List<Note>>

}