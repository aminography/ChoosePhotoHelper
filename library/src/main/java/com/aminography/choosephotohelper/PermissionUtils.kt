package com.aminography.choosephotohelper

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.Size
import android.support.v4.content.ContextCompat

/**
 * Created by aminography on 5/17/2019.
 */

fun hasPermissions(
    context: Context,
    @Size(min = 1) vararg permissionList: String
): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return true
    }
    for (permission in permissionList) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}