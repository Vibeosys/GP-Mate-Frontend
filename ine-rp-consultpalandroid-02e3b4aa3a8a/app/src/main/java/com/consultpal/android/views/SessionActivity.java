package com.consultpal.android.views;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.consultpal.android.ConsultPalApp;
import com.consultpal.android.R;
import com.consultpal.android.adapters.MessagesRVAdapter;
import com.consultpal.android.adapters.SymptomsRVAdapter;
import com.consultpal.android.listeners.OnStartDragListener;
import com.consultpal.android.model.Doctor;
import com.consultpal.android.model.Message;
import com.consultpal.android.model.Symptom;
import com.consultpal.android.model.rest.Session;
import com.consultpal.android.presenters.SessionPresenter;
import com.consultpal.android.services.CountdownService;
import com.consultpal.android.utils.Constants;
import com.consultpal.android.utils.SharedPrefManager;
import com.consultpal.android.utils.SimpleItemTouchHelperCallback;
import com.consultpal.android.utils.SymptomListDividerDecorator;
import com.consultpal.android.utils.Typewriter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SessionActivity extends AppCompatActivity implements OnStartDragListener {

    private static final String TAG = SessionActivity.class.getSimpleName();
    @Bind(R.id.session_symptoms_recycler_view)
    RecyclerView symptomsRV;
    @Bind(R.id.session_practice_picture)
    ImageView practiceImageView;
    @Bind(R.id.session_top_message)
    Typewriter topMessageTV;
    @Bind(R.id.session_countdown)
    TextView countdownTV;
    @Bind(R.id.session_new_message)
    TextView newMessageTV;

    private SessionPresenter presenter;
    // Use this list just to init adapter. When sending info to API use up to date Adapter's list (in case of swapping, editing)
    private ArrayList<Symptom> symptomsList = new ArrayList<>();
    private ItemTouchHelper itemTouchHelper;
    private Session session;
    private SymptomsRVAdapter adapter;
    private MessagesRVAdapter messagesRVAdapter;
    private List<Symptom> symptomsToDelete = new ArrayList<>();
    private ArrayList<Message> messagesList = new ArrayList<>();
    private AlertDialog messagesDialog;

    private Tracker mTracker;
    private long startTime = 2 * 60 * 1000; // 2 MINS IDLE TIME
    private final long interval = 1 * 1000;

    CountDownTimer cdtInterval;
    private SharedPrefManager sharedPrefManager;
    private int mMaxNoOfBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        presenter = new SessionPresenter(this);
        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        mMaxNoOfBox = sharedPrefManager.getMaxNoOfProbBox();
        cdtInterval = new MyCountDownTimer(startTime, interval);
        if (savedInstanceState != null) {
            session = (Session) savedInstanceState.getSerializable(Constants.KEY_SESSION);
            symptomsList = savedInstanceState.getParcelableArrayList(Constants.KEY_SESSION_SYMPTOMS);
            messagesList = savedInstanceState.getParcelableArrayList(Constants.KEY_SESSION_MESSAGES);
            setUnreadMessagesUI();
        } else {
            addInitialBoxes();
        }

        if (getIntent() != null && getIntent().hasExtra(Constants.LOG_IN_EXTRA_SESSION)) {
            session = (Session) getIntent().getSerializableExtra(Constants.LOG_IN_EXTRA_SESSION);
        }
        showInfoDialog();


        boolean isTablet = getResources().getBoolean(R.bool.is_tab);
        if (isTablet) {
            countdownTV.setVisibility(View.VISIBLE);
        } else {
            countdownTV.setVisibility(View.GONE);
        }
        symptomsRV.setHasFixedSize(true);
        symptomsRV.setLayoutManager(new LinearLayoutManager(this));
        symptomsRV.addItemDecoration(new SymptomListDividerDecorator(20));
        adapter = new SymptomsRVAdapter(this, symptomsList, this, mMaxNoOfBox);
        symptomsRV.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(symptomsRV);

        // Obtain the shared Tracker instance.
        ConsultPalApp application = (ConsultPalApp) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        cdtInterval.cancel();
        cdtInterval.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.KEY_SESSION, session);
        outState.putParcelableArrayList(Constants.KEY_SESSION_SYMPTOMS, symptomsList);
        outState.putParcelableArrayList(Constants.KEY_SESSION_MESSAGES, messagesList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cdtInterval.cancel();
        cdtInterval.start();
        presenter.start();
        registerReceiver(br, new IntentFilter(CountdownService.COUNTDOWN_BR));
        registerReceiver(brUpdate, new IntentFilter(CountdownService.COUNTDOWN_UPDATE));

        if (!isMyServiceRunning(CountdownService.class)) {
            deleteMessagingInstanceId();
            openFinishActivity();
        }

        mTracker.setScreenName("ActiveSessionScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onPause() {
        presenter.stop();
        super.onPause();
        unregisterReceiver(br);
        unregisterReceiver(brUpdate);
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
            unregisterReceiver(brUpdate);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        cdtInterval.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finishSession(true);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    private void addInitialBoxes() {
        for (int i = 1; i <= mMaxNoOfBox; i++) {
            symptomsList.add(new Symptom(null, i));
        }
    }

    public void addSymptomToDelete(Symptom symptomToDelete) {
        symptomsToDelete.add(symptomToDelete);
    }

    private void setTopLayout() {
        topMessageTV.setCharacterDelay(100);
        if (session != null) {
            if (session.getPracticePlace() != null) {
                setPracticeImageView(Constants.BASE_ENDPOINT_PICTURES + session.getPracticePlace().getImageProfileUrl());
            }
            // If Doctor has description, add that description at top message. Otherwise add custom doctor message
            // If session has no doctor, custom practice message is set in xml
            if (session.getDoctor() != null) {
                if (!TextUtils.isEmpty(session.getDoctor().getDescription())) {
                    topMessageTV.animateText(session.getDoctor().getDescription());
                } else {
                    topMessageTV.animateText(getString(R.string.session_top_message_doctor, session.getDoctor().getName() + " " + session.getDoctor().getLastName()));
                }
                // If doctor has picture, update imageview
                if (!TextUtils.isEmpty(session.getDoctor().getImageProfileUrl())) {
                    setPracticeImageView(Constants.BASE_ENDPOINT_PICTURES + session.getDoctor().getImageProfileUrl());
                }
            }
        }
    }

    private void setPracticeImageView(String url) {
        Picasso.with(this)
                .load(url)
                .into(practiceImageView);
    }

    @OnClick(R.id.session_help_button)
    public void openHelpDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.help_dialog_title));
        View v = getLayoutInflater().inflate(R.layout.dialog_help, null);
        TextView helpText = (TextView) v.findViewById(R.id.help_text);
        helpText.setText(Html.fromHtml(getString(R.string.help_html_content)));
        builder.setView(v);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    @OnClick(R.id.session_finish_button)
    public void finishSessionOnClick() {
        finishSession(false);
    }

    @OnClick(R.id.session_messages)
    public void openMessagesDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.messages_dialog_title));
        View v = getLayoutInflater().inflate(R.layout.dialog_messages, null);
        RecyclerView messagesRV = (RecyclerView) v.findViewById(R.id.messages_recycler_view);

        // Set messages adapter
        messagesRV.setHasFixedSize(true);
        messagesRV.setLayoutManager(new LinearLayoutManager(this));
        messagesRV.addItemDecoration(new SymptomListDividerDecorator(20));
        messagesRVAdapter = new MessagesRVAdapter(messagesList, this);
        messagesRV.setAdapter(messagesRVAdapter);

        // Set alertdialog height to show in 90% of screen and not wrap content
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        v.setMinimumHeight((int) (displayRectangle.height() * 0.9f));

        builder.setView(v);
        builder.setPositiveButton("CLOSE", null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setMessagesAsRead();
                newMessageTV.setVisibility(View.GONE);
            }
        });
        messagesDialog = builder.show();
    }

    private void finishSession(final boolean isFromBackButton) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you are ready to finish your session?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMessagingInstanceId(); // Clear Messaging token for next user
                presenter.updateBeforeFinish(session.getId(), session.getToken(),
                        adapter.getItemsList(), symptomsToDelete);
                stopService(new Intent(SessionActivity.this, CountdownService.class));
                if (isFromBackButton) {
                    finish();
                } else {
                    openFinishActivity();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cdtInterval.cancel();
                cdtInterval.start();
            }
        });
        builder.show();
    }

    public void sessionTimeOut() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.session_time_out));
        builder.setMessage(getResources().getString(R.string.session_time_out_msg));
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMessagingInstanceId(); // Clear Messaging token for next user
                stopService(new Intent(SessionActivity.this, CountdownService.class));
                openFinishActivity();
            }
        });
        builder.setCancelable(false);/*To disable back button or background click event*/
        builder.show();

    }

    private void openFinishActivity() {
        Intent intent = new Intent(SessionActivity.this, FinishActivity.class);
        intent.putExtra(Constants.LOG_IN_EXTRA_SESSION, session);
        startActivity(intent);
        this.finish();
    }

    // Called from SymptomsRVAdapter onClick
    public void openSymptomDetail(Symptom symptom, int position) {
        FragmentManager fm = getSupportFragmentManager();
        SymptomDialogFragment symptomDialogFragment = new SymptomDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.SYMPTOMS_EXTRA_DIALOG_SYMPTOM, symptom);
        args.putInt(Constants.SYMPTOMS_EXTRA_DIALOG_POSITION, position);
        symptomDialogFragment.setArguments(args);
        symptomDialogFragment.show(fm, "fragment_edit_symptom");
    }

    public void updateEditedSymptom(Symptom symptom, int position) {
        adapter.updateItem(symptom, position);
        if (adapter.addNewBox(position)) {
            symptomsRV.smoothScrollToPosition(position + 1);
        }
    }

    public void updateSymptomsList(ArrayList<Symptom> symptomsList) {
        adapter.updateItemsList(symptomsList);
    }

    // Not used now - but leaving code bc it's useful functionality in case client changes his mind
    public void deleteSymptom(int position) {
        adapter.deleteItem(position);
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null && intent.hasExtra(CountdownService.COUNTDOWN_BR)) {
                updateGUI(intent);
            }
            if (intent.getExtras() != null && intent.getBooleanExtra(CountdownService.COUNTDOWN_UPDATE, false)) {
                presenter.updateSession(session.getId(), session.getToken(), adapter.getItemsList(), symptomsToDelete);
            }
            if (intent.getExtras() != null && intent.getBooleanExtra(CountdownService.COUNTDOWN_FINISH, false)) {
                presenter.updateBeforeFinish(session.getId(), session.getToken(),
                        adapter.getItemsList(), symptomsToDelete);
            }
        }
    };

    private BroadcastReceiver brUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null && intent.getBooleanExtra(CountdownService.COUNTDOWN_UPDATE, false)) {
                presenter.updateSession(session.getId(), session.getToken(), adapter.getItemsList(), symptomsToDelete);
            }
        }
    };

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long secsUntilFinished = intent.getLongExtra(CountdownService.COUNTDOWN_BR, 0) / 1000;
            int minutes = ((int) (secsUntilFinished / 60)) % 60;
            long seconds = secsUntilFinished % 60;
            countdownTV.setText(((minutes < 10) ? "0" : "") + String.valueOf(minutes) + ":" +
                    ((seconds < 10) ? "0" : "") + String.valueOf(seconds));
        }
    }

    public void deleteMessagingInstanceId() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (Exception bug) {
                    System.out.println("firebase in catch");
                }

            }
        });
        thread.start();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void onAllocation(final long doctorId, final String doctorName, final String lastName, final String doctorPicture,
                             final String doctorDescription) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                updateDoctor(doctorId, doctorName, lastName, doctorPicture, doctorDescription);
            }
        });
    }

    public void onAllocationOfPracticeId() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (session.getPracticePlace() != null) {
                    if (!TextUtils.isEmpty(session.getPracticePlace().getDescription())) {
                        topMessageTV.animateText(session.getPracticePlace().getDescription());
                    } else {
                        topMessageTV.animateText(getString(R.string.session_top_message_doctor, session.getPracticePlace().getPracticeId()));
                    }
                    // If doctor has picture, update imageview
                    if (!TextUtils.isEmpty(session.getDoctor().getImageProfileUrl())) {
                        setPracticeImageView(Constants.BASE_ENDPOINT_PICTURES + session.getPracticePlace().getImageProfileUrl());
                    }
                }
            }
        });

    }

    public void onMsgReceived(final Long practicePlaceId, final String practiceName,
                              final Long doctorId, final String doctorName, final String message,
                              final long dateSent) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String senderName;
                // If practiceName is empty set Sender as Doctor
                if (!TextUtils.isEmpty(practiceName)) {
                    senderName = practiceName;
                } else {
                    senderName = doctorName;
                }

                showNewMessage(senderName, message, dateSent);
            }
        });
    }

    private void updateDoctor(long doctorId, String doctorName, String lastName, String doctorPicture, String doctorDescription) {
        Doctor doctor = new Doctor(doctorId, doctorName, lastName, doctorDescription, null, doctorPicture);
        session.setDoctor(doctor);
        setTopLayout();
    }


    private void showNewMessage(String name, String message, long dateSent) {

        // Now, instead of showing a notification in Msg button, the messages dialog is automatically opened
        // so part of this logic is not used: counting unread messages and updating messages button UI

        // show new message UI button
        if (messagesDialog != null && messagesDialog.isShowing()) {
            messagesList.add(new Message(message, name, dateSent, true));
        } else {
            messagesList.add(new Message(message, name, dateSent, false));
            //setUnreadMessagesUI();  not used any more, read above
            openMessagesDialog();
        }
        if (messagesRVAdapter != null) {
            messagesRVAdapter.notifyDataSetChanged();
        }

    }

    private int getUnreadMessagesCount() {
        int count = 0;
        for (int i = 0; i < messagesList.size(); i++) {
            if (!messagesList.get(i).isRead()) {
                count++;
            }
        }
        return count;
    }

    private void setMessagesAsRead() {
        for (int i = 0; i < messagesList.size(); i++) {
            messagesList.get(i).setRead(true);
        }
    }

    private void setUnreadMessagesUI() {
        if (messagesList != null && messagesList.size() > 0) {
            newMessageTV.setText(String.valueOf(getUnreadMessagesCount()));
            newMessageTV.setVisibility(View.VISIBLE);
        } else {
            newMessageTV.setVisibility(View.GONE);
        }
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            //2 min time interval dialog
            boolean isTablet = getResources().getBoolean(R.bool.is_tab);
            if (isTablet) {
                sessionTimeOut();
            } else {
                showErrorDialog(getResources().getString(R.string.time_interval_dialog_title),
                        getResources().getString(R.string.time_interval_dialog_msg));
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(TAG, "##" + String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            ));
        }
    }

    public void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cdtInterval.cancel();
                cdtInterval.start();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishSession(false);
            }
        });
        builder.show();
    }

    private void showInfoDialog() {
        final Dialog infoDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        infoDialog.setContentView(R.layout.activity_splash_screen);
        ImageView imgClose = (ImageView) infoDialog.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
                setTopLayout();
            }
        });

        infoDialog.show();
    }
}
