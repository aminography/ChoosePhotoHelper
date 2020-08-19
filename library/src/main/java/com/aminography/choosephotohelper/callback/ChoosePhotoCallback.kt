package com.aminography.choosephotohelper.callback

/**
 * @author aminography
 */
fun interface ChoosePhotoCallback<T> {
    fun onChoose(photo: T?)
}