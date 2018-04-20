package com.github.informramiz.androidfilepickerlibrary;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

/**
 * Created by ramiz on 20/04/2018
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class PermissionUtils {
    public static final String[] ATTACHMENT_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static boolean checkStoragePermissions(@NonNull Activity activity) {
        return areAllPermissionsGranted(activity, ATTACHMENT_PERMISSIONS);
    }

    public static void requestStoragePermissions(@NonNull Activity activity, final int requestCode) {
        ActivityCompat.requestPermissions(activity, ATTACHMENT_PERMISSIONS, requestCode);
    }

    public static void requestStoragePermissions(@NonNull Fragment fragment, final int requestCode) {
        fragment.requestPermissions(ATTACHMENT_PERMISSIONS, requestCode);
    }

    private static boolean areAllPermissionsGranted(Context context, String[] permissions) {
        for (String smsPermission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, smsPermission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static void requestPermissions(Object obj, int requestCode, String[] permissions) {
        if (obj instanceof Activity) {
            Activity activity = (Activity)obj;
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else if (obj instanceof Fragment) {
            Fragment fragment = (Fragment)obj;
            fragment.requestPermissions(permissions, requestCode);
        }
    }
}
