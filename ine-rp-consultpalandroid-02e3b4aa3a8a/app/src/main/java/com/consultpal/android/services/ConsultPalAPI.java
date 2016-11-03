package com.consultpal.android.services;

import com.consultpal.android.model.Configuration;
import com.consultpal.android.model.Doctor;
import com.consultpal.android.model.PracticePlace;
import com.consultpal.android.model.rest.Session;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ines on 5/14/16.
 */
public interface ConsultPalAPI {

    /**
     * Log In user
     *
     * @param userName        (O) User name entered to log in a session
     * @param userSurname     (1) User last name entered to log in a session
     * @param dob             (2) User dob entered to log in a session
     * @param practicePlaceId (3) Practice id selected to log in a session
     */
    @FormUrlEncoded
    @POST("medical-session/create")
    Call<Session> logIn(@Field("name") String userName,
                        @Field("lastname") String userSurname,
                        @Field("dateOfBirth") long dob,
                        @Field("email") String email,
                        @Field("practicePlaceId") long practicePlaceId,
                        @Field("gcmId") String gcmId,
                        @Field("dateOfAppointment") long dateOfAppointment,
                        @Field("doctorId") long doctorId);


    /**
     * Search Practice Ids
     *
     * @param practiceIdString (O) String typed by user to search for Practice Ids that contain that String
     */
    @GET("practice-place/searchByPracticeId/{PRACTICEID}")
    Call<ArrayList<PracticePlace>> fetchPracticeIds(
            @Path(value = "PRACTICEID") String practiceIdString);


    /**
     * Search the doctors from practice id
     *
     * @param practiceIdString
     * @return
     */
    @GET("practice-place/searchDoctorsByPracticeId/{practiceId}")
    Call<List<Doctor>> searchDoctorsByPracticeId(
            @Path(value = "practiceId") String practiceIdString);


    /**
     * Update session's symptoms
     *
     * @param sessionId             id of the session to update
     * @param symptomsListString    list of updated symptoms
     * @param symptomsRemovedString list of symptoms to remove from DB
     */
    @FormUrlEncoded
    @POST("medical-session/update")
    Call<Session> updateSession(@Field("sessionId") long sessionId,
                                @Field("token") String token,
                                @Field("symptoms") String symptomsListString,
                                @Field("symptomsRemoved") String symptomsRemovedString);


    /**
     * Finish session
     *
     * @param sessionId id of the session to finish
     */
    @FormUrlEncoded
    @POST("medical-session/finish")
    Call<Session> finishSession(@Field("sessionId") long sessionId,
                                @Field("token") String token);

    @GET("practice-place/configurations")
    Call<ArrayList<Configuration>> getConfig();
}
