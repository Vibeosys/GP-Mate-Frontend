package com.consultpal.android.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by akshay on 01-11-2016.
 */
public class PracticePlaceList implements Serializable {
    private ArrayList<PracticePlace> practicePlaces;

    public PracticePlaceList(ArrayList<PracticePlace> practicePlaces) {
        this.practicePlaces = practicePlaces;
    }

    public ArrayList<PracticePlace> getPracticePlaces() {
        return practicePlaces;
    }

    public void setPracticePlaces(ArrayList<PracticePlace> practicePlaces) {
        this.practicePlaces = practicePlaces;
    }
}
