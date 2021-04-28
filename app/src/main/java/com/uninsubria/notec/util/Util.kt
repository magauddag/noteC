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

    /*fun getDownsizedImageBitmap(currentPhotoPath: String?): Bitmap {

        // aiming for ~500kb max. assumes typical device image size is around 2megs
        val scaleDivider = 8

        val fullBitmap = BitmapFactory.decodeFile(currentPhotoPath)

        // 2. Get the downsized image content as a byte[]
        val scaleWidth = fullBitmap.width / scaleDivider
        val scaleHeight = fullBitmap.height / scaleDivider
        val downsizedImageBytes = getDownsizedImageBytes(fullBitmap, scaleWidth, scaleHeight)

        val converters = Converters()

        return converters.toBitmap(downsizedImageBytes)
    }

    @Throws(IOException::class)
    fun getDownsizedImageBytes(fullBitmap: Bitmap, scaleWidth: Int, scaleHeight: Int): ByteArray {
        val scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, scaleWidth, scaleHeight, true)

        // 2. Instantiate the downsized image content as a byte[]
        val baos = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()
    }*/
}