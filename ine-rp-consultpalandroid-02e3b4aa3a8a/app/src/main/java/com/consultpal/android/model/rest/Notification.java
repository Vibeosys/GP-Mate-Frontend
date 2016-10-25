package com.consultpal.android.model.rest;

/**
 * Created by ines on 6/12/16.
 */
public class Notification {

    private Long practicePlaceId;
    private Long doctorId;
    private String practiceName;
    private String doctorName;
    private String doctorPicture;
    private String doctorDescription;
    private String type;
    private String message;
    private Long dateSent;

    public Notification(Long practicePlaceId, Long doctorId, String practiceName, String doctorName,
                        String doctorPicture, String doctorDescription, String type, String message, Long dateSent) {
        this.practicePlaceId = practicePlaceId;
        this.doctorId = doctorId;
        this.practiceName = practiceName;
        this.doctorName = doctorName;
        this.doctorPicture = doctorPicture;
        this.type = type;
        this.message = message;
        this.dateSent = dateSent;
    }

    public Notification() {
        super();
    }

    public Long getPracticePlaceId() {
        return practicePlaceId;
    }

    public void setPracticePlaceId(Long practicePlaceId) {
        this.practicePlaceId = practicePlaceId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorPicture() {
        return doctorPicture;
    }

    public void setDoctorPicture(String doctorPicture) {
        this.doctorPicture = doctorPicture;
    }

    public void setDoctorDescription(String doctorDescription) {
        this.doctorDescription = doctorDescription;
    }

    public String getDoctorDescription() {
        return this.doctorDescription;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDateSent() {
        return dateSent;
    }

    public void setDateSent(Long dateSent) {
        this.dateSent = dateSent;
    }
}
