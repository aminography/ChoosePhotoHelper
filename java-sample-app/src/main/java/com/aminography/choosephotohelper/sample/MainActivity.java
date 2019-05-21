package com.aminography.choosephotohelper.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private ChoosePhotoHelper choosePhotoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = findViewById(R.id.imageView);
        final Button button = findViewById(R.id.button);

        choosePhotoHelper = ChoosePhotoHelper.with(this).asFilePath(new Function1<String, Unit>() {
            @Override
            public Unit invoke(String s) {
                Glide.with(MainActivity.this)
                        .load(s)
                        .apply(RequestOptions.placeholderOf(R.drawable.default_placeholder))
                        .into(imageView);
                return null;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoHelper.showChooser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        choosePhotoHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
