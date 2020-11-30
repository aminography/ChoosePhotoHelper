@file:Suppress("unused")

package com.aminography.choosephotohelper

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.SimpleAdapter
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.aminography.choosephotohelper.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author aminography
 */
class ChoosePhotoHelper private constructor(
    private val activity: Activity,
    private val fragment: Fragment?,
    private val whichSource: WhichSource,
    private val outputType: OutputType,
    private val callback: ChoosePhotoCallback<*>,
    private var filePath: String? = null,
    private var cameraFilePath: String? = null,
    private val alwaysShowRemoveOption: Boolean? = null
) {

    /**
     * Opens a chooser dialog to select the way of picking photo.
     *
     * @param dialogTheme the theme of chooser dialog
     */
    @JvmOverloads
    fun showChooser(@StyleRes dialogTheme: Int = 0) {
        AlertDialog.Builder(activity, dialogTheme).apply {
            setTitle(R.string.choose_photo_using)
            setNegativeButton(R.string.action_close, null)

            SimpleAdapter(
                activity,
                createOptionsList(),
                R.layout.simple_list_item,
                arrayOf(KEY_TITLE, KEY_ICON),
                intArrayOf(R.id.textView, R.id.imageView)
            ).let {
                setAdapter(it) { _, which ->
                    when (which) {
                        0 -> checkAndStartCamera()
                        1 -> checkAndShowPicker()
                        2 -> {
                            filePath = null
                            callback.onChoose(null)
                        }
                    }
                }
            }
            val dialog = create()
            dialog.listView.setPadding(0, activity.dp2px(16f).toInt(), 0, 0)
            dialog.show()
        }
    }

    /**
     * Opens camera to take a photo without showing the chooser dialog.
     */
    fun takePhoto() {
        checkAndStartCamera()
    }

    /**
     * Opens default device's image picker without showing the chooser dialog.
     */
    fun chooseFromGallery() {
        checkAndShowPicker()
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
            filePath?.let {
                @Suppress("UNCHECKED_CAST")
                when (outputType) {
                    OutputType.FILE_PATH -> {
                        (callback as ChoosePhotoCallback<String>).onChoose(it)
                    }
                    OutputType.URI -> {
                        val uri = Uri.fromFile(File(it))
                        (callback as ChoosePhotoCallback<Uri>).onChoose(uri)
                    }
                    OutputType.BITMAP -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            var bitmap = BitmapFactory.decodeFile(it)
                            try {
                                bitmap = modifyOrientationSuspending(bitmap, it)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            withContext(Dispatchers.Main) {
                                (callback as ChoosePhotoCallback<Bitmap>).onChoose(bitmap)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Call this method in Activity#onSaveInstanceState or Fragment#onSaveInstanceState
     * to save ChoosePhotoHelper state that can be later restored by withState(Bundle)
     */
    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(FILE_PATH, filePath)
        outState.putString(CAMERA_FILE_PATH, cameraFilePath)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        @Suppress("UNUSED_PARAMETER") permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_TAKE_PHOTO_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    onPermissionsGranted(requestCode)
                } else {
                    activity.toast(R.string.required_permissions_are_not_granted)
                }
            }
            REQUEST_CODE_PICK_PHOTO_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    onPermissionsGranted(requestCode)
                } else {
                    activity.toast(R.string.required_permission_is_not_granted)
                }
            }
        }
    }

    private fun createOptionsList(): List<Map<String, Any>> {
        return if (!filePath.isNullOrBlank() || alwaysShowRemoveOption == true) {
            mutableListOf<Map<String, Any>>(
                mutableMapOf(
                    KEY_TITLE to activity.getString(R.string.camera),
                    KEY_ICON to R.drawable.ic_photo_camera_black_24dp
                ),
                mutableMapOf(
                    KEY_TITLE to activity.getString(R.string.gallery),
                    KEY_ICON to R.drawable.ic_photo_black_24dp
                ),
                mutableMapOf(
                    KEY_TITLE to activity.getString(R.string.remove_photo),
                    KEY_ICON to R.drawable.ic_delete_black_24dp
                )
            )
        } else {
            mutableListOf<Map<String, Any>>(
                mutableMapOf(
                    KEY_TITLE to activity.getString(R.string.camera),
                    KEY_ICON to R.drawable.ic_photo_camera_black_24dp
                ),
                mutableMapOf(
                    KEY_TITLE to activity.getString(R.string.gallery),
                    KEY_ICON to R.drawable.ic_photo_black_24dp
                )
            )
        }
    }

    private fun onPermissionsGranted(requestCode: Int) {
        when (requestCode) {
            REQUEST_CODE_TAKE_PHOTO_PERMISSION -> {
                val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val file = File.createTempFile(
                    "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}",
                    ".jpg",
                    storageDir
                )
                cameraFilePath = file.absolutePath

                Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, file.grantedUri(activity))
//                    putExtra(MediaStore.EXTRA_SIZE_LIMIT, CAMERA_MAX_FILE_SIZE_BYTE)
                }.let {
                    when (whichSource) {
                        WhichSource.ACTIVITY -> activity.startActivityForResult(
                            it,
                            REQUEST_CODE_TAKE_PHOTO
                        )
                        WhichSource.FRAGMENT -> fragment?.startActivityForResult(
                            it,
                            REQUEST_CODE_TAKE_PHOTO
                        )
                    }
                }
            }
            REQUEST_CODE_PICK_PHOTO_PERMISSION -> {
                Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    addCategory(Intent.CATEGORY_OPENABLE)
                }.let {
                    when (whichSource) {
                        WhichSource.ACTIVITY -> activity.startActivityForResult(
                            Intent.createChooser(it, "Choose a Photo"),
                            REQUEST_CODE_PICK_PHOTO
                        )
                        WhichSource.FRAGMENT -> fragment?.startActivityForResult(
                            Intent.createChooser(it, "Choose a Photo"),
                            REQUEST_CODE_PICK_PHOTO
                        )
                    }
                }
            }
        }
    }

    private fun checkAndStartCamera() {
        if (hasPermissions(activity, *TAKE_PHOTO_PERMISSIONS)) {
            onPermissionsGranted(REQUEST_CODE_TAKE_PHOTO_PERMISSION)
        } else {
            when (whichSource) {
                WhichSource.ACTIVITY -> ActivityCompat.requestPermissions(
                    activity,
                    TAKE_PHOTO_PERMISSIONS,
                    REQUEST_CODE_TAKE_PHOTO_PERMISSION
                )
                WhichSource.FRAGMENT -> fragment?.requestPermissions(
                    TAKE_PHOTO_PERMISSIONS,
                    REQUEST_CODE_TAKE_PHOTO_PERMISSION
                )
            }
        }
    }

    private fun checkAndShowPicker() {
        if (hasPermissions(activity, *PICK_PHOTO_PERMISSIONS)) {
            onPermissionsGranted(REQUEST_CODE_PICK_PHOTO_PERMISSION)
        } else {
            when (whichSource) {
                WhichSource.ACTIVITY -> ActivityCompat.requestPermissions(
                    activity,
                    PICK_PHOTO_PERMISSIONS,
                    REQUEST_CODE_PICK_PHOTO_PERMISSION
                )
                WhichSource.FRAGMENT -> fragment?.requestPermissions(
                    PICK_PHOTO_PERMISSIONS,
                    REQUEST_CODE_PICK_PHOTO_PERMISSION
                )
            }
        }
    }

    enum class OutputType {
        FILE_PATH,
        URI,
        BITMAP
    }

    abstract class BaseRequestBuilder<T> internal constructor(
        private val activity: Activity?,
        private val fragment: Fragment?,
        private val which: WhichSource,
        private val outputType: OutputType
    ) {

        private var filePath: String? = null
        private var cameraFilePath: String? = null
        private var alwaysShowRemoveOption: Boolean? = null


        /**
         * Use this method to restore the state previously saved on onSaveInstanceState
         */
        fun withState(state: Bundle?): BaseRequestBuilder<T> {
            filePath = state?.getString(FILE_PATH)
            cameraFilePath = state?.getString(CAMERA_FILE_PATH)
            return this
        }

        fun alwaysShowRemoveOption(show: Boolean): BaseRequestBuilder<T> {
            alwaysShowRemoveOption = show
            return this
        }

        fun build(callback: ChoosePhotoCallback<T>): ChoosePhotoHelper {
            return when (which) {
                WhichSource.ACTIVITY -> ChoosePhotoHelper(
                    activity!!,
                    null,
                    which,
                    outputType,
                    callback,
                    filePath,
                    cameraFilePath,
                    alwaysShowRemoveOption
                )
                WhichSource.FRAGMENT -> ChoosePhotoHelper(
                    fragment!!.requireActivity(),
                    fragment,
                    which,
                    outputType,
                    callback,
                    filePath,
                    cameraFilePath,
                    alwaysShowRemoveOption
                )
            }
        }
    }

    class FilePathRequestBuilder internal constructor(
        activity: Activity?,
        fragment: Fragment?,
        which: WhichSource
    ) : BaseRequestBuilder<String>(activity, fragment, which, OutputType.FILE_PATH)

    class UriRequestBuilder internal constructor(
        activity: Activity?,
        fragment: Fragment?,
        which: WhichSource
    ) : BaseRequestBuilder<Uri>(activity, fragment, which, OutputType.URI)

    class BitmapRequestBuilder internal constructor(
        activity: Activity?,
        fragment: Fragment?,
        which: WhichSource
    ) : BaseRequestBuilder<Bitmap>(activity, fragment, which, OutputType.BITMAP)

    class RequestBuilder(
        private val activity: Activity? = null,
        private val fragment: Fragment? = null,
        private val which: WhichSource
    ) {

        fun asFilePath(): FilePathRequestBuilder {
            return FilePathRequestBuilder(activity, fragment, which)
        }

        fun asUri(): UriRequestBuilder {
            return UriRequestBuilder(activity, fragment, which)
        }

        fun asBitmap(): BitmapRequestBuilder {
            return BitmapRequestBuilder(activity, fragment, which)
        }
    }

    enum class WhichSource {
        ACTIVITY,
        FRAGMENT,
    }

    companion object {

        private const val KEY_TITLE = "title"
        private const val KEY_ICON = "icon"

        private const val CAMERA_MAX_FILE_SIZE_BYTE = 2 * 1024 * 1024
        private const val REQUEST_CODE_TAKE_PHOTO = 101
        private const val REQUEST_CODE_PICK_PHOTO = 102

        const val REQUEST_CODE_TAKE_PHOTO_PERMISSION = 103
        val TAKE_PHOTO_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        const val REQUEST_CODE_PICK_PHOTO_PERMISSION = 104
        val PICK_PHOTO_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        private const val FILE_PATH = "filePath"
        private const val CAMERA_FILE_PATH = "cameraFilePath"

        @JvmStatic
        fun with(activity: Activity): RequestBuilder =
            RequestBuilder(activity = activity, which = WhichSource.ACTIVITY)

        @JvmStatic
        fun with(fragment: Fragment): RequestBuilder =
            RequestBuilder(fragment = fragment, which = WhichSource.FRAGMENT)

    }

}
