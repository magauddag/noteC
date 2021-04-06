package com.uninsubria.notec.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uninsubria.notec.R

@Entity(tableName = "folder_table")
data class Folder(

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var category: String,

    var folderIMG: Int = R.drawable.folder_image
)