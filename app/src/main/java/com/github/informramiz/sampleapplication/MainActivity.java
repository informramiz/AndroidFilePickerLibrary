package com.github.informramiz.sampleapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.informramiz.androidfilepickerlibrary.FileInfo;
import com.github.informramiz.androidfilepickerlibrary.FilePicker;
import com.github.informramiz.androidfilepickerlibrary.FilePickerCustomFileProvider;
import com.github.informramiz.androidfilepickerlibrary.utils.LogUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.video_view)
    VideoView videoView;
    @BindView(R.id.textView_uri)
    TextView uriTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_camera, R.id.button_gallery, R.id.button_audio, R.id.button_file})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_camera:
                FilePicker.showCameraPickerDialog(this);
                break;
            case R.id.button_gallery:
                FilePicker.openGalleryPicker(this);
                break;
            case R.id.button_audio:
                FilePicker.openAudioPicker(this);
                break;
            case R.id.button_file:
                FilePicker.openDocumentPicker(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        //check if this result of AndroidFilePickerLibrary
        if (FilePicker.canHandleActivityResult(requestCode)) {
            //fetch the returned object
            FileInfo fileInfo = FilePicker.onActivityResult(requestCode, resultCode, data);
            //show the UI in a TextView
            uriTextView.setText("Uri: " + fileInfo.getUri().toString());

            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            videoView.stopPlayback();

            if (fileInfo.isImage()) {
                imageView.setVisibility(View.VISIBLE);
                Picasso.get().load(fileInfo.getUri()).into(imageView);
            } else if (fileInfo.isVideo()) {
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(fileInfo.getUri());
                videoView.start();
            }
        }
        LogUtils.i(MainActivity.class.getSimpleName(), "File provider Authority: " + FilePickerCustomFileProvider.getAuthority());
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.stopPlayback();
    }
}
