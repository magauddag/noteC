package com.uninsubria.notec.data

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {

    val readAllData: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    fun searchDatabase(filterQuery : String) : LiveData<List<Note>> {
        return noteDao.searchDatabase(filterQuery)
    }

    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }
}