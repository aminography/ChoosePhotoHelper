package com.aminography.choosephotohelper.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
