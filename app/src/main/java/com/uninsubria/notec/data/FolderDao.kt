package com.uninsubria.notec.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Query("SELECT * FROM folder_table ORDER BY category ASC")
    fun getAllFolders() : LiveData<List<Folder>>


}