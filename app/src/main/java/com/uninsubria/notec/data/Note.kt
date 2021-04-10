package com.uninsubria.notec.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note (

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var image: Int,

    var title: String,

    var body: String,

    var data: String,

    var category: String,

    var selected: Boolean = false)