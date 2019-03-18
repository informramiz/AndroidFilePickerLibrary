package com.github.informramiz.androidfilepickerlibrary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import com.github.informramiz.androidfilepickerlibrary.utils.CommonUtils;
import com.github.informramiz.androidfilepickerlibrary.utils.FileUtils;
import com.github.informramiz.androidfilepickerlibrary.utils.LogUtils;

import java.io.File;

/**
 * Created by ramiz on 20/04/2018
 */
public class AttachmentPickerActivity extends AppCompatActivity {
    private static final String TAG = AttachmentPickerActivity.class.getSimpleName();

    private static final String[] GALLERY_ATTACHMENT_MIME_TYPES = {
            FileUtils.MIME_TYPE_IMAGE,
            FileUtils.MIME_TYPE_VIDEO
    };

    private static final String[] DOC_ATTACHMENT_MIME_TYPES = {
            FileUtils.MIME_TYPE_TEXT,
            FileUtils.MIME_TYPE_APP
    };

    private static final String[] AUDIO_ATTACHMENT_MIME_TYPES = {
            FileUtils.MIME_TYPE_AUDIO
    };

    private static final String MAIN_MIME_TYPE_GALLERY_ATTACHMENT = "video/*,image/*";
    private static final String MAIN_MIME_TYPE_DOC_ATTACHMENT = "*/*";
//    private static final String MAIN_MIME_TYPE_AUDIO_ATTACHMENT = "audio/*";
    private static final String MAIN_MIME_TYPE_AUDIO_ATTACHMENT = "*/*";

    public static final String DATA_ATTACHMENT = "attachment";
    public static final String EXTRA_ATTACHMENT_TYPE = "attachmentType";
    public static final int ATTACHMENT_TYPE_GALLERY = 0;
    public static final int ATTACHMENT_TYPE_DOC = 1;
    public static final int ATTACHMENT_TYPE_CAPTURE_FROM_CAMERA = 2;
    public static final int ATTACHMENT_TYPE_PICK_AUDIO = 3;
    public static final String EXTRA_CAPTURE_TYPE = "capture_type";
    public static final int CAPTURE_TYPE_IMAGE = 0;
    public static final int CAPTURE_TYPE_VIDEO = 1;

    public static final int REQ_CODE_CAPTURE_IMAGE = 2;
    public static final int REQ_CODE_CAPTURE_VIDEO = 3;
    public static final int REQ_CODE_SELECT_ATTACHMENT = 4;

    private FileInfo captureFromCameraAttachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int attachmentType = getIntent().getIntExtra(EXTRA_ATTACHMENT_TYPE, ATTACHMENT_TYPE_GALLERY);

        switch (attachmentType) {
            case ATTACHMENT_TYPE_GALLERY:
                showFileChooser("*/*", GALLERY_ATTACHMENT_MIME_TYPES);
                break;
            case ATTACHMENT_TYPE_DOC:
                showFileChooser(MAIN_MIME_TYPE_DOC_ATTACHMENT, DOC_ATTACHMENT_MIME_TYPES);
                break;
            case ATTACHMENT_TYPE_CAPTURE_FROM_CAMERA:
                handleCameraAttachment();
                break;
            case ATTACHMENT_TYPE_PICK_AUDIO:
                showFileChooser(MAIN_MIME_TYPE_AUDIO_ATTACHMENT, AUDIO_ATTACHMENT_MIME_TYPES);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showFileChooser(@NonNull String mainMimeType, @Nullable String[] mimeTypes) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType(mainMimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        if (mimeTypes != null) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }

        startActivityForResult(intent, REQ_CODE_SELECT_ATTACHMENT);
    }

    private void handleCameraAttachment() {
        int captureType = getIntent().getIntExtra(EXTRA_CAPTURE_TYPE, CAPTURE_TYPE_IMAGE);
        if (captureType == CAPTURE_TYPE_VIDEO) {
            launchCameraVideoIntent();
        } else {
            captureFromCameraAttachment = launchCameraImageIntent();
        }
    }

    private void launchCameraVideoIntent() {
        Intent recordVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (recordVideoIntent.resolveActivity(this.getPackageManager()) != null) {
            this.startActivityForResult(recordVideoIntent, REQ_CODE_CAPTURE_VIDEO);
        } else {
            CommonUtils.showFeatureNotSupportedToast(this);
        }
    }

    @Nullable
    private FileInfo launchCameraImageIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) == null) {
            CommonUtils.showFeatureNotSupportedToast(this);
            return null;
        }

        File photoFile = FileUtils.createImageFile(this);
        // Continue only if the File was successfully created
        if (photoFile == null) {
            CommonUtils.showMessageSafe(this, R.string.error_msg_file_creation_failed);
            return null;
        }

        Uri contentUri = FilePickerCustomFileProvider.getUriForFile(this, photoFile);

        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                contentUri);
        this.startActivityForResult(takePictureIntent, REQ_CODE_CAPTURE_IMAGE);

        FileInfo fileInfo = FilePicker.convertFileToAttachment(photoFile);
        fileInfo.setUri(contentUri);
        return fileInfo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Intent dataIntent = new Intent();
        if (resultCode != RESULT_OK) {
            setResult(resultCode, dataIntent);
            finish();
            return;
        }

        FileInfo attachment = null;
        if (requestCode == REQ_CODE_CAPTURE_IMAGE) {
            //in this case we already have the attachment path and so attachment object
            FileUtils.addFileToGallery(this, captureFromCameraAttachment.getPath());
            attachment = captureFromCameraAttachment;
        } else if (requestCode == REQ_CODE_CAPTURE_VIDEO) {
            attachment = FilePicker.extractAttachInfoFromUri(getApplicationContext(), data.getData());
        }
        else if (requestCode == REQ_CODE_SELECT_ATTACHMENT) {
            attachment = extractFileInfo(getApplicationContext(), data);
        }

        dataIntent.putExtra(DATA_ATTACHMENT, attachment);
        setResult(RESULT_OK, dataIntent);
        finish();
    }

    private FileInfo extractFileInfo(Context context,
                                     Intent data) {
        Uri uri = data.getData();
        takePersistablePermissions(context, data, uri);
        LogUtils.i(TAG, "Uri received from File picker: " + uri.toString());
        LogUtils.i(TAG, "Path received from File picker: " + uri.getPath());

        FileInfo fileInfo = FilePicker.extractAttachInfoFromUri(context, uri);
        LogUtils.i(TAG, fileInfo.toString());

        return fileInfo;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void takePersistablePermissions(Context context, Intent data, Uri uri) {
        try {

            int flags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.getContentResolver().takePersistableUriPermission(uri, flags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
