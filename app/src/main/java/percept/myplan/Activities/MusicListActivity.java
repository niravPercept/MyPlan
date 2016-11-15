package percept.myplan.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Dialogs.dialogOk;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.MultiPartParsing;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.R;

import static percept.myplan.Activities.HopeDetailsActivity.GET_HOPE_DETAILS;

public class MusicListActivity extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private String[] nameFile;
    private ImageAdapter imageAdapter;
    private String FROM = "";
    private String HOPE_TITLE = "";
    private String HOPE_ID = "";
    private String HOPE_ELEMENT_ID = "";
    private boolean FROM_EDIT = false;
    private Utils UTILS;
    private CoordinatorLayout REL_COORDINATE;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.musiclist));

        if (getIntent().hasExtra("FROM_HOPE")) {
            FROM = getIntent().getExtras().getString("FROM_HOPE");
            HOPE_TITLE = getIntent().getExtras().getString("HOPE_TITLE");
            HOPE_ID = getIntent().getExtras().getString("HOPE_ID");
            if (getIntent().hasExtra("HOPE_ELEMENT_ID")) {
                HOPE_ELEMENT_ID = getIntent().getExtras().getString("HOPE_ELEMENT_ID");
            }
        }

        if (getIntent().hasExtra("FROM_EDIT")) {
            FROM_EDIT = true;
        }

        UTILS = new Utils(MusicListActivity.this);
        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            checkPermission();
        } else getMusicList();

//        final Button selectBtn = (Button) findViewById(R.id.selectBtn);
//        selectBtn.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                final int len = thumbnailsselection.length;
//                int cnt = 0;
//                String selectImages = "";
//                for (int i = 0; i < len; i++) {
//                    if (thumbnailsselection[i]) {
//                        cnt++;
//                        selectImages = selectImages + arrPath[i] + "|";
//                    }
//                }
//                if (cnt == 0) {
//                    Toast.makeText(getApplicationContext(),
//                            "Please select at least one image",
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(),
//                            "You've selected Total " + cnt + " image(s).",
//                            Toast.LENGTH_LONG).show();
//                    Log.d("SelectedImages", selectImages);
//                }
//            }
//        });
    }

    private void checkPermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();

            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add("Write Storage");
            if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
                permissionsNeeded.add("Read Storage");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                            new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            });
                    return;
                }

                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);

                return;
            } else getMusicList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> params = new HashMap<String, Integer>();
                // Initial

                params.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                params.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    params.put(permissions[i], grantResults[i]);
                if (params.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && params.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    getMusicList();
                } else {
                    // Permission Denied
                    Toast.makeText(MusicListActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MusicListActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();

    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }


    private void getMusicList() {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Audio.Media._ID};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
                + " AND " + MediaStore.Audio.Media.MIME_TYPE + "= 'audio/mpeg'";
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor imagecursor = managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, selection,
                null, orderBy);
        int image_column_index = imagecursor.getColumnIndex(MediaStore.Audio.Media._ID);
        this.count = imagecursor.getCount();
        this.thumbnails = new Bitmap[this.count];
        this.arrPath = new String[this.count];
        this.nameFile = new String[this.count];
        this.thumbnailsselection = new boolean[this.count];
        for (int i = 0; i < this.count; i++) {
            imagecursor.moveToPosition(i);
            int id = imagecursor.getInt(image_column_index);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Audio.Media.DATA);


            thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
                    getApplicationContext().getContentResolver(), id,
                    MediaStore.Images.Thumbnails.MICRO_KIND, null);


            arrPath[i] = imagecursor.getString(dataColumnIndex);


            nameFile[i] = arrPath[i].substring(arrPath[i].lastIndexOf("/") + 1, arrPath[i].length());

//			Log.d("soundPAth","");
        }
        ListView imagegrid = (ListView) findViewById(R.id.lstMusic);
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);
        imagecursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_strategy_music, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            thumbnailsselection.

            for(int i=0;i<thumbnailsselection.length;i++){
               thumbnailsselection[i]=false;
            }
            MusicListActivity.this.finish();
            return true;
        } else if (item.getItemId() == R.id.action_AddStrategyMusic) {
            if (thumbnailsselection != null) {
                final int len = thumbnailsselection.length;

//            String selectImages = "";
                if (FROM.equals("") || FROM_EDIT) {
                    if (FROM_EDIT) {
                        for (int i = 0; i < len; i++) {
                            if (thumbnailsselection[i]) {
//                            selectImages = selectImages + arrPath[i] + "|";
                                StrategyEditActivity.LIST_MUSIC.add(arrPath[i]);
                            }
                        }
                        setResult(RESULT_OK);
                        MusicListActivity.this.finish();
                    } else {
                        for (int i = 0; i < len; i++) {
                            if (thumbnailsselection[i]) {
//                            selectImages = selectImages + arrPath[i] + "|";
                                AddStrategyActivity.LIST_MUSIC.add(arrPath[i]);
                            }
                        }
                        setResult(RESULT_OK);
                        MusicListActivity.this.finish();
                    }
                } else {
                    AddMusicElement(len);
                }
            }

            return true;
        }
        return false;
    }

    private void AddMusicElement(final int len) {
        if (!UTILS.isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AddMusicElement(len);
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
        for (int i = 0; i < len; i++) {
            if (thumbnailsselection[i]) {
                addHopeBoxMusicElement(HOPE_TITLE, HOPE_ID, arrPath[i], "music");
                break;
            }
        }
    }

    public void addHopeBoxMusicElement(final String title, final String hopeId, final String musicpath, final String type) {

        if (HopeDetailsAddElementActivity.DATA_MUSIC.size() > 0) {
            HopeDetailsAddElementActivity.DATA_MUSIC.clear();
            if (HopeDetailsAddElementActivity.DATA_MUSIC.size() < 0) {
                HopeDetailsAddElementActivity.DATA_MUSIC.add(musicpath);
                HopeDetailsAddElementActivity.TYPE =type;
            }
        } else {
            HopeDetailsAddElementActivity.DATA_MUSIC.add(musicpath);
            HopeDetailsAddElementActivity.TYPE = type;
        }
            MusicListActivity.this.finish();

        /*mProgressDialog = new ProgressDialog(MusicListActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> params = new HashMap<>();
//        params.put(Constant.URL, getResources().getString(R.string.server_url) + ".saveHopemedia");
        if (!type.equals("music")) {

         *//*   File file = new File(musicpath);
            long musicLength = file.length() / 1024;
            if (musicLength <= (5 * 1024L))*//*
            if (!TextUtils.isEmpty(musicpath)) {
                if (musicpath.length() != 0) {
                    params.put("media", musicpath);
                    Log.d("msi", musicpath);

                } else {
                    showAlertMessage();
                    mProgressDialog.dismiss();
                    return;
                }
            }
        }
        if (!TextUtils.isEmpty(musicpath)) {

            *//*File file = new File(musicpath);
            long musicLength = file.length() / 1024;
            if (musicLength <= (5 * 1024L))*//*
            if (musicpath.length()!=0) {
            *//*if (musicpath.length()!=0) {*//*
                params.put("internal_audio", musicpath);
                Log.d("msi", musicpath);

            }else
            {
                showAlertMessage();
                mProgressDialog.dismiss();
                return;
            }
        }
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);

        params.put(Constant.ID, HOPE_ELEMENT_ID);
        params.put(Constant.HOPE_ID, hopeId);
        params.put(Constant.HOPE_TITLE, title);
        params.put(Constant.HOPE_TYPE, type);
        new MultiPartParsing(MusicListActivity.this, params, General.PHPServices.SAVE_HOPE_MEDIA, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                if (getIntent().hasExtra("FROM_HOPE")) {
                    GET_HOPE_DETAILS = true;
                }
                Log.d(":::::: ", response);
                mProgressDialog.dismiss();
                MusicListActivity.this.finish();
            }
        });*/
    }

//    private class AddHopeBoxMusicElement extends AsyncTask<Void, Integer, String> {
//
//        private String HOPE_TITLE, MUSIC_PATH, HOPE_ID, TYPE;
//
//        public AddHopeBoxMusicElement(String title, String hopeId, String musicpath, String type) {
//            this.HOPE_TITLE = title;
//            this.MUSIC_PATH = musicpath;
//            this.HOPE_ID = hopeId;
//            this.TYPE = type;
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
//            HttpPost httppost = new HttpPost(getResources().getString(R.string.server_url) + ".saveHopemedia");
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
//                if (!MUSIC_PATH.equals("")) {
//                    File sourceFile = new File(MUSIC_PATH);
//                    entity.addPart("media", new FileBody(sourceFile));
//                }
//                try {
//
//                    entity.addPart("sid", new StringBody(Constant.SID));
//                    entity.addPart("sname", new StringBody(Constant.SNAME));
//                    entity.addPart(Constant.ID, new StringBody(""));
//                    entity.addPart(Constant.HOPE_ID, new StringBody(HOPE_ID));
//                    entity.addPart(Constant.HOPE_TITLE, new StringBody(HOPE_TITLE));
//                    entity.addPart(Constant.HOPE_TYPE, new StringBody(TYPE));
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
//            if (getIntent().hasExtra("FROM_HOPE")) {
//                GET_HOPE_DETAILS=true;
//            }
//            Log.d(":::::: ", result);
//            CLOSE_PAGE = true;
//            MusicListActivity.this.finish();
//        }
//
//    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private int countSelection = 0;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.lay_music_list_item, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
                holder.txtview = (TextView) convertView.findViewById(R.id.itemtxtview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            holder.txtview.setText(nameFile[position]);
            holder.checkbox.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
//                    if (!FROM.equals("")) {
//                        for (int i = 0; i < count; i++) {
//                            thumbnailsselection[i] = false;
//                        }
//                    }
//                    if (thumbnailsselection[id]) {
//                        cb.setChecked(false);
//                        thumbnailsselection[id] = false;
//                    } else {
//                        cb.setChecked(true);
//                        thumbnailsselection[id] = true;
//                    }
//                    cb.setChecked(!cb.isChecked());
//                    thumbnailsselection[id] = cb.isChecked();
//                    notifyDataSetChanged();


                    if (!FROM.equals("")) {
                        for (int i = 0; i < count; i++) {
                            thumbnailsselection[i] = false;
                        }
                    } else if (cb.isChecked() && countSelection >= 5) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                        countSelection = 0;
                        notifyDataSetChanged();
                        return;
                    }
                    thumbnailsselection[id] = cb.isChecked();
                    countSelection = 0;
                    notifyDataSetChanged();
                }
            });
            holder.imageview.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = v.getId();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "audio/*");
                    startActivity(intent);
                }
            });
            holder.imageview.setImageBitmap(thumbnails[position]);
            holder.checkbox.setChecked(thumbnailsselection[position]);
            holder.id = position;
            if (position == 0)
                countSelection = 0;
            if (thumbnailsselection[position])
                ++countSelection;
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
        TextView txtview;
        int id;
    }

    private void showAlertMessage() {
        dialogOk dialogOk = new dialogOk(MusicListActivity.this,getString(R.string.music_size_bigger)) {
            @Override
            public void onClickOk() {
                dismiss();
            }
        };
        dialogOk.setCancelable(false);
        dialogOk.setCanceledOnTouchOutside(false);
        dialogOk.show();

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
