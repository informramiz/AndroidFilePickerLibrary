package com.github.informramiz.androidfilepickerlibrary.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import com.github.informramiz.androidfilepickerlibrary.Attach;
import com.github.informramiz.androidfilepickerlibrary.BuildConfig;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ramiz on 20/04/2018
 * Reference: https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
 */

public class FileUtils {
    /** TAG for log messages. */
    private static final String TAG = "FileUtils";
    private static final boolean DEBUG = false; // Set to true to enable logging

    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_APK = "apk/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";
    public static final String MIME_TYPE_PDF = "application/pdf";
    public static final String MIME_TYPE_PPT = "application/ppt";
    public static final String MIME_TYPE_DOC = "application/doc";
    public static final String MIME_TYPE_XLS = "application/xls";
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String FILES_PREFIX = "filePicker_";
    public static final String MEDIA_FOLDER = "pickerMedia";
    public static final String IMAGE_EXTENSION = ".jpg";
    public static final String VIDEO_EXTENSION = ".mp4";

    //Doc file extensions
    public static final String[] DOC_FILE_EXTENTSIONS = {"pdf", "doc", "ppt", "xls", "txt"};
    public static final String[] AUDIO_FILE_EXTENTSIONS = {"mp3"};

    public static final String HIDDEN_PREFIX = ".";

    public static String encodeFileToBase64Binary(File file) {
        String encodedFile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);
            fileInputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedFile;
    }

    public static String encodeFileToBase64Binary(ContentResolver contentResolver, Uri fileUri) {
        String encodedFile = null;
        try {
            InputStream fileInputStreamReader = contentResolver.openInputStream(fileUri);
            byte[] bytes = new byte[(int) fileInputStreamReader.available()];
            fileInputStreamReader.read(bytes);
            encodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);
            fileInputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedFile;
    }

    public static byte[] readFileBytesFromUri(ContentResolver contentResolver, Uri fileUri) {
        try {
            InputStream inputStream = contentResolver.openInputStream(fileUri);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] bytes = new byte[(int) bufferedInputStream.available()];
            bufferedInputStream.read(bytes);
            inputStream.close();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFileFromUri(ContentResolver contentResolver, Uri fileUri) {
        try {
            InputStream inputStream = contentResolver.openInputStream(fileUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStream.close();
            reader.close();

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     *         null if uri was null.
     */
    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    public static String getExtensionFromMimeType(String mimeType) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
    }

    public static String getMainFileType(String mimeType) {
        String type;
        if (mimeType.contains("image")) {
            type = "image";
        } else if (mimeType.contains("video")) {
            type = "video";
        } else if (mimeType.contains("audio")) {
            type = "audio";
        } else if (mimeType.contains("text")) {
            type = "text";
        } else {
            type = "application";
        }
        return type;
    }

    /**
     * @return Whether the URI is a local one.
     */
    public static boolean isLocal(String url) {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            return true;
        }
        return false;
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     */
    public static boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    /**
     * Returns the path only (without file name).
     *
     * @param file
     * @return
     */
    public static File getPathWithoutFilename(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                // no file to be split off. Return everything
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0,
                        filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    /**
     * @return The MIME type for the given file.
     */
    public static String getMimeType(File file) {

        String extension = getExtension(file.getName());

        if (extension.length() > 0)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));

        return "application/octet-stream";
    }

    /**
     * @return The MIME type for the give Uri.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getMimeType(Context context, Uri uri) {
        File file = new File(getPath(context, uri));
        return getMimeType(file);
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is {BuildConfig.FILES_AUTHORITY}.
     */
    public static boolean isLocalStorageDocument(Uri uri) {
        return BuildConfig.FILES_AUTHORITY.equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.Images.Media.DATA;
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @see #isLocal(String)
     * @see #getFile(Context, Uri)
     */
    public static String getPath(final Context context, final Uri uri) {

        if (DEBUG)
            LogUtils.i(TAG + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return getPathKitkatAndAbove(context, uri);
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            else if (isGoogleDriveUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String getPathKitkatAndAbove(final Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                uri.getPath();
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                LogUtils.i(TAG, "id=" + id);
                if (id.contains("raw:")) {
                    return id.replace("raw:", "");
                }

                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        return null;
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     *         Uri is unsupported or pointed to a remote resource.
     * @see #getPath(Context, Uri)
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static File getFile(Context context, Uri uri) {
        if (uri != null) {
            String path = getPath(context, uri);
            if (path != null && isLocal(path)) {
                return new File(path);
            }
        }
        return null;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isDocFile(@NonNull String extension, @Nullable String mimeType) {
        if (isImageAttachment(extension, mimeType)
                || isVideoAttachment(extension, mimeType)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isDocFile(@NonNull String fileName) {
        return FilenameUtils.isExtension(fileName, DOC_FILE_EXTENTSIONS);
    }

    public static boolean isDocFile(@NonNull Attach attach) {
        return isDocFile(attach.getName());
    }

    public static boolean isAudioFile(@NonNull String fileName) {
        return FilenameUtils.isExtension(fileName, AUDIO_FILE_EXTENTSIONS);
    }

    public static boolean isAudioFile(@NonNull Attach attach) {
        return isAudioFile(attach.getName()) || attach.getType().contains("audio");
    }

    public static boolean isImageAttachment(@NonNull String extension, @Nullable String mimeType) {
        if (mimeType == null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoAttachment(@NonNull String extension, @Nullable String mimeType) {
        if (mimeType == null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return mimeType != null && mimeType.startsWith("video");
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");

    }

    public static String getFileExtension(String path) {
        return FilenameUtils.getExtension(path);
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static String getFileType(File file) {
        String mimeType = null;
        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        if (extension != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        String type = "image";
        if (mimeType != null) {
            if (mimeType.contains("image")) {
                type = "image";
            } else if (mimeType.contains("video")) {
                type = "video";
            } else {
                type = "application";
            }
        }
        return type;

    }

    @Nullable
    static File createImageFile() {
        // Create an image file name
        String fileNamePrefix = getFileNamePrefix();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + MEDIA_FOLDER);
        boolean isDirectoryPresent = true;
        if (!storageDir.exists()) {
            try {
                isDirectoryPresent = storageDir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!isDirectoryPresent) {
            return null;
        }

        File imageFile = null;
        try {
            imageFile = File.createTempFile(
                    fileNamePrefix,  /* prefix */
                    IMAGE_EXTENSION,         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile;
    }

    static File createVideoFile() {
        String fileNamePrefix = getFileNamePrefix();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/" + MEDIA_FOLDER);
        boolean isDirectoryPresent = true;
        if (!storageDir.exists()) {
            isDirectoryPresent = storageDir.mkdir();
        }
        if (!isDirectoryPresent) {
            return null;
        }

        File videoFile = null;
        try {
            videoFile = File.createTempFile(
                    fileNamePrefix,  /* prefix */
                    VIDEO_EXTENSION,         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return videoFile;
    }

    @NonNull
    private static String getFileNamePrefix() {
        String timeStamp = new SimpleDateFormat(DATE_FORMAT, Locale.US).format(new Date());
        return FILES_PREFIX + timeStamp + "_";
    }

    public static void addFileToGallery(@NonNull Activity activity, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri contentUri = FileProvider.getUriForFile(activity,
                BuildConfig.FILES_AUTHORITY,
                file);
        mediaScanIntent.setData(contentUri);
        mediaScanIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.getApplicationContext().sendBroadcast(mediaScanIntent);
    }

    public static void addFileToGallery(@NonNull Activity activity, Uri contentUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        mediaScanIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.getApplicationContext().sendBroadcast(mediaScanIntent);
    }
}
