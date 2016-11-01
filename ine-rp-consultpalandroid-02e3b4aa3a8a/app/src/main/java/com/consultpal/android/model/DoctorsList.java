package com.consultpal.android.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by akshay on 01-11-2016.
 */
public class DoctorsList implements Serializable {
    private ArrayList<Doctor> doctors;

    public DoctorsList(ArrayList<Doctor> doctors) {
        this.doctors = doctors;
    }

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(ArrayList<Doctor> doctors) {
        this.doctors = doctors;
    }
}
