package com.consultpal.android.listeners;

import android.view.View;
import android.widget.AdapterView;

import com.consultpal.android.views.LogInActivity;

/**
 * Created by akshay on 28-10-2016.
 */
public class PracticeIdSelectedListener implements AdapterView.OnItemClickListener {
    private LogInActivity activity;

    public PracticeIdSelectedListener(LogInActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        activity.onPracticeSelected();
    }
}
