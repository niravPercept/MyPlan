package percept.myplan.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import percept.myplan.Dialogs.dialogOk;
import percept.myplan.Dialogs.dialogYesNoOption;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.ClickListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.SymptomStrategy;
import percept.myplan.R;
import percept.myplan.adapters.SymptomStrategyAdapter;

import static percept.myplan.fragments.fragmentSymptoms.GET_STRATEGY;

public class SymptomDetailsActivity extends AppCompatActivity {

    private static final int ADDSTRATEGY = 6;
    public static List<SymptomStrategy> LIST_SYMPTOMSTRATEGY;
    private String SYMPTOM_ID;
    private EditText TV_TITLE, TV_TEXT;
    private RecyclerView LST_SYMPTOMSTRATEGY;
    private SymptomStrategyAdapter ADAPTER;
    private LinearLayout LAY_ADDSTRATEGY;
    private TextView TV_ADDSTRATEGY;
    private boolean isEDIT = false;
    private String STR_STRATEGYID = "";
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;
    private Button btnDeleteSymptom;
    private TextView tvStrategiesToSymptom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptom_details);
        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.title_activity_symptoms));

        SYMPTOM_ID = getIntent().getExtras().getString("SYMPTOM_ID");
        TV_TITLE = (EditText) findViewById(R.id.tvTitle);
        TV_TEXT = (EditText) findViewById(R.id.tvText);
        tvStrategiesToSymptom = (TextView) findViewById(R.id.tvStrategiesToSymptom);

        TV_TITLE.setEnabled(false);
        TV_TEXT.setEnabled(false);

        LST_SYMPTOMSTRATEGY = (RecyclerView) findViewById(R.id.lstSymptomStrategy);

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        LAY_ADDSTRATEGY = (LinearLayout) findViewById(R.id.layAddStrategy);
        TV_ADDSTRATEGY = (TextView) findViewById(R.id.tvAddStrategy);
        LIST_SYMPTOMSTRATEGY = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SymptomDetailsActivity.this);
        LST_SYMPTOMSTRATEGY.setLayoutManager(mLayoutManager);
        LST_SYMPTOMSTRATEGY.setItemAnimator(new DefaultItemAnimator());

        GetSymptomDetail();
        TV_ADDSTRATEGY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(SymptomDetailsActivity.this, AddStrategyToSymptomActivity.class);
                _intent.putExtra("ADDED_STRATEGY", STR_STRATEGYID);
                startActivityForResult(_intent, ADDSTRATEGY);
            }
        });

        LST_SYMPTOMSTRATEGY.addOnItemTouchListener(new RecyclerTouchListener(SymptomDetailsActivity.this, LST_SYMPTOMSTRATEGY, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LIST_SYMPTOMSTRATEGY.get(position);

                Intent _intent = new Intent(SymptomDetailsActivity.this, StrategyDetailsOwnActivity.class);
                _intent.putExtra("STRATEGY_ID", LIST_SYMPTOMSTRATEGY.get(position).getId());
                _intent.putExtra("STRATEGY_NAME", LIST_SYMPTOMSTRATEGY.get(position).getTitle());
                _intent.putExtra("FROM_SYMPTOM", "FROM_SYMPTOM");
                startActivity(_intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        btnDeleteSymptom = (Button) findViewById(R.id.btnDeleteSymptom);
        btnDeleteSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogYesNoOption _dialog = new dialogYesNoOption(SymptomDetailsActivity.this, getString(R.string.delete_symptom)) {

                    @Override
                    public void onClickYes() {
                        if (!General.checkInternetConnection(SymptomDetailsActivity.this))
                            return;
                        HashMap<String, String> params = new HashMap<>();
                        params.put("sid", Constant.SID);
                        params.put("sname", Constant.SNAME);
                        params.put("id", SYMPTOM_ID);

                        try {
                            mProgressDialog = new ProgressDialog(SymptomDetailsActivity.this);
                            mProgressDialog.setMessage(getString(R.string.progress_loading));
                            mProgressDialog.setIndeterminate(false);
                            mProgressDialog.setCanceledOnTouchOutside(false);
                            mProgressDialog.show();
                            dismiss();
                            new General().getJSONContentFromInternetService(SymptomDetailsActivity.this,
                                    General.PHPServices.DELETE_SYMPTOM, params,
                                    true, false, true, new VolleyResponseListener() {

                                        @Override
                                        public void onError(VolleyError message) {
                                            mProgressDialog.dismiss();
                                        }

                                        @Override
                                        public void onResponse(JSONObject response) {
                                            mProgressDialog.dismiss();
                                            GET_STRATEGY = true;
                                            SymptomDetailsActivity.this.finish();
                                        }
                                    },SYMPTOM_ID);
                        } catch (Exception e) {
                            mProgressDialog.dismiss();
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onClickNo() {
                        ADAPTER.notifyDataSetChanged();
                        dismiss();
                    }
                };
                _dialog.setCancelable(false);
                _dialog.setCanceledOnTouchOutside(false);
                _dialog.show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ADDSTRATEGY) {
            if (resultCode == Activity.RESULT_OK) {
                STR_STRATEGYID = data.getStringExtra("result");
                ADAPTER.notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_save_details, menu);
        if (isEDIT) {
            menu.getItem(1).setVisible(true);
            menu.getItem(0).setVisible(false);
            TV_TITLE.setEnabled(true);
            TV_TEXT.setEnabled(true);
            isEDIT = false;
            if (tvStrategiesToSymptom.getVisibility() == View.GONE)
                tvStrategiesToSymptom.setVisibility(View.VISIBLE);
            btnDeleteSymptom.setVisibility(View.VISIBLE);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            TV_TITLE.setEnabled(false);
            TV_TEXT.setEnabled(false);
            btnDeleteSymptom.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SymptomDetailsActivity.this.finish();
        } else if (item.getItemId() == R.id.action_edit) {
//            Toast.makeText(SymptomDetailsActivity.this, "edit Called", Toast.LENGTH_SHORT).show();
            LAY_ADDSTRATEGY.setVisibility(View.VISIBLE);
            isEDIT = true;
            invalidateOptionsMenu();
        } else if (item.getItemId() == R.id.action_save) {

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            LAY_ADDSTRATEGY.setVisibility(View.GONE);
            isEDIT = false;
            invalidateOptionsMenu();

            if (TV_TITLE.getText().length()!=0 && TV_TITLE !=null){
              SaveSymptoms();
            }else {
                dialogOk _dialoglert=new dialogOk(SymptomDetailsActivity.this, getString(R.string.enter_titile)) {
                    @Override
                    public void onClickOk() {
                        dismiss();
                    }
                };
                _dialoglert.setCancelable(false);
                _dialoglert.setCanceledOnTouchOutside(false);
                _dialoglert.show();
            }



            return true;
        }
        return false;
    }

    private void SaveSymptoms() {
        if (!General.checkInternetConnection(SymptomDetailsActivity.this))
            return;
        mProgressDialog = new ProgressDialog(SymptomDetailsActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("id", SYMPTOM_ID);
        params.put("title", TV_TITLE.getText().toString().trim());
        params.put("description", TV_TEXT.getText().toString().trim());
        params.put("strategy_id", STR_STRATEGYID);
        params.put("state", "1");

        try {
            new General().getJSONContentFromInternetService(SymptomDetailsActivity.this, General.PHPServices.SAVE_SYMPTOM, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {

                }

                @Override
                public void onResponse(JSONObject response) {
                    mProgressDialog.dismiss();
                    GET_STRATEGY = true;
                    Log.d(":::::", response.toString());
                    SymptomDetailsActivity.this.finish();
                }
            },SYMPTOM_ID);
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SaveSymptoms();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

    private void GetSymptomDetail() {
        mProgressDialog = new ProgressDialog(SymptomDetailsActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("id", SYMPTOM_ID);
        try {
            new General().getJSONContentFromInternetService(SymptomDetailsActivity.this, General.PHPServices.GET_SYMPTOM, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(":::: ", response.toString());
                    try {
                        TV_TITLE.setText(response.getJSONObject(Constant.DATA).getString(Constant.TITLE));
                        TV_TEXT.setText(response.getJSONObject(Constant.DATA).getString(Constant.DESC));
                        Gson gson = new Gson();
                        try {
                            LIST_SYMPTOMSTRATEGY = gson.fromJson(response.getJSONObject(Constant.DATA).getJSONArray(Constant.STRATEGIE)
                                    .toString(), new TypeToken<List<SymptomStrategy>>() {
                            }.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ADAPTER = new SymptomStrategyAdapter(LIST_SYMPTOMSTRATEGY);

                        for (int i = 0; i < LIST_SYMPTOMSTRATEGY.size(); i++) {
                            if (STR_STRATEGYID.equals(""))
                                STR_STRATEGYID += LIST_SYMPTOMSTRATEGY.get(i).getId();
                            else
                                STR_STRATEGYID += "," + LIST_SYMPTOMSTRATEGY.get(i).getId();
                        }
                        if (LIST_SYMPTOMSTRATEGY.size() > 0)
                            tvStrategiesToSymptom.setVisibility(View.VISIBLE);
                        else tvStrategiesToSymptom.setVisibility(View.GONE);

                        LST_SYMPTOMSTRATEGY.setAdapter(ADAPTER);
                        mProgressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },SYMPTOM_ID);
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GetSymptomDetail();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

/*    private void initSwipe() {
        final Paint p = new Paint();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
//                if (direction == ItemTouchHelper.LEFT){
//                    adapter.removeItem(position);
//                } else {
                // Snackbar.make(getView(), "Swipe Right", Snackbar.LENGTH_LONG).show();
//                }
                dialogYesNoOption _dialog = new dialogYesNoOption(SymptomDetailsActivity.this, getString(R.string.delete_symptom)) {

                    @Override
                    public void onClickYes() {
                       *//* if (!General.checkInternetConnection(SymptomDetailsActivity.this))
                            return;
                        HashMap<String, String> params = new HashMap<>();
                        params.put("sid",Constant.SID);
                        params.put("sname",Constant.SNAME);
                        params.put("id",LIST_SYMPTOM.get(position).getId());

                        try {
                            showProgress(getString(R.string.progress_loading));
                            dismiss();
                            new General().getJSONContentFromInternetService(getActivity(),
                                    General.PHPServices.DELETE_SYMPTOM, params,
                                    true, false, true, new VolleyResponseListener() {

                                        @Override
                                        public void onError(VolleyError message) {
                                            dismissProgress();
                                            ADAPTER.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onResponse(JSONObject response) {
                                            dismissProgress();
                                            LIST_SYMPTOM.remove(position);
                                            ADAPTER.notifyDataSetChanged();
                                        }
                                    },LIST_SYMPTOM.get(position).getId());
                        } catch (Exception e) {
                            dismissProgress();
                            e.printStackTrace();
                        }*//*
                    }

                    @Override
                    public void onClickNo() {
                        ADAPTER.notifyDataSetChanged();
                        dismiss();
                    }
                };
                _dialog.setCancelable(false);
                _dialog.setCanceledOnTouchOutside(false);
                _dialog.show();

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

//                    if(dX > 0){
//                        p.setColor(Color.parseColor("#388E3C"));
//                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
//                        c.drawRect(background,p);
//                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
//                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
//                        c.drawBitmap(icon,null,icon_dest,p);
//                    } else {
                    p.setColor(getResources().getColor(android.R.color.white));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_delete);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);
//                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(LST_SYMPTOM);
    }*/






    public void autoScreenTracking(){
        TpaConfiguration config =
                new TpaConfiguration.Builder("d3baf5af-0002-4e72-82bd-9ed0c66af31c", "https://weiswise.tpa.io/")
                        // other config settings
                        .enableAutoTrackScreen(true)
                        .enableAnalytics(true)
                        .useShakeFeedback(true)
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
