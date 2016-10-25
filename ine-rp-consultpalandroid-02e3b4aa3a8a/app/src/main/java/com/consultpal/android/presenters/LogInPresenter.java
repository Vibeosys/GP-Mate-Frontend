package com.consultpal.android.presenters;

import com.consultpal.android.model.PracticePlace;
import com.consultpal.android.model.rest.Session;
import com.consultpal.android.providers.BusProvider;
import com.consultpal.android.services.DataService;
import com.consultpal.android.services.RestService;
import com.consultpal.android.views.LogInActivity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ines on 5/14/16.
 */
public class LogInPresenter {

    private LogInActivity logInView;
    private List<PracticePlace> practicePlaces;

    public LogInPresenter (LogInActivity view) {
        this.logInView = view;
    }

    public void start() {
        BusProvider.getRestBusInstance().register(this);
    }

    public void stop() {
        BusProvider.getRestBusInstance().unregister(this);
    }

    public void submit(String name, String surname, long dob, String email, long practiceId) {
        DataService dataService = RestService.getInstance();
        dataService.logIn(name, surname, dob, email, practiceId, FirebaseInstanceId.getInstance().getToken());
    }

    public void fetchPracticeIds(String stringToSearch) {
        DataService dataService = RestService.getInstance();
        dataService.fetchPracticeIds(stringToSearch);
    }

    @Subscribe
    public void onPracticeIdsResponse(ArrayList<PracticePlace> practicePlaces) {
        this.practicePlaces = new ArrayList<>(practicePlaces);

        final List<String> practiceIds = new ArrayList<>();
        for (int i = 0; i<practicePlaces.size(); i++) {
            practiceIds.add(practicePlaces.get(i).getPracticeId());
        }

        logInView.updatePracticeIdAutocompleteList(practiceIds, practicePlaces);
    }

    public long getIdFromPracticePlace (String practicePlaceId) {
        if (practicePlaces != null) {
            for (int i = 0; i < practicePlaces.size(); i++) {
                if (practicePlaces.get(i).getPracticeId().equals(practicePlaceId)) {
                    return practicePlaces.get(i).getId();
                }
            }
        }
        // No practice id matches user's input
        return -1;
    }

}
