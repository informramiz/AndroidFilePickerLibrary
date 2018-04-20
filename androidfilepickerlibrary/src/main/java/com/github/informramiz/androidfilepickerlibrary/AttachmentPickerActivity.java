package com.github.informramiz.androidfilepickerlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.informramiz.androidfilepickerlibrary.utils.CommonUtils;
import com.github.informramiz.androidfilepickerlibrary.utils.FileUtils;
import com.github.informramiz.androidfilepickerlibrary.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

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
    public static final int REQ_CODE_SELECT_ATTACHMENT_FOR_API_BELOW_21 = 5;
    public static final int REQ_CODE_SELECT_ATTACHMENT_BY_LIBRARY = 6;

    private Attach captureFromCameraAttachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int attachmentType = getIntent().getIntExtra(EXTRA_ATTACHMENT_TYPE, ATTACHMENT_TYPE_GALLERY);

        switch (attachmentType) {
            case ATTACHMENT_TYPE_GALLERY:
                handleGalleryAttachment();
                break;
            case ATTACHMENT_TYPE_DOC:
                handleDocAttachment();
                break;
            case ATTACHMENT_TYPE_CAPTURE_FROM_CAMERA:
                handleCameraAttachment();
                break;
            case ATTACHMENT_TYPE_PICK_AUDIO:
                handleAudioAttachment();
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
            captureFromCameraAttachment = launchCameraVideoIntent();
        } else {
            captureFromCameraAttachment = launchCameraImageIntent();
        }
    }

    private Attach launchCameraVideoIntent() {
        Intent recordVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (recordVideoIntent.resolveActivity(this.getPackageManager()) == null) {
            CommonUtils.showFeatureNotSupportedToast(this);
            return null;
        }

        File file = FileUtils.createVideoFile(this);
        // Continue only if the File was successfully created
        if (file == null) {
            CommonUtils.showMessageSafe(this, R.string.error_msg_file_creation_failed);
            return null;
        }

        Uri contentUri = FilePickerCustomFileProvider.getUriForFile(this,
                BuildConfig.FILES_AUTHORITY,
                file);

        recordVideoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (!isApiLollipopOrAbove()) {
            //there are some issues on Pre-Lollipop devices with granting uri permissions
            //so we have to use following method
            List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(recordVideoIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resolveInfoList) {
                grantUriPermission(resolveInfo.activityInfo.packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
        recordVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                contentUri);
        this.startActivityForResult(recordVideoIntent, REQ_CODE_CAPTURE_VIDEO);

        Attach attach = FilePicker.convertFileToAttachment(file);
        attach.setUri(contentUri);
        return attach;
    }

    @Nullable
    private Attach launchCameraImageIntent() {
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

        Uri contentUri = FilePickerCustomFileProvider.getUriForFile(this,
                BuildConfig.FILES_AUTHORITY,
                photoFile);

        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (!isApiLollipopOrAbove()) {
            //there are some issues on Pre-Lollipop devices with granting uri permissions
            //so we have to use following method
            List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resolveInfoList) {
                grantUriPermission(resolveInfo.activityInfo.packageName, contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                contentUri);
        this.startActivityForResult(takePictureIntent, REQ_CODE_CAPTURE_IMAGE);

        Attach attach = FilePicker.convertFileToAttachment(photoFile);
        attach.setUri(contentUri);
        return attach;
    }

    private void handleAudioAttachment() {
        if (isApiLollipopOrAbove()) {
            showFileChooser(MAIN_MIME_TYPE_AUDIO_ATTACHMENT, AUDIO_ATTACHMENT_MIME_TYPES);
        } else {
//            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getPackageManager()) == null) {
                Toast.makeText(this, R.string.error_msg_please_install_file_manager,
                        Toast.LENGTH_LONG).show();
                return;
            }
            startActivityForResult(intent, REQ_CODE_SELECT_ATTACHMENT_FOR_API_BELOW_21);
        }
    }

    private void handleGalleryAttachment() {
        if (isApiLollipopOrAbove()) {
            showFileChooser("*/*", GALLERY_ATTACHMENT_MIME_TYPES);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MAIN_MIME_TYPE_GALLERY_ATTACHMENT);
            if (intent.resolveActivity(getPackageManager()) == null) {
                Toast.makeText(this, R.string.error_msg_please_install_file_manager,
                        Toast.LENGTH_LONG).show();
                return;
            }
            startActivityForResult(intent, REQ_CODE_SELECT_ATTACHMENT_FOR_API_BELOW_21);
        }
    }

    private void handleDocAttachment() {
        if (isApiLollipopOrAbove()) {
            showFileChooser(MAIN_MIME_TYPE_DOC_ATTACHMENT, DOC_ATTACHMENT_MIME_TYPES);
        } else {
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("file/*");
//            startActivityForResult(intent, REQ_CODE_SELECT_ATTACHMENT_FOR_API_BELOW_21);
            FilePickerBuilder.getInstance()
                    .setMaxCount(1)
                    .enableCameraSupport(false)
                    .setActivityTheme(R.style.LibAppTheme)
                    .pickFile(this, REQ_CODE_SELECT_ATTACHMENT_BY_LIBRARY);
        }
    }

    private boolean isApiLollipopOrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
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

        Attach attachment = null;
        if (requestCode == REQ_CODE_CAPTURE_IMAGE
                || requestCode == REQ_CODE_CAPTURE_VIDEO) {
            //in this case we already have the attachment path and so attachment object
            FileUtils.addFileToGallery(this, captureFromCameraAttachment.getPath());
            attachment = captureFromCameraAttachment;
        } else if (requestCode == REQ_CODE_SELECT_ATTACHMENT
                || requestCode == REQ_CODE_SELECT_ATTACHMENT_FOR_API_BELOW_21) {
            attachment = onNativeFilePickerActivityResult(getApplicationContext(), data);
        } else if (requestCode == REQ_CODE_SELECT_ATTACHMENT_BY_LIBRARY) {
            attachment = onFilePickerLibraryActivityResult(data);
        }

        dataIntent.putExtra(DATA_ATTACHMENT, attachment);
        setResult(RESULT_OK, dataIntent);
        finish();
    }

    private Attach onNativeFilePickerActivityResult(Context context,
                                                    Intent data) {
        Uri uri = data.getData();
        if (isApiLollipopOrAbove()) {
            takePersistablePermissions(context, data, uri);
        }
        LogUtils.i(TAG, "Uri received from File picker: " + uri.toString());
        LogUtils.i(TAG, "Path received from File picker: " + uri.getPath());

        Attach attach = FilePicker.extractAttachInfoFromUri(context, uri);
        LogUtils.i(TAG, attach.toString());

        return attach;
    }

    private Attach onFilePickerLibraryActivityResult(Intent data) {
        ArrayList<String> paths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
        //we only allow 1 file selection at the same time
        String path = paths.get(0);
        Attach attach = FilePicker.convertFileToAttachment(path);
        return attach;
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
