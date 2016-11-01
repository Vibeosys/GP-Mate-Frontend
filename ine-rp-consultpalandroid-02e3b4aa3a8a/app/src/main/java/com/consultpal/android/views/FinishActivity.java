package com.consultpal.android.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.consultpal.android.ConsultPalApp;
import com.consultpal.android.R;
import com.consultpal.android.model.rest.Session;
import com.consultpal.android.utils.Constants;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;

public class FinishActivity extends AppCompatActivity {

    @Bind(R.id.finish_picture)
    ImageView picture;
    @Bind(R.id.email_notification_choice)
    Switch userNotification;
    @Bind(R.id.select_date)
    TextView selectDate;
    @Bind(R.id.select_time)
    TextView selectTime;
    @Bind(R.id.next_appointment_button)
    TextView nextAppointment;
    @Bind(R.id.next_appointment_bottom_layout)
    LinearLayout selectNextAppointment;
    @Bind(R.id.finish_exit_button)
    TextView finishButton;
    @Bind(R.id.date_underline)
    View dateUnderline;
    @Bind(R.id.time_underline)
    View timeUnderline;

    private Session session;
    private Tracker mTracker;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        ButterKnife.bind(this);
        mCalendar = Calendar.getInstance();

        if (getIntent() != null && getIntent().hasExtra(Constants.LOG_IN_EXTRA_SESSION)) {
            session = (Session) getIntent().getSerializableExtra(Constants.LOG_IN_EXTRA_SESSION);
        }

        // If doctor has picture show doctor's, otherwise show practice place image. If both are empty show nothing
        if (session != null && session.getDoctor() != null && !TextUtils.isEmpty(session.getDoctor().getImageProfileUrl())) {
            Picasso.with(this)
                    .load(Constants.BASE_ENDPOINT_PICTURES + session.getDoctor().getImageProfileUrl())
                    .into(picture);
        } else if (session != null && session.getPracticePlace() != null && !TextUtils.isEmpty(session.getPracticePlace().getImageProfileUrl())) {
            Picasso.with(this)
                    .load(Constants.BASE_ENDPOINT_PICTURES + session.getPracticePlace().getImageProfileUrl())
                    .into(picture);
        }

        // Obtain the shared Tracker instance.
        ConsultPalApp application = (ConsultPalApp) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @OnClick(R.id.finish_exit_button)
    public void onClickFinishButton() {
        this.finish();
    }

    /*Switch button event and visibility set for button*/
    @OnCheckedChanged(R.id.email_notification_choice)
    public void onClickSwitchButton(boolean checked) {
        if (checked) {
            selectDate.setVisibility(View.VISIBLE);
            selectTime.setVisibility(View.VISIBLE);
            selectNextAppointment.setVisibility(View.VISIBLE);
            timeUnderline.setVisibility(View.VISIBLE);
            dateUnderline.setVisibility(View.VISIBLE);
            finishButton.setVisibility(View.GONE);

        } else if (checked == false) {
            selectDate.setVisibility(View.GONE);
            selectTime.setVisibility(View.GONE);
            selectNextAppointment.setVisibility(View.GONE);
            timeUnderline.setVisibility(View.GONE);
            dateUnderline.setVisibility(View.GONE);
            finishButton.setVisibility(View.VISIBLE);
        }


    }

    /*Text View Select Date event*/
    @OnClick(R.id.select_date)
    public void onSelectDate() {
        new DatePickerDialog(this, date, mCalendar
                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    /*Text View Select Time event*/
    @OnClick(R.id.select_time)
    public void onSelectTime() {
        mCalendar=Calendar.getInstance();
        int hour= mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (selectedHour > 12) {
                    int timeIn12 = selectedHour - 12;
                    selectTime.setText(timeIn12 + ":" + selectedMinute + " PM");
                } else {
                    selectTime.setText(selectedHour + ":" + selectedMinute + " AM");
                }
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

    }

    /*Fix Appointment Button Click Event*/
    @OnClick(R.id.next_appointment_button)
    public void nextAppointmentButton()
    {
        /*To check whether user have selected next appointment Date or not*/
        if(selectDate.getText().toString().equals(getString(R.string.exit_screen_date)))
        {
            showErrorDialog(getString(R.string.date_validation_message));
        }
        /*To check whether user have selected next appointment Time or not*/
        else if(selectTime.getText().toString().equals(getString(R.string.exit_screen_time)))
        {
            showErrorDialog(getString(R.string.time_validation_message));
        }
        /*Date and Time Validations are Done*/
        else {
            Toast.makeText(getApplicationContext(), "All Validations are done", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("FinishScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /*Function for Date Picker*/
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            showDateOnTextView();
        }
    };

    /*Show Date on TextView*/
    private void showDateOnTextView() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        selectDate.setText(sdf.format(mCalendar.getTime()));
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
