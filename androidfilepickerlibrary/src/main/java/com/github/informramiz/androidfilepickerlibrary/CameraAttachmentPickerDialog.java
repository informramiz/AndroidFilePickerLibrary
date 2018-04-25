package com.github.informramiz.androidfilepickerlibrary;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by ramiz on 20/04/2018
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class CameraAttachmentPickerDialog {
    private AlertDialog.Builder builder;
    @Nullable
    private OnOptionSelectedListener onOptionSelectedListener;

    public CameraAttachmentPickerDialog(@NonNull Context context) {
        builder = new AlertDialog.Builder(context);
    }

    public void setTitle(String title) {
        builder.setTitle(title);
    }

    public void showCameraDialog() {
        String[] choices = new String[2];
        choices[0] = builder.getContext().getResources().getString(R.string.file_picker_take_picture);
        choices[1] = builder.getContext().getResources().getString(R.string.file_picker_record_video);
        builder.setItems(choices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onOptionSelectedListener == null) {
                    return;
                }

                switch (which) {
                    case 0: //image
                        onOptionSelectedListener.onImageOptionSelected();
                        break;
                    case 1: //video
                        onOptionSelectedListener.onVideoOptionSelected();
                        break;
                }
            }
        });
        builder.show();
    }

    public void setOnOptionSelectedListener(@Nullable OnOptionSelectedListener onOptionSelectedListener) {
        this.onOptionSelectedListener = onOptionSelectedListener;
    }

    public interface OnOptionSelectedListener {
        void onImageOptionSelected();
        void onVideoOptionSelected();
    }
}
