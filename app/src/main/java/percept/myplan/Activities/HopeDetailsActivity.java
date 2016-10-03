package percept.myplan.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.HopeDetail;
import percept.myplan.R;
import percept.myplan.adapters.Basic3Adapter;

public class HopeDetailsActivity extends AppCompatActivity {
    public static boolean GET_HOPE_DETAILS = false;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    Map<String, String> params;
    private List<HopeDetail> LIST_HOPEDETAILS;
    private ProgressBar PB;
    private String HOPE_TITLE;
    private CoordinatorLayout REL_COORDINATE;
    private int deletePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hope_details);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        if (getIntent().hasExtra("HOPE_TITLE")) {
            mTitle.setText(getIntent().getExtras().getString("HOPE_TITLE"));
            HOPE_TITLE = getIntent().getExtras().getString("HOPE_TITLE");
        } else
            mTitle.setText("Hope Box");

//        LST_HOPEDETAILS = (RecyclerView) findViewById(R.id.lstHopeDetails);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(HopeDetailsActivity.this);
//        LST_HOPEDETAILS.setLayoutManager(mLayoutManager);
//        LST_HOPEDETAILS.setItemAnimator(new DefaultItemAnimator());


        mRecyclerView = (RecyclerView) findViewById(R.id.lstHopeDetails);

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        PB = (ProgressBar) findViewById(R.id.pbgetHopeDetail);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HopeDetailsActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
//        if (layoutManager instanceof LinearLayoutManager) {
//            mRecyclerView.addItemDecoration(new DividerItemDecoration(HopeDetailsActivity.this,
//                    ((LinearLayoutManager) layoutManager).getOrientation()));
//        }


        GetHopeDetails();
    }

    private void GetHopeDetails() {
        params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("id", getIntent().getExtras().getString("HOPE_ID"));
        try {
            PB.setVisibility(View.VISIBLE);
            new General().getJSONContentFromInternetService(HopeDetailsActivity.this, General.PHPServices.GET_HOPEBOX, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    PB.setVisibility(View.GONE);
                }

                @Override
                public void onResponse(JSONObject response) {
                    PB.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    try {
                        LIST_HOPEDETAILS = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<HopeDetail>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("::::::  ", String.valueOf(LIST_HOPEDETAILS.size()));
//                    ADAPTER = new HopeDetailsAdapter(HopeDetailsActivity.this, LIST_HOPEDETAILS);
//                    LST_HOPEDETAILS.setAdapter(ADAPTER);
                    mAdapter = new Basic3Adapter(HopeDetailsActivity.this, LIST_HOPEDETAILS, HOPE_TITLE);
                    mRecyclerView.setHasFixedSize(false);
                    mRecyclerView.setAdapter(mAdapter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            PB.setVisibility(View.GONE);
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GetHopeDetails();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GET_HOPE_DETAILS) {
            GetHopeDetails();
            GET_HOPE_DETAILS = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_hope_element, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            HopeDetailsActivity.this.finish();
            return true;
        } else if (item.getItemId() == R.id.action_InsertHopeElement) {

            Intent _intent = new Intent(HopeDetailsActivity.this, HopeDetailsAddElementActivity.class);
            _intent.putExtra("HOPE_NAME", getIntent().getExtras().getString("HOPE_TITLE"));
            _intent.putExtra("HOPE_ID", getIntent().getExtras().getString("HOPE_ID"));
            startActivity(_intent);

//            Toast.makeText(HopeDetailsActivity.this, "Add Hope Element clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void editHopeElement(int position) {
        Intent intent = new Intent(HopeDetailsActivity.this, HopeDetailsAddElementActivity.class);
        intent.putExtra("IS_FOR_EDIT", true);
        intent.putExtra(Constant.DATA, LIST_HOPEDETAILS.get(position));
        intent.putExtra("HOPE_NAME", getIntent().getExtras().getString("HOPE_TITLE"));
        intent.putExtra("HOPE_ID", getIntent().getExtras().getString("HOPE_ID"));
        startActivity(intent);
    }
    public void playMusicHopeElement(int position) {
        Intent intent = new Intent(HopeDetailsActivity.this, WebViewActivity.class);
        intent.putExtra("URL_MUSIC", LIST_HOPEDETAILS.get(position).getMEDIA());
        intent.putExtra(Constant.DATA, LIST_HOPEDETAILS.get(position));
        startActivity(intent);
    }

    public void playVideoHopeElement(int position) {
        Intent intent = new Intent(HopeDetailsActivity.this, FullscreenVideoActivity.class);
        intent.putExtra("URL_MUSIC", LIST_HOPEDETAILS.get(position).getMEDIA());
        intent.putExtra(Constant.DATA, LIST_HOPEDETAILS.get(position));
        startActivity(intent);
    }

    public void deleteHopeElement(int poition) {
        deletePosition = poition;
        params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("id", LIST_HOPEDETAILS.get(poition).getID());
        try {
            PB.setVisibility(View.VISIBLE);
            new General().getJSONContentFromInternetService(HopeDetailsActivity.this, General.PHPServices.DELETE_HOPE_MEDIA, params,
                    true, false, false, new VolleyResponseListener() {
                        @Override
                        public void onError(VolleyError message) {
                            PB.setVisibility(View.GONE);
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            PB.setVisibility(View.GONE);
                            try {
                                if (response.has(Constant.DATA)) {

                                    if (response.getJSONObject(Constant.DATA).getString(Constant.STATUS).equals("Success")) {
                                        LIST_HOPEDETAILS.remove(deletePosition);
                                        deletePosition = -1;
                                        mAdapter.notifyDataSetChanged();
                                        Toast.makeText(HopeDetailsActivity.this, getString(R.string.delete_hope_media_success), Toast.LENGTH_LONG).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

            final Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteHopeElement(deletePosition);
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();
        }
    }
}
