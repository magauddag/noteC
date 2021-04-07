package com.uninsubria.notec.data

import androidx.lifecycle.LiveData

class FolderRepository(private val folderDao: FolderDao) {

    val readAllData: LiveData<List<Folder>> = folderDao.getAllFolders()

    suspend fun insert(folder: Folder) {
        folderDao.insert(folder)
    }

    suspend fun delete(folder: Folder) {
        folderDao.delete(folder)
    }


}