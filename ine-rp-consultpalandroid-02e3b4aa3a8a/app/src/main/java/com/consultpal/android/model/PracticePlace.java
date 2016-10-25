package com.consultpal.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ines on 5/14/16.
 */
public class PracticePlace implements Serializable {

    @SerializedName("id")
    private long id;
    @SerializedName("practiceId")
    private String practiceId;
    @SerializedName("description")
    private String description;
    @SerializedName("imageProfile")
    private ImageProfile imageProfile;
    private String imageProfileUrl;

    public PracticePlace(long id, String practiceId, String description, ImageProfile imageProfile, String imageProfileUrl){
        super();
        this.id = id;
        this.practiceId = practiceId;
        this.description = description;
        this.imageProfile = imageProfile;
        this.imageProfileUrl = imageProfileUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
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
