# ChoosePhotoHelper

**`ChoosePhotoHelper`** develops a component which facilitates the source code of picking photos in your Android apps. By using it, it's possible to pick photos from gallery or take an image with the camera without any boilerplate codes.

**`ChoosePhotoHelper`** provides 3 types of result to access the chosen photo:

| Builder Method | Result Type |
| --- | --- |
| `asFilePath()` | `String` |
| `asUri()` | `Uri` |
| `asBitmap()` | `Bitmap` |

# Usage

Use **`ChoosePhotoHelper`** simply in 3 steps:

## • First
Create an instance of **`ChoosePhotoHelper`** using its builder pattern specifying the result type:

```java
choosePhotoHelper = ChoosePhotoHelper.with(activity)
        .asFilePath()
        .build(new ChoosePhotoCallback<String>() {

            @Override
            public void onChoose(String photo) {
                Glide.with(MainActivity.this)
                        .load(photo)
                        .into(imageView);
            }
        });
```

## • Second
Override `onActivityResult` and `onRequestPermissionsResult` in your activity, then forward their result to the `choosePhotoHelper` instance:

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    choosePhotoHelper.onActivityResult(requestCode, resultCode, data);
}

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
}
```

## • Finally
Call `showChooser()` method from `choosePhotoHelper` instance:

```java
choosePhotoHelper.showChooser();
```

<br/>

Here is a detailed example in **kotlin** showing how to use `ChoosePhotoHelper`:

```kotlin
class MainActivity : AppCompatActivity() {

    private lateinit var choosePhotoHelper: ChoosePhotoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        choosePhotoHelper = ChoosePhotoHelper.with(this)
            .asFilePath()
            .build(ChoosePhotoCallback {
                Glide.with(this)
                    .load(it)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
```
