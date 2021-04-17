package com.uninsubria.notec.database.repository

import androidx.lifecycle.LiveData
import com.uninsubria.notec.database.Dao.NoteDao
import com.uninsubria.notec.database.model.Note

class NoteRepository(private val noteDao: NoteDao) {

    val readAllData: LiveData<List<Note>> = noteDao.getAllNotes()
    val readAllDataDesc: LiveData<List<Note>> = noteDao.getAllNotesDesc()
    val readAllDataByCategory: LiveData<List<Note>> = noteDao.getAllNotesByCategory()
    val readAllDataByTitle: LiveData<List<Note>> = noteDao.getAllNotesByTitle()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }

    fun searchDatabase(filterQuery : String) : LiveData<List<Note>> {
        return noteDao.searchDatabase(filterQuery)
    }

    fun getFilteredNotes(filterQuery : String): LiveData<List<Note>> {
        return noteDao.getFilteredNotes(filterQuery)
    }

    //fun getAllNotes() = noteDao.getAllNotes()

}