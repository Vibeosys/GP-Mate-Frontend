package com.consultpal.android.services;

import android.text.TextUtils;
import android.util.Log;

import com.consultpal.android.model.Configuration;
import com.consultpal.android.model.Doctor;
import com.consultpal.android.model.DoctorsList;
import com.consultpal.android.model.PracticePlaceList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.consultpal.android.model.PracticePlace;
import com.consultpal.android.model.Symptom;
import com.consultpal.android.model.rest.Session;
import com.consultpal.android.providers.BusProvider;
import com.consultpal.android.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ines on 5/14/16.
 */
public class RestService implements DataService {

    private static final String TAG = RestService.class.getSimpleName();
    public static RestService INSTANCE;
    private final ConsultPalAPI consultPalAPI;

    private RestService() {

        // Log
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // HTTP
        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_ENDPOINT)
                .build();

        consultPalAPI = retrofit.create(ConsultPalAPI.class);

    }

    public static RestService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RestService();
        }

        return INSTANCE;
    }

    @Override
    public void logIn(String userName, String userSurname, long dob, String email, long practiceId,
                      String gcmId, long appointmentDate, long doctorId) {
        Call<Session> response = consultPalAPI.logIn(userName, userSurname, dob, email, practiceId,
                gcmId, appointmentDate, doctorId);
        response.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                if (response.isSuccessful()) {
                    BusProvider.getRestBusInstance().post(response.body());
                } else {
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                    if (!TextUtils.isEmpty(response.message())) {
                        BusProvider.getRestBusInstance().post((response.message()));
                    }


                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {

            }
        });

    }

    @Override
    public void getConfig() {
        Call<ArrayList<Configuration>> response = consultPalAPI.getConfig();
        response.enqueue(new Callback<ArrayList<Configuration>>() {
            @Override
            public void onResponse(Call<ArrayList<Configuration>> call, Response<ArrayList<Configuration>> response) {
                BusProvider.getRestBusInstance().post(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Configuration>> call, Throwable t) {

            }
        });
    }

    @Override
    public void fetchPracticeIds(String stringToSearch) {
        Call<ArrayList<PracticePlace>> response = consultPalAPI.fetchPracticeIds(stringToSearch);
        response.enqueue(new Callback<ArrayList<PracticePlace>>() {
            @Override
            public void onResponse(Call<ArrayList<PracticePlace>> call, Response<ArrayList<PracticePlace>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<PracticePlace> practicePlaces = new ArrayList<PracticePlace>(response.body());
                    BusProvider.getRestBusInstance().post(new PracticePlaceList(practicePlaces));
                } else {
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<PracticePlace>> call, Throwable t) {

            }
        });
    }

    @Override
    public void searchDoctorsByPracticeId(String practiceId) {
        Call<List<Doctor>> response = consultPalAPI.searchDoctorsByPracticeId(practiceId);
        response.enqueue(new Callback<List<Doctor>>() {
            @Override
            public void onResponse(Call<List<Doctor>> call, Response<List<Doctor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Doctor> doctors = new ArrayList<Doctor>(response.body());
                    BusProvider.getRestBusInstance().post(new DoctorsList(doctors));
                } else {
                    int statusCode = response.code();
                    ResponseBody errorBody = response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<List<Doctor>> call, Throwable t) {
                Log.e(TAG, "Error to get the doctors");
            }
        });
    }

    @Override
    public void finishSession(long sessionId, String token) {
        Call<Session> response = consultPalAPI.finishSession(sessionId, token);
        response.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BusProvider.getRestBusInstance().post(response.body());
                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                BusProvider.getRestBusInstance().post(t);
            }
        });
    }

    @Override
    public void updateSession(long sessionId, String token, List<Symptom> symptoms, List<Symptom> deletedSymptoms) {
        // Organizing list of symptoms into json object
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < symptoms.size(); i++) {
            // TODO in case client asks not to send empty symptoms. the issue is that api returns without
            // TODO empty boxes so it collapses with existent empty boxes, or list doesn't show boxes to complete
            //if (!(symptoms.get(i).getId() == null && TextUtils.isEmpty(symptoms.get(i).getDescription()))) {
            JsonObject jsonObject = new JsonObject();
            if (symptoms.get(i).getId() != null) {
                jsonObject.addProperty("id", symptoms.get(i).getId());
            }
            jsonObject.addProperty("priority", symptoms.get(i).getPriority());
            jsonObject.addProperty("description", symptoms.get(i).getDescription());
            jsonObject.addProperty("answer1", symptoms.get(i).getAnswer1());
            jsonObject.addProperty("answer2", symptoms.get(i).getAnswer2());
            jsonArray.add(jsonObject);
            //}
        }

        // Organizing deletedSymptoms into json
        JsonArray jsonArrayDeleted = new JsonArray();
        for (int i = 0; i < deletedSymptoms.size(); i++) {
            if (deletedSymptoms.get(i).getId() != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", deletedSymptoms.get(i).getId());
                jsonArrayDeleted.add(jsonObject);
            }
        }

        Call<Session> response = consultPalAPI.updateSession(sessionId, token, jsonArray.toString(),
                jsonArrayDeleted.toString());
        response.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                // Update symptoms list to set symptom id fetch from api
                if (null != response.body() && null != response.body().getSymptoms()) {
                    BusProvider.getRestBusInstance().post(response.body().getSymptoms());
                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
            }
        });
    }


    // Added update and finish to differentiate between a regular update.
    @Override
    public void updateAndFinishSession(final long sessionId, final String sessionToken,
                                       List<Symptom> symptoms, List<Symptom> deletedSymptoms) {
        // Organizing list of symptoms into json object
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < symptoms.size(); i++) {
            JsonObject jsonObject = new JsonObject();
            if (symptoms.get(i).getId() != null) {
                jsonObject.addProperty("id", symptoms.get(i).getId());
            }
            jsonObject.addProperty("priority", symptoms.get(i).getPriority());
            jsonObject.addProperty("description", symptoms.get(i).getDescription());
            jsonObject.addProperty("answer1", symptoms.get(i).getAnswer1());
            jsonObject.addProperty("answer2", symptoms.get(i).getAnswer2());
            jsonArray.add(jsonObject);
        }

        // Organizing deletedSymptoms into json
        JsonArray jsonArrayDeleted = new JsonArray();
        for (int i = 0; i < deletedSymptoms.size(); i++) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", deletedSymptoms.get(i).getId());
            jsonArrayDeleted.add(jsonObject);
        }

        Call<Session> response = consultPalAPI.updateSession(sessionId, sessionToken,
                jsonArray.toString(), jsonArrayDeleted.toString());
        response.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                finishSession(sessionId, sessionToken);
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                // call finish even if couldn't update, so the session doesn't remain opened
                finishSession(sessionId, sessionToken);
            }
        });
    }

}
