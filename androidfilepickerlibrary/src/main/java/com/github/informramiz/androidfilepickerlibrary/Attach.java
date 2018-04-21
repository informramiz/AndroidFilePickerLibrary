package com.github.informramiz.androidfilepickerlibrary;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.github.informramiz.androidfilepickerlibrary.utils.FileUtils;

import java.util.Locale;

/**
 * * Created by ramiz on 20/04/2018
 */
public class Attach implements Parcelable {

    public static final Creator<Attach> CREATOR = new Creator<Attach>() {
        @Override
        public Attach createFromParcel(Parcel in) {
            return new Attach(in);
        }

        @Override
        public Attach[] newArray(int size) {
            return new Attach[size];
        }
    };

    /**
     * This field is only set if app has storage permissions otherwise
     * it is always null so your app should not rely on this field and
     * should only use `uri` field.
     */
    @Nullable
    private String path;
    private String name;
    private String type;
    private String extension;
    private Long size;
    private String uri;

    public Attach() {

    }

    protected Attach(Parcel in) {
        path = in.readString();
        name = in.readString();
        type = in.readString();
        extension = in.readString();
        size = in.readLong();
        uri = in.readString();
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Nullable
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        if (uri != null) {
            return Uri.parse(uri);
        }

        return null;
    }

    public void setUri(Uri uri) {
        this.uri = uri.toString();
    }

    public boolean isAttachmentValid() {
        return getPath() != null || getUri() != null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public boolean isDocument() {
        return FileUtils.isDocFile(getExtension(), getType());
    }

    public boolean isImage() {
        return FileUtils.isImageAttachment(getExtension(), getType());
    }

    public boolean isVideo() {
        return FileUtils.isVideoAttachment(getExtension(), getType());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(extension);
        dest.writeLong(size != null ? size : 0);
        dest.writeString(uri);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Attach: {path=%s, uri=%s, name=%s, type=%s, extension=%s, size=%d}",
                path, uri, name, type, extension, size);
    }
}
