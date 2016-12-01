package percept.myplan.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Dialogs.dialogSelectPic;
import percept.myplan.Dialogs.dialogYesNoOption;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.MultiPartParsing;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.R;

public class SettingProfileActivity extends AppCompatActivity {

    private static final int CAMERA_CROP_RESULT = 200;
    private static final int REQ_TAKE_PICTURE = 33;
    private static final int TAKE_PICTURE_GALLERY = 34;
    private final int CHANGE_PASSWORD_REQUEST_CODE = 1;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private EditText EDT_FIRSTNAME, EDT_LASTNAME, EDT_EMAIL;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;
    private LinearLayout LL_PASSWORD;
    private TextView TV_PASSWORD;
    private Utils utils;
    private String strOldPassword;
    private ImageView ivProfileCover;
    private TextView tvEditProfileCover, tvDeleteProfileCover, tvCaptureImg;
    private File file = null;
    private RelativeLayout rlCoverImage;
    private Target target = new Target() {

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    String fileName = "profile_cover_photo" + Constant.PROFILE_IMG_LINK.substring(Constant.PROFILE_IMG_LINK.lastIndexOf(".") + 1);
                    file = new File(Constant.APP_MEDIA_PATH + File.separator + "IMAGES" + File.separator + fileName);
                    try {
                        if (file.exists())
                            file.delete();
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

                        ostream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            ivProfileCover.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (placeHolderDrawable != null) {
            }
        }
    };
    private String FILE_PATH;
    private Uri IMG_URI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.title_activity_profile));

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        LL_PASSWORD = (LinearLayout) findViewById(R.id.llPassword);

        EDT_FIRSTNAME = (EditText) findViewById(R.id.edtProfileFirstName);
        EDT_LASTNAME = (EditText) findViewById(R.id.edtProfileLastName);
        TV_PASSWORD = (TextView) findViewById(R.id.tvPassword);
        EDT_EMAIL = (EditText) findViewById(R.id.edtProfileEmail);
        ivProfileCover = (ImageView) findViewById(R.id.ivProfileCover);
        tvEditProfileCover = (TextView) findViewById(R.id.tvEditProfileCover);
        tvDeleteProfileCover = (TextView) findViewById(R.id.tvDeleteProfileCover);
        tvCaptureImg = (TextView) findViewById(R.id.tvCaptureImg);
        rlCoverImage = (RelativeLayout) findViewById(R.id.rlCoverImage);


        LL_PASSWORD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SettingProfileActivity.this,
                        ChangePasswordActivity.class), CHANGE_PASSWORD_REQUEST_CODE);
            }
        });
        utils = new Utils(SettingProfileActivity.this);
        getProfile();
        EDT_FIRSTNAME.setText(utils.getPreference(Constant.PREF_PROFILE_FNAME));
        EDT_LASTNAME.setText(utils.getPreference(Constant.PREF_PROFILE_LNAME));
        EDT_EMAIL.setText(utils.getPreference(Constant.PREF_PROFILE_EMAIL));
        TV_PASSWORD.setText(utils.getPreference(Constant.PASSWORD));
        strOldPassword = utils.getPreference(Constant.PASSWORD);
        if (!TextUtils.isEmpty(Constant.PROFILE_IMG_LINK)) {
            Picasso.with(SettingProfileActivity.this).load(Constant.PROFILE_IMG_LINK).into(target);
            tvCaptureImg.setVisibility(View.GONE);
        } else {
            tvCaptureImg.setVisibility(View.VISIBLE);
            tvEditProfileCover.setVisibility(View.GONE);
            tvDeleteProfileCover.setVisibility(View.GONE);
        }
        rlCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermission();
            }
        });
        tvEditProfileCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (file != null)
                    CropImage.activity(Uri.fromFile(file))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(SettingProfileActivity.this);
            }
        });

        tvDeleteProfileCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogYesNoOption _dialog = new dialogYesNoOption(SettingProfileActivity.this, getString(R.string.delete_cover_photo)) {

                    @Override
                    public void onClickYes() {
                        if (!General.checkInternetConnection(SettingProfileActivity.this))
                            return;
                        HashMap<String, String> params = new HashMap<>();
                        params.put("sid", Constant.SID);
                        params.put("sname", Constant.SNAME);
                        try {
                            new General().getJSONContentFromInternetService(SettingProfileActivity.this, General.PHPServices.DELETE_PROFILEMAGE, params, true, false, true, new VolleyResponseListener() {
                                @Override
                                public void onError(VolleyError message) {

                                }

                                @Override
                                public void onResponse(JSONObject response) {

                                    Constant.PROFILE_IMG_LINK = "";
                                    tvCaptureImg.setVisibility(View.VISIBLE);
                                    tvEditProfileCover.setVisibility(View.GONE);
                                    tvDeleteProfileCover.setVisibility(View.GONE);
                                    ivProfileCover.setVisibility(View.GONE);
                                    dismiss();
                                }
                            },"");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


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
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_profile, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLifeCycle.getInstance().resumed(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SettingProfileActivity.this.finish();
            return true;
        } else if (item.getItemId() == R.id.action_SaveProfile) {
            SaveProfile();
            return true;
        }
        return false;
    }

    private void getProfile() {
        mProgressDialog = new ProgressDialog(SettingProfileActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        try {
            new General().getJSONContentFromInternetService(SettingProfileActivity.this, General.PHPServices.GET_PROFILE, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    if (response.has(Constant.DATA)) {
                        try {
                            Constant.PROFILE_IMG_LINK = response.getJSONObject(Constant.DATA).getString(Constant.PROFILE_IMAGE);
                            Constant.PROFILE_EMAIL = response.getJSONObject(Constant.DATA).getString(Constant.EMAIL);

                            utils.setPreference(Constant.PREF_EMAIL, EDT_EMAIL.getText().toString().trim());
                            utils.setPreference(Constant.PREF_PROFILE_IMG_LINK, Constant.PROFILE_IMG_LINK);
                            utils.setPreference(Constant.PREF_PROFILE_EMAIL, Constant.PROFILE_EMAIL);
                            utils.setPreference(Constant.PREF_PROFILE_FNAME, response.getJSONObject(Constant.DATA).getString(Constant.FIRST_NAME));
                            utils.setPreference(Constant.PREF_PROFILE_LNAME, response.getJSONObject(Constant.DATA).getString(Constant.FIRST_NAME));

                            EDT_FIRSTNAME.setText(utils.getPreference(Constant.PREF_PROFILE_FNAME));
                            EDT_LASTNAME.setText(utils.getPreference(Constant.PREF_PROFILE_LNAME));
                            EDT_EMAIL.setText(utils.getPreference(Constant.PREF_PROFILE_EMAIL));
                            TV_PASSWORD.setText(utils.getPreference(Constant.PASSWORD));
                            strOldPassword = utils.getPreference(Constant.PASSWORD);
                            if (!TextUtils.isEmpty(Constant.PROFILE_IMG_LINK)) {
                                Picasso.with(SettingProfileActivity.this).load(Constant.PROFILE_IMG_LINK).into(target);
                                tvEditProfileCover.setVisibility(View.VISIBLE);
                                tvDeleteProfileCover.setVisibility(View.VISIBLE);
                                tvCaptureImg.setVisibility(View.GONE);
                            } else {
                                tvCaptureImg.setVisibility(View.VISIBLE);
                                tvEditProfileCover.setVisibility(View.GONE);
                                tvDeleteProfileCover.setVisibility(View.GONE);
                            }
                            mProgressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            },"");
        } catch (Exception e) {
            mProgressDialog.dismiss();
            e.printStackTrace();
        }
    }

    private void SaveProfile() {
        if (!utils.isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SaveProfile();
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
        mProgressDialog = new ProgressDialog(SettingProfileActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        HashMap<String, String> params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("first_name", EDT_FIRSTNAME.getText().toString());
        params.put("last_name", EDT_LASTNAME.getText().toString());
        params.put("email", EDT_EMAIL.getText().toString());
        params.put("password", TV_PASSWORD.getText().toString());
        params.put("oldpass", strOldPassword);
        params.put("phone", "");
        params.put("dob", "");
        if (FILE_PATH != null)
            params.put("profile_image", FILE_PATH);


        new MultiPartParsing(SettingProfileActivity.this, params, General.PHPServices.PROFILE, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                Log.d(":::Profile Edit", response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getJSONObject("data").getString("status").equalsIgnoreCase("Success")) {
                        utils.setPreference(Constant.PREF_PROFILE_FNAME, EDT_FIRSTNAME.getText().toString());
                        utils.setPreference(Constant.PREF_PROFILE_LNAME, EDT_LASTNAME.getText().toString());
                        utils.setPreference(Constant.PASSWORD, TV_PASSWORD.getText().toString().trim());
                        Toast.makeText(SettingProfileActivity.this, "Profile saved Successfully", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(SettingProfileActivity.this, "Profile saved Failed", Toast.LENGTH_SHORT).show();
                    SettingProfileActivity.this.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressDialog.dismiss();


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                    break;
                case CHANGE_PASSWORD_REQUEST_CODE:
                    if (data != null && data.hasExtra("change_password")) {
                        strOldPassword = data.getStringExtra("old_password");
                        TV_PASSWORD.setText(data.getStringExtra("change_password"));
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    FILE_PATH = resultUri.getPath();
                    tvCaptureImg.setVisibility(View.GONE);
                    ivProfileCover.setVisibility(View.VISIBLE);
                    Picasso.with(SettingProfileActivity.this).load(resultUri).into(ivProfileCover);
                    break;
            }
        }

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
        dialogSelectPic _dialogDate = new dialogSelectPic(SettingProfileActivity.this) {
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
        new AlertDialog.Builder(SettingProfileActivity.this)
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
                    // Permission Denied
                    Toast.makeText(SettingProfileActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
