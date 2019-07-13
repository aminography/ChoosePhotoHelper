# ChoosePhotoHelper

**`ChoosePhotoHelper`** develops a component which facilitates the source code of picking photos in your Android apps. By using it, it's possible to pick photos from gallery or take an image with the camera without any boilerplate codes.
It also internally handles some issues like rotation correction of the taken photo, permission request for camera and gallery, etc.

| Take a Photo | Choose from Gallery |
| --- | --- |
| ![](https://media.giphy.com/media/KdBwb36QCTsgKbUftB/giphy.gif) | ![](https://media.giphy.com/media/H88UXvL0jqL4HS2vuJ/giphy.gif) |

<br/>

Download
--------
**`ChoosePhotoHelper`** is available on [bintray](https://bintray.com/aminography/maven/ChoosePhotoHelper) to download using build tools systems. Add the following lines to your `build.gradle` file:

```gradle
repositories {
    jcenter()
}

dependencies {
    implementation 'com.aminography:choosephotohelper:1.0.1'
}
```

<br/>

Usage
-----
First of all, **`ChoosePhotoHelper`** provides 3 types of result to access the chosen photo which are:

| Builder Method | Result Type |
| --- | --- |
| `asFilePath()` | `String` |
| `asUri()` | `Uri` |
| `asBitmap()` | `Bitmap` |

Then, use it simply in 3 steps:

### • First
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

### • Second
Override `onActivityResult` and `onRequestPermissionsResult` in your **Activity** class, then forward their result to the `choosePhotoHelper` instance:

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

### • Finally
Call `showChooser()` method on the `choosePhotoHelper` instance:

```java
choosePhotoHelper.showChooser();
```

<hr/>

Here is a detailed example which is written in **kotlin**:

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

<br/>

License
--------
```
Copyright 2019 Mohammad Amin Hassani.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

