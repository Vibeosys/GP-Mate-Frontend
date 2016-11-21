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
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.consultpal.android.utils.SharedPrefManager;
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
    /* @Bind(R.id.email_notification_choice)
     Switch userNotification;*/
    /*@Bind(R.id.select_date)
    TextView selectDate;
    @Bind(R.id.select_time)
    TextView selectTime;*/
    @Bind(R.id.next_appointment_button)
    TextView nextAppointment;
    @Bind(R.id.next_appointment_bottom_layout)
    LinearLayout selectNextAppointment;
    @Bind(R.id.finish_exit_button)
    TextView finishButton;
    /*@Bind(R.id.date_underline)
    View dateUnderline;
    @Bind(R.id.time_underline)
    View timeUnderline;*/
    @Bind(R.id.txt_msg_layout)
    LinearLayout layoutMsg;
    @Bind(R.id.email_msg)
    EditText txtMsg;

    private Session session;
    private Tracker mTracker;
    private Calendar mCalendar;
    private String mSenderEmail, mSenderPassword, mReceiverEmailId, msg;
    GMailSender mSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        ButterKnife.bind(this);
        mCalendar = Calendar.getInstance();
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        String emailIdId = sharedPrefManager.getGenericEmailId();
        if (!TextUtils.isEmpty(emailIdId)) {
            if (Patterns.EMAIL_ADDRESS.matcher(emailIdId).matches()) {
                mReceiverEmailId = emailIdId;
                mSenderEmail = "gpmateapp@gmail.com";
                mSenderPassword = "dummyPassword";
                //mReceiverEmailId= "@vibe.com";
                mSender = new GMailSender(mSenderEmail, mSenderPassword);
            }

        }
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
        Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.set_appointment_msg), Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        this.finish();
    }

    /*Switch button event and visibility set for button*/
  /*  @OnCheckedChanged(R.id.email_notification_choice)
    public void onClickSwitchButton(boolean checked) {
        if (checked) {
            selectNextAppointment.setVisibility(View.VISIBLE);
            finishButton.setVisibility(View.GONE);

        } else if (checked == false) {
            selectNextAppointment.setVisibility(View.GONE);
            finishButton.setVisibility(View.VISIBLE);
        }


    }*/

   /* *//*Text View Select Date event*//*
    @OnClick(R.id.select_date)
    public void onSelectDate() {
        new DatePickerDialog(this, date, mCalendar
                .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }*/

    /*Text View Select Time event*/
   /* @OnClick(R.id.select_time)
    public void onSelectTime() {
        mCalendar = Calendar.getInstance();
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
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

    }*/

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_yes:
                if (checked) {
                    selectNextAppointment.setVisibility(View.VISIBLE);
                    layoutMsg.setVisibility(View.VISIBLE);
                    finishButton.setVisibility(View.GONE);
                }
                break;
            case R.id.radio_no:
                if (checked) {
                    selectNextAppointment.setVisibility(View.GONE);
                    layoutMsg.setVisibility(View.GONE);
                    finishButton.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    /*Fix Appointment Button Click Event*/
    @OnClick(R.id.next_appointment_button)
    public void nextAppointmentButton() {
        /*To check whether user have selected next appointment Date or not*/
      /*  if (txtMsg.getHint().toString().equals(getResources().getString(R.string.exit_screen_date))) {
            showErrorDialog(getString(R.string.date_validation_message));
        }
        *//*To check whether user have selected next appointment Time or not*//*
        else if (selectTime.getHint().toString().equals(getResources().getString(R.string.exit_screen_time))) {
            showErrorDialog(getString(R.string.time_validation_message));
        }*/
        msg = txtMsg.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            showErrorDialog(getString(R.string.msg_error_validation));
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

   /* *//*Function for Date Picker*//*
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
    };*/

   /* *//*Show Date on TextView*//*
    private void showDateOnTextView() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        selectDate.setText(sdf.format(mCalendar.getTime()));
        selectDate.setHint(getResources().getString(R.string.select_date_hint));
    }*/

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
        Patient mPatient = session.getPatient();
        String customerName = mPatient.getName() + "\t" + mPatient.getLastName();
        String customerEmailId = mPatient.getEmail();

        // String customerAppointmentDateTime = selectDate.getText() + " " + selectTime.getText();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(FinishActivity.this);
            pDialog.setMessage(getResources().getString(R.string.please_wait_msg));
            pDialog.show();
            if (TextUtils.isEmpty(customerEmailId) || customerEmailId.equals(null)) {
                customerEmailId = getResources().getString(R.string.email_not_given);
            }

        }

        @Override
        protected Void doInBackground(String... mApi) {
            try {


                String htmlCode = "<html>\n" +
                        "<head>\n" +
                        "<style> \n" +
                        "   \n" +
                        "        .content{\n" +
                        "            height: 40px;\n" +
                        "    }\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<table class=\"table\" style=\"border: 2px solid #b9b6b6;padding: 0px 0px 0px 0px;text-align: left; width:100%;\">\n" +
                        "  <thead style=\"background: #E41118;\">\n" +
                        "    <tr>\n" +
                        "      <th style=\"padding: 10px 10px 10px 10px;color:#fff;\">\n" +
                        "          <div> GP Mate Application - Patient Information</div>\n" +
                        "         </th>\n" +
                        "    </tr>\n" +
                        "     \n" +
                        "  </thead>\n" +
                        "  <tbody>\n" +
                        "    <tr>\n" +
                        "     \n" +
                        "      <td style=\"padding: 7px 10px 4px 10px;background: #f5f5f5;\">\n" +
                        "           <div style=\"display:inline-block\"> \n" +
                        "        <div style=\"display:block;width:150px;font-weight:700;\">Patient Name </div>\n" +
                        "        <div class=\"content\" style=\"display:block\">" + "" + customerName + "</div>\n" +
                        "          </div>\n" +
                        "     \n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "        <td style=\"padding: 7px 10px 4px 10px;background: #fff;\">\n" +
                        "             <div style=\"display:inline-block\"> \n" +
                        "    <div style=\"display:block;width:150px;font-weight:700;\">Patient Email Id </div>\n" +
                        "        <div class=\"content\" style=\"display:block\">" + "" + customerEmailId + "</div>\n" +
                        "          </div>\n" +
                        "      </td>\n" +
                        "\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td style=\"padding: 7px 10px 4px 10px;background: #f5f5f5;\">\n" +
                        "    <div style=\"display:inline-block\"> \n" +
                        "        <div style=\"display:block;font-weight:700;\"> Message </div>\n" +
                        "        <div class=\"content\" style=\"display:block\">" + "" + msg + "</div>\n" +
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
                if (mSender != null)
                    mSender.sendMail(getResources().getString(R.string.email_subject), htmlCode, mSenderEmail, email);
//sender emailid and receviver emailid
                else {
                    Log.d("TAG", "EmailId is Null");
                }

            } catch (Exception ex) {
                Log.d("TAG", "Email sending exception");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.cancel();
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.set_appointment_msg), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();
        }
    }
}
