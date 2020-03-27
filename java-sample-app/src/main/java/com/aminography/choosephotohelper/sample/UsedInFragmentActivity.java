package com.aminography.choosephotohelper.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author aminography
 */
public class UsedInFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_used_in_fragment);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, UsedInFragmentFragment.newInstance())
                    .commitNow();
        }
    }

}
