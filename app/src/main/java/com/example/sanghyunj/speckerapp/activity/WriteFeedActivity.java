package com.example.sanghyunj.speckerapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanghyunj.speckerapp.R;
import com.example.sanghyunj.speckerapp.retrofit.Body.AddMarker;
import com.example.sanghyunj.speckerapp.retrofit.DefaultAsyncTask;
import com.example.sanghyunj.speckerapp.retrofit.DefaultResponse;
import com.example.sanghyunj.speckerapp.retrofit.FeedService;
import com.example.sanghyunj.speckerapp.retrofit.Html;
import com.example.sanghyunj.speckerapp.retrofit.RetrofitFactory;
import com.example.sanghyunj.speckerapp.retrofit.SendFeedData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import jp.wasabeef.richeditor.RichEditor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by sanghyunj on 15/03/2017.
 */
public class WriteFeedActivity extends AppCompatActivity {

    private TedPermission ted;

    private RichEditor mEditor;
    private TextView mPreview;

    private FirebaseAuth mFirebaseAuth;
    // private String token = null;

    private final int ACQUIRE_FROM_GALLERY_CODE = 400;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_feed);

        ted = new TedPermission(getApplicationContext());

        ted.setPermissionListener(new PermissionListener() {
            @Override  public void onPermissionGranted() { Toast.makeText(WriteFeedActivity.this, "Permission_Granted::READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show(); }
            @Override public void onPermissionDenied(ArrayList<String> deniedPermissions) { Toast.makeText(WriteFeedActivity.this, "Permission_Denied::"+deniedPermissions, Toast.LENGTH_SHORT).show(); }
        })
                .setRationaleMessage("이미지를 불러오기 위하여 권한이 필요합니다.")
                .setDeniedMessage("[설정] > [권한]에서 설정할 수 있습니다.")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        mEditor = (RichEditor) findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Insert text here...");

        mPreview = (TextView) findViewById(R.id.preview);
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnPost).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mFirebaseAuth.getCurrentUser().getToken(true)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            @Override
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    String token = task.getResult().getToken();
                                    String markup = mPreview.getText().toString();
                                    FeedService feedService = FeedService.retrofit.create(FeedService.class);
                                    Html html = new Html(markup);
                                    // final Call<DefaultResponse> call = feedService.sendFeed(token, markup,)
                                    final Call<DefaultResponse> call = feedService.sendFeed(token, new SendFeedData(html));
                                    // new DefaultAsyncTask<DefaultResponse>(getApplicationContext()).execute(call);
                                    // String[] data = markup.split(" ");
                                    // RetrofitFactory retrofitFactory = RetrofitFactory.instance.create(RetrofitFactory.class);
                                    // final Call<DefaultResponse> call = retrofitFactory.addMarker(token, new AddMarker(data[0], Double.parseDouble(data[1]), Double.parseDouble(data[2])));
                                    new DefaultAsyncTask<DefaultResponse>(getApplicationContext()).execute(call);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "다시 시도해보세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.setNumbers();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG", "dachshund");

                acquireImagesFromGallery();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });
        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mEditor.insertTodo();
            }
        });
    }

    private void acquireImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select image"), ACQUIRE_FROM_GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ACQUIRE_FROM_GALLERY_CODE:

                    mEditor.insertImage(data.getDataString(), "");
                    Log.d("DATASTRING", data.getDataString());

                    mFirebaseAuth.getCurrentUser().getToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                @Override
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        String token = task.getResult().getToken();
                                        /*
                                        FeedService feedService = FeedService.retrofit.create(FeedService.class);

                                        File imageFile = new File(data.getDataString());
                                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
                                        MultipartBody.Part body = MultipartBody.Part.createFormData("upload", imageFile.getName(), requestFile);

                                        final Call<DefaultResponse> call = feedService.sendImage(token, body);
                                        new DefaultAsyncTask<DefaultResponse>(getApplicationContext()).execute(call);
                                        */
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "다시 시도해보세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    // String image = loadImageFromURI(data);
                    // mEditor.insertImage(image.replaceAll("\r\n", "").replaceAll("\n", ""), "");
                    // mEditor.insertImage(loadImageFromURI(data), "");
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Can't access to gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String loadImageFromURI(Intent intent) {
        Uri imageURI = intent.getData();
        String imagePath = convertUriToPath(imageURI);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        String format = getContentResolver().getType(imageURI).toLowerCase();
        String tag = "data:" + format + ";base64, ";
        switch (format) {
            case "image/jpg": case "image/jpeg":
                return tag + encodeImageToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
            case "image/png":
                return tag + encodeImageToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
            default:
                return tag + encodeImageToBase64(bitmap, Bitmap.CompressFormat.WEBP, 100);
        }
    }

    private String convertUriToPath(Uri uri) {
        String[] project = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, project, null, null, null);
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }

    private String encodeImageToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        // return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);
    }
}