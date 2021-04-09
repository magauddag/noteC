package com.uninsubria.notec.data

import androidx.lifecycle.LiveData
import androidx.room.*

// TODO: Create Dao for each other entity (Folder)

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert (note: Note)

    @Update
    suspend fun update (note: Note)

    @Delete
    suspend fun delete (note: Note)

    @Query("DELETE FROM note_table")
    fun deleteAllNotes()

    @Query("SELECT * FROM note_table ORDER BY id ASC")
    fun getAllNotes() : LiveData<List<Note>>

}