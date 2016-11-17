package percept.myplan.Activities;

import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Dialogs.dialogOk;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.MultiPartParsing;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.POJO.Alarm;
import percept.myplan.POJO.StrategyContact;
import percept.myplan.R;
import percept.myplan.receivers.AlarmReceiver;

import static percept.myplan.Activities.StrategyDetailsOwnActivity.LIST_STRATEGYCONTACT;
import static percept.myplan.fragments.fragmentStrategies.ADDED_STRATEGIES;

public class StrategyEditActivity extends AppCompatActivity {

    public static List<Alarm> LIST_ALARM = new ArrayList<>();
    public static List<String> LIST_IMG = new ArrayList<>();
    public static List<String> LIST_MUSIC = new ArrayList<>();
    private final int SET_ALARM = 15;
    private final int SET_CONTACT = 18;
    private final int SET_IMAGE = 21;
    private final int SET_MUSIC = 24;
    private final int SET_LINK = 25;
    private EditText EDT_TITLE, EDT_TEXT;
    private TextView TV_ALARM, TV_IMAGES, TV_LINKS, TV_NETWORK, TV_MUSIC;
    private String STR_CONTACTID = "";
    private HashMap<String, List<Alarm>> MAP_ALARM;
    private Utils UTILS;
    private String STRATEGY_ID;
    private ProgressDialog mProgressDialog;
    private List<String> listLink = new ArrayList<>();

    private CoordinatorLayout REL_COORDINATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_strategy);

        autoScreenTracking();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.strategy));

        STRATEGY_ID = getIntent().getExtras().getString("STRATEGY_ID");

        EDT_TITLE = (EditText) findViewById(R.id.edtTitle);
        EDT_TEXT = (EditText) findViewById(R.id.edtText);

        EDT_TITLE.setText(getIntent().getExtras().getString("STRATEGY_TITLE"));
        EDT_TEXT.setText(getIntent().getExtras().getString("STRATEGY_DESC"));

        TV_ALARM = (TextView) findViewById(R.id.tvAlarm);
        TV_IMAGES = (TextView) findViewById(R.id.tvImages);
        TV_LINKS = (TextView) findViewById(R.id.tvLinks);
        TV_NETWORK = (TextView) findViewById(R.id.tvNetwork);
        TV_MUSIC = (TextView) findViewById(R.id.tvMusic);

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        LIST_ALARM = new ArrayList<>();
        LIST_IMG = new ArrayList<>();
        LIST_MUSIC = new ArrayList<>();
        MAP_ALARM = new HashMap<>();
        UTILS = new Utils(StrategyEditActivity.this);
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
        if (MAP_ALARM.containsKey(Constant.PROFILE_USER_ID + "_" + STRATEGY_ID))
            LIST_ALARM = MAP_ALARM.get(Constant.PROFILE_USER_ID + "_" + STRATEGY_ID);

        for (StrategyContact _obj : LIST_STRATEGYCONTACT) {
            if (STR_CONTACTID.equals("")) {
                STR_CONTACTID += _obj.getID();
            } else {
                STR_CONTACTID += "," + _obj.getID();
            }
        }

        listLink = new Gson().fromJson(getIntent().getExtras().getString("STR_LINK"), new TypeToken<ArrayList<String>>() {
        }.getType());
        TV_ALARM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(StrategyEditActivity.this, AlarmListActivity.class);
                _intent.putExtra("FROM_EDIT", "TRUE");
                startActivityForResult(_intent, SET_ALARM);
            }
        });

        TV_IMAGES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(StrategyEditActivity.this, AddStrategyImageActivity.class);
                _intent.putExtra("FROM_EDIT", "TRUE");
                startActivityForResult(_intent, SET_IMAGE);
            }
        });

        TV_LINKS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(StrategyEditActivity.this, AddStrategyLinksActivity.class);
                _intent.putExtra("FROM_EDIT", "TRUE");
                startActivityForResult(_intent, SET_LINK);
            }
        });

        TV_NETWORK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(StrategyEditActivity.this, AddStrategyContactActivity.class);
                _intent.putExtra("FROM_EDIT", "TRUE");
                startActivityForResult(_intent, SET_CONTACT);
            }
        });

        TV_MUSIC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent _intent = new Intent(StrategyEditActivity.this, AddStrategyMusicActivity.class);
                _intent.putExtra("FROM_EDIT", "TRUE");
                startActivityForResult(_intent, SET_MUSIC);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_strategy, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            LIST_MUSIC.clear();
            StrategyEditActivity.this.finish();
            return true;
        } else if (item.getItemId() == R.id.action_saveStrategy) {



            if(EDT_TITLE.getText().length()==0){
          dialogOk _dialoglert=new dialogOk(StrategyEditActivity.this, getString(R.string.enter_titile)) {
                    @Override
                    public void onClickOk() {
                        dismiss();
                    }
                };
                _dialoglert.setCancelable(false);
                _dialoglert.setCanceledOnTouchOutside(false);
                _dialoglert.show();
            }else {
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


            addStrategy(EDT_TITLE.getText().toString().trim(), EDT_TEXT.getText().toString().trim(),
                    STR_CONTACTID, LIST_IMG, LIST_MUSIC, TextUtils.join(",", listLink));
            return true;

            }
        }
        return false;
    }

    private void addStrategy(final String title, final String text, final String STR_CONTACTID, final List<String> listImg, final List<String> listMusic, final String STR_LINK) {
        if (!UTILS.isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addStrategy(title, text, STR_CONTACTID, listImg, listMusic, STR_LINK);
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();
            return;
        }
        mProgressDialog = new ProgressDialog(StrategyEditActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> params = new HashMap<>();
//        params.put(Constant.URL, getResources().getString(R.string.server_url) + ".saveStrategy");
        // Adding file data to http body

        //add data own audio data
        listMusic.addAll(StrategyDetailsOwnActivity.LISTMUSIC);
        if (listMusic.size() > 0) {
            for (int i = 0; i < listMusic.size(); i++) {
                //params.put("videos" + (i + 1), listMusic.get(i));
            }
        }
        if (listImg.size() > 0) {
            for (int i = 0; i < listImg.size(); i++) {
                params.put("image" + (i + 1), listImg.get(i));
            }
        }
        // Extra parameters if you want to pass to server
        String str= TextUtils.join(",", listMusic);
        params.put(Constant.DESC, text);
        params.put("internal_audio" ,str);


        params.put("image_count", String.valueOf(listImg.size()));
        params.put(Constant.ID, STRATEGY_ID);
        params.put(Constant.LINK, STR_LINK);
        params.put("sid", Constant.SID);
        params.put(Constant.CONTACTID, STR_CONTACTID);
        params.put("sname", Constant.SNAME);
        params.put(Constant.TITLE, title);
        Log.d("paramstargies",params.toString());

//
//
//
//        params.put("music_count", String.valueOf(listMusic.size()));
//
//
//
//
//
        new MultiPartParsing(StrategyEditActivity.this, params, General.PHPServices.SAVE_STRATEGY, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                mProgressDialog.dismiss();
                try {
                    Log.d("::::::Response Stragies", response);
                    String _id = "";
                    JSONObject _object = new JSONObject(response);
                    JSONObject _ObjData = _object.getJSONObject(Constant.DATA);
                    _id = _ObjData.getString(Constant.ID);
                    MAP_ALARM.put(Constant.PROFILE_USER_ID + "_" + _id, LIST_ALARM);
                    Gson gson = new Gson();
                    String _alarmList = gson.toJson(MAP_ALARM);
                    UTILS.setPreference("ALARMLIST", _alarmList);
                    Intent removeAlarmIntent = new Intent("MyPlan.Remove.Alarm");
                    removeAlarmIntent.putExtra("STRATEGY_ID", STRATEGY_ID);
                    sendBroadcast(removeAlarmIntent);
                    for (int i = 0; i < LIST_ALARM.size(); i++) {
                        Alarm alarm = LIST_ALARM.get(i);
                        if (alarm.isStatus()) {
                            String repeat[] = TextUtils.split(alarm.getAlarmRepeat(), ",");
                            if (repeat != null) {
                                for (int j = 0; j < repeat.length; j++) {

                                    setAlarms(alarm.getAlarmName(), alarm.getAlarmTime(), Integer.parseInt(repeat[j]),
                                            Integer.parseInt(Constant.PROFILE_USER_ID + _id + j), alarm.getAlarmTune());
                                }
                            }
                        }
                    }

                    Toast.makeText(StrategyEditActivity.this,
                            getResources().getString(R.string.strategyedit), Toast.LENGTH_SHORT).show();
                    StrategyEditActivity.this.finish();
                    ADDED_STRATEGIES = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
//    private class AddStrategy extends AsyncTask<Void, Integer, String> {
//
//        private String TITLE, TEXT, CONTACT_ID, LINK;
//        private List<String> LST_IMG, LST_MUSIC;
//
//        public AddStrategy(String title, String text, String STR_CONTACTID, List<String> listImg, List<String> listLink, String STR_LINK) {
//            this.TITLE = title;
//            this.TEXT = text;
//            this.CONTACT_ID = STR_CONTACTID;
//            this.LST_IMG = listImg;
//            this.LST_MUSIC = listLink;
//            this.LINK = STR_LINK;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            // setting progress bar to zero
//            super.onPreExecute();
//        }
//
//
//        @Override
//        protected String doInBackground(Void... params) {
//            return uploadFile();
//        }
//
//        @SuppressWarnings("deprecation")
//        private String uploadFile() {
//            String responseString = null;
//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(getResources().getString(R.string.server_url) + ".saveStrategy");
//
//            try {
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new AndroidMultiPartEntity.ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
////                                publishProgress((int) ((num / (float) totalSize) * 100));
//                            }
//                        });
//
////                if (!FILE_PATH.equals("")) {
////                    File sourceFile = new File(FILE_PATH);
//
//                // Adding file data to http body
//                if (LST_IMG.size() > 0) {
//                    for (int i = 0; i < LST_IMG.size(); i++) {
//                        File _f = new File(LST_IMG.get(i));
//                        entity.addPart("image" + (i + 1), new FileBody(_f));
//                    }
//                }
//                if (LST_MUSIC.size() > 0) {
//                    for (int i = 0; i < LST_MUSIC.size(); i++) {
//                        File _f = new File(LST_MUSIC.get(i));
//                        entity.addPart("videos" + (i + 1), new FileBody(_f));
//                    }
//                }
////                }
//                // Extra parameters if you want to pass to server
//                try {
//
//                    entity.addPart("sid", new StringBody(Constant.SID));
//                    entity.addPart("sname", new StringBody(Constant.SNAME));
//                    entity.addPart("image_count", new StringBody(String.valueOf(LST_IMG.size())));
//                    entity.addPart("music_count", new StringBody(String.valueOf(LST_MUSIC.size())));
//                    entity.addPart(Constant.ID, new StringBody(STRATEGY_ID));
//                    entity.addPart(Constant.TITLE, new StringBody(this.TITLE));
//                    entity.addPart(Constant.DESC, new StringBody(this.TEXT));
//                    entity.addPart(Constant.CONTACTID, new StringBody(this.CONTACT_ID));
//                    entity.addPart(Constant.LINK, new StringBody(this.LINK));
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//
////                totalSize = entity.getContentLength();
//                httppost.setEntity(entity);
//                long totalLength = entity.getContentLength();
//                System.out.println("TotalLength : " + totalLength);
//
//                // Making server call
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity r_entity = response.getEntity();
//
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    // Server response
//                    responseString = EntityUtils.toString(r_entity);
//
//                } else {
//                    responseString = "Error occurred! Http Status Code: "
//                            + statusCode;
//                }
//
//            } catch (ClientProtocolException e) {
//                responseString = e.toString();
//            } catch (IOException e) {
//                responseString = e.toString();
//            }
//
//            return responseString;
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            PB.setVisibility(View.GONE);
//            super.onPostExecute(result);
//            try {
//                Log.d(":::::: ", result);
//                String _id = "";
//                JSONObject _object = new JSONObject(result);
//                JSONObject _ObjData = _object.getJSONObject(Constant.DATA);
//                _id = _ObjData.getString(Constant.ID);
//                MAP_ALARM.put(_id, LIST_ALARM);
//                Gson gson = new Gson();
//                String _alarmList = gson.toJson(MAP_ALARM);
//                UTILS.setPreference("ALARMLIST", _alarmList);
//                Toast.makeText(StrategyEditActivity.this,
//                        getResources().getString(R.string.strategyedit), Toast.LENGTH_SHORT).show();
//                StrategyEditActivity.this.finish();
//                ADDED_STRATEGIES = true;
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SET_ALARM) {
            if (resultCode == Activity.RESULT_OK) {
                String _strAlarm = data.getStringExtra("ALARMS");
                Log.d(":::::: ", _strAlarm);
//
//                try {
//                    if (!_strAlarm.equals("") && !_strAlarm.equals("null")) {
//                        Type listType = new TypeToken<List<Alarm>>() {
//
//                        }.getType();
//                        try {
//                            LST_ALARM = new Gson().fromJson(_strAlarm, listType);
//                        } catch (JsonSyntaxException ex) {
//                            LST_ALARM = new ArrayList<Alarm>();
//                        }
//                    } else {
//                        LST_ALARM = new ArrayList<Alarm>();
//                    }
//                } catch (Exception ex) {
//
//                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == SET_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                STR_CONTACTID = data.getStringExtra("CONTACT_ID");
                Log.d(":::::: ", STR_CONTACTID);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == SET_LINK) {
            if (resultCode == Activity.RESULT_OK) {
                if (!TextUtils.isEmpty(data.getStringExtra("LINK")))
                    listLink.add(data.getStringExtra("LINK"));
//                Log.d(":::::: ", STR_LINK);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    public void autoScreenTracking(){
        TpaConfiguration config =
                new TpaConfiguration.Builder("d3baf5af-0002-4e72-82bd-9ed0c66af31c", "https://weiswise.tpa.io/")
                        // other config settings
                        .enableAutoTrackScreen(true)
                        .build();
    }
    private void setAlarms(String title, String time, int dayOfWeek, int requestCode, String uri) {

        //Date date=new Date(Long.parseLong(time));
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");


        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putString("ALARM_SOUND", uri);
        bundle.putString("ALARM_TITLE", title);
        alarmIntent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (dayOfWeek > 0)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        else
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
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


//            ALARM_MANAGER = (AlarmManager) AddAlarmActivity.this.getSystemService(ALARM_SERVICE);
//            // AlarmReceiver1 = broadcast receiver
////            Intent alarmIntent = new Intent(AddAlarmActivity.this, AlarmReceiver.class);
//
//            Intent alarmIntent = new Intent(AddAlarmActivity.this, AlarmReceiver.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("ALARM_SOUND", String.valueOf(uri));
//            alarmIntent.putExtras(bundle);
//
////            alarmIntent.putExtra("ALARM_SOUND",String.valueOf(uri));
//            PendingIntent _pendingIntent = PendingIntent.getBroadcast(AddAlarmActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            AlarmManager manager = (AlarmManager) AddAlarmActivity.this.getSystemService(Context.ALARM_SERVICE);
//            ALARM_MANAGER.cancel(_pendingIntent);
//            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000, _pendingIntent);
//            Toast.makeText(AddAlarmActivity.this, "Alarm Set", Toast.LENGTH_SHORT).show();   }
