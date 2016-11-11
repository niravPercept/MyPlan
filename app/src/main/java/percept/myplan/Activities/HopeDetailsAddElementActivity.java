package percept.myplan.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Dialogs.dialogOk;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.MultiPartParsing;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.POJO.HopeDetail;
import percept.myplan.R;

import static percept.myplan.Activities.HopeDetailsActivity.GET_HOPE_DETAILS;

public class HopeDetailsAddElementActivity extends AppCompatActivity {

    private EditText EDT_TITLE;
    private TextView TV_ADDIMAGE, TV_ADDMUSIC, TV_ADDVIDEO, TV_ADDNOTE, TV_ADDLINK;
    private HopeDetail hopeDetail;
    private String HOPE_NAME = "";
    private CoordinatorLayout REL_COORDINATE;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hope_details_add_element);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.newelement));

        if (getIntent().hasExtra("HOPE_NAME"))
            HOPE_NAME = getIntent().getExtras().getString("HOPE_NAME");
        if (getIntent().hasExtra("IS_FOR_EDIT"))
            hopeDetail = (HopeDetail) getIntent().getExtras().getSerializable(Constant.DATA);

        EDT_TITLE = (EditText) findViewById(R.id.edtHopeElementTitle);
        TV_ADDIMAGE = (TextView) findViewById(R.id.tvChooseHopElementImage);
        TV_ADDMUSIC = (TextView) findViewById(R.id.tvChooseHopElementMusic);
        TV_ADDVIDEO = (TextView) findViewById(R.id.tvChooseHopElementVideo);
        TV_ADDNOTE = (TextView) findViewById(R.id.tvChooseHopElementNote);
        TV_ADDLINK = (TextView) findViewById(R.id.tvChooseHopElementLink);
        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);


        if (hopeDetail != null)
            EDT_TITLE.setText(hopeDetail.getMEDIA_TITLE());

        TV_ADDIMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(HopeDetailsAddElementActivity.this, AddStrategyImageActivity.class);
                _intent.putExtra("HOPE_TITLE", EDT_TITLE.getText().toString().trim());
                _intent.putExtra("FROM_HOPE", "FROM_HOPE");
                _intent.putExtra("HOPE_ID", getIntent().getExtras().getString("HOPE_ID"));
                if (hopeDetail != null)
                    _intent.putExtra(Constant.DATA, hopeDetail);
                startActivity(_intent);
                HopeDetailsAddElementActivity.this.finish();
            }
        });

        TV_ADDMUSIC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(HopeDetailsAddElementActivity.this, AddStrategyMusicActivity.class);
                _intent.putExtra("HOPE_TITLE", EDT_TITLE.getText().toString().trim());
                _intent.putExtra("FROM_HOPE", "FROM_HOPE");
                _intent.putExtra("HOPE_ID", getIntent().getExtras().getString("HOPE_ID"));
                if (hopeDetail != null)
                    _intent.putExtra(Constant.DATA, hopeDetail);
                startActivity(_intent);
                HopeDetailsAddElementActivity.this.finish();
            }
        });

        TV_ADDVIDEO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(HopeDetailsAddElementActivity.this, AddVideoActivity.class);
                _intent.putExtra("HOPE_TITLE", EDT_TITLE.getText().toString().trim());
                _intent.putExtra("FROM_HOPE", "FROM_HOPE");
                _intent.putExtra("HOPE_ID", getIntent().getExtras().getString("HOPE_ID"));
                if (hopeDetail != null)
                    _intent.putExtra(Constant.DATA, hopeDetail);
                startActivity(_intent);
                HopeDetailsAddElementActivity.this.finish();
            }
        });

        TV_ADDNOTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(HopeDetailsAddElementActivity.this, AddNoteActivity.class);
                _intent.putExtra("HOPE_TITLE", EDT_TITLE.getText().toString().trim());
                _intent.putExtra("FROM_HOPE", "FROM_HOPE");
                _intent.putExtra("HOPE_ID", getIntent().getExtras().getString("HOPE_ID"));
                if (hopeDetail != null)
                    _intent.putExtra(Constant.DATA, hopeDetail);
                startActivity(_intent);
                HopeDetailsAddElementActivity.this.finish();
            }
        });

        TV_ADDLINK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(HopeDetailsAddElementActivity.this, AddStrategyLinksActivity.class);
                _intent.putExtra("HOPE_TITLE", EDT_TITLE.getText().toString().trim());
                _intent.putExtra("FROM_HOPE", "FROM_HOPE");
                _intent.putExtra("HOPE_ID", getIntent().getExtras().getString("HOPE_ID"));
                if (hopeDetail != null)
                    _intent.putExtra(Constant.DATA, hopeDetail);
                startActivity(_intent);
                HopeDetailsAddElementActivity.this.finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            HopeDetailsAddElementActivity.this.finish();
        } else if (item.getItemId() == R.id.action_Save) {
       if (hopeDetail != null) {
                if(EDT_TITLE.getText().length()==0){
                    dialogOk _dialoglert=new dialogOk(HopeDetailsAddElementActivity.this, getString(R.string.enter_titile)) {
                        @Override
                        public void onClickOk() {
                            dismiss();
                        }
                    };
                    _dialoglert.setCancelable(false);
                    _dialoglert.setCanceledOnTouchOutside(false);
                    _dialoglert.show();
                }else {

                    /*InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
*/
                    addHopeBoxElement(EDT_TITLE.getText().toString().trim(),
                            getIntent().getExtras().getString("HOPE_ID"), "", hopeDetail.getID(), hopeDetail.getTYPE());

                }
           }
        }
        return false;
    }

    private void addHopeBoxElement(final String title, final String hopeId, final String imgpath, final String hopeElementId, final String type) {

        if (!new Utils(HopeDetailsAddElementActivity.this).isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addHopeBoxElement(title, hopeId, imgpath, hopeElementId, type);
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();
            return;
        }
        mProgressDialog = new ProgressDialog(HopeDetailsAddElementActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(imgpath))
            params.put("media", imgpath);
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put(Constant.ID, hopeDetail.getID());
        params.put(Constant.HOPE_ID, hopeId);
        params.put(Constant.HOPE_TITLE, title);
        params.put(Constant.HOPE_TYPE, type);
        new MultiPartParsing(this, params, General.PHPServices.SAVE_HOPE_MEDIA, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                mProgressDialog.dismiss();
                Log.d(":::::: ", response.toString());

                GET_HOPE_DETAILS = true;

                HopeDetailsAddElementActivity.this.finish();
            }
        });

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
