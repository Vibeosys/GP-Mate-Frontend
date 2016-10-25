package com.consultpal.android.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.consultpal.android.ConsultPalApp;
import com.consultpal.android.R;
import com.consultpal.android.model.rest.Session;
import com.consultpal.android.utils.Constants;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FinishActivity extends AppCompatActivity {

    @Bind(R.id.finish_picture) ImageView picture;

    private Session session;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        ButterKnife.bind(this);

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

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("FinishScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
