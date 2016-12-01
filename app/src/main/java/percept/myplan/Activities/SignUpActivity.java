package percept.myplan.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Dialogs.dialogSelectPic;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.MultiPartParsing;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.R;

import static percept.myplan.Global.Utils.decodeFile;


public class SignUpActivity extends AppCompatActivity {
    private static final int REQ_TAKE_PICTURE = 33;
    private static final int TAKE_PICTURE_GALLERY = 34;
    private final static int MY_PERMISSIONS_REQUEST = 14;
    public static boolean PIC_FROM_GALLERY = true;
    private static Uri IMG_URI;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private ImageView IMG_USER;
    private EditText EDT_FIRSTNAME, EDT_LASTNAME, EDT_EMAIL, EDT_PHONENO, EDT_BIRTHDAY, EDT_PASSWORD;
    private Button BTN_ENTER;
    private int SYEAR = 0;//,SDAY = 0, SMONTH = 0;
    private TextView TV_CAPTUREIMG;
    private String FILE_PATH = "", YEAR = "1960";
    private Utils UTILS;
    private CoordinatorLayout REL_COORDINATE;
    private boolean HAS_PERMISSION = true;
    private ProgressDialog mProgressDialog;
//    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
//
//        @Override
//        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
//            // TODO Auto-generated method stub
//
//            showDate(arg1, arg2, arg3);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.app_name));

        UTILS = new Utils(SignUpActivity.this);
        TV_CAPTUREIMG = (TextView) findViewById(R.id.tvCaptureImg);
        IMG_USER = (ImageView) findViewById(R.id.imgUserImage);
        EDT_FIRSTNAME = (EditText) findViewById(R.id.edtFirstName);
        EDT_LASTNAME = (EditText) findViewById(R.id.edtLastName);
        EDT_EMAIL = (EditText) findViewById(R.id.edtEmail);
        EDT_PHONENO = (EditText) findViewById(R.id.edtPhoneNo);
        EDT_BIRTHDAY = (EditText) findViewById(R.id.edtBirthDay);
        EDT_PASSWORD = (EditText) findViewById(R.id.edtPassword);
        BTN_ENTER = (Button) findViewById(R.id.btnEnter);
        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        IMG_USER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermission();
            }
        });

        Calendar cal = Calendar.getInstance();
        SYEAR = cal.get(Calendar.YEAR);
//        SMONTH = cal.get(Calendar.MONTH);
//        SDAY = cal.get(Calendar.DAY_OF_MONTH);
//        showDate(SYEAR, SMONTH, SDAY);

        EDT_BIRTHDAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new DatePickerDialog(SignUpActivity.this, myDateListener, SYEAR, SMONTH - 1, SDAY).show();

                YearCalender _dialog = new YearCalender(SignUpActivity.this);
                _dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                _dialog.setCanceledOnTouchOutside(true);
                _dialog.show();

                _dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        EDT_BIRTHDAY.setText(YEAR);
                    }
                });
            }
        });

        BTN_ENTER.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
//                SignUpActivity.this.finish();

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


                if (UTILS.isNetConnected()) {
                    boolean error = false;
                    if (EDT_FIRSTNAME.getText().toString().trim().equals("")) {

                        error = true;
                    } else if (EDT_LASTNAME.getText().toString().trim().equals("")) {
                        error = true;
                    } else if (EDT_PASSWORD.getText().toString().trim().equals("")) {
                        error = true;
                    } else if (EDT_PHONENO.getText().toString().trim().equals("") && EDT_PHONENO.getText().toString().length() < 8) {
                        error = true;
                    } else if (EDT_BIRTHDAY.getText().toString().trim().equals("")) {
                        error = true;
                    }

                    if (error) {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.fillupfield),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (UTILS.isEmailValid(EDT_EMAIL.getText().toString())) {


                        mProgressDialog = new ProgressDialog(SignUpActivity.this);
                        mProgressDialog.setMessage(getString(R.string.progress_loading));
                        mProgressDialog.setIndeterminate(false);
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.show();
                        signup();

                    } else {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.writevalidemail), Toast.LENGTH_SHORT).show();
                    }

                } else {
//                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.internetconn), Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar
                            .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    signup();
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
//                Map<String, String> params = new HashMap<String, String>();
//                params.put(Constant.FIRST_NAME, EDT_FIRSTNAME.getText().toString().trim());
//                params.put(Constant.LAST_NAME, EDT_LASTNAME.getText().toString().trim());
//                params.put(Constant.EMAIL, EDT_EMAIL.getText().toString().trim());
//                params.put(Constant.PHONE, EDT_PHONENO.getText().toString().trim());
//                params.put(Constant.DOB, EDT_BIRTHDAY.getText().toString().trim());
//                params.put(Constant.PASSWORD, EDT_PASSWORD.getText().toString().trim());
//                params.put(Constant.PROFILE_IMAGE, "admin");
//                try {
//                    new General().getJSONContentFromInternetService(SignUpActivity.this, General.PHPServices.REGISTER, params, false, false, new VolleyResponseListener() {
//                        @Override
//                        public void onError(VolleyError message) {
//                            Log.d("::::::::::", message.toString());
//                        }
//
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Log.d("::::::::::", response.toString());
//                            startActivity(new Intent(SignUpActivity.this, LoginActivity_1.class));
//                            SignUpActivity.this.finish();
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }
        });
        setViews();
    }

//    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
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
//            HttpPost httppost = new HttpPost(getResources().getString(R.string.server_url) + ".register");
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
//                if (!FILE_PATH.equals("")) {
//                    File sourceFile = new File(FILE_PATH);
//
//                    // Adding file data to http body
//                    entity.addPart("profile_image", new FileBody(sourceFile));
//                }
//                // Extra parameters if you want to pass to server
//                try {
//                    entity.addPart(Constant.FIRST_NAME, new StringBody(EDT_FIRSTNAME.getText().toString().trim()));
//                    entity.addPart(Constant.LAST_NAME, new StringBody(EDT_LASTNAME.getText().toString().trim()));
//                    entity.addPart(Constant.EMAIL, new StringBody(EDT_EMAIL.getText().toString().trim()));
//                    entity.addPart(Constant.PHONE, new StringBody(EDT_PHONENO.getText().toString().trim()));
//                    entity.addPart(Constant.DOB, new StringBody(YEAR.toString().trim()));
//                    entity.addPart(Constant.PASSWORD, new StringBody(EDT_PASSWORD.getText().toString().trim()));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//
////                totalSize = entity.getContentLength();
//                httppost.setEntity(entity);
//
//                // Making server call
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity r_entity = response.getEntity();
//
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    // Server response
//                    responseString = EntityUtils.toString(r_entity);
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
//            PB.setVisibility(View.GONE);
//            try {
//                JSONObject _object = new JSONObject(result);
//                JSONObject _ObjData = _object.getJSONObject(Constant.DATA);
//                if (_ObjData.getString(Constant.STATUS).equals("Success")) {
//                    startActivity(new Intent(SignUpActivity.this, LoginActivity_1.class));
//                    SignUpActivity.this.finish();
//                } else {
//                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.signuperror), Toast.LENGTH_SHORT).show();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }

    private void signup() {
        if (!UTILS.isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            signup();
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
        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.URL, getResources().getString(R.string.server_url) + ".register");
        if (!FILE_PATH.equals("")) {
            params.put("profile_image", FILE_PATH);
        }
        params.put(Constant.FIRST_NAME, EDT_FIRSTNAME.getText().toString().trim());
        params.put(Constant.LAST_NAME, EDT_LASTNAME.getText().toString().trim());
        params.put(Constant.EMAIL, EDT_EMAIL.getText().toString().trim());
        params.put(Constant.PHONE, EDT_PHONENO.getText().toString().trim());
        params.put(Constant.DOB, YEAR.toString().trim());
        params.put(Constant.PASSWORD, EDT_PASSWORD.getText().toString().trim());
        new MultiPartParsing(SignUpActivity.this, params, General.PHPServices.REGISTER, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {

                try {
                    JSONObject _object = new JSONObject(response);
                    JSONObject _ObjData = _object.getJSONObject(Constant.DATA);
                    if (_ObjData.getString(Constant.STATUS).equals("Success")) {
                        LoginCall();
                       /* startActivity(new Intent(SignUpActivity.this, LoginActivity_1.class));
                        SignUpActivity.this.finish();*/
                    } else {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.signuperror), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void LoginCall() {
        Map<String, String> params = new HashMap<String, String>();
        params.put(Constant.USER_NAME, EDT_EMAIL.getText().toString().trim());
        params.put(Constant.PASSWORD, EDT_PASSWORD.getText().toString().trim());

        try {
            new General().getJSONContentFromInternetService(SignUpActivity.this, General.PHPServices.LOGIN, params, true, false, true, new VolleyResponseListener() {

                @Override
                public void onError(VolleyError message) {
                    Log.d(":::::::::: ", message.toString());
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(":::::::::: ", response.toString());
                    mProgressDialog.dismiss();
                    try {
                        if (response.has(Constant.DATA)) {
                            if (response.getJSONObject(Constant.DATA).getString(Constant.STATUS).equals("Success")) {
                                Constant.SID = response.getJSONObject(Constant.DATA).getString("sid");
                                Constant.SNAME = response.getJSONObject(Constant.DATA).getString("sname");
                                Constant.PROFILE_IMG_LINK = response.getJSONObject(Constant.DATA).getString(Constant.PROFILE_IMAGE);
                                Constant.PROFILE_EMAIL = response.getJSONObject(Constant.DATA).getJSONObject(Constant.USER).getString(Constant.EMAIL);
                                Constant.PROFILE_USER_NAME = response.getJSONObject(Constant.DATA).getJSONObject(Constant.USER).getString(Constant.USER_NAME);
                                Constant.PROFILE_NAME = response.getJSONObject(Constant.DATA).getJSONObject(Constant.USER).getString(Constant.NAME);
                                Constant.PROFILE_USER_ID = response.getJSONObject(Constant.DATA).getJSONObject(Constant.USER).getString(Constant.ID);

                                UTILS.setPreference(Constant.PREF_EMAIL, EDT_EMAIL.getText().toString().trim());
                                startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                SignUpActivity.this.finish();
                                UTILS.setPreference(Constant.PREF_LOGGEDIN, "true");
                                UTILS.setPreference(Constant.PREF_SID, Constant.SID);
                                UTILS.setPreference(Constant.PREF_SNAME, Constant.SNAME);
                                UTILS.setPreference(Constant.PREF_PROFILE_IMG_LINK, Constant.PROFILE_IMG_LINK);
                                UTILS.setPreference(Constant.PREF_PROFILE_USER_NAME, Constant.PROFILE_USER_NAME);
                                UTILS.setPreference(Constant.PREF_PROFILE_EMAIL, Constant.PROFILE_EMAIL);
                                UTILS.setPreference(Constant.PREF_PROFILE_EMAIL, Constant.PROFILE_EMAIL);
                                String names[] = TextUtils.split(Constant.PROFILE_NAME, " ");
                                UTILS.setPreference(Constant.PREF_PROFILE_FNAME, names[0]);
                                if (names.length > 1)
                                    UTILS.setPreference(Constant.PREF_PROFILE_LNAME, names[1]);
                                UTILS.setPreference(Constant.PASSWORD, EDT_PASSWORD.getText().toString().trim());
//                            } else {
//                                Toast.makeText(SignUpActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mProgressDialog.dismiss();
                    }
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
                            LoginCall();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void setViews() {
        IMG_USER = (ImageView) findViewById(R.id.imgUserImage);

    }

//    private void showDate(int year, int month, int day) {
//
////        EDT_BIRTHDAY.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year));
//        SYEAR = year;
//        SMONTH = month;
//        SDAY = day;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE_GALLERY:
                    Uri selectedImage = data.getData();

                    CropImage.activity(selectedImage)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                    break;
                case REQ_TAKE_PICTURE:

                    CropImage.activity(IMG_URI)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
//                    try {
//
//                        Calendar cal = Calendar.getInstance();
//                        int seconds = cal.get(Calendar.SECOND);
//                        int hour = cal.get(Calendar.HOUR);
//                        int min = cal.get(Calendar.MINUTE);
//                        String currentDateTimeString = new SimpleDateFormat("ddMMMyyyy").format(new Date());
//
//                        String name = "IMG_" + currentDateTimeString + seconds + hour + min + ".jpeg";
//
//                        String _imgPath = IMG_URI.getPath();
//
//                        File mediaStorageDir = new File(Constant.APP_MEDIA_PATH + File.separator + "IMAGES");
//
//                        // Create the storage directory if it does not exist
//                        if (!mediaStorageDir.exists()) {
//                            if (!mediaStorageDir.mkdirs()) {
//
//                            }
//                        }
//
//                        Constant.copyFile(_imgPath, Constant.APP_MEDIA_PATH + File.separator + "IMAGES", name);
//
//                        File file = new File(_imgPath);
//                        if (file.exists()) {
////                        file.delete();
//                        }
//                        FILE_PATH = Constant.APP_MEDIA_PATH + File.separator + "IMAGES" + File.separator + name;
//
//                        Picasso.with(SignUpActivity.this).load(IMG_URI).into(IMG_USER);
//                        TV_CAPTUREIMG.setVisibility(View.INVISIBLE);
//                    } catch (NullPointerException e) {
//                        e.printStackTrace();
//                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    FILE_PATH = resultUri.getPath();
                    Picasso.with(SignUpActivity.this).load(resultUri).into(IMG_USER);
                    TV_CAPTUREIMG.setVisibility(View.INVISIBLE);
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        SignUpActivity.this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void getPermission() {
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
            } else {
                OpenDialog();
            }

        } else {
            OpenDialog();
        }
    }

    public void OpenDialog() {
        dialogSelectPic _dialogDate = new dialogSelectPic(SignUpActivity.this) {
            @Override
            public void fromGallery() {
                dismiss();
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, TAKE_PICTURE_GALLERY);
            }

            @Override
            public void fromCamera() {
                dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                IMG_URI = Uri.fromFile(Constant.getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, IMG_URI);
                // start the image capture Intent
                startActivityForResult(intent, REQ_TAKE_PICTURE);
            }
        };
        _dialogDate.setCanceledOnTouchOutside(false);
        _dialogDate.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SignUpActivity.this)
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
                    OpenDialog();
                } else {
                    HAS_PERMISSION = false;
                    // Permission Denied
                    Toast.makeText(SignUpActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public class YearCalender extends Dialog {

        private NumberPicker YEAR_PICKER;
        private TextView TV_DONE;

        public YearCalender(Context context) {
            super(context, R.style.DialogTheme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.lay_year_picker);

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            TV_DONE = (TextView) findViewById(R.id.tvDone);

            YEAR_PICKER = (NumberPicker) findViewById(R.id.pickYear);
            //setNumberPickerTextColor(YEAR_PICKER, android.R.color.black);

            YEAR_PICKER.setMinValue(1960);
            YEAR_PICKER.setMaxValue(SYEAR - 10);

            TV_DONE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YEAR_PICKER.clearFocus();
                    YEAR = String.valueOf(YEAR_PICKER.getValue());
                    YearCalender.this.dismiss();
                }
            });
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
