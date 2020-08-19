package com.aminography.choosephotohelper.callback;

/**
 * @author aminography
 */
@FunctionalInterface
public interface ChoosePhotoCallback<T> {

    void onChoose(T photo);
}
