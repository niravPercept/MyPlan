package percept.myplan.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import percept.myplan.R;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class MyIntentService extends IntentService {

    private static int NOTIFICATION_ID = 1;
    Notification NOTI;
    private NotificationManager NOTI_MANAGER;
    private PendingIntent PENDING_INTENT;

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Bundle _bundle = intent.getExtras();
            String _URI = _bundle.getString("ALARM_SOUND");
            Uri _uri;
            if (!TextUtils.isEmpty(_URI))
                _uri = Uri.parse(_URI);
            else _uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            Context context = this.getApplicationContext();

            NOTI_MANAGER = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent mIntent = new Intent(this, HomeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("FROM", "NOTIFICATION");
            mIntent.putExtras(bundle);
            PENDING_INTENT = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Resources res = this.getResources();
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            NOTI = new NotificationCompat.Builder(this)
                    .setContentIntent(PENDING_INTENT)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                    .setTicker("ticker value")
                    .setAutoCancel(true)
                    .setPriority(8)
                    .setSound(_uri)
                    .setContentTitle("Reminder")
                    .setContentText("Remember Your MoodRating For Today.").build();
            NOTI.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
//            NOTI.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
            NOTI.ledARGB = 0xFFFFA500;
            NOTI.ledOnMS = 800;
            NOTI.ledOffMS = 1000;
            NOTI_MANAGER = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NOTI_MANAGER.notify(NOTIFICATION_ID, NOTI);

            Log.i("notif", "Notifications sent.");

        }
    }


}
