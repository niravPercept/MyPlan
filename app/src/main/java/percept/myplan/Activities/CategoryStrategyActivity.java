package percept.myplan.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import percept.myplan.CustomListener.RecyclerTouchListener;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.ClickListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.InspirationWiseStrategy;
import percept.myplan.R;
import percept.myplan.adapters.InspirationWiseStrategyAdapter;

import static percept.myplan.Activities.AddStrategyToSymptomActivity.GET_STRATEGIES;
import static percept.myplan.fragments.fragmentStrategies.ADDED_STRATEGIES;


public class CategoryStrategyActivity extends AppCompatActivity {

    private RecyclerView LST_CATEGORY_STRATEGY;
    private List<InspirationWiseStrategy> LIST_STRATEGY_INSPIRATION;
    private String CATEGORYID;
    private InspirationWiseStrategyAdapter ADAPTER;
    private ProgressDialog mProgressDialog;

    private CoordinatorLayout REL_COORDINATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_strategy);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        if (getIntent().hasExtra("CATEGORY_NAME")) {
            mTitle.setText(getIntent().getExtras().getString("CATEGORY_NAME"));
            CATEGORYID = getIntent().getExtras().getString("CATEGORY_ID");
        }

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        LST_CATEGORY_STRATEGY = (RecyclerView) findViewById(R.id.lstCategoryStrategy);

        LIST_STRATEGY_INSPIRATION = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CategoryStrategyActivity.this);
        LST_CATEGORY_STRATEGY.setLayoutManager(mLayoutManager);
        LST_CATEGORY_STRATEGY.setItemAnimator(new DefaultItemAnimator());
        LST_CATEGORY_STRATEGY.addOnItemTouchListener(new RecyclerTouchListener(CategoryStrategyActivity.this, LST_CATEGORY_STRATEGY, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LIST_STRATEGY_INSPIRATION.get(position);
                Intent _intent = new Intent(CategoryStrategyActivity.this, StrategyDetailsOtherActivity.class);
                _intent.putExtra("STRATEGY_ID", LIST_STRATEGY_INSPIRATION.get(position).getStrategyId());
                _intent.putExtra("STRATEGY_NAME", LIST_STRATEGY_INSPIRATION.get(position).getStrategyName());
                if (getIntent().hasExtra("CATEGORY_NAME"))
                    _intent.putExtra("CATEGORY_NAME", getIntent().getExtras().getString("CATEGORY_NAME"));
                if (getIntent().hasExtra("FROM_SYMPTOM_INSPI"))
                    _intent.putExtra("FROM_SYMPTOM_INSPI", getIntent().getExtras().getString("FROM_SYMPTOM_INSPI"));
                startActivity(_intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        GetCategoryStrategy();

    }

    private void GetCategoryStrategy() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("catid", CATEGORYID);
        try {
            mProgressDialog = new ProgressDialog(CategoryStrategyActivity.this);
            mProgressDialog.setMessage(getString(R.string.progress_loading));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            new General().getJSONContentFromInternetService(CategoryStrategyActivity.this, General.PHPServices.GET_CATEGORY_INSPIRATIONS, params, true, false, true, new VolleyResponseListener() {
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
                        LIST_STRATEGY_INSPIRATION = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<InspirationWiseStrategy>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ADAPTER = new InspirationWiseStrategyAdapter(LIST_STRATEGY_INSPIRATION);
                    LST_CATEGORY_STRATEGY.setAdapter(ADAPTER);
                }
            },CATEGORYID);
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GetCategoryStrategy();
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
            CategoryStrategyActivity.this.finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLifeCycle.getInstance().resumed(this);
        if (ADDED_STRATEGIES) {
            CategoryStrategyActivity.this.finish();

        }
        if (GET_STRATEGIES) {
            CategoryStrategyActivity.this.finish();
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
