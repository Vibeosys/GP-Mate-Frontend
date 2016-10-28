package com.consultpal.android.views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.consultpal.android.ConsultPalApp;
import com.consultpal.android.R;
import com.consultpal.android.listeners.DatePickerListener;
import com.consultpal.android.listeners.PracticeIdSearchListener;
import com.consultpal.android.model.Patient;
import com.consultpal.android.model.PracticePlace;
import com.consultpal.android.model.rest.Session;
import com.consultpal.android.presenters.LogInPresenter;
import com.consultpal.android.services.CountdownService;
import com.consultpal.android.utils.Constants;
import com.consultpal.android.utils.DateUtils;
import com.consultpal.android.utils.Validations;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;

import android.app.DatePickerDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class LogInActivity extends AppCompatActivity {

    @Bind(R.id.log_in_name_et)
    EditText nameET;
    @Bind(R.id.log_in_surname_et)
    EditText surnameET;
    @Bind(R.id.log_in_dob_et)
    EditText dobET;
    @Bind(R.id.log_in_email_et)
    EditText emailET;
    @Bind(R.id.log_in_practice_id_autocomplete)
    AppCompatAutoCompleteTextView practiceIdAutocomplete;
    @Bind(R.id.log_in_spn_doctor_list)
    Spinner spnDoctors;

    private LogInPresenter presenter;
    private List<String> practiceIdsList = new ArrayList<>();
    private List<PracticePlace> practicePlacesList = new ArrayList<>();
    ArrayAdapter<String> practiceIdsAdapter;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);

        // bind to presenter
        presenter = new LogInPresenter(this);

        practiceIdsAdapter = new ArrayAdapter<>(LogInActivity.this, android.R.layout.select_dialog_item, practiceIdsList);
        practiceIdAutocomplete.setThreshold(2);
        practiceIdAutocomplete.setAdapter(practiceIdsAdapter);
        practiceIdAutocomplete.addTextChangedListener(new PracticeIdSearchListener(this));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    // Initialize token here since it has a few seconds delay the first time.
                    FirebaseInstanceId.getInstance().getToken();
                } catch (Exception bug) {
                    bug.printStackTrace();
                }

            }
        });
        thread.start();

        // Obtain the shared Tracker instance.
        ConsultPalApp application = (ConsultPalApp) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.start();
        mTracker.setScreenName("LogInScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        presenter.stop();
        super.onPause();
    }

    public void showErrorDialog(String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle(message);
        builder.setMessage(getString(R.string.log_in_validation_dialog_subtitle));
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    public void showErrorMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.log_in_dob_et)
    public void openCalendar() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog,
                new DatePickerListener(dobET),
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

        datePicker.setTitle("Select your date of birth");
        datePicker.show();
    }

    @OnClick(R.id.log_in_button)
    public void onClickStartButton() {
        String validationMsg = Validations.validateLogInForm(nameET.getText().toString(),
                surnameET.getText().toString(), dobET.getText().toString(), emailET.getText().toString(),
                practiceIdAutocomplete.getText().toString());
        if (validationMsg.equals(Validations.VALIDATION_OK)) {
            long practiceId = getIdFromPracticePlaceList();
            if (practiceId >= 0) {
                openWelcomeActivity(nameET.getText().toString(),
                        surnameET.getText().toString(), DateUtils.stringDateToLong(dobET.getText().toString()),
                        emailET.getText().toString(), getSelectedPracticePlace(practiceId));
            } else {
                showErrorDialog(getString(R.string.log_in_validation_invalid_practice_id));
            }
        } else {
            showErrorDialog(Validations.getErrorMsg(this, validationMsg));
        }
    }

    @OnItemClick(R.id.log_in_practice_id_autocomplete)
    public void onPracticeSelected() {
        long practiceId = getIdFromPracticePlaceList();
        if (practiceId >= 0) {
            // ToDO Fetch and add the data to doctor spinner
        } else {
            showErrorDialog(getString(R.string.log_in_validation_invalid_practice_id));
        }
    }

    public long getIdFromPracticePlaceList() {
        return presenter.getIdFromPracticePlace(practiceIdAutocomplete.getText().toString());
    }

    public PracticePlace getSelectedPracticePlace(long selectedId) {
        if (practicePlacesList != null) {
            for (int i = 0; i < practicePlacesList.size(); i++) {
                if (practicePlacesList.get(i).getId() == selectedId) {
                    return practicePlacesList.get(i);
                }
            }
        }
        return null;
    }

    public void searchPracticeIds(String stringToSearch) {
        presenter.fetchPracticeIds(stringToSearch);
    }

    public void updatePracticeIdAutocompleteList(List<String> practiceIds, List<PracticePlace> practicePlaces) {
        practiceIdsList.clear();
        practiceIdsList = new ArrayList<>(practiceIds);

        practicePlacesList.clear();
        practicePlacesList = new ArrayList<>(practicePlaces);

        // For some reason, I have to re create adapter and notifydatasetchanged() for this to work properly
        practiceIdsAdapter = new ArrayAdapter<>(LogInActivity.this, android.R.layout.select_dialog_item, practiceIdsList);
        practiceIdAutocomplete.setAdapter(practiceIdsAdapter);
        practiceIdsAdapter.notifyDataSetChanged();
    }

    public void openWelcomeActivity(String name, String surname, long dob, String email, PracticePlace practicePlace) {
        Session session = new Session();
        Patient patient = new Patient();
        patient.setName(name);
        patient.setLastName(surname);
        patient.setDateOfBirth(dob);
        patient.setEmail(email);
        session.setPatient(patient);
        session.setPracticePlace(practicePlace);

        Intent intent = new Intent(LogInActivity.this, WelcomeActivity.class);
        intent.putExtra(Constants.LOG_IN_EXTRA_SESSION, session);
        startActivity(intent);
        this.finish();
    }

}
