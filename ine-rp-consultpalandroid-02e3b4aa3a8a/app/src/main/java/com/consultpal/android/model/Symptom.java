package com.consultpal.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ines on 5/14/16.
 */
public class Symptom implements Parcelable {

    private Long id;
    private String description;
    private String answer1;
    private Boolean answer2;
    private int priority;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Symptom(String description, int priority) {
        this.description = description;
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public Boolean getAnswer2() {
        return answer2;
    }

    public void setAnswer2(Boolean answer2) {
        this.answer2 = answer2;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    protected Symptom(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readLong();
        description = in.readString();
        answer1 = in.readString();
        byte answer2Val = in.readByte();
        answer2 = answer2Val == 0x02 ? null : answer2Val != 0x00;
        priority = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(id);
        }
        dest.writeString(description);
        dest.writeString(answer1);
        if (answer2 == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (answer2 ? 0x01 : 0x00));
        }
        dest.writeInt(priority);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Symptom> CREATOR = new Parcelable.Creator<Symptom>() {
        @Override
        public Symptom createFromParcel(Parcel in) {
            return new Symptom(in);
        }

        @Override
        public Symptom[] newArray(int size) {
            return new Symptom[size];
        }
    };
}
