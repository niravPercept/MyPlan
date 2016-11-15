package percept.myplan.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import me.crosswall.photo.pick.PickConfig;
import percept.myplan.Dialogs.dialogOk;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.MultiPartParsing;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.POJO.HopeDetail;
import percept.myplan.R;
import percept.myplan.adapters.ImageDeleteAdapter;

import static percept.myplan.Activities.HopeDetailsActivity.GET_HOPE_DETAILS;
import static percept.myplan.Global.Utils.decodeFile;


public class AddStrategyImageActivity extends AppCompatActivity {

    private static final int REQ_TAKE_PICTURE = 33;
    private final static int MY_PERMISSIONS_REQUEST = 22;
    private static Uri IMG_URI;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private TextView TV_CHOOSEEXISTING, TV_TAKENEW;
    private String FROM = "";
    private String HOPE_TITLE = "";
    private String HOPE_ID = "";
    private String HOPE_ELEMENT_ID = "";
    private boolean FROM_EDIT = false;
    private ProgressDialog mProgressDialog;
    private Utils UTILS;
    private CoordinatorLayout REL_COORDINATE;
    private RecyclerView rvPhotos;
    private boolean HAS_PERMISSION = true;
    private ImageDeleteAdapter imageAdapter;
    private TextView tvSelectedText, tvNoOfImaged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_strategy_image);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.addimage));

        if (getIntent().hasExtra("FROM_HOPE")) {
            FROM = getIntent().getExtras().getString("FROM_HOPE");
            HOPE_TITLE = getIntent().getExtras().getString("HOPE_TITLE");
            HOPE_ID = getIntent().getExtras().getString("HOPE_ID");
            if (getIntent().hasExtra(Constant.DATA)) {
                HopeDetail _Detail = (HopeDetail) getIntent().getExtras().getSerializable(Constant.DATA);
                HOPE_ELEMENT_ID = _Detail.getID();
            }
        }


        UTILS = new Utils(AddStrategyImageActivity.this);
        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        TV_CHOOSEEXISTING = (TextView) findViewById(R.id.tvChooseExisting);
        TV_TAKENEW = (TextView) findViewById(R.id.tvTakeNew);

        tvSelectedText = (TextView) findViewById(R.id.tvSelectedText);
        tvNoOfImaged = (TextView) findViewById(R.id.tvNoOfImaged);

        rvPhotos = (RecyclerView) findViewById(R.id.rvPhotos);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(AddStrategyImageActivity.this, 3);
        rvPhotos.setLayoutManager(mLayoutManager);
        rvPhotos.setItemAnimator(new DefaultItemAnimator());

        if (getIntent().hasExtra("FROM_EDIT")) {
            FROM_EDIT = true;
            tvSelectedText.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            insertDummyPermissionWrapper();
        }
        if (getIntent().hasExtra("FROM_HOPE")) {
            tvSelectedText.setVisibility(View.GONE);
            tvNoOfImaged.setVisibility(View.GONE);
        }
        TV_CHOOSEEXISTING.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HAS_PERMISSION) {
                    Toast.makeText(AddStrategyImageActivity.this, R.string.requiredpermissionn, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (FROM.equals("") || FROM_EDIT) {
                    new PickConfig.Builder(AddStrategyImageActivity.this)
                            .pickMode(PickConfig.MODE_MULTIP_PICK)
                            .maxPickSize(10)
                            .spanCount(3)
                            //.showGif(true)
                            .checkImage(false) //default false
                            .useCursorLoader(false) //default true
                            .toolbarColor(R.color.colorPrimary)
                            .build();
                } else {
                    new PickConfig.Builder(AddStrategyImageActivity.this)
                            .pickMode(PickConfig.MODE_SINGLE_PICK)
                            .spanCount(3)
                            //.showGif(true)
                            .checkImage(false) //default false
                            .useCursorLoader(false) //default true
                            .toolbarColor(R.color.colorPrimary)
                            .build();
                }
            }
        });

        TV_TAKENEW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HAS_PERMISSION)
                    return;

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                IMG_URI = Uri.fromFile(Constant.getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, IMG_URI);
                // start the image capture Intent
                startActivityForResult(intent, REQ_TAKE_PICTURE);


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
        new AlertDialog.Builder(AddStrategyImageActivity.this)
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (FROM.equals("") || FROM_EDIT) {
                if (FROM_EDIT) {
                    if (StrategyEditActivity.LIST_IMG.size() > 10) {
                        Snackbar.make(getWindow().getDecorView(), getString(R.string.max_10_images), Snackbar.LENGTH_LONG).show();
                    } else AddStrategyImageActivity.this.finish();
                } else {
                    if (AddStrategyActivity.LIST_IMG.size() > 10) {
                        Snackbar.make(getWindow().getDecorView(), getString(R.string.max_10_images), Snackbar.LENGTH_LONG).show();
                    } else AddStrategyImageActivity.this.finish();
                }

            } else

                AddStrategyImageActivity.this.finish();
            return true;
        }
        return false;
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
                    Toast.makeText(AddStrategyImageActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == PickConfig.PICK_REQUEST_CODE) {

            if (FROM.equals("") || FROM_EDIT) {
                final List<String> _LIST_IMG = data.getStringArrayListExtra(PickConfig.EXTRA_STRING_ARRAYLIST);
                mProgressDialog = new ProgressDialog(AddStrategyImageActivity.this);
                mProgressDialog.setMessage(getString(R.string.progress_loading));
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for (String path : _LIST_IMG)
                            rotateImage(path);


                    }
                }).start();
                mProgressDialog.dismiss();
                if (FROM_EDIT) {
                    tvSelectedText.setVisibility(View.VISIBLE);
                    imageAdapter = new ImageDeleteAdapter(AddStrategyImageActivity.this, StrategyEditActivity.LIST_IMG);
                    rvPhotos.setAdapter(imageAdapter);
//                        AddStrategyImageActivity.this.finish();
                } else {
                    imageAdapter = new ImageDeleteAdapter(AddStrategyImageActivity.this, AddStrategyActivity.LIST_IMG);
                    rvPhotos.setAdapter(imageAdapter);
//                        AddStrategyImageActivity.this.finish();
                }
//                if (FROM_EDIT) {
////                    StrategyEditActivity.LIST_IMG.addAll(data.getStringArrayListExtra(PickConfig.EXTRA_STRING_ARRAYLIST));
//                    StrategyEditActivity.LIST_IMG.addAll(_LIST_IMG);
////                    AddStrategyImageActivity.this.finish();
//                    tvSelectedText.setVisibility(View.VISIBLE);
//                    imageAdapter = new ImageDeleteAdapter(AddStrategyImageActivity.this, StrategyEditActivity.LIST_IMG);
//                    rvPhotos.setAdapter(imageAdapter);
//                } else {
//                    AddStrategyActivity.LIST_IMG.addAll(_LIST_IMG);
////                    AddStrategyImageActivity.this.finish();
//                    imageAdapter = new ImageDeleteAdapter(AddStrategyImageActivity.this, AddStrategyActivity.LIST_IMG);
//                    rvPhotos.setAdapter(imageAdapter);
//                }
//                imageAdapter.notifyDataSetChanged();
            } else {
                final List<String> _LIST_IMG = data.getStringArrayListExtra(PickConfig.EXTRA_STRING_ARRAYLIST);

                if (_LIST_IMG.size() > 0) {
                    // addHopeBoxImageElement(HOPE_TITLE, HOPE_ID, _LIST_IMG.get(0), HOPE_ELEMENT_ID, "image");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            rotateImage(_LIST_IMG.get(0));
                        }
                    }).start();

                }
            }
        }
        if (requestCode == REQ_TAKE_PICTURE) {

            try {

                Calendar cal = Calendar.getInstance();
                int seconds = cal.get(Calendar.SECOND);
                int hour = cal.get(Calendar.HOUR);
                int min = cal.get(Calendar.MINUTE);
                String currentDateTimeString = new SimpleDateFormat("ddMMMyyyy").format(new Date());

                String name = "IMG_" + currentDateTimeString + seconds + hour + min + ".jpeg";

                String _imgPath = IMG_URI.getPath();

                File mediaStorageDir = new File(Constant.APP_MEDIA_PATH + File.separator + "IMAGES");

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {

                    }
                }

                Constant.copyFile(_imgPath, Constant.APP_MEDIA_PATH + File.separator + "IMAGES", name);

                File file = new File(_imgPath);
                if (file.exists()) {
                    file.delete();
                }
                String _Path = Constant.APP_MEDIA_PATH + File.separator + "IMAGES" + File.separator + name;

                Log.d("::::::: ", _Path);

                rotateImage(_Path);

                if (FROM_EDIT) {
                    tvSelectedText.setVisibility(View.VISIBLE);
                    imageAdapter = new ImageDeleteAdapter(AddStrategyImageActivity.this, StrategyEditActivity.LIST_IMG);
                    rvPhotos.setAdapter(imageAdapter);
//                        AddStrategyImageActivity.this.finish();
                }
//                else {
//                    imageAdapter = new ImageDeleteAdapter(AddStrategyImageActivity.this, AddStrategyActivity.LIST_IMG);
//                    rvPhotos.setAdapter(imageAdapter);
////                        AddStrategyImageActivity.this.finish();
//                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void rotateImage(final String FILE_PATH) {
        try {
            File f = new File(FILE_PATH);

            int angle = 0;
            ExifInterface exif = new ExifInterface(FILE_PATH);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, options);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
                default:
                    break;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate(angle);


            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    outputStream);

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(FILE_PATH);
                        fos.write(outputStream.toByteArray());
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if (FROM.equals("") || FROM_EDIT) {
                        if (FROM_EDIT) {
                            StrategyEditActivity.LIST_IMG.add(decodeFile(FILE_PATH,800,800));
                            tvSelectedText.setVisibility(View.VISIBLE);
//                            imageAdapter = new ImageDeleteAdapter(AddStrategyImageActivity.this, StrategyEditActivity.LIST_IMG);
//                            rvPhotos.setAdapter(imageAdapter);
//                        AddStrategyImageActivity.this.finish();
                        } else {
                            AddStrategyActivity.LIST_IMG.add(decodeFile(FILE_PATH,800,800));
//                            imageAdapter = new ImageDeleteAdapter(AddStrategyImageActivity.this, AddStrategyActivity.LIST_IMG);
//                            rvPhotos.setAdapter(imageAdapter);
//                        AddStrategyImageActivity.this.finish();
                        }
                        imageAdapter.notifyDataSetChanged();
                    } else {
//                        addHopeBoxImageElement(HOPE_TITLE, HOPE_ID, FILE_PATH, HOPE_ELEMENT_ID, "image");

                        if (HopeDetailsAddElementActivity.DATA_IMAGE.size() > 0) {
                            HopeDetailsAddElementActivity.DATA_IMAGE.clear();
                            if (HopeDetailsAddElementActivity.DATA_IMAGE.size() < 0) {
                                HopeDetailsAddElementActivity.DATA_IMAGE.add(FILE_PATH);
                                HopeDetailsAddElementActivity.TYPE ="image";
                            }
                        } else{
                            HopeDetailsAddElementActivity.DATA_IMAGE.add(FILE_PATH);
                        HopeDetailsAddElementActivity.TYPE ="image";
                    }
                        AddStrategyImageActivity.this.finish();
                    }
                }
            }.execute();


        } catch (IOException e) {
            Log.w("TAG", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.w("TAG", "-- OOM Error in setting image");
        }
    }

    private void addHopeBoxImageElement(final String title, final String hopeId, final String imgpath, final String hopeElementId, final String type) {




     /*   if (!UTILS.isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addHopeBoxImageElement(title, hopeId, imgpath, hopeElementId, type);
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

        mProgressDialog = new ProgressDialog(AddStrategyImageActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_uploading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> params = new HashMap<>();
//        params.put(Constant.URL, getResources().getString(R.string.server_url) + ".saveHopemedia");
        String _path = decodeFile(imgpath, 800, 800);
        File file = new File(_path);
        long imageLength = file.length() / 1024;
        if (imageLength > 512) {
            showAlertMessage();
            mProgressDialog.dismiss();
            return;
        }

        params.put("media", _path);
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put(Constant.ID, HOPE_ELEMENT_ID);
        params.put(Constant.HOPE_ID, hopeId);
        params.put(Constant.HOPE_TITLE, title);
        params.put(Constant.HOPE_TYPE, type);
        new MultiPartParsing(this, params, General.PHPServices.SAVE_HOPE_MEDIA, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                mProgressDialog.dismiss();
                Log.d(":::::: ", response.toString());
                if (getIntent().hasExtra("FROM_HOPE")) {
                    GET_HOPE_DETAILS = true;
                }

            }
        });*/

    }

    private void showAlertMessage() {
        dialogOk dialogOk = new dialogOk(AddStrategyImageActivity.this, getString(R.string.image_size_bigger)) {
            @Override
            public void onClickOk() {
                dismiss();
            }
        };
        dialogOk.setCancelable(false);
        dialogOk.setCanceledOnTouchOutside(false);
        dialogOk.show();

    }

//    private class AddHopeBoxImageElement extends AsyncTask<Void, Integer, String> {
//
//        private String HOPE_TITLE, IMG_PATH, HOPE_ID, TYPE;
//
//        public AddHopeBoxImageElement(String title, String hopeId, String imgpath, String type) {
//            this.HOPE_TITLE = title;
//            this.IMG_PATH = imgpath;
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
//                if (!IMG_PATH.equals("")) {
//                    File sourceFile = new File(IMG_PATH);
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
//            AddStrategyImageActivity.this.finish();
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
