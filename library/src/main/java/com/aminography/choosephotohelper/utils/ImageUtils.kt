package com.aminography.choosephotohelper.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Created by aminography on 5/17/2019.
 */

fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
    val matrix = Matrix()
    matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

@Throws(IOException::class)
fun modifyOrientation(bitmap: Bitmap, absolutePath: String): Bitmap {
    val ei = ExifInterface(absolutePath)
    val orientation =
        ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotate(
            bitmap,
            90f
        )
        ExifInterface.ORIENTATION_ROTATE_180 -> rotate(
            bitmap,
            180f
        )
        ExifInterface.ORIENTATION_ROTATE_270 -> rotate(
            bitmap,
            270f
        )
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(
            bitmap,
            horizontal = true,
            vertical = false
        )
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(
            bitmap,
            horizontal = false,
            vertical = true
        )
        else -> bitmap
    }
}

@Throws(IOException::class)
fun modifyOrientationAndResize(absolutePath: String): ByteArray? {
    var bitmap = BitmapFactory.decodeFile(absolutePath)

    var preferredHeight = bitmap.height
    var preferredWidth = bitmap.width
    if (preferredHeight > preferredWidth) {
        preferredHeight = 640
        preferredWidth = (640 * (bitmap.width.toDouble() / bitmap.height.toDouble())).toInt()
    } else {
        preferredWidth = 640
        preferredHeight = (640 * (bitmap.height.toDouble() / bitmap.width.toDouble())).toInt()
    }
    bitmap = Bitmap.createScaledBitmap(bitmap, preferredWidth, preferredHeight, true)

    try {
        bitmap = modifyOrientation(bitmap, absolutePath)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bos)
    return bos.toByteArray()
}
