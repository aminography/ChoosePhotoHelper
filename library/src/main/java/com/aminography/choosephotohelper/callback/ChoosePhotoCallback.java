package com.aminography.choosephotohelper.callback;

@FunctionalInterface
public interface ChoosePhotoCallback<T> {

    void onChoose(T photo);
}
