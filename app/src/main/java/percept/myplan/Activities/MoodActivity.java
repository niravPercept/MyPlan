package percept.myplan.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.p_v.flexiblecalendar.FlexibleCalendarView;
import com.p_v.flexiblecalendar.view.BaseCellView;
import com.p_v.flexiblecalendar.view.SquareCellView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.CustomEvent;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.Mood;
import percept.myplan.R;

public class MoodActivity extends AppCompatActivity implements FlexibleCalendarView.OnMonthChangeListener,
        FlexibleCalendarView.OnDateClickListener {

    Map<String, String> params;
    private FlexibleCalendarView calendarView;
    private TextView TV_CALENDERMONTH, TV_CALENDERYEAR;
    private Map<Integer, List<CustomEvent>> eventMap;
    private ImageView IMG_VSAD, IMG_SAD, IMG_OK, IMG_HAPPY, IMG_VHAPPY;
    private String STR_NOTE = "";
    private List<Mood> LIST_MOOD, LIST_MOOD_TMP;
    private Button BTN_SEEALLNOTE;
    private ProgressDialog mProgressDialog;

    private CoordinatorLayout REL_COORDINATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.mood);

//        initializeEvents();
        eventMap = new HashMap<>();
        calendarView = (FlexibleCalendarView) findViewById(R.id.calendar_view);
        TV_CALENDERMONTH = (TextView) findViewById(R.id.tvCalenderMonth);
        TV_CALENDERYEAR = (TextView) findViewById(R.id.tvCalenderYear);

        IMG_VSAD = (ImageView) findViewById(R.id.imgVerySad);
        IMG_SAD = (ImageView) findViewById(R.id.imgSad);
        IMG_OK = (ImageView) findViewById(R.id.imgOk);
        IMG_HAPPY = (ImageView) findViewById(R.id.imgHappy);
        IMG_VHAPPY = (ImageView) findViewById(R.id.imgVeryHappy);

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        BTN_SEEALLNOTE = (Button) findViewById(R.id.btnSeeAllNote);


        calendarView.setCalendarView(new FlexibleCalendarView.CalendarView() {
            @Override
            public BaseCellView getCellView(int position, View convertView, ViewGroup parent, @BaseCellView.CellType int cellType) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(MoodActivity.this);
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar1_date_cell_view, null);
                }
                if (cellType == BaseCellView.OUTSIDE_MONTH) {
                    cellView.setTextColor(getResources().getColor(R.color.date_outside_month_text_color_activity_1));
                }
                return cellView;
            }

            @Override
            public BaseCellView getWeekdayCellView(int position, View convertView, ViewGroup parent) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(MoodActivity.this);
                    cellView = (SquareCellView) inflater.inflate(R.layout.calendar1_week_cell_view, null);
                }
                return cellView;
            }

            @Override
            public String getDayOfWeekDisplayValue(int dayOfWeek, String defaultValue) {
                return String.valueOf(defaultValue.charAt(0));
            }
        });
        calendarView.setOnMonthChangeListener(this);
        calendarView.setOnDateClickListener(this);

        calendarView.setEventDataProvider(new FlexibleCalendarView.EventDataProvider() {
            @Override
            public List<CustomEvent> getEventsForTheDay(int year, int month, int day) {
                return getEvents(year, month, day);
            }
        });

        updateTitle(calendarView.getSelectedDateItem().getYear(), calendarView.getSelectedDateItem().getMonth());

        calendarView.setDisableAutoDateSelection(true);


        IMG_HAPPY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.goToCurrentDay();
                List<CustomEvent> _Happy = new ArrayList<>();
                _Happy.add(new CustomEvent(R.color.happy));
                eventMap.put(calendarView.getSelectedDateItem().getDay(), _Happy);
                calendarView.refresh();
                MoodRatingAddNoteDialog("2");
            }
        });

        IMG_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.goToCurrentDay();
                List<CustomEvent> _Ok = new ArrayList<>();
                _Ok.add(new CustomEvent(R.color.ok));
                eventMap.put(calendarView.getSelectedDateItem().getDay(), _Ok);
                calendarView.refresh();
                MoodRatingAddNoteDialog("3");
            }
        });

        IMG_VHAPPY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.goToCurrentDay();
                List<CustomEvent> _Vhappy = new ArrayList<>();
                _Vhappy.add(new CustomEvent(R.color.veryhappy));
                eventMap.put(calendarView.getSelectedDateItem().getDay(), _Vhappy);
                calendarView.refresh();
                MoodRatingAddNoteDialog("1");
            }
        });

        IMG_SAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.goToCurrentDay();
                List<CustomEvent> _Sad = new ArrayList<>();
                _Sad.add(new CustomEvent(R.color.sad));
                eventMap.put(calendarView.getSelectedDateItem().getDay(), _Sad);
                calendarView.refresh();
                MoodRatingAddNoteDialog("4");
            }
        });

        IMG_VSAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.goToCurrentDay();
                List<CustomEvent> _vSad = new ArrayList<>();
                _vSad.add(new CustomEvent(R.color.verysad));
                eventMap.put(calendarView.getSelectedDateItem().getDay(), _vSad);
                calendarView.refresh();
                MoodRatingAddNoteDialog("5");
            }
        });

        BTN_SEEALLNOTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(MoodActivity.this, MoodSummaryActivity.class);
                Log.d("::: ", String.valueOf(calendarView.getCurrentMonth()));
                _intent.putExtra("MONTH", calendarView.getCurrentMonth());
                _intent.putExtra("YEAR", calendarView.getCurrentYear());
                startActivity(_intent);
            }
        });
        //Uncomment for enable current date selection
        calendarView.selectDate(Calendar.getInstance().getTime());

        GetMoodCalender();
    }

    private void GetMoodCalender() {
        mProgressDialog = new ProgressDialog(MoodActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        eventMap.clear();
        params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("year", String.valueOf(calendarView.getCurrentYear()));
        params.put("month", String.valueOf(calendarView.getCurrentMonth() + 1));
        try {
            new General().getJSONContentFromInternetService(MoodActivity.this, General.PHPServices.GET_MOODCALENDER, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    try {
                        if (response.get(Constant.DATA) instanceof JSONObject ||
                                response.get(Constant.DATA) instanceof String) {
//                            showMessageOK(response.getString(Constant.DATA));
                            return;
                        }
                        LIST_MOOD_TMP = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<Mood>>() {
                        }.getType());

//                        Collections.sort(LIST_MOOD_TMP, new Comparator<Mood>() {
//                            @Override
//                            public int compare(Mood o1, Mood o2) {
//                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                try {
//                                    Date date = format.parse(o1.getMOOD_DATE());
//                                    Date date2 = format.parse(o2.getMOOD_DATE());
//                                    return date.compareTo(date2);
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                                return 0;
//
//                            }
//                        });
                        LIST_MOOD = new ArrayList<Mood>();
                        int i = 0;
                        while (i < LIST_MOOD_TMP.size()) {
                            Mood _obj = LIST_MOOD_TMP.get(i);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = format.parse(_obj.getMOOD_DATE());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            String strDate = cal.get(Calendar.YEAR) + "-" +
                                    cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DAY_OF_MONTH);
                            boolean flag = false;
                            for (int j = i + 1; j < LIST_MOOD_TMP.size(); j++) {
                                Date date1 = format.parse(LIST_MOOD_TMP.get(j).getMOOD_DATE());
                                Calendar cal1 = Calendar.getInstance();
                                cal1.setTime(date1);
                                String strDate1 = cal1.get(Calendar.YEAR) + "-" +
                                        cal1.get(Calendar.MONTH) + "-" + cal1.get(Calendar.DAY_OF_MONTH);

                                if (strDate.contains(strDate1)) {
                                    _obj.setMEASUREMENT(_obj.getMEASUREMENT() + "|"
                                            + LIST_MOOD_TMP.get(j).getMEASUREMENT());
                                    if (!TextUtils.isEmpty(LIST_MOOD_TMP.get(j).getNOTE()))
                                        _obj.setNOTE(_obj.getNOTE() + "|"
                                                + LIST_MOOD_TMP.get(j).getNOTE());
                                    //

                                    LIST_MOOD_TMP.remove(j);
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) {
                                i++;
                                LIST_MOOD.add(_obj);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Log.i("Mood::::", new Gson().toJson(LIST_MOOD));
                    for (Mood _obj : LIST_MOOD) {

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = format.parse(_obj.getMOOD_DATE());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            int _day = cal.get(Calendar.DAY_OF_MONTH);
                            int _Month = cal.get(Calendar.MONTH);
                            List<CustomEvent> colorLst = new ArrayList<>();
                            if (_Month == calendarView.getCurrentMonth()) {
                                String measurement[] = TextUtils.split(_obj.getMEASUREMENT(), "|");
                                String measures=_obj.getMEASUREMENT();

                                int count = 0;
                                for (String strMeasurement : measurement) {

                                    switch (strMeasurement) {
                                        case "1":
                                            ++count;
                                            colorLst.add(new CustomEvent(R.color.veryhappy));
                                            break;
                                        case "2":
                                            ++count;
                                            colorLst.add(new CustomEvent(R.color.happy));
                                            break;
                                        case "3":
                                            ++count;
                                            colorLst.add(new CustomEvent(R.color.ok));
                                            break;
                                        case "4":
                                            ++count;
                                            colorLst.add(new CustomEvent(R.color.sad));
                                            break;
                                        case "5":
                                            ++count;
                                            colorLst.add(new CustomEvent(R.color.verysad));
                                            break;
                                    }

                                    if (count > 5)
                                        break;
                                }
                                eventMap.put(_day, colorLst);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    mProgressDialog.dismiss();
                    calendarView.refresh();
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
                            GetMoodCalender();
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
        if (item.getItemId() == android.R.id.home) {
            MoodActivity.this.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onDateClick(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
//        Toast.makeText(MoodActivity.this,cal.getTime().toString()+ " Clicked",Toast.LENGTH_SHORT).show();
        String NOTE = "";
        String STATE="";
        for (Mood _obj : LIST_MOOD) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(_obj.getMOOD_DATE());
                Calendar _cal = Calendar.getInstance();
                _cal.setTime(date);
                int _day = _cal.get(Calendar.DAY_OF_MONTH);
                int _Month = _cal.get(Calendar.MONTH);
                if (_day == day && _Month == month) {
                    NOTE = _obj.getNOTE();
                    STATE=_obj.getMEASUREMENT();
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(NOTE)) {
            NoteCalender _dialog = new NoteCalender(MoodActivity.this, NOTE,STATE);
            _dialog.setCanceledOnTouchOutside(true);
            _dialog.show();
        }
    }

    @Override
    public void onMonthChange(int year, int month, int direction) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        updateTitle(year, month);


        GetMoodCalender();
        Log.d(":::::::::: ", String.valueOf(month));
//        eventMap.clear();
//        for (Mood _obj : LIST_MOOD) {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            try {
//                Date date = format.parse(_obj.getMOOD_DATE());
//                Calendar _cal = Calendar.getInstance();
//                _cal.setTime(date);
//                int _day = _cal.get(Calendar.DAY_OF_MONTH);
//                int _Month = _cal.get(Calendar.MONTH);
//                List<CustomEvent> colorLst = new ArrayList<>();
//                if (_Month == month) {
//                    switch (_obj.getMEASUREMENT()) {
//                        case "1":
//                            colorLst.add(new CustomEvent(R.color.veryhappy));
//                            eventMap.put(_day, colorLst);
//                            break;
//                        case "2":
//                            colorLst.add(new CustomEvent(R.color.happy));
//                            eventMap.put(_day, colorLst);
//                            break;
//                        case "3":
//                            colorLst.add(new CustomEvent(R.color.ok));
//                            eventMap.put(_day, colorLst);
//                            break;
//                        case "4":
//                            colorLst.add(new CustomEvent(R.color.sad));
//                            eventMap.put(_day, colorLst);
//                            break;
//                        case "5":
//                            colorLst.add(new CustomEvent(R.color.verysad));
//                            eventMap.put(_day, colorLst);
//                            break;
//                    }
//                }
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//        calendarView.refresh();
    }

    private void MoodRatingAddNoteDialog(final String mood) {
        fragmentAddNoteCalender _dialog = new fragmentAddNoteCalender(MoodActivity.this, mood);
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.show();

    }

    private void SubmitTodayMood(final String mood) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("note", STR_NOTE);
        params.put("measurement", mood);
        try {
            if (!General.checkInternetConnection(MoodActivity.this))
                return;
            new General().getJSONContentFromInternetService(MoodActivity.this, General.PHPServices.ADD_MOOD, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {

                }

                @Override
                public void onResponse(JSONObject response) {
                    GetMoodCalender();
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
                            SubmitTodayMood(mood);
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

//    private void initializeEvents() {
//        eventMap = new HashMap<>();
//        List<CustomEvent> colorLst = new ArrayList<>();
//        colorLst.add(new CustomEvent(R.color.sad));
//        eventMap.put(25, colorLst);
//
//        List<CustomEvent> colorLst1 = new ArrayList<>();
//        colorLst1.add(new CustomEvent(R.color.veryhappy));
//        eventMap.put(22, colorLst1);
//
//        List<CustomEvent> colorLst2 = new ArrayList<>();
//        colorLst2.add(new CustomEvent(R.color.ok));
//        eventMap.put(28, colorLst2);
//
//        List<CustomEvent> colorLst3 = new ArrayList<>();
//        colorLst3.add(new CustomEvent(R.color.verysad));
//        eventMap.put(29, colorLst3);
//    }

    public List<CustomEvent> getEvents(int year, int month, int day) {
        return eventMap.get(day);
    }


    private void updateTitle(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        TV_CALENDERMONTH.setText("Calender - " + cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
                this.getResources().getConfiguration().locale));

        TV_CALENDERYEAR.setText(String.valueOf(year));

//        someTextView.setText(cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
//                this.getResources().getConfiguration().locale) + " " + year);
    }

    private void showMessageOK(String message) {
        new AlertDialog.Builder(MoodActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        MoodActivity.this.finish();
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    public class fragmentAddNoteCalender extends Dialog {

        private EditText EDT_NOTE;
        private Button BTN_SAVENOTE;
        private String mood;
        private Context CONTEXT;

        public fragmentAddNoteCalender(Context context, String mood) {
            super(context, R.style.DialogTheme);
            CONTEXT = context;
            this.mood = mood;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            setContentView(R.layout.lay_moodratings_savenote);

            EDT_NOTE = (EditText) findViewById(R.id.edtNote);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(EDT_NOTE, InputMethodManager.SHOW_IMPLICIT);
            BTN_SAVENOTE = (Button) findViewById(R.id.btnSaveNote);

            BTN_SAVENOTE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(":::: ", EDT_NOTE.getText().toString());
                    STR_NOTE = EDT_NOTE.getText().toString().trim();
                    SubmitTodayMood(mood);
                    fragmentAddNoteCalender.this.dismiss();


                }
            });
            findViewById(R.id.imgCloseMoodRating).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentAddNoteCalender.this.dismiss();
                }
            });
        }
    }

    public class NoteCalender extends Dialog {

        private TextView TV_NOTE;
        private String NOTE;
        private String STATE;
        private Context CONTEXT;
        private ImageView img;

        String as="";


        ListView lst;


        public NoteCalender(Context context, String note,String state) {
            super(context, R.style.DialogTheme);
            CONTEXT = context;
            NOTE = note;
            STATE=state;
        }



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.lay_note);

            lst= (ListView) findViewById(R.id.listpop_mood);


           // String note[] = TextUtils.split(NOTE, "|");

            /*for (String stnote : note ){

                if(stnote.length()!=0){
                    if (!stnote.equals('|')) {
                        if (stnote.length() != 0) {
                            as.concat(stnote);
                        }else {
                            if (as.length()!=0)
                                   note1.add(as);
                        }
                    }
                }
            }
*/
//            TV_NOTE = (TextView) findViewById(R.id.tvNote);
//             img= (ImageView) findViewById(R.id.colorstate1);
//
//            TV_NOTE.setText(NOTE.replace("|", "\n"));

//            if (STATE.equals("1"))
//                laystate.setBackgroundColor(R.color.veryhappy);
//            if (STATE.equals("2"))
//                laystate.setBackgroundColor(R.color.happy);
//            if (STATE.equals("3"))
//                laystate.setBackgroundColor(R.color.ok);
//            if (STATE.equals("4"))
//                laystate.setBackgroundColor(R.color.sad);
//            if (STATE.equals("5"))
//                laystate.setBackgroundColor(R.color.verysad);


           /*String measurement[]=null;
            List<String> arr=new ArrayList<String>();
            if(STATE.contains("|")) {
                measurement = STATE.split("|");
                arr = Arrays.asList(measurement);
            }
            else{
                arr.add(STATE);
            }*/

           /* String Notess[]=null;
            List<String> note1=new ArrayList<String>();
            if(NOTE.contains("|")) {
                Notess = NOTE.split("|");

                note1 = Arrays.asList(Notess);
            }
            else{
                note1.add(NOTE);
            }*/
            List<String> note1=new ArrayList<String>();
            StringTokenizer token = new StringTokenizer(NOTE, "|");

//            ArrayList note1= new ArrayList();

            while (token.hasMoreTokens() ) {
                note1.add(token.nextToken());
            }

            List<String> arr=new ArrayList<String>();
            StringTokenizer toke = new StringTokenizer(STATE, "|");

            while (toke.hasMoreTokens() ) {
                arr.add(toke.nextToken());
            }


           /* int cout1 = 0;
            for (String strMeasurement : measurement) {

                switch (strMeasurement) {
                    case "1":
                        ++count1;
                        arr.add(strMeasurement);
//                        img.setImageResource(R.drawable.circcle_happy);
//                        laystate.setBackgroundColor(R.color.veryhappy);
                        break;
                    case "2":
                        ++count1;
                        arr.add(strMeasurement);
//                        img.setImageResource(R.drawable.circle_happy);
//                        laystate.setBackgroundColor(R.color.happy);
                        break;
                    case "3":
                        ++count1;
                        arr.add(strMeasurement);
//                        img.setImageResource(R.drawable.circle_ok);
//                        laystate.setBackgroundColor(R.color.ok);
                        break;
                    case "4":
                        ++count1;
                        arr.add(strMeasurement);
//                        img.setImageResource(R.drawable.circle_sad);
//                        laystate.setBackgroundColor(R.color.sad);
                        break;
                    case "5":
                        ++count1;
                        arr.add(strMeasurement);
//                        img.setImageResource(R.drawable.circle_verysad);
//                        laystate.setBackgroundColor(R.color.verysad);
                        break;
                }
                if (count1 > 5)
                    break;
            }*/


            mooddatewise nodeadapter=new mooddatewise(MoodActivity.this,note1,arr);
            lst.setAdapter(nodeadapter);
        }
    }

    class  mooddatewise extends BaseAdapter{

        List<String> adnote,adstate;
        Context context;
        ImageView img_mood;
        TextView txtmood;

        public mooddatewise(MoodActivity moodActivity, List<String> note1, List<String> arr) {
            this.context=moodActivity;
            this.adnote=note1;
            this.adstate=arr;
        }

        @Override
        public int getCount() {
            return adstate.size();
        }

        @Override
        public Object getItem(int i) {
            return adstate.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v=View.inflate(getApplicationContext(),R.layout.popup_mood_date,null);
            if (v!=null){
                img_mood= (ImageView) v.findViewById(R.id.img_mood_popup);
                txtmood= (TextView) v.findViewById(R.id.txt_mood_popup);
            }

            txtmood.setText(adnote.get(i));

            String a=adstate.get(i);

            switch (a){
                case "1":
                     img_mood.setImageResource(R.drawable.circcle_happy);
                    break;
                case "2":
                    img_mood.setImageResource(R.drawable.circle_happy);
                    break;
                case "3":
                    img_mood.setImageResource(R.drawable.circle_ok);
                    break;
                case "4":
                    img_mood.setImageResource(R.drawable.circle_sad);
                    break;
                case "5":
                    img_mood.setImageResource(R.drawable.circle_verysad);
                    break;
            }


            return v;
        }
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
