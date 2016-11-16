package percept.myplan.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import percept.myplan.Global.Constant;
import percept.myplan.Global.Utils;
import percept.myplan.POJO.Alarm;

/**
 * Created by percept on 21/9/16.
 */

public class AddAllAlarmReceiver extends BroadcastReceiver {
    String[] Vocabularies;
    String[] meanings;
    private HashMap<String, List<Alarm>> MAP_ALARM;
    private List<Alarm> LIST_ALARM;
    private String STRATEGY_ID;
    private Context context;


    public AddAllAlarmReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent != null && intent.hasExtra("STRATEGY_ID"))
            STRATEGY_ID = intent.getStringExtra("STRATEGY_ID");
        Utils UTILS = new Utils(context);
        String _strAlarm = UTILS.getPreference("ALARMLIST");
        try {
            if (!TextUtils.isEmpty(_strAlarm)) {
                Type listType = new TypeToken<HashMap<String, List<Alarm>>>() {

                }.getType();
                try {
                    MAP_ALARM = new Gson().fromJson(_strAlarm, listType);
                } catch (JsonSyntaxException ex) {
                    MAP_ALARM = new HashMap<>();
                }
            } else {
                MAP_ALARM = new HashMap<>();
            }
        } catch (Exception ex) {

        }
        for (String key : MAP_ALARM.keySet()) {
            if (key.contains(Constant.PROFILE_USER_ID)) {
                LIST_ALARM = MAP_ALARM.get(key);
                if (LIST_ALARM != null) {
                    String arrKey[] = key.split("_");
                    for (Alarm alarm : LIST_ALARM) {
                        String repeat[] = TextUtils.split(alarm.getAlarmRepeat(), ",");
                        if (repeat != null) {
                            for (int j = 0; j < repeat.length; j++) {
                                setAlarms(alarm.getAlarmName(), alarm.getAlarmTime(), Integer.parseInt(repeat[j]),
                                        Integer.parseInt(Constant.PROFILE_USER_ID + arrKey[1] + j), alarm.getAlarmTune());
                            }
                        }

                    }
                }
            }
        }


    }


    private void setAlarms(String title, String time, int dayOfWeek, int requestCode, String uri) {

        Date date = new Date(time);

        Calendar calendar = Calendar.getInstance();
        if (dayOfWeek > 0)
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
        calendar.set(Calendar.MINUTE, date.getMinutes());
        calendar.set(Calendar.SECOND, 0);


        Log.i("::::Time", calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE));

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            if (dayOfWeek > 0)
                calendar.add(Calendar.DAY_OF_YEAR, 7);
            else
                calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString("ALARM_SOUND", uri);
        bundle.putString("ALARM_TITLE", title);
        alarmIntent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (dayOfWeek > 0)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}
