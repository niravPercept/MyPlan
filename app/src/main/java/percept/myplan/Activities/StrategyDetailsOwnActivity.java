package percept.myplan.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.CustomListener.RecyclerTouchListener;
import percept.myplan.Dialogs.dialogDeleteAlert;
import percept.myplan.Dialogs.dialogYesNoOption;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.ClickListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.Alarm;
import percept.myplan.POJO.ContactDisplay;
import percept.myplan.POJO.StrategyContact;
import percept.myplan.POJO.StrategyDetails;
import percept.myplan.R;
import percept.myplan.adapters.ImageAdapter;
import percept.myplan.adapters.StrategyAlarmAdapter;
import percept.myplan.adapters.StrategyContactSimpleAdapter;
import percept.myplan.adapters.StrategyLinkAdapter;
import percept.myplan.adapters.StrategyMusicAdapter;

public class StrategyDetailsOwnActivity extends AppCompatActivity {


    public static List<StrategyContact> LIST_STRATEGYCONTACT;
    public static List<Alarm> LIST_ALARM = new ArrayList<>();
    private final int SET_ALARM = 15;
    Map<String, String> params;
    private RecyclerView LST_OWNSTRATEGYIMG;
    private List<String> LIST_IMAGE, listLink;
    public static List<String> LISTMUSIC;
    private ImageAdapter ADAPTER_IMG;
    private String STRATEGY_ID;
    private Utils UTILS;
    private HashMap<String, List<Alarm>> MAP_ALARM;
    private StrategyDetails clsStrategy;
    private EditText EDT_STRATEGYTITLE, EDT_STRATEGYDESC;
    private StrategyContactSimpleAdapter ADAPTER;
    private StrategyAlarmAdapter ADAPTER_ALARM;
    private StrategyMusicAdapter musicAdapter;
    private StrategyLinkAdapter linkAdapter;
    private RecyclerView LST_STRATEGYCONTACT, LST_STRATEGYALARM, lstOwnStrategyMusic, lstOwnStrategyLink;
    private Button BTN_SHARESTRATEGY;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;
    private TextView tvImages, tvNetwork, tvAlarms, tvMusic, tvLink;
    private View vImages, vNetwork, vAlarms, vMusic, vLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy_details_own);

        autoScreenTracking();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        if (getIntent().hasExtra("STRATEGY_ID")) {
            STRATEGY_ID = getIntent().getExtras().getString("STRATEGY_ID");
            mTitle.setText(getIntent().getExtras().getString("STRATEGY_NAME"));
        }
        tvAlarms = (TextView) findViewById(R.id.tvAlarms);
        tvNetwork = (TextView) findViewById(R.id.tvNetwork);
        tvImages = (TextView) findViewById(R.id.tvImages);
        tvMusic = (TextView) findViewById(R.id.tvMusic);
        tvLink = (TextView) findViewById(R.id.tvLink);

        vAlarms = findViewById(R.id.vAlarm);
        vNetwork = findViewById(R.id.vNetwork);
        vImages = findViewById(R.id.vImage);
        vMusic = findViewById(R.id.vMusic);
        vLink = findViewById(R.id.vLink);

        LST_OWNSTRATEGYIMG = (RecyclerView) findViewById(R.id.lstOwnStrategyImage);
        lstOwnStrategyMusic = (RecyclerView) findViewById(R.id.lstOwnStrategyMusic);
        lstOwnStrategyLink = (RecyclerView) findViewById(R.id.lstOwnStrategyLink);
        LST_STRATEGYCONTACT = (RecyclerView) findViewById(R.id.lstStrategyContacts);
        LST_STRATEGYALARM = (RecyclerView) findViewById(R.id.lstStrategyAlarm);

        EDT_STRATEGYTITLE = (EditText) findViewById(R.id.edtStrategyTitle);
        EDT_STRATEGYDESC = (EditText) findViewById(R.id.edtStrategyDesc);
        EDT_STRATEGYDESC.setEnabled(false);
        EDT_STRATEGYTITLE.setEnabled(false);

        LIST_STRATEGYCONTACT = new ArrayList<>();
        LIST_ALARM = new ArrayList<>();
        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        BTN_SHARESTRATEGY = (Button) findViewById(R.id.btnShareStrategyAnony);
        BTN_SHARESTRATEGY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new dialogYesNoOption(
                        StrategyDetailsOwnActivity.this, getString(R.string.share_strategy_confirm)) {
                    @Override
                    public void onClickYes() {
                        dismiss();
                        shareStrategy();
                    }

                    @Override
                    public void onClickNo() {
                        dismiss();
                    }
                }.show();

            }
        });



        EDT_STRATEGYTITLE.setText(getIntent().getExtras().getString("STRATEGY_NAME"));

        UTILS = new Utils(StrategyDetailsOwnActivity.this);


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LST_OWNSTRATEGYIMG.setLayoutManager(mLayoutManager);
        LST_OWNSTRATEGYIMG.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager _mLayoutManager = new LinearLayoutManager(StrategyDetailsOwnActivity.this);
        LST_STRATEGYCONTACT.setLayoutManager(_mLayoutManager);
        LST_STRATEGYCONTACT.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager _mLayoutManagerMusic = new LinearLayoutManager(StrategyDetailsOwnActivity.this);
        lstOwnStrategyMusic.setLayoutManager(_mLayoutManagerMusic);
        lstOwnStrategyMusic.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager _mLayoutManagerLink = new LinearLayoutManager(StrategyDetailsOwnActivity.this);
        lstOwnStrategyLink.setLayoutManager(_mLayoutManagerLink);
        lstOwnStrategyLink.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager _mLayoutManagerAlarm = new LinearLayoutManager(StrategyDetailsOwnActivity.this);
        LST_STRATEGYALARM.setLayoutManager(_mLayoutManagerAlarm);
        LST_STRATEGYALARM.setItemAnimator(new DefaultItemAnimator());

        LIST_IMAGE = new ArrayList<>();
        LISTMUSIC = new ArrayList<>();
        listLink = new ArrayList<>();

        LST_STRATEGYCONTACT.addOnItemTouchListener(new RecyclerTouchListener(StrategyDetailsOwnActivity.this, LST_STRATEGYCONTACT, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d("::::::::", LIST_STRATEGYCONTACT.get(position).getNAME());
                Map<String, String> params = new HashMap<String, String>();
                params.put("sid", Constant.SID);
                params.put("sname", Constant.SNAME);
                params.put(Constant.ID, LIST_STRATEGYCONTACT.get(position).getID());
                try {
                    new General().getJSONContentFromInternetService(StrategyDetailsOwnActivity.this, General.PHPServices.GET_CONTACT, params, true, false, true, new VolleyResponseListener() {
                        @Override
                        public void onError(VolleyError message) {

                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(":::::::: Response----", response.toString());

                            try {
                                ContactDisplay _contactDisplay = new Gson().fromJson(response.getJSONObject(Constant.DATA)
                                        .toString(), new TypeToken<ContactDisplay>() {
                                }.getType());
                                Intent intent = new Intent(StrategyDetailsOwnActivity.this, AddContactDetailActivity.class);
                                intent.putExtra("IS_FOR_EDIT", true);
                                intent.putExtra("DATA", _contactDisplay);
                                startActivity(intent);
                                /*JSONObject _jsonContactDetail=response.getJSONObject(Constant.DATA);
                               if (_jsonContactDetail!=null)
                               {
                                   ContactDisplay _contactDisplay=new ContactDisplay();
                                   _contactDisplay.setCon_image(_jsonContactDetail.getString("con_image"));
                                   _contactDisplay.setFirst_name(_jsonContactDetail.getString("first_name"));

                               }*/
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },LIST_STRATEGYCONTACT.get(position).getID());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        lstOwnStrategyLink.addOnItemTouchListener(new RecyclerTouchListener(StrategyDetailsOwnActivity.this,
                lstOwnStrategyLink, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(StrategyDetailsOwnActivity.this, WebViewActivity.class);
                intent.putExtra("URL_MUSIC",LISTMUSIC.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        lstOwnStrategyMusic.addOnItemTouchListener(new RecyclerTouchListener(StrategyDetailsOwnActivity.this,
                lstOwnStrategyMusic, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                Intent _intent = new Intent(StrategyDetailsOwnActivity.this, AudioviewActivity.class);
                _intent.putExtra("Audio",LISTMUSIC.get(position).toString());
                startActivity(_intent);
               /* MediaPlayer mp = new MediaPlayer();

                String path=LISTMUSIC.get(positio
                try {
                    mp.setDataSource(path);
                    mp.prepare();
                    mp.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error audio",e.toString());
                }*/

//                Toast.makeText(getApplicationContext(),lstOwnStrategyMusic.,Toast.LENGTH_LONG).show();


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        LST_STRATEGYALARM.addOnItemTouchListener(new RecyclerTouchListener(StrategyDetailsOwnActivity.this, LST_STRATEGYALARM, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent _intent = new Intent(StrategyDetailsOwnActivity.this, AlarmListActivity.class);
                _intent.putExtra("FROM_DETAIL", "TRUE");
                startActivityForResult(_intent, SET_ALARM);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));




//        initSwipe(LST_STRATEGYCONTACT);
        initSwipe(lstOwnStrategyMusic);

    }


    private void initSwipe(final RecyclerView recyclerView) {
        final Paint p = new Paint();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
//                if (direction == ItemTouchHelper.LEFT){
//                    adapter.removeItem(position);
//                } else {
                // Snackbar.make(getView(), "Swipe Right", Snackbar.LENGTH_LONG).show();
//                }
                if (recyclerView == LST_STRATEGYALARM)
                    deleteAlarms(position);
                else if (recyclerView == LST_STRATEGYCONTACT)
                    deleteContacts(position);
                else if (recyclerView==lstOwnStrategyMusic)
                   // deleteAudio(position);
                    deletemessagebox(position);


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
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLifeCycle.getInstance().resumed(this);
        /*if (ADDED_STRATEGIES) {
            getStrategy();
        }*/
        getStrategy();
    }

    private void deleteContacts(final int position) {
        dialogYesNoOption _dialog = new dialogYesNoOption(StrategyDetailsOwnActivity.this, getString(R.string.delete_contact)) {

            @Override
            public void onClickYes() {
                if (!General.checkInternetConnection(StrategyDetailsOwnActivity.this))
                    return;
                mProgressDialog = new ProgressDialog(StrategyDetailsOwnActivity.this);
                mProgressDialog.setMessage(getString(R.string.progress_loading));
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                HashMap<String, String> params = new HashMap<>();
                params.put("sid", Constant.SID);
                params.put("sname", Constant.SNAME);
                params.put("id", Constant.SNAME);
                try {
                    new General().getJSONContentFromInternetService(StrategyDetailsOwnActivity.this, General.PHPServices.DELETE_CONTACT, params, true, false, true, new VolleyResponseListener() {
                        @Override
                        public void onError(VolleyError message) {
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            mProgressDialog.dismiss();
                            LIST_STRATEGYCONTACT.remove(position);
                            ADAPTER.notifyDataSetChanged();
                        }
                    },"");
                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                }
                dismiss();


                findViewById(android.R.id.content).invalidate();
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

    private void deleteAlarms(final int position) {
        dialogYesNoOption _dialog = new dialogYesNoOption(StrategyDetailsOwnActivity.this, getString(R.string.delete_alarm)) {

            @Override
            public void onClickYes() {
                dismiss();
                HashMap<String, String> params = new HashMap<>();
                params.put("sid", Constant.SID);
                params.put("sname", Constant.SNAME);
                LIST_ALARM.remove(position);
                ADAPTER_ALARM.notifyDataSetChanged();
                getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
            }

            @Override
            public void onClickNo() {

                dismiss();
            }
        };
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.show();
    }

    private void getStrategy() {
        mProgressDialog = new ProgressDialog(StrategyDetailsOwnActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("id", STRATEGY_ID);
        try {
            LIST_IMAGE.clear();
            LIST_STRATEGYCONTACT.clear();
            LISTMUSIC.clear();
            listLink.clear();
            Log.d("remove music",LISTMUSIC.toString());
            new General().getJSONContentFromInternetService(StrategyDetailsOwnActivity.this, General.PHPServices.GET_STRATEGY, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    try {
                        clsStrategy = gson.fromJson(response.getJSONObject(Constant.DATA)
                                .toString(), new TypeToken<StrategyDetails>() {
                        }.getType());

                        LIST_STRATEGYCONTACT = gson.fromJson(response.getJSONObject(Constant.DATA).getJSONArray("contact")
                                .toString(), new TypeToken<List<StrategyContact>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    if (LIST_STRATEGYCONTACT != null && LIST_STRATEGYCONTACT.size() > 0) {
                        ADAPTER = new StrategyContactSimpleAdapter(LIST_STRATEGYCONTACT);
                        LST_STRATEGYCONTACT.setAdapter(ADAPTER);
                        tvNetwork.setVisibility(View.VISIBLE);
                        vNetwork.setVisibility(View.VISIBLE);
                    } else {
                        tvNetwork.setVisibility(View.GONE);
                        vNetwork.setVisibility(View.GONE);
                    }
                    EDT_STRATEGYDESC.setText(clsStrategy.getDescription());

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

                    LIST_ALARM = MAP_ALARM.get(Constant.PROFILE_USER_ID + "_" + STRATEGY_ID);

                    if (LIST_ALARM != null && LIST_ALARM.size() > 0) {
                        ADAPTER_ALARM = new StrategyAlarmAdapter(LIST_ALARM);
                        LST_STRATEGYALARM.setAdapter(ADAPTER_ALARM);
                        tvAlarms.setVisibility(View.VISIBLE);
                    } else tvAlarms.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(clsStrategy.getImage())) {
                        String _images = clsStrategy.getImage();
                        String[] _arrImg = _images.split(",");
                        for (int i = 0; i < _arrImg.length; i++) {
                            LIST_IMAGE.add(_arrImg[i]);
                        }


                        if (LIST_IMAGE != null && LIST_IMAGE.size() > 0) {
                            ADAPTER_IMG = new ImageAdapter(StrategyDetailsOwnActivity.this, LIST_IMAGE);
                            LST_OWNSTRATEGYIMG.setAdapter(ADAPTER_IMG);
                            tvImages.setVisibility(View.VISIBLE);
                            vImages.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvImages.setVisibility(View.GONE);
                        vImages.setVisibility(View.GONE);
                    }


                    if (!TextUtils.isEmpty(clsStrategy.getInternalAudio())) {
                        String _images = clsStrategy.getInternalAudio();
                        String[] _arrImg = _images.split(",");
                        for (int i = 0; i < _arrImg.length; i++) {
                            LISTMUSIC.add(_arrImg[i]);
                        }

                        Log.d("data",LISTMUSIC.toString());

                        if (LISTMUSIC != null && LISTMUSIC.size() > 0) {
                            musicAdapter = new StrategyMusicAdapter(StrategyDetailsOwnActivity.this,LISTMUSIC);
                            lstOwnStrategyMusic.setAdapter(musicAdapter);
                            tvMusic.setVisibility(View.VISIBLE);
                            vMusic.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvMusic.setVisibility(View.GONE);
                        vMusic.setVisibility(View.GONE);
                    }
                    if (!TextUtils.isEmpty(clsStrategy.getLink())) {
                        String links = clsStrategy.getLink();
                        String[] _arrLink = links.split(",");
                        for (int i = 0; i < _arrLink.length; i++) {
                            listLink.add(_arrLink[i]);
                        }


                        if (listLink != null && listLink.size() > 0) {
                            linkAdapter = new StrategyLinkAdapter(listLink);
                            lstOwnStrategyLink.setAdapter(linkAdapter);
                            tvLink.setVisibility(View.VISIBLE);
                            vLink.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvLink.setVisibility(View.GONE);
                        vLink.setVisibility(View.GONE);
                    }


                    mProgressDialog.dismiss();
                }
            },"own_"+STRATEGY_ID);
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getStrategy();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_strategy, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            StrategyDetailsOwnActivity.this.finish();
        } else if (item.getItemId() == R.id.action_editStrategy) {
            if (clsStrategy != null) {

                Intent _intent = new Intent(StrategyDetailsOwnActivity.this, StrategyEditActivity.class);
                _intent.putExtra("STRATEGY_ID", STRATEGY_ID);
                _intent.putExtra("STRATEGY_TITLE", EDT_STRATEGYTITLE.getText().toString());
                _intent.putExtra("STRATEGY_DESC", EDT_STRATEGYDESC.getText().toString());
                _intent.putExtra("STR_LINK", new Gson().toJson(listLink));
                startActivity(_intent);
            }
//            Toast.makeText(StrategyDetailsOwnActivity.this, "Edit Strategy called", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void shareStrategy() {
        if (!General.checkInternetConnection(StrategyDetailsOwnActivity.this))
            return;
        mProgressDialog = new ProgressDialog(StrategyDetailsOwnActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("id", STRATEGY_ID);
        try {
            new General().getJSONContentFromInternetService(StrategyDetailsOwnActivity.this,
                    General.PHPServices.SHARE_STRATEGIES, params, true, false, true, new VolleyResponseListener() {

                        @Override
                        public void onError(VolleyError message) {
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            mProgressDialog.dismiss();
                            Toast.makeText(StrategyDetailsOwnActivity.this, getString(R.string.share_strategy_success), Toast.LENGTH_LONG).show();
                            Log.d("::::::share:::::", response.toString());
                        }
                    },STRATEGY_ID);
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            shareStrategy();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

    }

    public void deleteImages(final int position) {
        if (!General.checkInternetConnection(StrategyDetailsOwnActivity.this))
            return;
        mProgressDialog = new ProgressDialog(StrategyDetailsOwnActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("id", STRATEGY_ID);
        params.put("images", LIST_IMAGE.get(position).substring(LIST_IMAGE.get(position).lastIndexOf('/') + 1));
        try {
            new General().getJSONContentFromInternetService(StrategyDetailsOwnActivity.this,
                    General.PHPServices.DELETE_STRATEGY_IMAGES, params, true, false, true, new VolleyResponseListener() {

                        @Override
                        public void onError(VolleyError message) {
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            mProgressDialog.dismiss();
                            LIST_IMAGE.remove(position);
                            ADAPTER_IMG.notifyDataSetChanged();
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
                            deleteImages(position);
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }




    // massage box delete

    public void deletemessagebox(final int is){

        final dialogDeleteAlert alert=new dialogDeleteAlert(StrategyDetailsOwnActivity.this,getString(R.string.audioremove)) {
            @Override
            public void onClickYes() {
                deleteAudio(is);
              //  alert.dismiss();
                dismiss();
            }

            @Override
            public void onClickNo() {
             dismiss();
            }
        };
        alert.show();

    }



    //ketan code
    //strategy Music delete remove item
    public void deleteAudio(final int posi){
        if (!General.checkInternetConnection(StrategyDetailsOwnActivity.this))
            return;

        mProgressDialog = new ProgressDialog(StrategyDetailsOwnActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("audios",LISTMUSIC.get(posi).toString());
        params.put("id", STRATEGY_ID);


        try {
            new General().getJSONContentFromInternetService(StrategyDetailsOwnActivity.this,
                    General.PHPServices.DELETE_STRATEGY_AUDIO, params, true, false, true, new VolleyResponseListener() {

                        @Override
                        public void onError(VolleyError message) {
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            mProgressDialog.dismiss();
                            LISTMUSIC.remove(posi);
                            musicAdapter.notifyDataSetChanged();
                            Log.d("reso",response.toString());
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
//                            deleteImages(posi);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SET_ALARM:
                MAP_ALARM.put(Constant.PROFILE_USER_ID + "_" + STRATEGY_ID, LIST_ALARM);
                Gson gson = new Gson();
                String _alarmList = gson.toJson(MAP_ALARM);
                UTILS.setPreference("ALARMLIST", _alarmList);
                ADAPTER_ALARM.notifyDataSetChanged();
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
