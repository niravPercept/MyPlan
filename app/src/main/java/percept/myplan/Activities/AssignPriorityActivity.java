package percept.myplan.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.R;

/**
 * Created by percept on 22/8/16.
 */
public class AssignPriorityActivity extends AppCompatActivity {
    private TextView tvHelp, tvEmergency;
    private int con_priority = 0; // 0: Default 1:Help  2:Emergency
    private int _count = 0;
    private CheckBox imgTickHelp, imgTickEmergency;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_priority);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.assign_priority));

        tvEmergency = (TextView) findViewById(R.id.tvEmergency);
        tvHelp = (TextView) findViewById(R.id.tvHelp);
        imgTickHelp = (CheckBox) findViewById(R.id.imgTickHelp);
        imgTickEmergency = (CheckBox) findViewById(R.id.imgTickEmergency);

        getContacts();
        if (getIntent().hasExtra(Constant.HELP_COUNT)) {
            _count = getIntent().getIntExtra(Constant.HELP_COUNT, 0);
        }

        if (_count < 10 && getIntent().getStringExtra("ADD_TO_HELP").equals("1")) {
            imgTickHelp.setChecked(true);
        }
        if (getIntent().hasExtra("FROM_EMERGENCY"))
            imgTickEmergency.setChecked(true);

//        tvEmergency.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tvEmergency.setBackgroundColor(getResources().getColor(R.color.sidemenu_seperator));
//                tvHelp.setBackgroundColor(getResources().getColor(android.R.color.white));
////                con_priority = 2;
//            }
//        });
//
//        tvHelp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (_count >= 10) {
//                    Toast.makeText(AssignPriorityActivity.this, getString(R.string.help_contact_validate), Toast.LENGTH_LONG).show();
//                    return;
//                }
//                tvHelp.setBackgroundColor(getResources().getColor(R.color.sidemenu_seperator));
//                tvEmergency.setBackgroundColor(getResources().getColor(android.R.color.white));
////                con_priority = 1;
//            }
//        });
        imgTickHelp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSelect) {
//                if (_count >= 10) {
//                    Toast.makeText(AssignPriorityActivity.this, getString(R.string.help_contact_validate), Toast.LENGTH_LONG).show();
//                    compoundButton.setChecked(true);
//                    return;
//                } else if (!isSelect)
//                    imgTickEmergency.setChecked(false);
//                else
//                    imgTickEmergency.setChecked(true);
                if (!isSelect)
                    imgTickHelp.setChecked(false);
                else {
                    if (_count > 10)
                        imgTickHelp.setChecked(true);
                }
            }
        });
        imgTickEmergency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isSelect) {
                if (!isSelect)
                    imgTickHelp.setChecked(false);
                else {
                    if (_count > 10)
                        imgTickHelp.setChecked(true);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_saveNote) {
            Intent intent = new Intent();

            if (imgTickEmergency.isChecked() && imgTickHelp.isChecked() ){

            }else if(!imgTickEmergency.isChecked() && !imgTickHelp.isChecked()){
                con_priority = 0;
            }else if (imgTickEmergency.isChecked())
                con_priority = 2;
            else con_priority = 1;
            intent.putExtra("FROM_PRIORITY", con_priority);
            setResult(Activity.RESULT_OK, intent);
            AssignPriorityActivity.this.finish();
            return true;
        }
        return false;
    }

    private void getContacts() {
        mProgressDialog = new ProgressDialog(AssignPriorityActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("helplist", "1");
        try {
            new General().getJSONContentFromInternetService(AssignPriorityActivity.this, General.PHPServices.GET_CONTACTS, params, true, false, false, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(":::::::::::::: ", response.toString());

                    try {
                        JSONArray jsonArray = response.getJSONArray(Constant.DATA);
                        if (jsonArray != null)
                            _count = jsonArray.length();
                        mProgressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            },"_1");
        } catch (Exception e) {
            mProgressDialog.dismiss();
            e.printStackTrace();
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
