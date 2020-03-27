package com.aminography.choosephotohelper.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author aminography
 */
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
