package com.uninsubria.notec.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.uninsubria.notec.data.Folder
import com.uninsubria.notec.data.Note
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Util {

    @SuppressLint("NewApi")
    fun getData (): String {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("d MMMM, kk:mm")
        val formatted = currentDate.format(formatter)

        return formatted
    }

    fun generateCardList(size: Int): ArrayList<Note> {

        val list = ArrayList<Note>()
        val util = Util()

        for (i in 1 .. size) {
            val card = Note(0,0,"Titolo $i", "Corpo $i", util.getData(), "Categoria $i")
            list.add(card)
        }

        return list
    }

    /*fun generateFolderList(size: Int): ArrayList<Folder> {

        val list = ArrayList<Folder>()

        for (i in 1..size) {
            val folder = Folder(0, "Category $i")
            list.add(folder)
        }

        return list
    }*/
}