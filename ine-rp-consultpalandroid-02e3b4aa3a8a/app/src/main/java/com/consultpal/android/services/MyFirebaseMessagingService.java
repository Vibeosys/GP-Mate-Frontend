package com.consultpal.android.services;

import android.widget.Toast;

import com.consultpal.android.model.rest.Notification;
import com.consultpal.android.providers.BusProvider;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

/**
 * Created by ines on 5/31/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Gson gson = new Gson();
        String json = gson.toJson(remoteMessage.getData());
        Notification notification = gson.fromJson(json, Notification.class);

        BusProvider.getRestBusInstance().post(notification);
    }

}
