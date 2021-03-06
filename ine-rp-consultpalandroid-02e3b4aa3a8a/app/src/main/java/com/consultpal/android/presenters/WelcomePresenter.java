package com.consultpal.android.presenters;

import com.consultpal.android.model.Configuration;
import com.consultpal.android.model.rest.Session;
import com.consultpal.android.providers.BusProvider;
import com.consultpal.android.services.DataService;
import com.consultpal.android.services.RestService;
import com.consultpal.android.utils.ConfigurationManager;
import com.consultpal.android.utils.SharedPrefManager;
import com.consultpal.android.views.WelcomeActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by ines on 5/25/16.
 */
public class WelcomePresenter {

    private WelcomeActivity welcomeView;

    public WelcomePresenter(WelcomeActivity view) {
        this.welcomeView = view;
    }

    public void start() {
        BusProvider.getRestBusInstance().register(this);
    }

    public void stop() {
        BusProvider.getRestBusInstance().unregister(this);
    }


    public void submit(String name, String surname, long dob, String email, long practiceId,
                       long appointmentDate, long doctorId) {
        DataService dataService = RestService.getInstance();
        dataService.logIn(name, surname, dob, email, practiceId, FirebaseInstanceId.getInstance().getToken(),
                appointmentDate, doctorId);
    }

    public void getConfig(String practicePlaceUserId) {
        DataService dataService = RestService.getInstance();
        dataService.getConfig(practicePlaceUserId);
    }

    @Subscribe
    public void onLogInResponse(Session session) {
        welcomeView.openSessionActivity(session);
        welcomeView.startCountDown();
    }

    @Subscribe
    public void onLogInError(String errorMessage) {
        welcomeView.showErrorMsg(errorMessage);
    }

    @Subscribe
    public void onConfigGet(ArrayList<Configuration> configurations) {
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(welcomeView.getApplicationContext());
        ConfigurationManager configurationManager = new ConfigurationManager(sharedPrefManager);
        configurationManager.setValues(configurations);
    }

}
