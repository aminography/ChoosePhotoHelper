package com.aminography.choosephotohelper

import android.graphics.Bitmap
import android.net.Uri

/**
 * Created by aminography on 5/17/2019.
 */

internal typealias FilePathCallback = (String?) -> Unit

internal typealias UriCallback = (Uri?) -> Unit

internal typealias BitmapCallback = (Bitmap?) -> Unit