package com.uninsubria.notec.util

import android.annotation.SuppressLint
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

    fun lowerCaseNotFirst(str: String): String {
        return str.substring(0, 1).toUpperCase() + str.substring(1, str.length).toLowerCase()
    }
}