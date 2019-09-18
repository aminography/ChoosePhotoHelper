package com.aminography.choosephotohelper.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_used_in_activity.*

class UsedInActivityActivity : AppCompatActivity() {

    private lateinit var choosePhotoHelper: ChoosePhotoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_used_in_activity)

        choosePhotoHelper = ChoosePhotoHelper.with(this)
            .asFilePath()
            .build(ChoosePhotoCallback {
                Glide.with(this)
                    .load(it)
                    .apply(RequestOptions.placeholderOf(R.drawable.default_placeholder))
                    .into(imageView)
            })

        button.setOnClickListener {
            choosePhotoHelper.showChooser()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        choosePhotoHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(
                Intent(context, UsedInActivityActivity::class.java)
            )
        }
    }

}
