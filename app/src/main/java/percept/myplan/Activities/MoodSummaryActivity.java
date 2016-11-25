package percept.myplan.Activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.Mood;
import percept.myplan.R;
import percept.myplan.adapters.MoodSummaryAdapter;

public class MoodSummaryActivity extends AppCompatActivity {

    private RecyclerView LST_MOODSUMMARY;
    private List<Mood> LIST_MOOD;
    Map<String, String> params;
    MoodSummaryAdapter ADAPTER;
    private Utils UTILS;
    private int MONTH;
    private int YEAR;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_summary);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.seenotes);

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        MONTH = getIntent().getExtras().getInt("MONTH");
        YEAR = getIntent().getExtras().getInt("YEAR");
        UTILS = new Utils(MoodSummaryActivity.this);
        LST_MOODSUMMARY = (RecyclerView) findViewById(R.id.lstMoodSummay);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MoodSummaryActivity.this);
        LST_MOODSUMMARY.setLayoutManager(mLayoutManager);
        LST_MOODSUMMARY.setItemAnimator(new DefaultItemAnimator());

        GetMoodSummary();

    }

    private void GetMoodSummary() {
        mProgressDialog = new ProgressDialog(MoodSummaryActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("year", String.valueOf(YEAR));
        params.put("month", String.valueOf(MONTH + 1));
        try {
            new General().getJSONContentFromInternetService(MoodSummaryActivity.this, General.PHPServices.GET_MOODCALENDER, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    try {
                        LIST_MOOD = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<Mood>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < LIST_MOOD.size(); i++) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date = format.parse(LIST_MOOD.get(i).getMOOD_DATE());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            String _suffix = UTILS.getDayOfMonthSuffix(cal.get(Calendar.DAY_OF_MONTH));
                            SimpleDateFormat _getDay = new SimpleDateFormat("EEEE d'" + _suffix + "' ' of ' MMMM");
                            LIST_MOOD.get(i).setMOOD_DATE_STRING(_getDay.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    mProgressDialog.dismiss();
                    ADAPTER = new MoodSummaryAdapter(MoodSummaryActivity.this, LIST_MOOD);
                    LST_MOODSUMMARY.setAdapter(ADAPTER);
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
                            GetMoodSummary();
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
            MoodSummaryActivity.this.finish();
        }
        return false;
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
