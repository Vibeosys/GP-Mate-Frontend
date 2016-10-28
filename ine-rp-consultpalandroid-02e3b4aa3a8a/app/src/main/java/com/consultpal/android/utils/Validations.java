package com.consultpal.android.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import com.consultpal.android.R;
import com.consultpal.android.services.MyFirebaseInstanceIDService;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by ines on 5/8/16.
 */
public class Validations {

    public static final String VALIDATION_OK = "VALIDATION_OK";
    public static final String NAME_EMPTY = "VALIDATION_NAME_EMPTY";
    public static final String SURNAME_EMPTY = "VALIDATION_SURNAME_EMPTY";
    public static final String NOT_VALID_EMAIL = "VALIDATION_EMAIL_INVALID";
    public static final String DOB_EMPTY = "VALIDATION_DOB_EMPTY";
    public static final String PRACTICE_ID_EMPTY = "VALIDATION_PRACTICE_ID_EMPTY";
    public static final String FCM_ID_NULL = "VALIDATION_FCM_ID_NULL";
    private static final String APPOINTMENT_DATE = "APPOINTMENT_DATE_NULL";
    private static final String APPOINTMENT_TIME = "APPOINTMENT_TIME_NULL";

    public static String validateLogInForm(String name, String surname, String dob, String email, String practiceId) {
        if (TextUtils.isEmpty(name)) {
            return NAME_EMPTY;
        }
        if (TextUtils.isEmpty(surname)) {
            return SURNAME_EMPTY;
        }
        if (TextUtils.isEmpty(dob)) {
            return DOB_EMPTY;
        }
        if (TextUtils.isEmpty(practiceId)) {
            return PRACTICE_ID_EMPTY;
        }
        // If user wrote something as an email, validate it. If not, ignore it. Email is not mandatory
        if (!TextUtils.isEmpty(email)) {
            if (!isEmailFormatValid(email)) {
                return NOT_VALID_EMAIL;
            }
        }

        if (FirebaseInstanceId.getInstance().getToken() == null) {
            return FCM_ID_NULL;
        }

        return VALIDATION_OK;
    }

    public static boolean isEmailFormatValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static String getErrorMsg(Context context, String validationResult) {
        switch (validationResult) {
            case NAME_EMPTY:
                return context.getString(R.string.log_in_validation_empty_name);
            case SURNAME_EMPTY:
                return context.getString(R.string.log_in_validation_empty_surname);
            case NOT_VALID_EMAIL:
                return context.getString(R.string.log_in_validation_invalid_email);
            case DOB_EMPTY:
                return context.getString(R.string.log_in_validation_empty_dob);
            case PRACTICE_ID_EMPTY:
                return context.getString(R.string.log_in_validation_empty_practice_id);
            case FCM_ID_NULL:
                return context.getString(R.string.log_in_validation_null_fcm_id);
            case APPOINTMENT_DATE:
                return context.getString(R.string.prev_appoint_validation_date_empty);
            case APPOINTMENT_TIME:
                return context.getString(R.string.prev_appoint_validation_time_empty);
            default:
                return context.getString(R.string.log_in_validation_error);
        }
    }


    public static String validatePrevAppoint(String date, String time) {
        if (TextUtils.isEmpty(date)) {
            return APPOINTMENT_DATE;
        }
        if (TextUtils.isEmpty(time)) {
            return APPOINTMENT_TIME;
        }
        return VALIDATION_OK;
    }
}
