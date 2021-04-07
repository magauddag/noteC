package com.uninsubria.notec.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderViewModel(application: Application): AndroidViewModel(application) {

    private val readAllFolder: LiveData<List<Folder>>
    private val repository: FolderRepository

    init {
        val folderDao = NoteDatabase.getDatabase(application).folderDao()
        repository = FolderRepository(folderDao)
        readAllFolder = repository.readAllData
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

}