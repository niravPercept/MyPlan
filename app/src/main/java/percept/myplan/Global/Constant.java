package percept.myplan.Global;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import percept.myplan.POJO.Contact;
import percept.myplan.receivers.MyReceiver;

/**
 * Created by percept on 12/7/16.
 */

public class Constant {

    public final static String PREF_EMAIL = "EMAIL";
    public final static String PREF_LOGGEDIN = "LOGGEDIN";
    public final static String PREF_SID = "sid";
    public final static String PREF_USER_ID = "userId";
    public final static String PREF_SNAME = "sname";
    public final static String PREF_PROFILE_IMG_LINK = "profileImg";
    public final static String PREF_PROFILE_USER_NAME = "profileUSerName";
    public final static String PREF_PROFILE_EMAIL = "profileEmail";
    public final static String PREF_PROFILE_FNAME = "firstName";
    public final static String PREF_PROFILE_LNAME = "lastName";
    public final static String PREF_LOCATION = "PREF_LOCATION";
    public final static String PREF_NOTIFICATION = "PREF_LOCATION";
    //Login Page
    public final static String USER_NAME = "username";
    public final static String PASSWORD = "password";
    public final static String MESSAGE = "message";
    public final static String STATUS = "status";
    public final static String DATA = "data";
    public final static String USER = "user";
    public final static String NAME = "name";
    public final static String USER_ID = "user_id";
    public final static String CREATED_BY_NAME = "created_by_name";
    //Register Page
    public final static String FIRST_NAME = "first_name";
    public final static String LAST_NAME = "last_name";
    public final static String EMAIL = "email";
    public final static String PHONE = "phone";
    public final static String DOB = "dob";
    public final static String PROFILE_IMAGE = "profile_image";
    //SymptomDetails Pate
    public final static String TITLE = "title";
    public final static String DESC = "description";
    public final static String STRATEGIE = "strategie";
    //Contact Add
    public final static String URL = "url";
    public final static String HELP_COUNT = "HELP_COUNT";
    public final static String ID = "id";
    public final static String CON_IMAGE = "con_image";
    public final static String SKYPE = "skype";
    public final static String HELPLIST = "helplist";
    public final static String NOTE = "note";
    public final static String COMPANY_NAME = "company_name";
    public final static String WEB_ADDRESS = "web_address";
    public final static String RINGTONE = "ringtone";
    public final static String ADDRESS = "address";
    //add Strategy
    public final static String CONTACTID = "contacts";
    public final static String LINK = "link";
    //Hope Element
    public final static String HOPE_ID = "hope_id";
    public final static String HOPE_TITLE = "media_title";
    public final static String HOPE_TYPE = "type";
    public static final String APP_MEDIA_PATH = Environment.getExternalStorageDirectory()
            + File.separator + "MyPlan";
    //Notification REQ Code
    public static final int NOTI_REQ_CODE_SIDAS = 500;
    public static final int NOTI_REQ_CODE_MOOD_1 = 501;
    public static final int NOTI_REQ_CODE_MOOD_2 = 502;
    public static int CURRENT_FRAGMENT = 0;
    public static String SID = "";
    public static String SNAME = "";
    public static String PROFILE_IMG_LINK = "";
    public static String PROFILE_USER_NAME = "";
    public static String PROFILE_EMAIL = "";
    public static String PROFILE_NAME = "";
    public static String PROFILE_USER_ID = "";

    public static File getOutputMediaFile() {
        // External sdcard location
        File mediaStorageDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyPlan");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + "IMG.jpg");
        return mediaFile;
    }

    public static boolean copyFile(String from, String to, String name) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                int end = from.toString().lastIndexOf("/");
                String str1 = from.toString().substring(0, end);
                String str2 = from.toString().substring(end + 1, from.length());
                File source = new File(str1, str2);
                File destination = new File(to, name);
                if (!destination.exists())
                    destination.createNewFile();

                if (source.exists()) {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(destination);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void removeAlarms(Context context) {
        Intent myIntent = new Intent(context, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, NOTI_REQ_CODE_MOOD_1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, NOTI_REQ_CODE_MOOD_2, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntentSidas = PendingIntent.getBroadcast(context, NOTI_REQ_CODE_SIDAS, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        alarmManager.cancel(pendingIntent1);
        alarmManager.cancel(pendingIntentSidas);
    }
}
