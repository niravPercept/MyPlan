package percept.myplan.Activities;

import android.app.Activity;
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
import android.view.Menu;
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
import java.util.Arrays;
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
import percept.myplan.POJO.Strategy;
import percept.myplan.POJO.SymptomStrategy;
import percept.myplan.R;
import percept.myplan.adapters.StrategySelectionAdapter;

import static percept.myplan.Activities.AddNewSymptomActivity.LIST_ADDSYMPTOMSTRATEGY;
import static percept.myplan.Activities.SymptomDetailsActivity.LIST_SYMPTOMSTRATEGY;

public class AddStrategyToSymptomActivity extends AppCompatActivity {


    private TextView TV_ADDSTRATEGY;
    private RecyclerView LST_STRATEGY;
    private List<Strategy> LIST_STRATEGY;
    private StrategySelectionAdapter ADAPTER;
    private Button BTN_INSPIRATION;
    private List<String> LIST_SELECTEDID;
    public static boolean GET_STRATEGIES = false;
    Map<String, String> params;
    private ProgressDialog mProgressDialog;

    private CoordinatorLayout REL_COORDINATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_strategy_to_symptom);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.addastrategy));

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        if (getIntent().hasExtra("ADDED_STRATEGY")) {
            String _str = getIntent().getExtras().getString("ADDED_STRATEGY");

            String[] _arr = _str.split(",");
            LIST_SELECTEDID = new ArrayList<String>(Arrays.asList(_arr));
        } else {
            LIST_SELECTEDID = new ArrayList<>();
        }

        TV_ADDSTRATEGY = (TextView) findViewById(R.id.tvAddNewSymptom);

        TV_ADDSTRATEGY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(AddStrategyToSymptomActivity.this, AddStrategyActivity.class);
                _intent.putExtra("FROM_SYMPTOM", "TRUE");
                startActivity(_intent);
            }
        });

        LST_STRATEGY = (RecyclerView) findViewById(R.id.lstStrategy);
        BTN_INSPIRATION = (Button) findViewById(R.id.btnInspiration);


        LIST_STRATEGY = new ArrayList<>();
        params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AddStrategyToSymptomActivity.this);
        LST_STRATEGY.setLayoutManager(mLayoutManager);
        LST_STRATEGY.setItemAnimator(new DefaultItemAnimator());

        getStrategies();

        LST_STRATEGY.addOnItemTouchListener(new RecyclerTouchListener(AddStrategyToSymptomActivity.this, LST_STRATEGY, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LIST_STRATEGY.get(position);
//                Intent _intent = new Intent(AddStrategyToSymptomActivity.this, StrategyDetailsOwnActivity.class);
//                _intent.putExtra("STRATEGY_ID", LIST_STRATEGY.get(position).getId());
//                startActivity(_intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        BTN_INSPIRATION.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(AddStrategyToSymptomActivity.this, InspirationCategoryActivity.class);
                _intent.putExtra("FROM_SYMPTOM_INSPI", "FROM_SYMPTOM_INSPI");
                startActivity(_intent);
            }
        });
    }

    private void getStrategies() {
        mProgressDialog = new ProgressDialog(AddStrategyToSymptomActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        try {
            new General().getJSONContentFromInternetService(AddStrategyToSymptomActivity.this, General.PHPServices.GET_STRATEGIES, params, true, false, false, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(":::: ", response.toString());
                    Gson gson = new Gson();
                    try {
                        LIST_STRATEGY = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<Strategy>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (LIST_SELECTEDID.size() > 0) {
                        for (int i = 0; i < LIST_STRATEGY.size(); i++) {
                            if (LIST_SELECTEDID.contains(LIST_STRATEGY.get(i).getID())) {
                                LIST_STRATEGY.get(i).setSelected(true);
                            }
                        }
                    }
                    mProgressDialog.dismiss();
                    ADAPTER = new StrategySelectionAdapter(LIST_STRATEGY);
                    LST_STRATEGY.setAdapter(ADAPTER);
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
                            getStrategies();
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
    protected void onResume() {
        super.onResume();
        AppLifeCycle.getInstance().resumed(this);
        if (GET_STRATEGIES) {
            getStrategies();
            GET_STRATEGIES = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.strategy, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent returnIntent = new Intent();
            String _str = "";
            if (getIntent().hasExtra("ADDED_STRATEGY")) {
                LIST_SYMPTOMSTRATEGY.clear();
            } else {
                LIST_ADDSYMPTOMSTRATEGY.clear();
            }
            for (int i = 0; i < LIST_STRATEGY.size(); i++) {
                if (LIST_STRATEGY.get(i).isSelected()) {
                    if (_str.equals(""))
                        _str += LIST_STRATEGY.get(i).getID();
                    else
                        _str += "," + LIST_STRATEGY.get(i).getID();

                    if (getIntent().hasExtra("ADDED_STRATEGY")) {
                        LIST_SYMPTOMSTRATEGY.add(new SymptomStrategy(LIST_STRATEGY.get(i).getID(),
                                LIST_STRATEGY.get(i).getTitle()));
                    } else {
                        LIST_ADDSYMPTOMSTRATEGY.add(new SymptomStrategy(LIST_STRATEGY.get(i).getID(),
                                LIST_STRATEGY.get(i).getTitle()));
                    }

                }

            }
            returnIntent.putExtra("result", _str);
            setResult(Activity.RESULT_OK, returnIntent);
            AddStrategyToSymptomActivity.this.finish();
            return true;
        } else if (item.getItemId() == R.id.action_addStrategy) {
            Intent _intent = new Intent(AddStrategyToSymptomActivity.this, AddStrategyActivity.class);
            _intent.putExtra("FROM_SYMPTOM", "TRUE");
            startActivity(_intent);
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
