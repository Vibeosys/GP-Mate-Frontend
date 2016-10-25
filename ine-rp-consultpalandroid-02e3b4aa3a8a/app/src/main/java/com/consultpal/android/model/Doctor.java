package com.consultpal.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ines on 5/14/16.
 */
public class Doctor implements Serializable{

    private String name;
    private String description;
    @SerializedName("imageProfile")
    private ImageProfile imageProfile;
    private String imageProfileUrl;

    public Doctor(String name, String description, ImageProfile imageProfile, String imageProfileUrl) {
        this.name = name;
        this.description = description;
        this.imageProfile = imageProfile;
        this.imageProfileUrl = imageProfileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ImageProfile getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(ImageProfile imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getImageProfileUrl() {
        return imageProfileUrl;
    }

    public void setImageProfileUrl(String imageProfileUrl) {
        this.imageProfileUrl = imageProfileUrl;
    }
}
