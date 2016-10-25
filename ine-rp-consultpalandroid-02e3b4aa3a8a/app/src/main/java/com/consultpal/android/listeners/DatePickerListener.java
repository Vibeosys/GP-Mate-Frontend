package com.consultpal.android.listeners;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * Created by ines on 5/8/16.
 */
public class DatePickerListener implements DatePickerDialog.OnDateSetListener {

    private EditText dobET;

    public DatePickerListener(EditText dobET) {
        this.dobET = dobET;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        dobET.setText(date);
    }
}
