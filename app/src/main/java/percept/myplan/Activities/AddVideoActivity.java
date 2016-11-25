package percept.myplan.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.apache.http.util.TextUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import percept.myplan.POJO.HopeDetail;
import percept.myplan.R;
import percept.myplan.videocompressor.MediaController;

import static percept.myplan.Activities.HopeDetailsActivity.GET_HOPE_DETAILS;

public class AddVideoActivity extends AppCompatActivity {

    private final static int REQ_PICK_VID_GALLERY = 10;
    private final static int REQ_TAKE_VIDEO = 11;
    private final static int MY_PERMISSIONS_REQUEST = 22;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    String workFolder = null;
    String demoVideoFolder = null;
    String vkLogPath = null;
    private TextView TV_CHOOSEVIDEO, TV_RECORDVIDEO, TV_CHOOSEVIDLINK;
    private Uri videFileUri;
    private String FROM = "";
    private String HOPE_TITLE = "";
    private String HOPE_ID = "";
    private String HOPE_ELEMENT_ID = "";
//    private ProgressDialog MPROGRESSDIALOG;
    public static ProgressDialog MPROGRESSDIALOG;
    private boolean HAS_PERMISSION = true;
    private Utils UTILS;
    private CoordinatorLayout REL_COORDINATE;
    private HopeDetail hopeDetail;
    private VideoView vvPlayVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.add_video));

        if (getIntent().hasExtra("FROM_HOPE")) {
            FROM = getIntent().getExtras().getString("FROM_HOPE");
            HOPE_TITLE = getIntent().getExtras().getString("HOPE_TITLE");
            HOPE_ID = getIntent().getExtras().getString("HOPE_ID");
            if (getIntent().hasExtra(Constant.DATA)) {
                hopeDetail = (HopeDetail) getIntent().getExtras().getSerializable(Constant.DATA);
                HOPE_ELEMENT_ID = hopeDetail.getID();
            }
        }

        UTILS = new Utils(AddVideoActivity.this);
        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        vvPlayVideo = (VideoView) findViewById(R.id.vvPlayVideo);

        TV_CHOOSEVIDEO = (TextView) findViewById(R.id.tvChooseExistingVideo);
        TV_RECORDVIDEO = (TextView) findViewById(R.id.tvRecordVideo);
        TV_CHOOSEVIDLINK = (TextView) findViewById(R.id.tvChooseFromLink);



        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            insertDummyPermissionWrapper();
        }


        TV_CHOOSEVIDEO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HAS_PERMISSION) {
                    Toast.makeText(AddVideoActivity.this, R.string.requiredpermissionn, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.setType("video/*");
                startActivityForResult(intent, REQ_PICK_VID_GALLERY);
            }
        });

        TV_RECORDVIDEO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HAS_PERMISSION) {
                    Toast.makeText(AddVideoActivity.this, R.string.requiredpermissionn, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intents = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intents.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20);
                intents.putExtra(MediaStore.EXTRA_OUTPUT, videFileUri);
                startActivityForResult(intents, REQ_TAKE_VIDEO);
            }
        });

        TV_CHOOSEVIDLINK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(AddVideoActivity.this, AddStrategyLinksActivity.class);
                _intent.putExtra("HOPE_TITLE", HOPE_TITLE);
                _intent.putExtra("FROM_HOPE", "FROM_HOPE");
                _intent.putExtra("HOPE_ID", HOPE_ID);
                if (hopeDetail != null)
                    _intent.putExtra(Constant.DATA, hopeDetail);
                startActivity(_intent);
                AddVideoActivity.this.finish();
            }
        });
    }

    private void insertDummyPermissionWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();
            if (!addPermission(permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add("Camera");
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
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AddVideoActivity.this)
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    HAS_PERMISSION = true;
                } else {
                    HAS_PERMISSION = false;
                    // Permission Denied
                    Toast.makeText(AddVideoActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            AddVideoActivity.this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getData() != null) {
            Calendar cal = Calendar.getInstance();

            int seconds = cal.get(Calendar.SECOND);
            int hour = cal.get(Calendar.HOUR);
            int min = cal.get(Calendar.MINUTE);
            String currentDateTimeString = new SimpleDateFormat("ddMMMyyyy").format(new Date());
            if (requestCode == REQ_PICK_VID_GALLERY) {
                //region PICK_VID_GALLERY
                Uri selectedVideos = data.getData();
                String[] videoFilePath = {MediaStore.Video.Media.DATA};
                Cursor c = getContentResolver().query(selectedVideos, videoFilePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(videoFilePath[0]);
                String videosPath = c.getString(columnIndex);
                c.close();

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videosPath);
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long _timeInmillisec = Long.parseLong(time);
                long _duration = _timeInmillisec / 1000;
                long _hours = _duration / 3600;
                long _minutes = (_duration - _hours * 3600) / 60;
                long _seconds = _duration - (_hours * 3600);

                if (_duration > 20) {
                    Toast.makeText(AddVideoActivity.this, R.string.pickagainwithlesstime, Toast.LENGTH_SHORT).show();
                    return;
                }
                File file = new File(videosPath);
                long videoLength = file.length() / 1024;
                if (videoLength > (5 * 1024L)) {
//                    String _path = Constant.APP_MEDIA_PATH + File.separator + "VIDEOS" + File.separator + "compress.mp4";
//                    new TranscdingBackground(AddVideoActivity.this).execute(videosPath, _path);
                    new VideoCompressor().execute(videosPath);
                } else {

                    if (HopeDetailsAddElementActivity.DATA.size()>0){
                        HopeDetailsAddElementActivity.DATA.clear();
                        if (HopeDetailsAddElementActivity.DATA.size()<0){
                            HopeDetailsAddElementActivity.DATA.add(videosPath);
                            HopeDetailsAddElementActivity.TYPE="video";
                        }
                    }else
                        HopeDetailsAddElementActivity.DATA.add(videosPath);
                    HopeDetailsAddElementActivity.TYPE="video";
//                    HopeDetailsAddElementActivity.DATA.add(videosPath);

//               addHopeBoxVideoElement(HOPE_TITLE, HOPE_ID, HOPE_ELEMENT_ID, _path, "video");
                   /* MPROGRESSDIALOG.dismiss();
                    AddVideoActivity.this.finish();*/
//                    addHopeBoxVideoElement(HOPE_TITLE, HOPE_ID, HOPE_ELEMENT_ID, videosPath, "video");
                }


//              int end = videosPath.toString().lastIndexOf("/");
//            String str2 = videosPath.toString().substring(end + 1, videosPath.length());
//
//            String name = "VID_" + currentDateTimeString + seconds + hour + min + ".mp4";
//
//            File mediaStorageDir = new File(Constant.APP_MEDIA_PATH + File.separator + "VIDEOS");
//
//            // Create the storage directory if it does not exist
//            if (!mediaStorageDir.exists()) {
//                if (!mediaStorageDir.mkdirs()) {
//
//                }
//            }
//
//            boolean success = Constant.copyFile(videosPath, Constant.APP_MEDIA_PATH + File.separator + "VIDEOS", name);
//
//            if (success) {
//                String _Path = Constant.APP_MEDIA_PATH + File.separator + "VIDEOS" + File.separator + name;
//                Log.d("::::::::::: ", _Path);
                //
//            }
                //endregion

            } else if (requestCode == REQ_TAKE_VIDEO) {
                //region VIDEO_CAPTURE
                Uri videoUris = data.getData();
                String[] filePaths = {MediaStore.Video.Media.DATA};
                Cursor cu = getContentResolver().query(videoUris, filePaths, null, null, null);
                cu.moveToFirst();
                int columnIndex = cu.getColumnIndex(filePaths[0]);
                String picturePaths = cu.getString(columnIndex);
                cu.close();
                int end = picturePaths.toString().lastIndexOf("/");
                String str2 = picturePaths.toString().substring(end + 1, picturePaths.length());
                File mediaStorageDir = new File(Constant.APP_MEDIA_PATH + File.separator + "VIDEOS");

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {

                    }
                }

                String name = "VID_" + currentDateTimeString + seconds + hour + min + ".mp4";

//                Constant.copyFile(picturePaths, Constant.APP_MEDIA_PATH + File.separator + "VIDEOS", name);

//                File file = new File(_imgPath);
//                if (file.exists()) {
//                    file.delete();
//                }

                String _path = Constant.APP_MEDIA_PATH + File.separator + "VIDEOS" + File.separator + name;
//                new TranscdingBackground(AddVideoActivity.this).execute(picturePaths, _path);
                new VideoCompressor().execute(picturePaths);
                Log.d("::::::::::: ", _path);
//                addHopeBoxVideoElement(HOPE_TITLE, HOPE_ID, HOPE_ELEMENT_ID, _path, "video");
            }
        }
    }

    public void addHopeBoxVideoElement(final String title, final String hopeId, final String hopeElementId, final String vidpath, final String type) {
        if (!UTILS.isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                                addHopeBoxVideoElement(title, hopeId, hopeElementId, vidpath, type);
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
//                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//                textView.setTextColor(Color.YELLOW);

            snackbar.show();
//                return;
        }
        MPROGRESSDIALOG = new ProgressDialog(AddVideoActivity.this);
        MPROGRESSDIALOG.setIndeterminate(false);
        MPROGRESSDIALOG.setCanceledOnTouchOutside(false);
        MPROGRESSDIALOG.setMessage(getString(R.string.progress_uploading));
        MPROGRESSDIALOG.show();

        HashMap<String, String> params = new HashMap<>();
//        params.put(Constant.URL,getResources().getString(R.string.server_url) + ".saveHopemedia");
        if (!TextUtils.isEmpty(vidpath)) {
            params.put("media", vidpath);
        }

        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put(Constant.ID, hopeElementId);
        params.put(Constant.HOPE_ID, hopeId);
        params.put(Constant.HOPE_TITLE, title);
        params.put(Constant.HOPE_TYPE, type);



        new MultiPartParsing(AddVideoActivity.this, params, General.PHPServices.SAVE_HOPE_MEDIA, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {

                try {
                    File file = new File(vidpath);
                    file.delete();
                    if (file.exists()) {
                        file.getCanonicalFile().delete();
                        if (file.exists()) {
                            getApplicationContext().deleteFile(file.getName());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(":::::: ", response);
                if (getIntent().hasExtra("FROM_HOPE")) {
                    GET_HOPE_DETAILS = true;
                }
                AddVideoActivity.this.finish();
            }
        });

    }


    private void showAlertMessage() {
        dialogOk dialogOk = new dialogOk(AddVideoActivity.this,getString(R.string.video_size_bigger)) {
            @Override
            public void onClickOk() {
                dismiss();
            }
        };
        dialogOk.setCancelable(false);
        dialogOk.setCanceledOnTouchOutside(false);
        dialogOk.show();

    }

    class VideoCompressor extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MPROGRESSDIALOG = new ProgressDialog(AddVideoActivity.this);
            MPROGRESSDIALOG.setMessage(getString(R.string.progress_loading));
//            MPROGRESSDIALOG.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            MPROGRESSDIALOG.setIndeterminate(false);
            MPROGRESSDIALOG.setCanceledOnTouchOutside(false);
//            MPROGRESSDIALOG.setProgress(10);
            MPROGRESSDIALOG.show();
//            Log.d(TAG,"Start video compression");
        }

        @Override
        protected String doInBackground(String... path) {

            return MediaController.getInstance().convertVideo(path[0]);
        }

        @Override
        protected void onPostExecute(String _path) {
            super.onPostExecute(_path);
            MPROGRESSDIALOG.dismiss();
            if (!android.text.TextUtils.isEmpty(_path)) {
//                Log.d(TAG,"Compression successfully!");
                File file = new File(_path);
                long videoLength = file.length() / 1024;
                if (videoLength <= (5 * 1024L)){

                    if (HopeDetailsAddElementActivity.DATA.size()>0){
                       HopeDetailsAddElementActivity.DATA.clear();
                        if (HopeDetailsAddElementActivity.DATA.size()<0){
                            HopeDetailsAddElementActivity.DATA.add(_path);
                            HopeDetailsAddElementActivity.TYPE="video";
                        }
                    }else
                        HopeDetailsAddElementActivity.DATA.add(_path);
                        HopeDetailsAddElementActivity.TYPE="video";


//               addHopeBoxVideoElement(HOPE_TITLE, HOPE_ID, HOPE_ELEMENT_ID, _path, "video");
               /*MPROGRESSDIALOG.dismiss();
               AddVideoActivity.this.finish();*/
                 MPROGRESSDIALOG.dismiss();
                 AddVideoActivity.this.finish();
            }else
                    showAlertMessage();
            }
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            MPROGRESSDIALOG.setTitle("file upload"+values.toString());
        }
    }

//    public class TranscdingBackground extends AsyncTask<String, Integer, Integer> {
//
//        Activity _act;
//        String _path = null;
//        public TranscdingBackground(Activity act) {
//            _act = act;
//        }
//
//
//        @Override
//        protected void onPreExecute() {
//            demoVideoFolder = Constant.APP_MEDIA_PATH + File.separator + "VIDEOS";
//
//            Log.i(Prefs.TAG, getString(R.string.app_name) + " version: " + GeneralUtils.getVersionName(getApplicationContext()));
//            workFolder = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
//            Log.i(Prefs.TAG, "workFolder (license and logs location) path: " + workFolder);
//            vkLogPath = workFolder + "vk.log";
//            Log.i(Prefs.TAG, "vk log (native log) path: " + vkLogPath);
//
//            GeneralUtils.copyLicenseFromAssetsToSDIfNeeded(AddVideoActivity.this, workFolder);
//            PB.setVisibility(View.VISIBLE);
//        }
//
//        protected Integer doInBackground(String... paths) {
//            Log.i(Prefs.TAG, "doInBackground started...");
//
//            // delete previous log
//            GeneralUtils.deleteFileUtil(workFolder + "/vk.log");
//
//            PowerManager powerManager = (PowerManager) _act.getSystemService(Activity.POWER_SERVICE);
//            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
//            Log.d(Prefs.TAG, "Acquire wake lock");
//            wakeLock.acquire();
//
//            String commandStr = "";
//            _path = paths[1];
//            ///////////// Set Command using code (overriding the UI EditText) /////
//            //String commandStr = "ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -s 320x240 -r 30 -aspect 3:4 -ab 48000 -ac 2 -ar 22050 -vcodec mpeg4 -b 2097152 /sdcard/videokit/out.mp4";
//            //String[] complexCommand = {"ffmpeg", "-y" ,"-i", "/sdcard/videokit/in.mp4","-strict","experimental","-s", "160x120","-r","25", "-vcodec", "mpeg4", "-b", "150k", "-ab","48000", "-ac", "2", "-ar", "22050", "/sdcard/videokit/out.mp4"};
//            ///////////////////////////////////////////////////////////////////////
//
//            commandStr = "ffmpeg -y -i " + paths[0] + " -strict experimental " + _path;
//            Log.d(":::: Command", commandStr);
//            LoadJNI vk = new LoadJNI();
//            try {
//
//                //vk.run(complexCommand, workFolder, getApplicationContext());
//                vk.run(GeneralUtils.utilConvertToComplex(commandStr), workFolder, getApplicationContext());
//
//                // copying vk.log (internal native log) to the videokit folder
//                GeneralUtils.copyFileToFolder(vkLogPath, demoVideoFolder);
//
//            } catch (Throwable e) {
//                Log.e(Prefs.TAG, "vk run exeption.", e);
//            } finally {
//                if (wakeLock.isHeld())
//                    wakeLock.release();
//                else {
//                    Log.i(Prefs.TAG, "Wake lock is already released, doing nothing");
//                }
//            }
//            Log.i(Prefs.TAG, "doInBackground finished");
//            return Integer.valueOf(0);
//        }
//
//        protected void onProgressUpdate(Integer... progress) {
//        }
//
//        @Override
//        protected void onCancelled() {
//            Log.i(Prefs.TAG, "onCancelled");
//            //progressDialog.dismiss();
//            super.onCancelled();
//        }
//
//
//        @Override
//        protected void onPostExecute(Integer result) {
//            Log.i(Prefs.TAG, "onPostExecute");
////            progressDialog.dismiss();
//            super.onPostExecute(result);
//            final String status = GeneralUtils.getReturnCodeFromLog(workFolder + "/vk.log");
//
//            AddVideoActivity.this.runOnUiThread(new Runnable() {
//                public void run() {
////                    Toast.makeText(AddVideoActivity.this, status, Toast.LENGTH_LONG).show();
////                    if (status.equals("Transcoding Status: Failed")) {
////                        Toast.makeText(AddVideoActivity.this, "Check: " + workFolder + "vk.log" + " for more information.", Toast.LENGTH_LONG).show();
////                    }
//
//                    // copying vk.log (internal native log) to the sdcard folder
//                    GeneralUtils.copyFileToFolder(vkLogPath, demoVideoFolder);
//                    addHopeBoxVideoElement(HOPE_TITLE, HOPE_ID, HOPE_ELEMENT_ID, _path, "video");
////                    vvPlayVideo.setVideoURI(Uri.parse(_path));
////
////
////
////                    vvPlayVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
////                        @Override
////                        public void onPrepared(MediaPlayer mediaPlayer) {
////                            mediaPlayer.start();
////                        }
////                    });
//                }
//            });
//
//        }
//
//    }
//    private class AddHopeBoxVideoElement extends AsyncTask<Void, Integer, String> {
//
//        private String HOPE_TITLE, VID_PATH, HOPE_ID, TYPE;
//
//        public AddHopeBoxVideoElement(String title, String hopeId, String vidpath, String type) {
//            this.HOPE_TITLE = title;
//            this.VID_PATH = vidpath;
//            this.HOPE_ID = hopeId;
//            this.TYPE = type;
//
//        }
//
//        @Override
//        protected void onPreExecute() {
//            // setting progress bar to zero
//            super.onPreExecute();
//            PB.setVisibility(View.VISIBLE);
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
//                if (!VID_PATH.equals("")) {
//                    File sourceFile = new File(VID_PATH);
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
//            PB.setVisibility(View.GONE);
//            super.onPostExecute(result);
//            Log.d(":::::: ", result);
//            if (getIntent().hasExtra("FROM_HOPE")) {
//                GET_HOPE_DETAILS = true;
//            }
//            AddVideoActivity.this.finish();
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



    // new add video progress shown





}
