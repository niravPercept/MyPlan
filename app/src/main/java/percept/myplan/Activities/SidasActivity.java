package percept.myplan.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.SidaSummary;
import percept.myplan.R;
import percept.myplan.adapters.SidaSummaryAdapter;

public class SidasActivity extends AppCompatActivity {

    private final int REQ_CODE_TEST = 23;
    SidaSummaryAdapter ADAPTER;
    Map<String, String> params;
    private Button BTN_TAKETEST;
    private RecyclerView LST_SIDASUMMARY;
    private List<SidaSummary> LIST_SIDA;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidas);
        autoScreenTracking();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.sidas));

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        LST_SIDASUMMARY = (RecyclerView) findViewById(R.id.lstSidaSummary);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SidasActivity.this);
        LST_SIDASUMMARY.setLayoutManager(mLayoutManager);
        LST_SIDASUMMARY.setItemAnimator(new DefaultItemAnimator());
        LIST_SIDA = new ArrayList<>();




    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLifeCycle.getInstance().resumed(this);
        GetSida();
    }

    private void GetSida() {
        mProgressDialog = new ProgressDialog(SidasActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        try {
            new General().getJSONContentFromInternetService(SidasActivity.this, General.PHPServices.GET_SIDACALENDER, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.get(Constant.DATA) instanceof JSONObject
                                || response.get(Constant.DATA) instanceof String) {
//                            showMessageOK(response.getString(Constant.DATA));
                            return;
                        }
                        Gson gson = new Gson();

                        LIST_SIDA = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<SidaSummary>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressDialog.dismiss();
                    ADAPTER = new SidaSummaryAdapter(SidasActivity.this, LIST_SIDA);
                    LST_SIDASUMMARY.setAdapter(ADAPTER);
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
                            GetSida();
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
            SidasActivity.this.finish();
            return true;
        }
        return false;
    }

    private void showMessageOK(String message) {
        new AlertDialog.Builder(SidasActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        SidasActivity.this.finish();

                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_TEST:
                if (resultCode == RESULT_OK) {
                    Intent _iIntent=new Intent();
                    _iIntent.putExtra("SIDAS_OPEN_STRATEGIES",true);
                    setResult(RESULT_OK,_iIntent);
                    SidasActivity.this.finish();
                }
                break;
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
