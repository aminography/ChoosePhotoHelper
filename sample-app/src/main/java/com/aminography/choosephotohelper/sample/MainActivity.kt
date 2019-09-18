package com.aminography.choosephotohelper.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usedInActivityButton.setOnClickListener {
            UsedInActivityActivity.start(this)
        }

        usedInFragmentButton.setOnClickListener {
            UsedInFragmentActivity.start(this)
        }
    }

}
