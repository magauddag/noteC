package com.uninsubria.notec.database.repository

import androidx.lifecycle.LiveData
import com.uninsubria.notec.database.Dao.FolderDao
import com.uninsubria.notec.database.model.Folder

class FolderRepository(private val folderDao: FolderDao) {

    val readAllData: LiveData<List<Folder>> = folderDao.getAllFolders()
    val readAllCategories: LiveData<List<String>> = folderDao.getAllCategories()

    suspend fun insert(folder: Folder) {
        folderDao.insert(folder)
    }

    suspend fun delete(folder: Folder) {
        folderDao.delete(folder)
    }
}