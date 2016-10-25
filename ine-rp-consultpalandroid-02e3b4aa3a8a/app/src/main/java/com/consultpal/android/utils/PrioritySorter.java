package com.consultpal.android.utils;

import com.consultpal.android.model.Symptom;

import java.util.Comparator;

/**
 * Created by ines on 6/5/16.
 */
public class PrioritySorter implements Comparator<Symptom> {

    public int compare(Symptom one, Symptom another) {
        int returnVal = 0;

        if (one.getPriority() < another.getPriority()) {
            returnVal = -1;
        } else if (one.getPriority() > another.getPriority()) {
            returnVal = 1;
        } else if (one.getPriority() == another.getPriority()) {
            returnVal = 0;
        }
        return returnVal;

    }
}
