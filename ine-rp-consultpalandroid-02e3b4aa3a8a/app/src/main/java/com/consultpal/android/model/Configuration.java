package com.consultpal.android.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by akshay on 03-11-2016.
 */
public class Configuration implements Serializable {

    @SerializedName("id")
    private long id;
    @SerializedName("key")
    private String key;
    @SerializedName("value")
    private String value;

    public Configuration(long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
