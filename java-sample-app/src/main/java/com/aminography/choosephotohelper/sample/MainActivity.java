package com.aminography.choosephotohelper.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button usedInActivityButton = findViewById(R.id.usedInActivityButton);
        final Button usedInFragmentButton = findViewById(R.id.usedInFragmentButton);

        usedInActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UsedInActivityActivity.class));
            }
        });

        usedInFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UsedInFragmentActivity.class));
            }
        });
    }

}
