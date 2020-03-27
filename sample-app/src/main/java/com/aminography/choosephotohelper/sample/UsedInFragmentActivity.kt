package com.aminography.choosephotohelper.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author aminography
 */
class UsedInFragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_used_in_fragment)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, UsedInFragmentFragment.newInstance())
                .commitNow()
        }
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(
                Intent(context, UsedInFragmentActivity::class.java)
            )
        }
    }

}
