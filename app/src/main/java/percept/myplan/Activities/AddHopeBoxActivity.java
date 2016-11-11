package percept.myplan.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import me.crosswall.photo.pick.PickConfig;
import me.crosswall.photo.pick.util.UriUtil;
import percept.myplan.Dialogs.dialogOk;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.MultiPartParsing;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.R;

import static percept.myplan.fragments.fragmentHopeBox.ADDED_HOPEBOX;

public class AddHopeBoxActivity extends AppCompatActivity {

    private Button BTN_ADDFOLDERIMG;
    private String FOLDER_IMG_PATH = "";
    private EditText EDT_FOLDERNAME;
    private ImageView IMG_HOPE;
    private RelativeLayout LAY_SELECTIMG, LAY_SELECTEDIMG;
    private Utils UTILS;
    private CoordinatorLayout REL_COORDINATE;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hope_box);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.title_activity_addhopebox));
        UTILS = new Utils(AddHopeBoxActivity.this);
        LAY_SELECTIMG = (RelativeLayout) findViewById(R.id.relSelectImg);
        LAY_SELECTEDIMG = (RelativeLayout) findViewById(R.id.relSelectedImg);
        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);


        LAY_SELECTEDIMG.setVisibility(View.GONE);

        BTN_ADDFOLDERIMG = (Button) findViewById(R.id.btnAddFolderImage);
        EDT_FOLDERNAME = (EditText) findViewById(R.id.edtFolderName);
        IMG_HOPE = (ImageView) findViewById(R.id.imgHopeFolder);
        BTN_ADDFOLDERIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PickConfig.Builder(AddHopeBoxActivity.this)
                        .pickMode(PickConfig.MODE_SINGLE_PICK)
                        .spanCount(3)
                        //.showGif(true)
                        .checkImage(false) //default false
                        .useCursorLoader(false) //default true
                        .toolbarColor(R.color.colorPrimary)
                        .build();
            }
        });

        LAY_SELECTEDIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PickConfig.Builder(AddHopeBoxActivity.this)
                        .pickMode(PickConfig.MODE_SINGLE_PICK)
                        .spanCount(3)
                        //.showGif(true)
                        .checkImage(false) //default false
                        .useCursorLoader(false) //default true
                        .toolbarColor(R.color.colorPrimary)
                        .build();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_hopebox, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            AddHopeBoxActivity.this.finish();
        } else if (item.getItemId() == R.id.action_addHopeBox) {

            if(EDT_FOLDERNAME.getText().length()==0){
                dialogOk _dialoglert=new dialogOk(AddHopeBoxActivity.this, getString(R.string.hopeboxenterfolder)) {
                    @Override
                    public void onClickOk() {
                        dismiss();
                    }
                };
                _dialoglert.setCancelable(false);
                _dialoglert.setCanceledOnTouchOutside(false);
                _dialoglert.show();
            }else {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

//            Toast.makeText(AddHopeBoxActivity.this, "Saved Called", Toast.LENGTH_SHORT).show();

//            new AddHopeBox(EDT_FOLDERNAME.getText().toString(), FOLDER_IMG_PATH).execute();
                addHopeBox(EDT_FOLDERNAME.getText().toString(), FOLDER_IMG_PATH);
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == PickConfig.PICK_REQUEST_CODE) {
            LAY_SELECTIMG.setVisibility(View.GONE);
            LAY_SELECTEDIMG.setVisibility(View.VISIBLE);
            ArrayList<String> pick = data.getStringArrayListExtra(PickConfig.EXTRA_STRING_ARRAYLIST);
            FOLDER_IMG_PATH = pick.get(0).toString();
            Uri uri = UriUtil.generatorUri(FOLDER_IMG_PATH, UriUtil.LOCAL_FILE_SCHEME);
            Picasso.with(AddHopeBoxActivity.this).load(uri).into(IMG_HOPE);

        }
    }

    private void addHopeBox(final String TITLE, final String IMG_PATH) {

        if (!UTILS.isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addHopeBox(TITLE, IMG_PATH);
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
        mProgressDialog = new ProgressDialog(AddHopeBoxActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_uploading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> map = new HashMap<>();
//        map.put(Constant.URL, getResources().getString(R.string.server_url) + ".saveHopebox");
        map.put("cover", Utils.decodeFile(IMG_PATH, 800, 800));
        map.put("sid", Constant.SID);
        map.put("sname", Constant.SNAME);
        map.put(Constant.ID, "");
        map.put(Constant.TITLE, TITLE);
        new MultiPartParsing(this, map, General.PHPServices.ADD_HOPEBOX, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                Log.d(":::::: ", response);
                Toast.makeText(AddHopeBoxActivity.this,
                        getResources().getString(R.string.hopeboxadded), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                AddHopeBoxActivity.this.finish();
                ADDED_HOPEBOX = true;
            }
        });
    }

//    private class AddHopeBox extends AsyncTask<Void, Integer, String> {
//
//        private String TITLE, IMG_PATH;
//
//        public AddHopeBox(String title, String folderImg) {
//            this.TITLE = title;
//            this.IMG_PATH = folderImg;
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            // setting progress bar to zero
//            super.onPreExecute();
//        }
//
//
//        @Override
//        protected String doInBackground(Void... params) {
//            return uploadFile();
//        }
//
//        @SuppressWarnings("deprecation")
//        private String uploadFile() {
//            String responseString = null;
//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(getResources().getString(R.string.server_url) + ".saveHopebox");
//
//            try {
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new AndroidMultiPartEntity.ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
////                                publishProgress((int) ((num / (float) totalSize) * 100));
//                            }
//                        });
//
//                if (!IMG_PATH.equals("")) {
//                    File sourceFile = new File(IMG_PATH);
//                    entity.addPart("cover", new FileBody(sourceFile));
//                }
//                try {
//
//                    entity.addPart("sid", new StringBody(Constant.SID));
//                    entity.addPart("sname", new StringBody(Constant.SNAME));
//                    entity.addPart(Constant.ID, new StringBody(""));
//                    entity.addPart(Constant.TITLE, new StringBody(TITLE));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//
////                totalSize = entity.getContentLength();
//                httppost.setEntity(entity);
//                long totalLength = entity.getContentLength();
//                System.out.println("TotalLength : " + totalLength);
//
//                // Making server call
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity r_entity = response.getEntity();
//
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    // Server response
//                    responseString = EntityUtils.toString(r_entity);
//
//                } else {
//                    responseString = "Error occurred! Http Status Code: "
//                            + statusCode;
//                }
//
//            } catch (ClientProtocolException e) {
//                responseString = e.toString();
//            } catch (IOException e) {
//                responseString = e.toString();
//            }
//
//            return responseString;
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//            super.onPostExecute(result);
//
//            Log.d(":::::: ", result);
//            Toast.makeText(AddHopeBoxActivity.this,
//                    getResources().getString(R.string.hopeboxadded), Toast.LENGTH_SHORT).show();
//            AddHopeBoxActivity.this.finish();
//            ADDED_HOPEBOX = true;
//
//
//        }
//
//    }
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
