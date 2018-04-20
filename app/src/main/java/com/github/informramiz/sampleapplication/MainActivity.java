package com.github.informramiz.sampleapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.informramiz.androidfilepickerlibrary.Attach;
import com.github.informramiz.androidfilepickerlibrary.FilePicker;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.image_view)
    ImageView imageView;
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

        Attach attach = FilePicker.onActivityResult(requestCode, resultCode, data);
        uriTextView.setText("Uri: " + attach.getUri().toString());
        if (attach.isImage()) {
            imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(attach.getUri()).into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }
}
