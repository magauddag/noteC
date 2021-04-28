package com.uninsubria.notec.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var image: String?,

    @ColumnInfo (name = "title")
    var title: String,

    var body: String,

    var data: String,

    @ColumnInfo (name = "category")
    var category: String

)