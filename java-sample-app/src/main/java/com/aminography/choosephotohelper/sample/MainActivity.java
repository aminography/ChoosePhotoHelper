package com.aminography.choosephotohelper.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author aminography
 */
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
