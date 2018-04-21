package com.github.informramiz.androidfilepickerlibrary;

import android.content.Context;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by ramiz on 20/04/2018
 */
public class FilePickerCustomFileProvider extends FileProvider {
    @NonNull
    private static ProviderInfo providerInfo;

    @Override
    public void attachInfo(@NonNull Context context, @NonNull ProviderInfo info) {
        super.attachInfo(context, info);

        //reference: https://medium.com/@andretietz/auto-initialize-your-android-library-2349daf06920
        // So if the authority has libraries package name as prefix
        // then then the developer forgot to set his applicationId
        String wrongFilesAuthority1 = BuildConfig.APPLICATION_ID + "." +  BuildConfig.FILES_AUTHORITY_SUFFIX;
        String wrongFilesAuthority2 = "null." + BuildConfig.FILES_AUTHORITY_SUFFIX;
        if (wrongFilesAuthority1.equals(info.authority) || wrongFilesAuthority2.equals(info.authority)) {
            throw new IllegalStateException("Incorrect provider authority in manifest. Most likely due to a "
                    + "missing applicationId variable in application\'s build.gradle.");
        }

        providerInfo = info;
    }

    public static String getAuthority() {
        return providerInfo.authority;
    }

    public static Uri getUriForFile(@NonNull Context context, @NonNull File file) {
        return getUriForFile(context, getAuthority(), file);
    }
}
