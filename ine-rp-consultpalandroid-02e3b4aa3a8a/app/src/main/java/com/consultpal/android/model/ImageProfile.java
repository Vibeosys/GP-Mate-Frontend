package com.consultpal.android.model;

import java.io.Serializable;

/**
 * Created by ines on 5/30/16.
 */
public class ImageProfile implements Serializable{

    private long id;
    private String photo;
    private String extension;
    private Boolean hasPhoto;

    public ImageProfile(long id, String photo, String extension, Boolean hasPhoto) {
        this.id = id;
        this.photo = photo;
        this.extension = extension;
        this.hasPhoto = hasPhoto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Boolean getHasPhoto() {
        return hasPhoto;
    }

    public void setHasPhoto(Boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

}
