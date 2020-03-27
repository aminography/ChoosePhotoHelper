package com.aminography.choosephotohelper.utils

import android.content.Context
import android.widget.Toast

/**
 * @author aminography
 */

fun Context.toast(message: CharSequence?) = message?.let {
    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: Int) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.longToast(message: CharSequence?) = message?.let {
    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
}

fun Context.longToast(message: Int) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun Context.dp2px(dp: Float): Float = dp * resources.displayMetrics.density

fun Context.px2dp(px: Float): Float = px / resources.displayMetrics.density