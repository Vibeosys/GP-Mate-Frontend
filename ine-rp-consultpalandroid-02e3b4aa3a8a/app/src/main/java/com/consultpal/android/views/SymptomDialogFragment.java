package com.consultpal.android.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.consultpal.android.ConsultPalApp;
import com.consultpal.android.R;
import com.consultpal.android.model.Symptom;
import com.consultpal.android.utils.Constants;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ines on 5/26/16.
 */
public class SymptomDialogFragment extends AppCompatDialogFragment {

    @Bind(R.id.symptom_edit_description)
    EditText descriptionET;
    @Bind(R.id.symptom_question_1_answer)
    EditText answer1ET;
    @Bind(R.id.symptom_question_2_answer)
    RadioGroup answer2RG;
    private OkClickListener listener;
    private Symptom symptom;
    private int position;
    SessionActivity callingActivity;
    private Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            symptom = (Symptom) getArguments().getParcelable(Constants.SYMPTOMS_EXTRA_DIALOG_SYMPTOM);
            position = getArguments().getInt(Constants.SYMPTOMS_EXTRA_DIALOG_POSITION);
        }

        callingActivity = (SessionActivity) getActivity();
        // Obtain the shared Tracker instance.
        ConsultPalApp application = (ConsultPalApp) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        // request a window without the title
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_symptom, container, false);
        ButterKnife.bind(this, view);

        if (symptom != null) {
            descriptionET.setText(symptom.getDescription());
            answer1ET.setText(symptom.getAnswer1());

            // Set y/n second answer
            if (symptom.getAnswer2() != null) {
                if (symptom.getAnswer2()) {
                    ((RadioButton) answer2RG.getChildAt(0)).setChecked(true);
                } else if (!symptom.getAnswer2()) {
                    ((RadioButton) answer2RG.getChildAt(1)).setChecked(true);
                }
            }
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);


        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @OnClick(R.id.symptom_edit_save)
    public void saveSymptom() {
        symptom.setDescription(descriptionET.getText().toString());
        symptom.setAnswer1(answer1ET.getText().toString());
        mTracker.setScreenName("SymptomDialogScreen");
        mTracker.send(new HitBuilders.EventBuilder().
                setCategory("Symptoms").
                setAction(descriptionET.getText().toString()).
                setLabel(descriptionET.getText().toString()).
                setValue(1).build());
        Log.d("TAG", mTracker.toString());

        if (answer2RG.getCheckedRadioButtonId() == R.id.symptom_question_2_positive) {
            symptom.setAnswer2(true);
        } else if (answer2RG.getCheckedRadioButtonId() == R.id.symptom_question_2_negative) {
            symptom.setAnswer2(false);
        }

        callingActivity.updateEditedSymptom(symptom, position);
        if (listener != null) {
            listener.OnOkClick();
        }
        this.dismiss();
    }

    @OnClick(R.id.symptom_edit_cancel)
    public void cancelDialog() {
        this.dismiss();
    }

    @OnClick(R.id.symptom_edit_clear)
    public void clearDialog() {
        descriptionET.setText("");
        answer1ET.setText("");
        answer2RG.clearCheck();
    }

    // Commented in case client changes his mind about adding a delete functionality
    /*public void deleteSymptom() {
        callingActivity.deleteSymptom(position);
        this.dismiss();
    }*/

    public void setOkClickListener(OkClickListener listener) {
        this.listener = listener;
    }

    public interface OkClickListener {
        public void OnOkClick();
    }
}
