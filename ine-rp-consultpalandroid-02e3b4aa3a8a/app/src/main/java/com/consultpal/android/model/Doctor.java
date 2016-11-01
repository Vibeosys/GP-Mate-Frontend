package com.consultpal.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ines on 5/14/16.
 */
public class Doctor implements Serializable {

    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("lastname")
    private String lastName;
    @SerializedName("description")
    private String description;
    @SerializedName("imageProfile")
    private ImageProfile imageProfile;
    @SerializedName("imageProfileUrl")
    private String imageProfileUrl;
    @SerializedName("welcomeMessage")
    private String welcomeMessage;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("practiceLogged")
    private PracticeLogged practiceLogged;

    public Doctor(String name, String description, ImageProfile imageProfile, String imageProfileUrl) {
        this.name = name;
        this.description = description;
        this.imageProfile = imageProfile;
        this.imageProfileUrl = imageProfileUrl;
    }

    public Doctor(long id, String name, String lastName, String description, ImageProfile imageProfile,
                  String imageProfileUrl, String welcomeMessage, String fullName,
                  PracticeLogged practiceLogged) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.description = description;
        this.imageProfile = imageProfile;
        this.imageProfileUrl = imageProfileUrl;
        this.welcomeMessage = welcomeMessage;
        this.fullName = fullName;
        this.practiceLogged = practiceLogged;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public PracticeLogged getPracticeLogged() {
        return practiceLogged;
    }

    public void setPracticeLogged(PracticeLogged practiceLogged) {
        this.practiceLogged = practiceLogged;
    }
}
