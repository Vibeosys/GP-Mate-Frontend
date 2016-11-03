package com.consultpal.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by akshay on 03-11-2016.
 */
public class SharedPrefManager {

    private static final String PROJECT_PREFERENCES = "com.gpmates";


    private static SharedPrefManager mSharedPrefManager;
    private static SharedPreferences mProjectSharedPref = null;
    private static Context mContext = null;

    public static SharedPrefManager getInstance(Context context) {
        if (mSharedPrefManager != null)
            return mSharedPrefManager;
        mContext = context;
        mSharedPrefManager = new SharedPrefManager();
        if (mProjectSharedPref == null) {
            mProjectSharedPref = mContext.getSharedPreferences(PROJECT_PREFERENCES, Context.MODE_PRIVATE);
        }

        return mSharedPrefManager;

    }

    public int getTimesInMin() {
        return mProjectSharedPref.getInt(Constants.TIMES_IN_MINUTE, 0);
    }

    public int getMaxNoOfProbBox() {
        return mProjectSharedPref.getInt(Constants.MAX_NO_OF_PROB_BOX, 0);
    }

    public void setTimesInMin(int value) {
        setIntValuesInSharedPrefs(Constants.TIMES_IN_MINUTE, value);
    }

    public void setMaxNoOfProbBox(int value) {
        setIntValuesInSharedPrefs(Constants.MAX_NO_OF_PROB_BOX, value);
    }

    private static void setIntValuesInSharedPrefs(String sharedPrefKey, int sharedPrefValue) {
        SharedPreferences.Editor editor = mProjectSharedPref.edit();
        editor.putInt(sharedPrefKey, sharedPrefValue);
        editor.apply();
    }
}
