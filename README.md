# ChoosePhotoHelper

**`ChoosePhotoHelper`** develops a component which facilitates the source code of picking photos in your Android apps. By using it, it's possible to pick photos from gallery or take an image with the camera without any boilerplate codes.

# Usage

Here is an example showing how to use `ChoosePhotoHelper`:

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var choosePhotoHelper: ChoosePhotoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        choosePhotoHelper = ChoosePhotoHelper.with(this).asFilePath {
            Glide.with(this)
                .load(it)
                .apply(RequestOptions.placeholderOf(R.drawable.default_placeholder))
                .into(imageView)
        }

        button.setOnClickListener {
            choosePhotoHelper.showChooser()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        choosePhotoHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
```
