package com.consultpal.android.services;

import com.consultpal.android.model.Symptom;

import java.util.List;

/**
 * Created by ines on 5/14/16.
 */
public interface DataService {

    void logIn(String userName, String userSurname, long dob, String email, long practiceId,
               String gcmId, long appointmentDate, long doctorId);

    void fetchPracticeIds(String stringToSearch);

    void searchDoctorsByPracticeId(String practiceId);

    void updateSession(long sessionId, String token, List<Symptom> symptoms, List<Symptom> deletedSymptoms);

    void finishSession(long sessionId, String token);

    void updateAndFinishSession(long sessionId, String sessionToken, List<Symptom> symptoms, List<Symptom> deletedSymptoms);

    void getConfig(String practicePlaceUserId);
}
