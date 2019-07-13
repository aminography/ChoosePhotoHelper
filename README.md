# ChoosePhotoHelper

**`ChoosePhotoHelper`** develops a component which facilitates the source code of picking photos in your Android apps. By using it, it's possible to pick photos from gallery or take an image with the camera without any boilerplate codes.

**`ChoosePhotoHelper`** provides three types of result to access the chosen photo:

| Builder Method | Result Type |
| --- | --- |
| `asFilePath()` | `String` |
| `asUri()` | `Uri` |
| `asBitmap()` | `Bitmap` |

# Usage

Here is an example showing how to use `ChoosePhotoHelper`:

> Java
```java
public class MainActivity extends AppCompatActivity {

    private ChoosePhotoHelper choosePhotoHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = findViewById(R.id.imageView);
        final Button button = findViewById(R.id.button);

        choosePhotoHelper = ChoosePhotoHelper.with(this)
                .asFilePath()
                .build(new ChoosePhotoCallback<String>() {
                    @Override
                    public void onChoose(String photo) {
                        Glide.with(MainActivity.this)
                                .load(photo)
                                .into(imageView);
                    }
                });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoHelper.showChooser();
            }
        });
    }

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

}
```

> Kotlin
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
