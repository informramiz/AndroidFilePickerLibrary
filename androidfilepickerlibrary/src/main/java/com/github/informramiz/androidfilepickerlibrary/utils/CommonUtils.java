package com.github.informramiz.androidfilepickerlibrary.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import android.widget.Toast;

import com.github.informramiz.androidfilepickerlibrary.R;

/**
 * Created by Ramiz Raja on 20/04/2018.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class CommonUtils {
    public static void showFeatureNotSupportedToast(@NonNull Context context) {
        Toast.makeText(context, R.string.error_feature_not_supported, Toast.LENGTH_LONG).show();
    }

    public static void showMessageSafe(@Nullable Context context, String text) {
        if (context == null) {
            return;
        }

        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showMessageSafe(@Nullable Context context, @StringRes int resId) {
        if (context == null) {
            return;
        }

        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }
}
