package com.aminography.choosephotohelper

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.widget.SimpleAdapter
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.aminography.choosephotohelper.utils.hasPermissions
import com.aminography.choosephotohelper.utils.modifyOrientation
import com.aminography.choosephotohelper.utils.pathFromUri
import com.aminography.choosephotohelper.utils.uriFromFile
import org.jetbrains.anko.dip
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by aminography on 5/17/2019.
 */
class ChoosePhotoHelper private constructor(
    private val activity: Activity,
    private val callback: ChoosePhotoCallback<*>
) {

    private var outputType: OutputType = OutputType.FILE_PATH
    private var filePath: String? = null
    private var cameraFilePath: String? = null

    fun showChooser() {
        AlertDialog.Builder(activity).apply {
            setTitle(R.string.choose_photo_using)
            setNegativeButton(R.string.action_close, null)

            val items: List<Map<String, Any>> = if (!filePath.isNullOrBlank()) {
                mutableListOf<Map<String, Any>>(
                    mutableMapOf(
                        "title" to activity.getString(R.string.camera),
                        "icon" to R.drawable.ic_photo_camera_black_24dp
                    ),
                    mutableMapOf(
                        "title" to activity.getString(R.string.gallery),
                        "icon" to R.drawable.ic_photo_black_24dp
                    ),
                    mutableMapOf(
                        "title" to activity.getString(R.string.remove_photo),
                        "icon" to R.drawable.ic_delete_black_24dp
                    )
                )
            } else {
                mutableListOf<Map<String, Any>>(
                    mutableMapOf(
                        "title" to activity.getString(R.string.camera),
                        "icon" to R.drawable.ic_photo_camera_black_24dp
                    ),
                    mutableMapOf(
                        "title" to activity.getString(R.string.gallery),
                        "icon" to R.drawable.ic_photo_black_24dp
                    )
                )
            }
            val adapter = SimpleAdapter(
                activity,
                items,
                R.layout.simple_list_item,
                arrayOf("title", "icon"),
                intArrayOf(R.id.textView, R.id.imageView)
            )
            setAdapter(adapter) { _, which ->
                when (which) {
                    0 -> checkAndStartCamera()
                    1 -> checkAndShowPicker()
                    2 -> {
                        filePath = null
                        callback.onChoose(null)
                    }
                }
            }
            val dialog = create()
            dialog.listView.setPadding(0, activity.dip(16), 0, 0)
            dialog.show()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_TAKE_PHOTO -> {
                    filePath = cameraFilePath
                }
                REQUEST_CODE_PICK_PHOTO -> {
                    filePath = pathFromUri(
                        activity,
                        Uri.parse(intent?.data?.toString())
                    )
                }
            }
            filePath?.apply {
                @Suppress("UNCHECKED_CAST")
                when (outputType) {
                    OutputType.FILE_PATH -> {
                        (callback as ChoosePhotoCallback<String>).onChoose(filePath)
                    }
                    OutputType.URI -> {
                        val uri = Uri.fromFile(File(filePath))
                        (callback as ChoosePhotoCallback<Uri>).onChoose(uri)
                    }
                    OutputType.BITMAP -> {
                        doAsync {
                            //                            val bitmapBytes = modifyOrientationAndResize(this@apply)
                            var bitmap = BitmapFactory.decodeFile(this@apply)
                            try {
                                bitmap = modifyOrientation(
                                    bitmap,
                                    this@apply
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            uiThread {
                                (callback as ChoosePhotoCallback<Bitmap>).onChoose(bitmap)
                            }
                        }
                    }
                }
            }
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        @Suppress("UNUSED_PARAMETER") permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_TAKE_PHOTO_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    onPermissionsGranted(requestCode)
                } else {
                    activity.toast(R.string.required_permissions_are_not_granted)
                }
            }
            REQUEST_CODE_PICK_PHOTO_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onPermissionsGranted(requestCode)
                } else {
                    activity.toast(R.string.required_permission_is_not_granted)
                }
            }
        }
    }

    private fun onPermissionsGranted(requestCode: Int) {
        when (requestCode) {
            REQUEST_CODE_TAKE_PHOTO_PERMISSION -> {
                val picturesPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraFilePath =
                    picturesPath.toString() +
                            File.separator +
                            SimpleDateFormat(
                                "yyyy-MMM-dd_HH-mm-ss",
                                Locale.getDefault()
                            ).format(Date()) +
                            ".jpg"
                takePicture.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    uriFromFile(
                        activity,
                        activity.application.packageName,
                        File(cameraFilePath)
                    )
                )
                takePicture.putExtra(MediaStore.EXTRA_SIZE_LIMIT, CAMERA_MAX_FILE_SIZE_BYTE)
                activity.startActivityForResult(
                    takePicture,
                    REQUEST_CODE_TAKE_PHOTO
                )
            }
            REQUEST_CODE_PICK_PHOTO_PERMISSION -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                activity.startActivityForResult(
                    Intent.createChooser(intent, "Choose Photo"),
                    REQUEST_CODE_PICK_PHOTO
                )
            }
        }
    }

    private fun checkAndStartCamera() {
        if (hasPermissions(
                activity,
                *TAKE_PHOTO_PERMISSIONS
            )
        ) {
            onPermissionsGranted(
                REQUEST_CODE_TAKE_PHOTO_PERMISSION
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                TAKE_PHOTO_PERMISSIONS,
                REQUEST_CODE_TAKE_PHOTO_PERMISSION
            )
        }
    }

    private fun checkAndShowPicker() {
        if (hasPermissions(
                activity,
                *PICK_PHOTO_PERMISSIONS
            )
        ) {
            onPermissionsGranted(
                REQUEST_CODE_PICK_PHOTO_PERMISSION
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                PICK_PHOTO_PERMISSIONS,
                REQUEST_CODE_PICK_PHOTO_PERMISSION
            )
        }
    }

    enum class OutputType {
        FILE_PATH,
        URI,
        BITMAP
    }

    abstract class BaseRequestBuilder<T> internal constructor(private val activity: Activity) {

        fun build(callback: ChoosePhotoCallback<T>): ChoosePhotoHelper {
            return ChoosePhotoHelper(activity, callback)
        }
    }

    class FilePathRequestBuilder internal constructor(activity: Activity) :
        BaseRequestBuilder<String>(activity)

    class UriRequestBuilder internal constructor(activity: Activity) :
        BaseRequestBuilder<Uri>(activity)

    class BitmapRequestBuilder internal constructor(activity: Activity) :
        BaseRequestBuilder<BitmapRequestBuilder>(activity)

    class RequestBuilder(private val activity: Activity) {

        fun asFilePath(): FilePathRequestBuilder {
            return FilePathRequestBuilder(activity)
        }

        fun asUri(): UriRequestBuilder {
            return UriRequestBuilder(activity)
        }

        fun asBitmap(): BitmapRequestBuilder {
            return BitmapRequestBuilder(activity)
        }
    }

    companion object {
        private const val CAMERA_MAX_FILE_SIZE_BYTE = 2 * 1024 * 1024
        private const val REQUEST_CODE_TAKE_PHOTO = 101
        private const val REQUEST_CODE_PICK_PHOTO = 102

        val TAKE_PHOTO_PERMISSIONS =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        const val REQUEST_CODE_TAKE_PHOTO_PERMISSION = 103

        val PICK_PHOTO_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        const val REQUEST_CODE_PICK_PHOTO_PERMISSION = 104

        @JvmStatic
        fun with(activity: Activity): RequestBuilder = RequestBuilder(activity)
    }

}
