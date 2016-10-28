package com.consultpal.android.listeners;

import android.text.Editable;
import android.text.TextWatcher;

import com.consultpal.android.views.LogInActivity;

/**
 * Created by ines on 5/15/16.
 */
public class PracticeIdSearchListener implements TextWatcher {
    private LogInActivity view;

    public PracticeIdSearchListener(LogInActivity view) {
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Type 2 characters before searching
        if (s.length() > 1) {
            view.searchPracticeIds(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        view.onPracticeSelected();
    }
}
