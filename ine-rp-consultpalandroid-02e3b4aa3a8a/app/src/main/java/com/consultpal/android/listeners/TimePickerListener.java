package com.consultpal.android.listeners;

import android.app.TimePickerDialog;
import android.widget.EditText;
import android.widget.TimePicker;

import com.consultpal.android.utils.DateUtils;

import java.util.Calendar;

/**
 * Created by akshay on 28-10-2016.
 */
public class TimePickerListener implements TimePickerDialog.OnTimeSetListener {

    private EditText timeET;
    private Calendar calendar;

    public TimePickerListener(EditText timeET, Calendar calendar) {
        this.timeET = timeET;
        this.calendar = calendar;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        timeET.setText(DateUtils.getLocalTimeInReadableFormat(calendar.getTime()));
    }
}
