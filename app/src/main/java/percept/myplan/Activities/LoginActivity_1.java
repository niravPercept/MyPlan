package percept.myplan.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.R;
import percept.myplan.customviews.PinEntryEditText;
import percept.myplan.receivers.MyReceiver;

import static percept.myplan.Global.Constant.NOTI_REQ_CODE_MOOD_1;
import static percept.myplan.Global.Constant.NOTI_REQ_CODE_MOOD_2;
import static percept.myplan.Global.Constant.NOTI_REQ_CODE_SIDAS;

public class LoginActivity_1 extends AppCompatActivity {

    private TextView TV_FORGETPWD;
    private Utils UTILS;
    private EditText EDT_EMAIL;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;
    private PinEntryEditText pinEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_1);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.app_name));

        UTILS = new Utils(LoginActivity_1.this);
        TV_FORGETPWD = (TextView) findViewById(R.id.tvForgotPwd);
        EDT_EMAIL = (EditText) findViewById(R.id.edtEmail);
        pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);


        if (!UTILS.getPreference(Constant.PREF_EMAIL).equals("")) {
            EDT_EMAIL.setText(UTILS.getPreference(Constant.PREF_EMAIL));
            pinEntry.requestFocus();
        }

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);


        if (pinEntry != null) {
            pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
                @Override
                public void onPinEntered(CharSequence str) {
//                    if (str.toString().equals("1234")) {
//                    Toast.makeText(LoginActivity_1.this, "SUCCESS", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(LoginActivity_1.this, HomeActivity.class));
//                        LoginActivity_1.this.finish();

                    try {
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        LoginCall(str.toString().trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                        mProgressDialog.dismiss();
                    }

//                    } else {
//                        pinEntry.setError(true);
//                        Toast.makeText(LoginActivity_1.this, "FAIL", Toast.LENGTH_SHORT).show();
//                        pinEntry.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                pinEntry.setText(null);
//                            }
//                        }, 1000);
//                    }
                }
            });
        }

//        startActivity(new Intent(LoginActivity_1.this, HomeActivity.class));
//        LoginActivity_1.this.finish();


        TV_FORGETPWD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity_1.this, ForgetPwdActivity.class));
                LoginActivity_1.this.finish();
            }
        });
    }

    private void LoginCall(final String str) {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.USER_NAME, EDT_EMAIL.getText().toString().trim());
        params.put(Constant.PASSWORD, str.toString().trim());
        mProgressDialog = new ProgressDialog(LoginActivity_1.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        try {
            new General().getJSONContentFromInternetService(LoginActivity_1.this, General.PHPServices.LOGIN, params, true, false, true, new VolleyResponseListener() {

                @Override
                public void onError(VolleyError message) {
                    Log.d(":::::::::: ", message.toString());
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(":::::::::: ", response.toString());
                    mProgressDialog.dismiss();
                    try {
                        if (response.has(Constant.DATA)) {
                            if (response.getJSONObject(Constant.DATA).getString(Constant.STATUS).equals("Success")) {
                                Constant.SID = response.getJSONObject(Constant.DATA).getString("sid");
                                Constant.SNAME = response.getJSONObject(Constant.DATA).getString("sname");
                                Constant.PROFILE_USER_ID = response.getJSONObject(Constant.DATA).getJSONObject(Constant.USER).getString(Constant.ID);
                                Constant.PROFILE_IMG_LINK = response.getJSONObject(Constant.DATA).getString(Constant.PROFILE_IMAGE);
                                Constant.PROFILE_EMAIL = response.getJSONObject(Constant.DATA).getJSONObject(Constant.USER).getString(Constant.EMAIL);
                                Constant.PROFILE_USER_NAME = response.getJSONObject(Constant.DATA).getJSONObject(Constant.USER).getString(Constant.USER_NAME);
                                Constant.PROFILE_NAME = response.getJSONObject(Constant.DATA).getJSONObject(Constant.USER).getString(Constant.NAME);

                                UTILS.setPreference(Constant.PREF_EMAIL, EDT_EMAIL.getText().toString().trim());
                                startActivity(new Intent(LoginActivity_1.this, HomeActivity.class));
                                LoginActivity_1.this.finish();
                                UTILS.setBoolPrefrences(Constant.PREF_LOCATION, true);
                                UTILS.setBoolPrefrences(Constant.PREF_NOTIFICATION, true);
                                UTILS.setPreference(Constant.PREF_LOGGEDIN, "true");
                                UTILS.setPreference(Constant.PREF_USER_ID, Constant.PROFILE_USER_ID);
                                UTILS.setPreference(Constant.PREF_SID, Constant.SID);
                                UTILS.setPreference(Constant.PREF_SNAME, Constant.SNAME);
                                UTILS.setPreference(Constant.PREF_PROFILE_IMG_LINK, Constant.PROFILE_IMG_LINK);
                                UTILS.setPreference(Constant.PREF_PROFILE_USER_NAME, Constant.PROFILE_USER_NAME);
                                UTILS.setPreference(Constant.PREF_PROFILE_EMAIL, Constant.PROFILE_EMAIL);
                                String names[] = TextUtils.split(Constant.PROFILE_NAME, " ");
                                UTILS.setPreference(Constant.PREF_PROFILE_FNAME, names[0]);
                                if (names.length > 1)
                                    UTILS.setPreference(Constant.PREF_PROFILE_LNAME, names[1]);
                                UTILS.setPreference(Constant.PASSWORD, str.trim());
                                Intent AddAlarmIntent = new Intent("MyPlan.AddAll.Alarm");
                                sendBroadcast(AddAlarmIntent);
                                setAlarms(response);
                            } else {
                                pinEntry.setText("", null);
                                Toast.makeText(LoginActivity_1.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mProgressDialog.dismiss();
                    }
                }
            },"");
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LoginCall(str);
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoginActivity_1.this, LoginActivity.class));
    }

    private void setAlarms(JSONObject response) {
        int countMoodRatingNotification = 0;
        String mood = "", moodNotification1 = "", moodNotification2 = "", sidas = "", interval = "";
        try {
            mood = response.getJSONObject(Constant.DATA).getString("mood");
            if (TextUtils.isEmpty(mood)) {
                mood = "1";
                countMoodRatingNotification = 1;
                moodNotification1 = "09:00";
            } else if (mood.equals("1")) {
                countMoodRatingNotification = Integer.parseInt(response.getJSONObject(Constant.DATA).getString("countperday"));
                String strAlarm[] = TextUtils.split(response.getJSONObject(Constant.DATA).getString("time"), ",");
                if (strAlarm.length > 0)
                    moodNotification1 = strAlarm[0];
                if (strAlarm.length > 1)
                    moodNotification2 = strAlarm[1];
            }
            sidas = response.getJSONObject(Constant.DATA).getString("sidas");
            if (TextUtils.isEmpty(sidas)) {
                sidas = "1";
                interval = "1";
            } else if (sidas.equals("1")) {
                interval = response.getJSONObject(Constant.DATA).getString("interval");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mood.equals("1") && countMoodRatingNotification > 0) {
            if (countMoodRatingNotification == 2) {
                setAlarmOnTime(moodNotification2, NOTI_REQ_CODE_MOOD_2);
            }
            setAlarmOnTime(moodNotification1, NOTI_REQ_CODE_MOOD_1);
        }
        int _interval = Integer.parseInt(interval);
        if (sidas.equals("1") && _interval > 0) {
            switch (_interval) {
                case 1:
                    setAlarmForEveryWeek();
                    break;
                case 2:
                    setAlarmForEvery2Week();
                    break;
                case 3:
                    setAlarmForEvery3Week();
                    break;
                case 4:
                    setAlarmForOnceAMonth();
                    break;
            }
        }

    }

    private void setAlarmOnTime(String time, int requestCode) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = format.parse(time);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
            calendar.set(Calendar.MINUTE, date.getMinutes());
            calendar.set(Calendar.SECOND, 0);
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            Log.i("::::Time", calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE));

            Intent myIntent = new Intent(getApplicationContext(), MyReceiver.class);
            myIntent.putExtra("NOTI_MSG", getString(R.string.notification_mood));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void setAlarmForEveryWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        Intent myIntent = new Intent(getApplicationContext(), MyReceiver.class);
        myIntent.putExtra("NOTI_MSG", getString(R.string.notification_sidas));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NOTI_REQ_CODE_SIDAS, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    private void setAlarmForEvery2Week() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 15);
        }

        Intent myIntent = new Intent(getApplicationContext(), MyReceiver.class);
        myIntent.putExtra("NOTI_MSG", getString(R.string.notification_sidas));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NOTI_REQ_CODE_SIDAS, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 15, pendingIntent);
    }

    private void setAlarmForEvery3Week() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 21);
        }

        Intent myIntent = new Intent(getApplicationContext(), MyReceiver.class);
        myIntent.putExtra("NOTI_MSG", getString(R.string.notification_sidas));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NOTI_REQ_CODE_SIDAS, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 21, pendingIntent);
    }

    private void setAlarmForOnceAMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 15);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 30);
        }
        Intent myIntent = new Intent(getApplicationContext(), MyReceiver.class);
        myIntent.putExtra("NOTI_MSG", getString(R.string.notification_sidas));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NOTI_REQ_CODE_SIDAS, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 30, pendingIntent);
    }
    public void autoScreenTracking(){
        TpaConfiguration config =
                new TpaConfiguration.Builder("d3baf5af-0002-4e72-82bd-9ed0c66af31c", "https://weiswise.tpa.io/")
                        // other config settings
                        .enableAutoTrackScreen(true)
                        .build();
    }
    @Override
    public void onResume() {
        super.onResume();
        AppLifeCycle.getInstance().resumed(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppLifeCycle.getInstance().paused(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        AppLifeCycle.getInstance().stopped(this);
    }
}
