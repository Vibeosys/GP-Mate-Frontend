package com.consultpal.android.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.consultpal.android.R;
import com.consultpal.android.listeners.DatePickerListener;
import com.consultpal.android.listeners.TimePickerListener;
import com.consultpal.android.utils.Constants;
import com.consultpal.android.utils.DateUtils;
import com.consultpal.android.utils.Validations;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SelectPrevAppointment extends AppCompatActivity {

    @Bind(R.id.prev_appoint_swt)
    Switch isPrevAppointment;
    @Bind(R.id.prev_appoint_txt_msg)
    TextView appointmentMsgTV;
    @Bind(R.id.prev_appoint_date_edt)
    EditText dateET;
    @Bind(R.id.prev_appoint_time_edt)
    EditText timeET;
    @Bind(R.id.prev_appoint_next_btn)
    TextView nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_prev_appointment);
        ButterKnife.bind(this);
        isPrevAppointment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appointmentMsgTV.setText(getResources().getString(R.string.prev_appoint_taken_message));
                } else {
                    appointmentMsgTV.setText(getResources().getString(R.string.prev_appoint_message));
                }
            }
        });
    }

    @OnClick(R.id.prev_appoint_date_edt)
    public void openCalendar() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog,
                new DatePickerListener(dateET),
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)) {
            // OnCreate added to set background to Transparent, to hide a weird second bg shown with Holo_Dialog
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        };

        datePicker.setTitle("Select appointment date");
        datePicker.show();
    }

    @OnClick(R.id.prev_appoint_time_edt)
    public void openTime() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog,
                new TimePickerListener(timeET, now),
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE), false) {
            // OnCreate added to set background to Transparent, to hide a weird second bg shown with Holo_Dialog
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        };

        timePickerDialog.setTitle("Select appointment time");
        timePickerDialog.show();
    }

    @OnClick(R.id.prev_appoint_next_btn)
    public void onNextButtonClick() {
        String strDate = dateET.getText().toString();
        String strTime = timeET.getText().toString();
        String validationMsg = Validations.validatePrevAppoint(strDate,
                strTime);

        if (validationMsg.equals(Validations.VALIDATION_OK)) {
            long dateTimeLong = DateUtils.stringDateTimeToLong(strDate + " " + strTime);
            Intent intent = new Intent(SelectPrevAppointment.this, LogInActivity.class);
            intent.putExtra(Constants.APPOINTMENT_DATE_TIME, dateTimeLong);
            startActivity(intent);
            this.finish();
        } else {
            showErrorDialog(Validations.getErrorMsg(this, validationMsg));
        }
    }

    public void showErrorDialog(String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle(message);
        builder.setMessage(getString(R.string.log_in_validation_dialog_subtitle));
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
