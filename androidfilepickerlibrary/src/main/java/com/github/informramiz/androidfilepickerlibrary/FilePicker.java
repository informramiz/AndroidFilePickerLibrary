package com.github.informramiz.androidfilepickerlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.informramiz.androidfilepickerlibrary.utils.FileUtils;
import com.github.informramiz.androidfilepickerlibrary.utils.PermissionUtils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by ramiz on 20/04/2018
 * */

public class FilePicker {
    public static final String TAG = FilePicker.class.getSimpleName();

    //activity request request code for result for attachment selection
    public static final int REQ_CODE_CAPTURE_IMAGE = 2;
    public static final int REQ_CODE_CAPTURE_VIDEO = 3;
    public static final int REQ_CODE_PICK_AUDIO_FILE = 4;
    public static final int REQ_CODE_PICK_GALLERY = 5;
    public static final int REQ_CODE_PICK_DOC = 6;


    public static FileInfo convertFileToAttachment(String filePath) {
        return convertFileToAttachment(new File(filePath));
    }

    public static FileInfo convertFileToAttachment(File file) {
        FileInfo attachment = new FileInfo();
        attachment.setExtension(FilenameUtils.getExtension(file.getAbsolutePath()));
        attachment.setName(file.getName());
        attachment.setPath(file.getPath());
        attachment.setSize(file.length());
        attachment.setType(FileUtils.getFileType(file));
        return attachment;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Following methods are for other classes like Activities/Fragments to use
    ///////////////////////////////////////////////////////////////////////////

    private static void openCaptureImageFromCameraPicker(@NonNull Activity activity, @Nullable Fragment fragment) {
        Intent intent = new Intent(activity, AttachmentPickerActivity.class);
        intent.putExtra(AttachmentPickerActivity.EXTRA_ATTACHMENT_TYPE, AttachmentPickerActivity.ATTACHMENT_TYPE_CAPTURE_FROM_CAMERA);
        intent.putExtra(AttachmentPickerActivity.EXTRA_CAPTURE_TYPE, AttachmentPickerActivity.CAPTURE_TYPE_IMAGE);
        if (fragment != null) {
            fragment.startActivityForResult(intent, REQ_CODE_CAPTURE_IMAGE);
        } else {
            activity.startActivityForResult(intent, REQ_CODE_CAPTURE_IMAGE);
        }
    }

    public static void openCaptureImageFromCameraPicker(@NonNull Activity activity) {
        openCaptureImageFromCameraPicker(activity, null);
    }

    public static void openCaptureImageFromCameraPicker(@NonNull Fragment fragment) {
        if (fragment.getActivity() == null || !fragment.isAdded()) {
            return;
        }
        openCaptureImageFromCameraPicker(fragment.getActivity(), fragment);
    }

    private static void openCaptureVideoFromCameraPicker(@NonNull Activity activity, @Nullable Fragment fragment) {
        Intent intent = new Intent(activity, AttachmentPickerActivity.class);
        intent.putExtra(AttachmentPickerActivity.EXTRA_ATTACHMENT_TYPE, AttachmentPickerActivity.ATTACHMENT_TYPE_CAPTURE_FROM_CAMERA);
        intent.putExtra(AttachmentPickerActivity.EXTRA_CAPTURE_TYPE, AttachmentPickerActivity.CAPTURE_TYPE_VIDEO);
        if (fragment != null) {
            fragment.startActivityForResult(intent, REQ_CODE_CAPTURE_VIDEO);
        } else {
            activity.startActivityForResult(intent, REQ_CODE_CAPTURE_VIDEO);
        }
    }

    public static void showCameraPickerDialog(@NonNull Activity activity) {
        showCameraPickerDialog((Object)activity);
    }

    public static void showCameraPickerDialog(@NonNull Fragment fragment) {
        showCameraPickerDialog((Object)fragment);
    }

    private static void showCameraPickerDialog(@NonNull Object object) {
        Fragment fragment = null;
        Activity activity = null;
        if (object instanceof Fragment) {
            fragment = (Fragment)object;
            activity = fragment.getActivity();
        } else {
            activity = (Activity)object;
        }

        final Fragment finalFragment = fragment;
        final Activity finalActivity = activity;
        CameraAttachmentPickerDialog cameraAttachmentPickerDialog = new CameraAttachmentPickerDialog(finalActivity);
        cameraAttachmentPickerDialog.setOnOptionSelectedListener(new CameraAttachmentPickerDialog.OnOptionSelectedListener() {
            @Override
            public void onImageOptionSelected() {
                openCaptureImageFromCameraPicker(finalActivity, finalFragment);
            }

            @Override
            public void onVideoOptionSelected() {
                openCaptureVideoFromCameraPicker(finalActivity, finalFragment);
            }
        });
        cameraAttachmentPickerDialog.showCameraDialog();
    }

    public static void openCaptureVideoFromCameraPicker(@NonNull Activity activity) {
        openCaptureVideoFromCameraPicker(activity, null);
    }

    public static void openCaptureVideoFromCameraPicker(@NonNull Fragment fragment) {
        if (fragment.getActivity() == null || !fragment.isAdded()) {
            return;
        }
        openCaptureVideoFromCameraPicker(fragment.getActivity(), fragment);
    }

    private static void openGalleryPicker(@NonNull Activity activity, @Nullable Fragment fragment) {
        Intent intent = new Intent(activity, AttachmentPickerActivity.class);
        intent.putExtra(AttachmentPickerActivity.EXTRA_ATTACHMENT_TYPE, AttachmentPickerActivity.ATTACHMENT_TYPE_GALLERY);
        if (fragment != null) {
            fragment.startActivityForResult(intent, REQ_CODE_PICK_GALLERY);
        } else {
            activity.startActivityForResult(intent, REQ_CODE_PICK_GALLERY);
        }
    }

    public static void openGalleryPicker(@NonNull Activity activity) {
        openGalleryPicker(activity, null);
    }

    public static void openGalleryPicker(@NonNull Fragment fragment) {
        if (fragment.getActivity() == null || !fragment.isAdded()) {
            return;
        }
        openGalleryPicker(fragment.getActivity(), fragment);
    }

    private static void openDocumentPicker(@NonNull Activity activity, @Nullable Fragment fragment) {
        Intent intent = new Intent(activity, AttachmentPickerActivity.class);
        intent.putExtra(AttachmentPickerActivity.EXTRA_ATTACHMENT_TYPE, AttachmentPickerActivity.ATTACHMENT_TYPE_DOC);
        if (fragment != null) {
            fragment.startActivityForResult(intent, REQ_CODE_PICK_DOC);
        } else {
            activity.startActivityForResult(intent, REQ_CODE_PICK_DOC);
        }
    }

    public static void openDocumentPicker(@NonNull Activity activity) {
        openDocumentPicker(activity, null);
    }

    public static void openDocumentPicker(@NonNull Fragment fragment) {
        if (fragment.getActivity() == null || !fragment.isAdded()) {
            return;
        }
        openDocumentPicker(fragment.getActivity(), fragment);
    }

    private static void openAudioPicker(@NonNull Activity activity, @Nullable Fragment fragment) {
        Intent intent = new Intent(activity, AttachmentPickerActivity.class);
        intent.putExtra(AttachmentPickerActivity.EXTRA_ATTACHMENT_TYPE, AttachmentPickerActivity.ATTACHMENT_TYPE_PICK_AUDIO);
        if (fragment != null) {
            fragment.startActivityForResult(intent, REQ_CODE_PICK_AUDIO_FILE);
        } else {
            activity.startActivityForResult(intent, REQ_CODE_PICK_AUDIO_FILE);
        }
    }

    public static void openAudioPicker(@NonNull Activity activity) {
        openAudioPicker(activity, null);
    }

    public static void openAudioPicker(@NonNull Fragment fragment) {
        if (fragment.getActivity() == null || !fragment.isAdded()) {
            return;
        }
        openAudioPicker(fragment.getActivity(), fragment);
    }

    public static FileInfo onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return null;
        }

        FileInfo attachment = null;
        if (requestCode == REQ_CODE_CAPTURE_IMAGE
                || requestCode == REQ_CODE_CAPTURE_VIDEO
                || requestCode == REQ_CODE_PICK_GALLERY
                || requestCode == REQ_CODE_PICK_DOC
                || requestCode == REQ_CODE_PICK_AUDIO_FILE) {
            attachment = data.getParcelableExtra(AttachmentPickerActivity.DATA_ATTACHMENT);
        }

        return attachment;
    }

    public static boolean canHandleActivityResult(int requestCode) {
        if (requestCode == REQ_CODE_CAPTURE_IMAGE
                || requestCode == REQ_CODE_CAPTURE_VIDEO
                || requestCode == REQ_CODE_PICK_GALLERY
                || requestCode == REQ_CODE_PICK_DOC
                || requestCode == REQ_CODE_PICK_AUDIO_FILE) {
            return true;
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static FileInfo extractAttachInfoFromUri(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null
                , null, null);

        FileInfo fileInfo = new FileInfo();
        if (PermissionUtils.checkStoragePermissions(context)) {
            fileInfo.setPath(FileUtils.getPath(context, uri));
        }
        fileInfo.setUri(uri);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                fileInfo.setName(name);

                String mimeType = cursor.getString(cursor.getColumnIndex("mime_type"));
                if (mimeType == null) {
                    mimeType = context.getContentResolver().getType(uri);
                }
                fileInfo.setType(mimeType);

                String extension = FileUtils.getExtensionFromMimeType(mimeType);
                fileInfo.setExtension(extension);

                int sizeColIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                Long size = null;
                if (!cursor.isNull(sizeColIndex)) {
                    size = (long) cursor.getInt(sizeColIndex);
                }
                fileInfo.setSize(size);
            } else if (fileInfo.getPath() != null) {
                fileInfo = FilePicker.convertFileToAttachment(fileInfo.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return fileInfo;
    }
}
