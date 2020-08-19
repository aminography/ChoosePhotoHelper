# `ChoosePhotoHelper` :zap:
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ChoosePhotoHelper-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7777)
[![Download](https://api.bintray.com/packages/aminography/maven/ChoosePhotoHelper/images/download.svg) ](https://bintray.com/aminography/maven/ChoosePhotoHelper/_latestVersion)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e6f9a19772c24493b0fc365a86f88b18)](https://www.codacy.com/manual/aminography/ChoosePhotoHelper?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=aminography/ChoosePhotoHelper&amp;utm_campaign=Badge_Grade)

Picking an image as an avatar in Android apps needs to write a bunch of error-prone boilerplate codes. **`ChoosePhotoHelper`** develops a component which facilitates picking photos from gallery or taking an image with the camera without any boilerplate codes.
It also internally handles some issues like **rotation correction of the taken photos**, **permission request** for camera and gallery (if needed), **URI exposure** problem, etc.

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
    implementation 'com.aminography:choosephotohelper:1.3.0'
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

Now, use it simply in 3 steps:

### • First
Create an instance of **`ChoosePhotoHelper`** using its builder pattern specifying the result type:

```java
choosePhotoHelper = ChoosePhotoHelper.with(activity)
        .asFilePath()
        .build(new ChoosePhotoCallback<String>() {
            @Override
            public void onChoose(String photo) {
                Glide.with(imageView)
                        .load(photo)
                        .into(imageView);
            }
        });
```

### • Second
Override `onActivityResult` and `onRequestPermissionsResult` in your **Activity** / **Fragment** class, then forward their result to the `choosePhotoHelper` instance:

```java
@Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
            .withState(savedInstanceState)
            .build {
                Glide.with(this)
                    .load(it)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        choosePhotoHelper.onSaveInstanceState(outState)
    }

}
```

<br/>

Change Log
----------
### Version 1.3.0
- Migrating to Kotlin 1.4.0.

### Version 1.2.1
- Some minor improvements.
- Adding ability to always show remove photo option.

### Version 1.2.0
- File path problem targeting api 29 is fixed.

### Version 1.1.0
- Migrating to AndroidX.
- Adding `onSaveInstanceState` to save and restore state.

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

