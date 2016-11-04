package com.consultpal.android.presenters;

import android.widget.Toast;

import com.consultpal.android.model.Symptom;
import com.consultpal.android.model.rest.Notification;
import com.consultpal.android.model.rest.NotificationType;
import com.consultpal.android.model.rest.Session;
import com.consultpal.android.providers.BusProvider;
import com.consultpal.android.services.DataService;
import com.consultpal.android.services.RestService;
import com.consultpal.android.utils.PrioritySorter;
import com.consultpal.android.views.SessionActivity;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ines on 5/29/16.
 */
public class SessionPresenter {

    private SessionActivity sessionView;

    public SessionPresenter(SessionActivity view) {
        this.sessionView = view;
    }

    public void start() {
        BusProvider.getRestBusInstance().register(this);
        BusProvider.getUIBusInstance().register(this);
    }

    public void stop() {
        BusProvider.getRestBusInstance().unregister(this);
        BusProvider.getUIBusInstance().unregister(this);
    }

    public void updateSession(long sessionId, String token, List<Symptom> symptomsList, List<Symptom> symptomsToDelete) {
        DataService dataService = RestService.getInstance();
        dataService.updateSession(sessionId, token, symptomsList, symptomsToDelete);
    }


    public void updateBeforeFinish(long sessionId, String sessionToken, List<Symptom> symptomsList, List<Symptom> symptomsToDelete) {
        DataService dataService = RestService.getInstance();
        dataService.updateAndFinishSession(sessionId, sessionToken, symptomsList, symptomsToDelete);
    }

    @Subscribe
    public void updateSymptomsList(ArrayList<Symptom> symptomsArrayList) {
        if (symptomsArrayList != null && symptomsArrayList.size() > 0
                && symptomsArrayList.get(0) instanceof Symptom) {

            // Order by priority before sending to activity
            Collections.sort(symptomsArrayList, new PrioritySorter());
            sessionView.updateSymptomsList(symptomsArrayList);
        }
    }

    @Subscribe
    public void onNotificationReceived(Notification notification) {
        NotificationType notificationType = NotificationType.valueOf(notification.getType().toUpperCase());

        String firstName = "";
        String surname = "";

        // Removing ',' from doctor name and surname received from api
        if (null != notification.getDoctorName()) {
            String[] nameArray = notification.getDoctorName().split(",");
            if (nameArray.length > 0) {
                firstName = nameArray[0];
            }
            if (nameArray.length > 1) {
                surname = nameArray[1];
            }
        }

        switch (notificationType) {
            case ALLOCATION:
                sessionView.onAllocation(notification.getDoctorId(), firstName + surname,
                        notification.getDoctorPicture(), notification.getDoctorDescription());
                break;
            case MESSAGE:
                sessionView.onMsgReceived(notification.getPracticePlaceId(), notification.getPracticeName(),
                        notification.getDoctorId(), firstName + surname, notification.getMessage(),
                        notification.getDateSent());
                break;
        }


    }

    @Subscribe
    public void sessionTimeOut(Session session) {
        if (!session.isOpen()) {
            sessionView.sessionTimeOut();
        }
    }
}
