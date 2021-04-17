package com.uninsubria.notec.database.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.uninsubria.notec.database.model.Note

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
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM note_table ORDER BY id ASC")
    fun getAllNotes() : LiveData<List<Note>>

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getAllNotesDesc() : LiveData<List<Note>>

    @Query("SELECT * FROM note_table ORDER BY title ASC")
    fun getAllNotesByTitle() : LiveData<List<Note>>

    @Query("SELECT * FROM note_table ORDER BY category ASC")
    fun getAllNotesByCategory() : LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE title LIKE :filterQuery OR body LIKE :filterQuery OR category LIKE :filterQuery")
    fun searchDatabase(filterQuery: String) : LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE category = :filterQuery")
    fun getFilteredNotes(filterQuery: String) : LiveData<List<Note>>

}