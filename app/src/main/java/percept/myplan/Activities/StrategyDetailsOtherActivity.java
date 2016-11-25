package percept.myplan.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Dialogs.dialogYesNoOption;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.Strategy;
import percept.myplan.R;

import static percept.myplan.Activities.AddStrategyToSymptomActivity.GET_STRATEGIES;
import static percept.myplan.fragments.fragmentStrategies.ADDED_STRATEGIES;


public class StrategyDetailsOtherActivity extends AppCompatActivity {

    private final int REQUEST_CODE_DETAIL_ACTIVITY = 1003;
    Map<String, String> params;
    private String STRATEGY_ID;
    private ProgressDialog mProgressDialog;
    private Strategy clsStrategy;
    private TextView TV_DESCRIPTION, TV_USEDBY, TV_SUBMITTEDBY, TV_CATEGORY;
    private Button BTN_ADDTOMYSTRATEGIES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_details_other);
        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.title_activity_strategy_details_other));


        TV_DESCRIPTION = (TextView) findViewById(R.id.tvDescription);
        TV_USEDBY = (TextView) findViewById(R.id.tvUsedBy);
        TV_SUBMITTEDBY = (TextView) findViewById(R.id.tvSubmittedBy);
        TV_CATEGORY = (TextView) findViewById(R.id.tvCategory);

        BTN_ADDTOMYSTRATEGIES = (Button) findViewById(R.id.btnAddToMyStrategies);

        if (getIntent().hasExtra("FROM_SYMPTOM")) {
            BTN_ADDTOMYSTRATEGIES.setVisibility(View.GONE);
        }

        if (getIntent().hasExtra("STRATEGY_ID")) {
            STRATEGY_ID = getIntent().getExtras().getString("STRATEGY_ID");
            mTitle.setText(getIntent().getExtras().getString("STRATEGY_NAME"));
        }
        getStrategyDetailsOther();

        BTN_ADDTOMYSTRATEGIES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!General.checkInternetConnection(StrategyDetailsOtherActivity.this))
                    return;
                dialogYesNoOption _dialogDate = new dialogYesNoOption(StrategyDetailsOtherActivity.this) {
                    @Override
                    public void onClickYes() {
                        dismiss();
                        mProgressDialog = new ProgressDialog(StrategyDetailsOtherActivity.this);
                        mProgressDialog.setMessage(getString(R.string.progress_loading));
                        mProgressDialog.setIndeterminate(false);
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.show();
                        try {
                            new General().getJSONContentFromInternetService(StrategyDetailsOtherActivity.this,
                                    General.PHPServices.ADD_MYSTRATEGY, params, true, false, true, new VolleyResponseListener() {
                                        @Override
                                        public void onError(VolleyError message) {
                                            mProgressDialog.dismiss();
                                        }

                                        @Override
                                        public void onResponse(JSONObject response) {
                                            mProgressDialog.dismiss();
                                            Log.d("::::::: ", response.toString());
                                            ADDED_STRATEGIES = true;
                                            if (getIntent().hasExtra("FROM_SYMPTOM")) {
                                                GET_STRATEGIES = true;
                                            }
                                            if (getIntent().hasExtra("FROM_SYMPTOM_INSPI")) {
                                                GET_STRATEGIES = true;
                                            }
                                            StrategyDetailsOtherActivity.this.finish();

                                        }
                                    }, "");
                        } catch (Exception e) {
                            mProgressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onClickNo() {
                        dismiss();
                    }
                };
                _dialogDate.setCanceledOnTouchOutside(false);
                _dialogDate.show();

            }
        });
        TV_SUBMITTEDBY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clsStrategy != null) {
                    Intent intent = new Intent(StrategyDetailsOtherActivity.this, StrategySubmittedDetailActivity.class);
                    intent.putExtra(Constant.USER_ID, clsStrategy.getCreatedBy());
                    intent.putExtra(Constant.CREATED_BY_NAME, clsStrategy.getCreatedByName());
                    if (getIntent().hasExtra("FROM_SYMPTOM")) {
                        intent.putExtra("FROM_SYMPTOM", getIntent().getExtras().getString("FROM_SYMPTOM"));
                    }
                    if (getIntent().hasExtra("FROM_SYMPTOM_INSPI")) {
                        intent.putExtra("FROM_SYMPTOM_INSPI", getIntent().getExtras().getString("FROM_SYMPTOM_INSPI"));
                    }
                    startActivityForResult(intent, REQUEST_CODE_DETAIL_ACTIVITY);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            StrategyDetailsOtherActivity.this.finish();
            return true;
        }
        return false;
    }

    public void getStrategyDetailsOther() {
        params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("id", STRATEGY_ID);

        try {
            mProgressDialog = new ProgressDialog(StrategyDetailsOtherActivity.this);
            mProgressDialog.setMessage(getString(R.string.progress_loading));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            new General().getJSONContentFromInternetService(StrategyDetailsOtherActivity.this, General.PHPServices.GET_STRATEGY, params,
                    true, false, true, new VolleyResponseListener() {
                        @Override
                        public void onError(VolleyError message) {
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(JSONObject response) {

                            mProgressDialog.dismiss();
                            Log.d(":::: ", response.toString());
                            Gson gson = new Gson();
                            try {
                                clsStrategy = gson.fromJson(response.getJSONObject(Constant.DATA)
                                        .toString(), new TypeToken<Strategy>() {
                                }.getType());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("::: Strategy Details", clsStrategy.getID());
                            TV_DESCRIPTION.setText(clsStrategy.getDescription());
                            TV_USEDBY.setText(clsStrategy.getUsedBy());
                            TV_SUBMITTEDBY.setText("ID " + clsStrategy.getCreatedBy());

                            if (getIntent().hasExtra("CATEGORY_NAME"))
                                TV_CATEGORY.setText(getIntent().getExtras().getString("CATEGORY_NAME"));
                            else
                                TV_CATEGORY.setText(clsStrategy.getCategoryName());
                        }
                    }, "other_" + STRATEGY_ID);
        } catch (Exception e) {
            mProgressDialog.dismiss();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_DETAIL_ACTIVITY:
                if (resultCode == RESULT_OK)
                    StrategyDetailsOtherActivity.this.finish();
                break;
        }
    }

    public void autoScreenTracking() {
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
