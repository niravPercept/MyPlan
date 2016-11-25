package percept.myplan.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import percept.myplan.Activities.HomeActivity;
import percept.myplan.Global.Constant;
import percept.myplan.Global.Utils;
import percept.myplan.R;
import percept.myplan.services.MyIntentService;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(":::::::::::::::: Alarm ", "BroadCast received.");

        if (new Utils(context).getBoolPref(Constant.PREF_NOTIFICATION)) {
            if (intent != null) {
                Bundle _bundle = intent.getExtras();
                String _URI = _bundle.getString("ALARM_SOUND");
                String _title = _bundle.getString("ALARM_TITLE");
                Uri _uri;
                if (!TextUtils.isEmpty(_URI))
                    _uri = Uri.parse(_URI);
                else _uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


                Intent mIntent = new Intent(context, HomeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("FROM", "NOTIFICATION");
                mIntent.putExtras(bundle);
                PendingIntent PENDING_INTENT = PendingIntent.getActivity(context, 0, mIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                Notification NOTI = new NotificationCompat.Builder(context)
                        .setContentIntent(PENDING_INTENT)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                context.getResources(), R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setSound(_uri)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(_title).build();
                NOTI.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
//            NOTI.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
                NOTI.ledARGB = 0xFFFFA500;
                NOTI.ledOnMS = 800;
                NOTI.ledOffMS = 1000;
                NotificationManager NOTI_MANAGER = (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);
                NOTI_MANAGER.notify(0, NOTI);

                Log.i("notif", "Notifications sent.");

            }
        }
    }
}
