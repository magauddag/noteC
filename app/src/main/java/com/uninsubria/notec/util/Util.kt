package com.uninsubria.notec.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Util {

    @SuppressLint("NewApi")
    fun getData (): String {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("d MMM, kk:mm")
        return currentDate.format(formatter)
    }

    fun getDataShort (): String {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("d MMM")
        return currentDate.format(formatter)
    }

    fun getDataForPath (): String {
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM")
        return currentDate.format(formatter)

    }

    fun lowerCaseNotFirst(str: String): String {
        return str.substring(0, 1).toUpperCase() + str.substring(1, str.length).toLowerCase()
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


}