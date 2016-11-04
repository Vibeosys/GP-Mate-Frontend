package com.consultpal.android.model.rest;

import com.google.gson.annotations.SerializedName;
import com.consultpal.android.model.Doctor;
import com.consultpal.android.model.Patient;
import com.consultpal.android.model.PracticePlace;
import com.consultpal.android.model.Symptom;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ines on 5/14/16.
 */
public class Session implements Serializable {

    @SerializedName("id")
    private long id;
    @SerializedName("patient")
    private Patient patient;
    @SerializedName("practicePlace")
    private PracticePlace practicePlace;
    @SerializedName("doctor")
    private Doctor doctor;
    @SerializedName("startDate")
    private Long startDate;
    @SerializedName("finishDate")
    private Long finishDate;
    @SerializedName("symptoms")
    private List<Symptom> symptoms;
    @SerializedName("token")
    private String token;
    @SerializedName("open")
    private boolean open;
    private long appointmentDate;

    public Session() {
        super();
    }

    public Session(long id, Patient patient, PracticePlace practicePlace, Doctor doctor, Long startDate,
                   Long finishDate, List<Symptom> symptoms, String token) {
        this.id = id;
        this.patient = patient;
        this.practicePlace = practicePlace;
        this.doctor = doctor;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.symptoms = symptoms;
        this.token = token;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public PracticePlace getPracticePlace() {
        return practicePlace;
    }

    public void setPracticePlace(PracticePlace practicePlace) {
        this.practicePlace = practicePlace;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Long finishDate) {
        this.finishDate = finishDate;
    }

    public List<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<Symptom> symptoms) {
        this.symptoms = symptoms;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(long appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
