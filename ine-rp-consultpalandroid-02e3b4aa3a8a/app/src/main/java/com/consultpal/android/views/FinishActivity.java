package com.consultpal.android.views;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.consultpal.android.model.Patient;
import com.consultpal.android.model.rest.Session;
import com.consultpal.android.utils.Constants;
import com.consultpal.android.utils.GMailSender;
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
    private String mSenderEmail,mSenderPassword,mReceiverEmailId;
    GMailSender mSender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        ButterKnife.bind(this);
        mCalendar = Calendar.getInstance();
        mSenderEmail ="senderemail@gmail.com";
        mSenderPassword = "pwd";
        mReceiverEmailId="receiver@vibeosys.com";
        mSender = new GMailSender(mSenderEmail,mSenderPassword);
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
                    selectTime.setHint("Time Selected");
                } else {
                    selectTime.setText(selectedHour + ":" + selectedMinute + " AM");
                    selectTime.setHint("Time Selected");
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
        if(selectDate.getHint().toString().equals(getResources().getString(R.string.exit_screen_date)))
        {
            showErrorDialog(getString(R.string.date_validation_message));
        }
        /*To check whether user have selected next appointment Time or not*/
        else if(selectTime.getHint().toString().equals(getResources().getString(R.string.exit_screen_time)))
        {
            showErrorDialog(getString(R.string.time_validation_message));
        }
        /*Date and Time Validations are Done*/
        else {
            new MyAsyncClass().execute(mReceiverEmailId);
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
        selectDate.setHint(getResources().getString(R.string.select_date_hint));
        }
    /*Error message dialog*/
    public void showErrorDialog(String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle(message);
        builder.setMessage(getString(R.string.log_in_validation_dialog_subtitle));
        builder.setPositiveButton("OK", null);
        builder.show();
    }

/*Inner class for sending email to call GmailSender*/
    class MyAsyncClass extends AsyncTask<String, Void, Void> {

        ProgressDialog pDialog;
     Patient mPatient= session.getPatient();
        String customerName =mPatient.getName()+mPatient.getLastName();
        String customerEmailId = mPatient.getEmail();
        /*String customerName ="Dummy Name";
        String customerEmailId = "Dummy email Id";*/
        String customerAppointmentDateTime = selectDate.getText()+" "+selectTime.getText();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(FinishActivity.this);
            pDialog.setMessage(getResources().getString(R.string.please_wait_msg));
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... mApi) {
            try {


                String htmlCode="<html>\n" +
                        "<head>\n" +
                        "<style> \n" +
                        "    @media only screen and (max-width:620px;){\n" +
                        "        .content{\n" +
                        "            height: 60px;\n" +
                        "        }\n" +
                        "    }\n" +
                        "    @media only screen and (min-width:620px){\n" +
                        "        .content{\n" +
                        "            height: 40px;\n" +
                        "        }\n" +
                        "    }\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<table class=\"table\" style=\"border: 2px solid #b9b6b6;padding: 0px 0px 10px 0px;text-align: left;\">\n" +
                        "  <thead style=\"background: #4DB6AC;\">\n" +
                        "    <tr>\n" +
                        "      <th colspan=\"3\" style=\"padding: 10px 10px 10px 10px;color:#fff;\">\n" +
                        "          <div> Consult pal Application</div>\n" +
                        "         </th>\n" +
                        "    </tr>\n" +
                        "     \n" +
                        "  </thead>\n" +
                        "  <tbody>\n" +
                        "    <tr>\n" +
                        "     \n" +
                        "      <td style=\"padding: 7px 10px 4px 10px;\">\n" +
                        "          <div>\n" +
                        "          <div style=\"width:70px;display:inline-block\">\n" +
                        "              Customer Name</div>\n" +
                        "              <div style=\"width:15px;display:inline-block;\">:</div>\n" +
                        "              <div style=\"display:inline-block;\">"+""+customerName+"</div>\n" +
                        "          </div> </td>\n" +
                        "     \n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <td style=\"padding: 0px 10px 4px 10px;\">\n" +
                        "     <div>\n" +
                        "          <div style=\"width:70px;display:inline-block\">\n" +
                        "              Customer EmailId</div>\n" +
                        "              <div style=\"width:15px;display:inline-block;\">:</div>\n" +
                        "              <div style=\"display:inline-block;\">"+""+customerEmailId+"</div>\n" +
                        "          </div>\n" +
                        "      </td>\n" +
                        "\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td colspan=\"3\" style=\"padding: 7px 10px 4px 10px;background: #f5f5f5;\">\n" +
                        "    <div > \n" +
                        "        <div>Customer Appointment Date</div>\n" +
                        "        <div><hr style=\"margin-top: 4px;border: 1px solid #efeeee;\"></div>\n" +
                        "        <div class=\"content\">"+""+customerAppointmentDateTime+"</div>\n" +
                        "          </div>\n" +
                        "        </td>\n" +
                        "\n" +
                        "    </tr>\n" +
                        "      \n" +
                        "  </tbody>\n" +
                        "</table>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>";



                //  messageTxt= template + mUserPwd;
                String email = mApi[0];

                // Add subject, Body, your mail Id, and receiver mail Id.
                mSender.sendMail(getResources().getString(R.string.app_name), htmlCode, mSenderEmail,email );
//sender emailid and receviver emailid

            }

            catch (Exception ex) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.cancel();
            Toast toast=Toast.makeText(getApplicationContext(), getResources().getString(R.string.set_appointment_msg), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            finish();
        }
    }
}
