package com.consultpal.android.views;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.consultpal.android.ConsultPalApp;
import com.consultpal.android.R;
import com.consultpal.android.model.rest.Session;
import com.consultpal.android.presenters.WelcomePresenter;
import com.consultpal.android.services.CountdownService;
import com.consultpal.android.utils.Constants;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @Bind(R.id.welcome_practice_picture)
    ImageView practicePlaceIV;
    @Bind(R.id.welcome_practice_place)
    TextView practicePlaceTV;
    @Bind(R.id.welcome_practice_description)
    TextView practiceDescriptionTV;
    @Bind(R.id.welcome_consent)
    TextView welcomeConsentTV;
    @Bind(R.id.welcome_continue_button)
    TextView continueButton;
    @Bind(R.id.welcome_exit_button)
    TextView exitButton;
    @Bind(R.id.welcome_progress_bar)
    ProgressWheel progressBar;

    private WelcomePresenter presenter;
    private Session session;

    private Tracker mTracker;
    private long selectedDoctorId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        presenter = new WelcomePresenter(this);

        if (getIntent() != null && getIntent().hasExtra(Constants.LOG_IN_EXTRA_SESSION)) {
            session = (Session) getIntent().getSerializableExtra(Constants.LOG_IN_EXTRA_SESSION);
            selectedDoctorId = getIntent().getExtras().getLong(Constants.DOCTOR_ID);
        }
        presenter.getConfig(session.getPracticePlace().getPracticeId());
        if (session != null && session.getPracticePlace() != null) {
            Picasso.with(this)
                    .load(Constants.BASE_ENDPOINT_PICTURES + session.getPracticePlace().getImageProfileUrl())
                    .into(practicePlaceIV);
            practicePlaceTV.setText(getString(R.string.welcome_practice_place_name, session.getPracticePlace().getPracticeId()));
            practiceDescriptionTV.setText(session.getPracticePlace().getDescription());
        }
        setTermsOfUseClickableText();

        // Obtain the shared Tracker instance.
        ConsultPalApp application = (ConsultPalApp) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private void setTermsOfUseClickableText() {
        welcomeConsentTV.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableStringBuilder ssb = new SpannableStringBuilder(getString(R.string.welcome_consent_text));
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);

                WebView wv = new WebView(WelcomeActivity.this);
                wv.loadUrl("file:///android_res/raw/eula.html");
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);

                        return true;
                    }
                });

                builder.setView(wv);
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        }, 138, 164, 0);
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);

                WebView wv = new WebView(WelcomeActivity.this);
                wv.loadUrl("file:///android_res/raw/privacy_policy.html");
                wv.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);

                        return true;
                    }
                });

                builder.setView(wv);
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        }, 169, 183, 0);

        welcomeConsentTV.setText(ssb, TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.start();

        mTracker.setScreenName("WelcomeScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPause() {
        presenter.stop();
        super.onPause();
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideBottomContent() {
        continueButton.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);
    }

    @OnClick(R.id.welcome_continue_button)
    public void onContinueClick() {
        presenter.submit(session.getPatient().getName(), session.getPatient().getLastName(),
                session.getPatient().getDateOfBirth(), session.getPatient().getEmail(),
                session.getPracticePlace().getId(), session.getAppointmentDate(), selectedDoctorId);
    }

    public void openSessionActivity(Session session) {
        Intent intent = new Intent(WelcomeActivity.this, SessionActivity.class);
        intent.putExtra(Constants.LOG_IN_EXTRA_SESSION, session);
        startActivity(intent);
        this.finish();
    }

    @OnClick(R.id.welcome_exit_button)
    public void finishSession() {
        deleteMessagingInstanceId();
        openLogIn();
    }

    public void openLogIn() {
        Intent intent = new Intent(WelcomeActivity.this, LogInActivity.class);
        intent.putExtra(Constants.APPOINTMENT_DATE_TIME, session.getAppointmentDate());
        startActivity(intent);
        this.finish();
    }

    public void deleteMessagingInstanceId() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (Exception bug) {
                    System.out.println("firebase in catch");
                }

            }
        });
        thread.start();
    }

    public void startCountDown() {
        startService(new Intent(this, CountdownService.class));
    }

    public void showErrorMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
