package percept.myplan.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import percept.myplan.Global.Constant;
import percept.myplan.Global.Utils;
import percept.myplan.POJO.Alarm;

/**
 * Created by percept on 21/9/16.
 */

public class AlarmRemoveReceiver extends BroadcastReceiver {
    String[] Vocabularies;
    String[] meanings;
    private HashMap<String, List<Alarm>> MAP_ALARM;
    private List<Alarm> LIST_ALARM;
    private String STRATEGY_ID;
    private Context context;


    public AlarmRemoveReceiver() {
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
        if (TextUtils.isEmpty(STRATEGY_ID)) {
            for (String key : MAP_ALARM.keySet()) {
                if (key.contains(Constant.PROFILE_USER_ID)) {
                    LIST_ALARM = MAP_ALARM.get(key);
                    if (LIST_ALARM != null) {
                        String arrKey[] = key.split("_");
                        for (Alarm alarm : LIST_ALARM) {
                            String repeat[] = TextUtils.split(alarm.getAlarmRepeat(), ",");
                            if (repeat != null) {
                                for (int j = 0; j < repeat.length; j++) {
                                    removeAlarms(Integer.parseInt(Constant.PROFILE_USER_ID + arrKey[1] + j));
                                }
                            }

                        }
                    }
                }
            }
        } else {
            LIST_ALARM = MAP_ALARM.get(Constant.USER_ID + "_" + STRATEGY_ID);
            if (LIST_ALARM != null) {
                for (Alarm alarm : LIST_ALARM) {
                    String repeat[] = TextUtils.split(alarm.getAlarmRepeat(), ",");
                    if (repeat != null) {
                        for (int j = 0; j < repeat.length; j++) {
                            removeAlarms(Integer.parseInt(Constant.PROFILE_USER_ID + STRATEGY_ID + j));
                        }
                    }

                }
            }

        }


    }


    private void removeAlarms(int requestCode) {

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
