package com.consultpal.android.utils;

import android.util.Log;

import com.consultpal.android.model.Configuration;

import java.util.ArrayList;

/**
 * Created by akshay on 03-11-2016.
 */
public class ConfigurationManager {
    private static final String TAG = ConfigurationManager.class.getSimpleName();
    SharedPrefManager sharedPrefManager;


    public ConfigurationManager(SharedPrefManager sharedPrefManager) {
        this.sharedPrefManager = sharedPrefManager;
    }

    public void setValues(ArrayList<Configuration> configurations) {
        for (Configuration configuration :
                configurations) {
            String key = configuration.getKey();
            if (key.equals(Constants.MAX_NO_OF_PROB_BOX)) {
                try {
                    this.sharedPrefManager.setMaxNoOfProbBox(Integer.parseInt(configuration.getValue()));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "String can not convert into integer");
                } catch (Exception e) {

                }
            } else if (key.equals(Constants.TIMES_IN_MINUTE)) {
                try {
                    this.sharedPrefManager.setTimesInMin(Integer.parseInt(configuration.getValue()));
                } catch (NumberFormatException e) {
                    Log.e(TAG, "String can not convert into integer");
                } catch (Exception e) {

                }
            }
            else if (key.equals(Constants.GENERIC_EMAIL_ID)) {
                try {
                    this.sharedPrefManager.setGenericEmailId(configuration.getValue());
                }  catch (Exception e) {
                    Log.e(TAG, "Exception durin");
                }
            }

        }
    }
}
