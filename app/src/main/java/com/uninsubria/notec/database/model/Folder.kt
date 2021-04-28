package com.uninsubria.notec.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uninsubria.notec.R

@Entity(tableName = "folder_table")
data class Folder(

    @ColumnInfo (name = "category")
    @PrimaryKey
    var category: String,

    var folderIMG: Int = R.drawable.folder_image
)