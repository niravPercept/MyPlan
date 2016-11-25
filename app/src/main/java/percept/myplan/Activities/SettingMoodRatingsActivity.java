package percept.myplan.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.SidaSchedule;
import percept.myplan.R;
import percept.myplan.adapters.SidaScheduleAdapter;
import percept.myplan.receivers.MyReceiver;

import static percept.myplan.Global.Constant.NOTI_REQ_CODE_MOOD_1;
import static percept.myplan.Global.Constant.NOTI_REQ_CODE_MOOD_2;
import static percept.myplan.Global.Constant.NOTI_REQ_CODE_SIDAS;
import static percept.myplan.Global.Constant.removeAlarms;

public class SettingMoodRatingsActivity extends AppCompatActivity {


    private SwitchCompat SWITCH_SIDAS, SWITCH_MOOD;
    private LinearLayout LAY_SIDAS, layMood, llNotificationOne, llNotificationTwo;
    private RecyclerView rcvSidas;
    private ArrayList<SidaSchedule> listSidasSchedule = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;
    private CheckBox chkOneDay, chkTwoDay;
    private TextView tvMoodTimeTitle, tvAlarmOne, tvAlarmTwo;
    private int countMoodRatingNotification = 0;
    private String strAlarms = "";
    private int _interval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_ratings);
        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.title_activity_moodratings));

        SWITCH_MOOD = (SwitchCompat) findViewById(R.id.switchMood);
        SWITCH_SIDAS = (SwitchCompat) findViewById(R.id.switchSidas);

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);


        chkOneDay = (CheckBox) findViewById(R.id.chkOneDay);
        chkTwoDay = (CheckBox) findViewById(R.id.chkTwoDay);

        LAY_SIDAS = (LinearLayout) findViewById(R.id.laySidas);
        layMood = (LinearLayout) findViewById(R.id.layMood);
        llNotificationOne = (LinearLayout) findViewById(R.id.llNotificationOne);
        llNotificationTwo = (LinearLayout) findViewById(R.id.llNotificationTwo);

        rcvSidas = (RecyclerView) findViewById(R.id.rcvSidas);

        tvMoodTimeTitle = (TextView) findViewById(R.id.tvMoodTimeTitle);
        tvAlarmOne = (TextView) findViewById(R.id.tvAlarmOne);
        tvAlarmTwo = (TextView) findViewById(R.id.tvAlarmTwo);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SettingMoodRatingsActivity.this);
        rcvSidas.setLayoutManager(mLayoutManager);
        rcvSidas.setItemAnimator(new DefaultItemAnimator());
        getSetting();

        chkOneDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    chkTwoDay.setChecked(false);
                } else {
                    chkTwoDay.setChecked(true);
                }
            }
        });
        chkTwoDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    tvMoodTimeTitle.setText(getString(R.string.mood_times));
                    chkOneDay.setChecked(false);
                    llNotificationTwo.setVisibility(View.VISIBLE);
                } else {
                    tvMoodTimeTitle.setText(getString(R.string.mood_time));
                    chkOneDay.setChecked(true);
                    llNotificationTwo.setVisibility(View.GONE);
                }
            }
        });

        SWITCH_SIDAS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    LAY_SIDAS.setVisibility(View.VISIBLE);
                else
                    LAY_SIDAS.setVisibility(View.GONE);
            }
        });

        SWITCH_MOOD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    layMood.setVisibility(View.VISIBLE);
                    chkOneDay.setChecked(true);
                } else {
                    layMood.setVisibility(View.GONE);
                }
            }
        });
        tvAlarmOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeDialog(tvAlarmOne);
            }
        });
        tvAlarmTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeDialog(tvAlarmTwo);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else if (item.getItemId()==R.id.action_saveNote)
        {
            if (!General.checkInternetConnection(SettingMoodRatingsActivity.this))
                return false;
            saveSetting();
        }
        return false;
    }

    private void saveSetting() {
        int _sidas = SWITCH_SIDAS.isChecked() ? 1 : 0;
        int _mood = SWITCH_MOOD.isChecked() ? 1 : 0;
        _interval = 1;

        for (int i = 0; i < listSidasSchedule.size(); i++) {
            if (listSidasSchedule.get(i).isSelected()) {
                _interval = i + 1;
                break;
            }
        }
        mProgressDialog = new ProgressDialog(SettingMoodRatingsActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("sidas", String.valueOf(_sidas));
        params.put("interval", String.valueOf(_interval));
        params.put("mood", String.valueOf(_mood));
        if (_mood == 1) {
            if (chkOneDay.isChecked())
                countMoodRatingNotification = 1;
            else countMoodRatingNotification = 2;

            params.put("countperday", String.valueOf(countMoodRatingNotification));
            strAlarms = tvAlarmOne.getText().toString().trim();
            if (countMoodRatingNotification == 2)
                strAlarms += "," + tvAlarmTwo.getText().toString().trim();
            params.put("time", strAlarms);
        } else countMoodRatingNotification = 0;
        try {
            new General().getJSONContentFromInternetService(SettingMoodRatingsActivity.this, General.PHPServices.SAVE_SETTINGS, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    mProgressDialog.dismiss();
                    removeAlarms(getApplicationContext());
                    setAlarms();
                    SettingMoodRatingsActivity.this.finish();
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

                            saveSetting();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void getSetting() {
        mProgressDialog = new ProgressDialog(SettingMoodRatingsActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        try {
            new General().getJSONContentFromInternetService(SettingMoodRatingsActivity.this,
                    General.PHPServices.GET_SETTINGS, params, true, false, true, new VolleyResponseListener() {
                        @Override
                        public void onError(VolleyError message) {
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            mProgressDialog.dismiss();
                            Log.d(":::GET SETTING", response.toString());
                            try {
                                if (response.has(Constant.DATA)) {

                                    JSONObject jsonObject = response.getJSONObject(Constant.DATA);
                                    if (jsonObject.getInt("mood") == 1)
                                        SWITCH_MOOD.setChecked(true);
                                    else SWITCH_MOOD.setChecked(false);

                                    if (jsonObject.getInt("sidas") == 1)
                                        SWITCH_SIDAS.setChecked(true);
                                    else SWITCH_SIDAS.setChecked(false);
                                    int _interval = 1;
                                    if (jsonObject.has("interval"))
                                        _interval = jsonObject.getInt("interval");

                                    String _sidasInterval[] = getResources().getStringArray(R.array.sidas_interval);
                                    for (int i = 0; i < _sidasInterval.length; i++) {
                                        if (i == (_interval - 1))
                                            listSidasSchedule.add(new SidaSchedule(_sidasInterval[i], true));
                                        else
                                            listSidasSchedule.add(new SidaSchedule(_sidasInterval[i], false));
                                    }

                                    countMoodRatingNotification = Integer.parseInt(jsonObject.getString("countperday"));
                                    if (countMoodRatingNotification == 1)
                                        chkOneDay.setChecked(true);
                                    else if (countMoodRatingNotification == 2)
                                        chkTwoDay.setChecked(true);
                                    strAlarms = jsonObject.getString("time");
                                    if (!TextUtils.isEmpty(strAlarms)) {
                                        String[] arrAlarms = TextUtils.split(strAlarms, ",");
                                        tvAlarmOne.setText(arrAlarms[0]);
                                        if (arrAlarms.length > 1)
                                            tvAlarmTwo.setText(arrAlarms[1]);
                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                SWITCH_MOOD.setChecked(true);
                                SWITCH_SIDAS.setChecked(true);
                                String _sidasInterval[] = getResources().getStringArray(R.array.sidas_interval);
                                for (int i = 0; i < _sidasInterval.length; i++) {
                                    if (i == 0)
                                        listSidasSchedule.add(new SidaSchedule(_sidasInterval[i], true));
                                    else
                                        listSidasSchedule.add(new SidaSchedule(_sidasInterval[i], false));
                                }

                            }
                            removeAlarms(getApplicationContext());
                            setAlarms();

                            rcvSidas.setAdapter(new SidaScheduleAdapter(SettingMoodRatingsActivity.this, listSidasSchedule));

                        }
                    },"Settings");
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getSetting();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

    private void openTimeDialog(final TextView view) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(SettingMoodRatingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                view.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle(getString(R.string.select_time));

        mTimePicker.show();
    }

    private void setAlarms() {
        if (countMoodRatingNotification > 0) {
            if (countMoodRatingNotification == 2) {
                setAlarmOnTime(tvAlarmTwo.getText().toString().trim(), NOTI_REQ_CODE_MOOD_2);
            }
            setAlarmOnTime(tvAlarmOne.getText().toString().trim(), NOTI_REQ_CODE_MOOD_1);
        }

        if (SWITCH_SIDAS.isChecked() && _interval > 0) {
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
            myIntent.putExtra("NOTI_MSG",getString(R.string.notification_mood));
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
        myIntent.putExtra("NOTI_MSG",getString(R.string.notification_sidas));
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
        myIntent.putExtra("NOTI_MSG",getString(R.string.notification_sidas));
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
        myIntent.putExtra("NOTI_MSG",getString(R.string.notification_sidas));
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
        myIntent.putExtra("NOTI_MSG",getString(R.string.notification_sidas));
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
