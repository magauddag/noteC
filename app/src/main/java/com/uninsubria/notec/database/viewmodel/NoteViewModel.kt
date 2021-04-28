package com.uninsubria.notec.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.uninsubria.notec.database.NoteDatabase
import com.uninsubria.notec.database.model.Note
import com.uninsubria.notec.database.repository.NoteRepository
import com.uninsubria.notec.util.SortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application): AndroidViewModel(application) {

    private val readAllDataAsc: LiveData<List<Note>>
    private val readAllDataDesc: LiveData<List<Note>>
    private val readAllDataByCategory: LiveData<List<Note>>
    private val readAllDataByTitle: LiveData<List<Note>>
    private val repository: NoteRepository
    val notes = MediatorLiveData<List<Note>>()
    var sortType = SortType.ASC

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)

        readAllDataAsc = repository.readAllData
        readAllDataDesc = repository.readAllDataDesc
        readAllDataByCategory = repository.readAllDataByCategory
        readAllDataByTitle = repository.readAllDataByTitle

        notes.addSource(readAllDataAsc) {result ->
            if(sortType == SortType.ASC) {
                result?.let { notes.value = it }
            }
        }

        notes.addSource(readAllDataDesc) {result ->
            if(sortType == SortType.DESC) {
                result?.let { notes.value = it }
            }
        }

        notes.addSource(readAllDataByCategory) {result ->
            if(sortType == SortType.CATEGORY) {
                result?.let { notes.value = it }
            }
        }

        notes.addSource(readAllDataByTitle) {result ->
            if(sortType == SortType.TITLE) {
                result?.let { notes.value = it }
            }
        }
    }

    fun sortNotes(sortType: SortType) = when(sortType) {
        SortType.ASC -> readAllDataAsc.value?.let { notes.value = it }
        SortType.DESC -> readAllDataDesc.value?.let { notes.value = it }
        SortType.CATEGORY -> readAllDataByCategory.value?.let { notes.value = it }
        SortType.TITLE -> readAllDataByTitle.value?.let { notes.value = it }
    }.also {
        this.sortType = sortType
    }

    fun insert (note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(note)
        }
    }

    fun update (note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(note)
        }
    }

    fun delete (note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(note)
        }
    }

    fun deleteAllaNotes () {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllNotes()
        }
    }

    fun updateCategory(deletedCategory : String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategory(deletedCategory)
        }
    }

    fun searchDatabase (filterQuery : String) = repository.searchDatabase(filterQuery)

    fun getFilteredNotes(filterQuery: String) = repository.getFilteredNotes(filterQuery)

    fun getAllNotesAsc() = repository.readAllData

}