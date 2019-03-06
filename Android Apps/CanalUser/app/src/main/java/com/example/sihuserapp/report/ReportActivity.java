package com.example.sihuserapp.report;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sihuserapp.ApiUtils;
import com.example.sihuserapp.Interfaces.UploadImageInterface;
import com.example.sihuserapp.Objects.UploadResponse;
import com.example.sihuserapp.R;
import com.example.sihuserapp.UploadUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportActivity extends AppCompatActivity {


    // Log tag that is used to distinguish log info.
    private final static String TAG_BROWSE_PICTURE = "BROWSE_PICTURE";
    private final static String TAG = "TAG ReportActivity";
    // Used when request action Intent.ACTION_GET_CONTENT
    private final static int REQUEST_CODE_BROWSE_PICTURE = 1;
    private final static int REQUEST_PERMISSION_READ_EXTERNAL = 2;
    private final static int PICK_IMAGE_MULTIPLE = 1;
    private static final int REQUEST_TAKE_PHOTO = 3;
    private static final int REQUEST_VIDEO_CAPTURE = 4;

    UploadImageInterface mAPIService;
    String imageEncoded;
    List<String> imagesEncodedList;
    List<Uri> mImageArrayUri;
    List<Uri> mVideoArrayUri;
    String currentPhotoPath;
    Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initializeVariables();
    }

    private void initializeVariables() {

        Intent intent = getIntent();
        id = (Integer) Objects.requireNonNull(intent.getExtras()).get("id");
        Toast.makeText(getApplicationContext(), id.toString(), Toast.LENGTH_SHORT).show();
        mAPIService = UploadUtils.getAPIService();

        FloatingActionButton addFromGallery = findViewById(R.id.fab_gallery);
        addFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int readExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    String requirePermission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(ReportActivity.this, requirePermission, REQUEST_PERMISSION_READ_EXTERNAL);
                }
                if (readExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please Grant File Access Permissions",
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });
        FloatingActionButton addFromCamImage = findViewById(R.id.fab_camera);
        addFromCamImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        FloatingActionButton addFromCamVideo = findViewById(R.id.fab_video);
        addFromCamVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });

        RecyclerView recycler = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),
                2,
                LinearLayoutManager.HORIZONTAL,
                false);
        recycler.setLayoutManager(gridLayoutManager);

        Button upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToServer();
            }
        });
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(getApplicationContext(), "Error occurred while creating the File", Toast.LENGTH_SHORT);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //for gallery
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                //If empty then create new, else append
                if (imagesEncodedList == null) imagesEncodedList = new ArrayList<>();
                if (mImageArrayUri == null) mImageArrayUri = new ArrayList<>();
                if (mVideoArrayUri == null) mVideoArrayUri = new ArrayList<>();
                if (data.getData() != null) {
                    Uri mUri = data.getData();

                    Uri mImageUri = mUri;
                    mImageArrayUri.add(mImageUri);
                    //Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                    //image.setImageBitmap(bitmapImage);
                    Log.v("LOG_TAG", mImageUri.getEncodedPath());
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    imagesEncodedList.add(imageEncoded);
                    cursor.close();
                    Toast.makeText(getApplicationContext(), "There are "
                                    + mImageArrayUri.size() + " images and " + mVideoArrayUri.size() + " videos"
                            , Toast.LENGTH_SHORT).show();
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mImageArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();
                        }
                        Log.v("LOG_TAG", "Selected Images" + mImageArrayUri.size());
                        Toast.makeText(getApplicationContext(), "There are "
                                        + mImageArrayUri.size() + " images and " + mVideoArrayUri.size() + " videos"
                                , Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == PICK_IMAGE_MULTIPLE) {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong, Please Try Again", Toast.LENGTH_SHORT)
                    .show();
        }

        try {
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
                File f = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                if (mImageArrayUri == null) mImageArrayUri = new ArrayList<>();
                if (mVideoArrayUri == null) mVideoArrayUri = new ArrayList<>();
                mImageArrayUri.add(contentUri);
                Toast.makeText(getApplicationContext(), "There are "
                                + mImageArrayUri.size() + " images and " + mVideoArrayUri.size() + " videos"
                        , Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Image Capture Failure", Toast.LENGTH_SHORT)
                    .show();
            Log.v("Log_capture", e.toString());
        }

        try {
            if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
                Uri videoUri = data.getData();
                Log.v(TAG,videoUri.getEncodedPath());
                if (mVideoArrayUri == null) mVideoArrayUri = new ArrayList<>();
                if (mImageArrayUri == null) mImageArrayUri = new ArrayList<>();
                mVideoArrayUri.add(videoUri);
                Toast.makeText(getApplicationContext(), "There are "
                                + mImageArrayUri.size() + " images and " + mVideoArrayUri.size() + " videos"
                        , Toast.LENGTH_SHORT).show();
                //videoView.setVideoURI(videoUri);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Video Capture Failure", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void sendToServer() {
        if (mImageArrayUri != null) {
            for (int i = 0; i < mImageArrayUri.size(); i++) {
                try {
                    String filePath = getRealPathFromURIPath(mImageArrayUri.get(i), ReportActivity.this);
                    File file = new File(filePath);
                    Log.d(TAG, "Filename " + file.getName());
                    //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                    RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                    RequestBody imageid = RequestBody.create(MediaType.parse("text/plain"), id.toString());


                    mAPIService.uploadFile(fileToUpload, imageid)
                            .enqueue(new Callback<UploadResponse>() {
                                @Override
                                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(ReportActivity.this, "An image was uploaded", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(ReportActivity.this, "Success" + response.body().getData(), Toast.LENGTH_LONG).show();

                                    }
                                }

                                @Override
                                public void onFailure(Call<UploadResponse> call, Throwable t) {
                                    Log.d(TAG, "Error " + t.getMessage());
                                }
                            });
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Some error occured!!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());

                }

            }
        }
        if(mVideoArrayUri!=null){
            for(int i=0; i<mVideoArrayUri.size();i++){
                String pathToStoredVideo = getRealVideoPathFromURIPath(mVideoArrayUri.get(i), ReportActivity.this);
                Log.d(TAG, "Recorded Video Path " + pathToStoredVideo);
                //Store the video to your server
                uploadVideoToServer(pathToStoredVideo);

            }
        }


    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    private void uploadVideoToServer(String pathToVideoFile){
        File videoFile = new File(pathToVideoFile);
        RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
        MultipartBody.Part vFile = MultipartBody.Part.createFormData("file", videoFile.getName(), videoBody);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        UploadImageInterface vInterface = retrofit.create(UploadImageInterface.class);
        RequestBody imageid = RequestBody.create(MediaType.parse("text/plain"), id.toString());

        Call<UploadResponse>  serverCom = vInterface.uploadFile(vFile,imageid);
        serverCom.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if(response.isSuccessful()){
                    UploadResponse result = response.body();
                    if(!TextUtils.isEmpty(result.getData())){
                        Toast.makeText(ReportActivity.this, "Result " + result.getData(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Result " + result.getData());
                    }
                }

            }
            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.d(TAG, "Error message " + t.getMessage());
            }
        });
    }

    private String getRealVideoPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
}
