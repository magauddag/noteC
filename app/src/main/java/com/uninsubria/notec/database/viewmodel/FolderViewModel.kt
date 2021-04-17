package com.uninsubria.notec.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.uninsubria.notec.database.NoteDatabase
import com.uninsubria.notec.database.model.Folder
import com.uninsubria.notec.database.repository.FolderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderViewModel(application: Application): AndroidViewModel(application) {

    private val readAllFolder: LiveData<List<Folder>>
    private val readAllCategories: LiveData<List<String>>
    private val repository: FolderRepository

    init {
        val folderDao = NoteDatabase.getDatabase(application)
            .folderDao()
        repository = FolderRepository(folderDao)
        readAllFolder = repository.readAllData
        readAllCategories = repository.readAllCategories
    }

    fun insert (folder: Folder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(folder)
        }
    }

    fun delete (folder: Folder) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(folder)
        }
    }

    fun getAllFolders(): LiveData<List<Folder>> {
        return readAllFolder
    }

    fun getAllCategories(): LiveData<List<String>> {
        return readAllCategories
    }

}