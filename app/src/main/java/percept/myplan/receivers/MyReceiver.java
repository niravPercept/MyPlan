package percept.myplan.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import percept.myplan.Global.Constant;
import percept.myplan.Global.Utils;
import percept.myplan.R;
import percept.myplan.SplashActivity;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("MyReceiver", "MyReceiver");
        if (new Utils(context).getBoolPref(Constant.PREF_NOTIFICATION)) {
            String notificationMsg = intent.getStringExtra("NOTI_MSG");
            NotificationManager mManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(context, SplashActivity.class);
            Notification notification;
            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(context);

            builder.setContentTitle(context.getString(R.string.app_name))
                    .setContentText(notificationMsg)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pendingNotificationIntent)
                    .setAutoCancel(true);
            builder.build();

            notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults = Notification.DEFAULT_ALL;

            mManager.notify(0, notification);
        }

    }
}