package percept.myplan.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import percept.myplan.Activities.HelpListActivity;
import percept.myplan.Activities.LoginActivity_1;
import percept.myplan.AppController;
import percept.myplan.Dialogs.dialogSelectPic;
import percept.myplan.Dialogs.dialogYesNoOption;
import percept.myplan.Dialogs.fragmentAddNote;
import percept.myplan.Dialogs.fragmentMoodSummary;
import percept.myplan.Dialogs.fragmentSidasRating;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.MultiPartParsing;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.ContactDisplay;
import percept.myplan.R;
import percept.myplan.receivers.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;
import static percept.myplan.Global.Utils.decodeFile;


public class fragmentHome extends Fragment {


    public static final int INDEX = 0;
    public static final int DIALOG_ADDNOTE = 1;
    private static final int REQ_TAKE_PICTURE = 33;
    private static final int TAKE_PICTURE_GALLERY = 34;
    private static Uri IMG_URI;
    final private int REQUEST_CODE_CALL_PERMISSIONS = 123;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private ImageView IMG_MOODRATING_CLOSE;
    private LinearLayout LAY_HELP, LAY_EMERGENCY, LAY_MOODRATING;
    private ImageView IMG_USERPROFILE;
    private TextView tvCaptureImg;
    private AlarmManager ALARM_MANAGER;
    private String FILE_PATH = "";
    private Utils utils;
    private CoordinatorLayout REL_COORDINATE;
    private Utils UTILS;
    private String _phoneNo = "112";
    private Bitmap _tmpBitmap;
    private ProgressDialog mProgressDialog;

    public fragmentHome() {
        // Required empty public constructor
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View _View = inflater.inflate(R.layout.fragment_home, container, false);

        LAY_HELP = (LinearLayout) _View.findViewById(R.id.layHelpHome);
        LAY_MOODRATING = (LinearLayout) _View.findViewById(R.id.layMoodRatings);

        IMG_USERPROFILE = (ImageView) _View.findViewById(R.id.imgUserImage);
        tvCaptureImg = (TextView) _View.findViewById(R.id.tvCaptureImg);

        IMG_MOODRATING_CLOSE = (ImageView) _View.findViewById(R.id.imgCloseMoodRating);
//        Picasso.with(getActivity()).load(Constant.PROFILE_IMG_LINK).into(IMG_USERPROFILE);
        REL_COORDINATE = (CoordinatorLayout) _View.findViewById(R.id.snakeBar);

        if (TextUtils.isEmpty(Constant.PROFILE_IMG_LINK)) {
            tvCaptureImg.setVisibility(View.VISIBLE);
        } else {

            tvCaptureImg.setVisibility(View.GONE);
            ImageLoader imageLoader = AppController.getInstance().getImageLoader();

            imageLoader.get(Constant.PROFILE_IMG_LINK, new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                    if (response.getBitmap() != null) {
                        // load image into imageview
                        IMG_USERPROFILE.setImageBitmap(response.getBitmap());
                        tvCaptureImg.setVisibility(View.GONE);
                    } else if (!TextUtils.isEmpty(response.getRequestUrl())) {
                        tvCaptureImg.setVisibility(View.GONE);
                        Picasso.with(getActivity()).load(Constant.PROFILE_IMG_LINK).into(IMG_USERPROFILE);
                    }
                }
            });

        }
        UTILS = new Utils(getActivity());
        IMG_MOODRATING_CLOSE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LAY_MOODRATING.setVisibility(View.GONE);
            }
        });
        IMG_USERPROFILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermission();
            }
        });
        tvCaptureImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermission();
            }
        });
        LAY_EMERGENCY = (LinearLayout) _View.findViewById(R.id.layEmergencyHome);
        LAY_EMERGENCY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                onCall();
                getEmergencyContact();
            }
        });

        LAY_HELP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(getActivity(), HelpListActivity.class);
                startActivity(_intent);
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home Fragments");
//        setAlarmForMood();

//        MoodRatingAddNoteConfirmDialog();

//        SidasDialog();
        getProfile();
        return _View;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        utils = new Utils(getActivity());

    }

    private void getEmergencyContact() {

        showProgress(getString(R.string.progress_loading));
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("helplist", "2");
        try {
            new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.GET_CONTACTS, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    dismissProgress();
                }

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        dismissProgress();
                        if (response.getJSONArray(Constant.DATA) != null && response.getJSONArray(Constant.DATA).length() > 0) {
                            ContactDisplay _contactDisplay = new Gson().fromJson(response.getJSONArray(Constant.DATA).get(0)
                                    .toString(), new TypeToken<ContactDisplay>() {
                            }.getType());
                            UTILS.setPreference("EMERGENCY_CONTACT_NAME", _contactDisplay.getFirst_name());
                            UTILS.setPreference("EMERGENCY_CONTACT_NO", _contactDisplay.getPhone());
                            onCall();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },"_2");
        } catch (Exception e) {
            e.printStackTrace();
            dismissProgress();
        }
    }

    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_CALL_PERMISSIONS);
        } else {

            if (!TextUtils.isEmpty(new Utils(getActivity()).getPreference("EMERGENCY_CONTACT_NAME"))) {
                _phoneNo = new Utils(getActivity()).getPreference("EMERGENCY_CONTACT_NO");
            }
            new dialogYesNoOption(getActivity(), getString(R.string.confimation_call) + "\n" + _phoneNo) {
                @Override
                public void onClickYes() {
                    dismiss();
                    try {
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + _phoneNo));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            phoneIntent.setPackage("com.android.server.telecom");
                        else
                            phoneIntent.setPackage("com.android.phone");
                        startActivity(phoneIntent);

                    } catch (ActivityNotFoundException e) {
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + _phoneNo));
                        startActivity(phoneIntent);
                    }
                }

                @Override
                public void onClickNo() {
                    dismiss();
                }
            }.show();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CALL_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onCall();
                break;
        }
    }

    private void SidasDialog() {

        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_add_note");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        fragmentSidasRating fraSidasDialog = new fragmentSidasRating();
//        fraSidasDialog.setTargetFragment(this, DIALOG_ADDNOTE);

        fraSidasDialog.show(getFragmentManager().beginTransaction(), "fragment_add_note");
    }

    private void MoodRatingAddNoteConfirmDialog() {
        // Create custom dialog object
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // Include dialog.xml file
        dialog.setContentView(R.layout.lay_moodratings_addnote);
        // Set dialog TV_TITLE
        dialog.setTitle("Custom Dialog");

        // set values for custom dialog components - text, image and button
        TextView _tvNo = (TextView) dialog.findViewById(R.id.tvNo);
        TextView _tvYes = (TextView) dialog.findViewById(R.id.tvYes);

        dialog.show();
        _tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        _tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                MoodRatingAddNoteDialog();
            }
        });
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
            } else OpenDialog();
        } else {
            OpenDialog();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE_GALLERY:
                    Uri selectedImage = data.getData();
                    CropImage.activity(selectedImage)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(getContext(), this);
//
//                String[] filePath = {MediaStore.Images.Media.DATA};
//                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath,
//                        null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                FILE_PATH = c.getString(columnIndex);
//                c.close();
////                IMG_USER.setImageURI(selectedImage);
//                IMG_URI = selectedImage;
////                SaveProfile();
//                rotateImage();
                    break;
                case REQ_TAKE_PICTURE:
                    CropImage.activity(IMG_URI)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(getContext(), this);
//                try {
//
//                    Calendar cal = Calendar.getInstance();
//                    int seconds = cal.get(Calendar.SECOND);
//                    int hour = cal.get(Calendar.HOUR);
//                    int min = cal.get(Calendar.MINUTE);
//                    String currentDateTimeString = new SimpleDateFormat("ddMMMyyyy").format(new Date());
//
//                    String name = "IMG_" + currentDateTimeString + seconds + hour + min + ".jpeg";
//
//                    String _imgPath = IMG_URI.getPath();
//
//                    File mediaStorageDir = new File(Constant.APP_MEDIA_PATH + File.separator + "IMAGES");
//
//                    // Create the storage directory if it does not exist
//                    if (!mediaStorageDir.exists()) {
//                        if (!mediaStorageDir.mkdirs()) {
//
//                        }
//                    }
//
//                    Constant.copyFile(_imgPath, Constant.APP_MEDIA_PATH + File.separator + "IMAGES", name);
//
//                    File file = new File(_imgPath);
//                    if (file.exists()) {
////                        file.delete();
//                    }
//                    FILE_PATH = Constant.APP_MEDIA_PATH + File.separator + "IMAGES" + File.separator + name;
//
////                    Picasso.with(getActivity()).load(IMG_URI).into(IMG_USERPROFILE);
////                    tvCaptureImg.setVisibility(View.GONE);
////                    SaveProfile();
//                    rotateImage();
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
                    break;
                case DIALOG_ADDNOTE:
                    if (resultCode == Activity.RESULT_OK) {
                        String str = data.getStringExtra("NOTE");
                        Log.d(":::::::::::::::: ", str);
                        MoodRatingSummaryDialog(str);
                        // After Ok code.
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        // After Cancel code.
                        MoodRatingSummaryDialog("");
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();

                    FILE_PATH =resultUri.getPath();
                    Picasso.with(getActivity()).load(new File(FILE_PATH)).into(IMG_USERPROFILE);
                    tvCaptureImg.setVisibility(View.INVISIBLE);
                    SaveProfile();
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    public void OpenDialog() {
        dialogSelectPic _dialogDate = new dialogSelectPic(getActivity()) {
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
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }

    private void MoodRatingAddNoteDialog() {

        // close existing dialog fragments
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_add_note");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        fragmentAddNote fraAddNoteDialog = new fragmentAddNote();
        fraAddNoteDialog.setTargetFragment(this, DIALOG_ADDNOTE);

        fraAddNoteDialog.show(getFragmentManager().beginTransaction(), "fragment_add_note");
    }

    private void MoodRatingSummaryDialog(String note) {

        // close existing dialog fragments
        FragmentManager manager = getFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_add_note");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        DialogFragment fraMoodSummartDialog = fragmentMoodSummary.newInstance(note);
        fraMoodSummartDialog.setTargetFragment(this, DIALOG_ADDNOTE);

        fraMoodSummartDialog.show(getFragmentManager().beginTransaction(), "fragment_add_note");
    }

    private void setAlarmForMood() {
        ALARM_MANAGER = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        // AlarmReceiver1 = broadcast receiver
        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);

        PendingIntent _pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        ALARM_MANAGER.cancel(_pendingIntent);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000000, _pendingIntent);
        Toast.makeText(getActivity(), "Alarm Set", Toast.LENGTH_SHORT).show();
//        alarmIntent.setData((Uri.parse("custom://"+System.currentTimeMillis())));
//        ALARM_MANAGER.cancel(_pendingIntent);
//
//        Calendar alarmStartTime = Calendar.getInstance();
//        Calendar now = Calendar.getInstance();
//        alarmStartTime.set(Calendar.HOUR_OF_DAY, 8);
//        alarmStartTime.set(Calendar.MINUTE, 00);
//        alarmStartTime.set(Calendar.SECOND, 0);
//        if (now.after(alarmStartTime)) {
//            Log.d("Hey","Added a day");
//            alarmStartTime.add(Calendar.DATE, 1);
//        }
//        ALARM_MANAGER.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, _pendingIntent);
//        Log.d("Alarm","Alarms set for everyday 8 am.");


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
            snackbar.show();
            return;
        }

        showProgress(getString(R.string.progress_uploading));
        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(FILE_PATH))
            params.put("profile_image", FILE_PATH);
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("first_name", utils.getPreference(Constant.PREF_PROFILE_FNAME));
        params.put("last_name", utils.getPreference(Constant.PREF_PROFILE_LNAME));
        params.put("email", utils.getPreference(Constant.PREF_PROFILE_EMAIL));
        params.put("password", "");
        params.put("phone", "");
        params.put("dob", "");


        new MultiPartParsing(getActivity(), params, General.PHPServices.PROFILE, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                Log.d(":::Profile Edit", response.toString());
                getProfile();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getJSONObject("data").getString("status").equalsIgnoreCase("Success")) {
                        Toast.makeText(getActivity(), "Cover photo changed Successfully", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getActivity(), "Cover photo not changed", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void getProfile() {
        showProgress(getString(R.string.progress_loading));
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        try {
            new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.GET_PROFILE, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    dismissProgress();
                }

                @Override
                public void onResponse(JSONObject response) {
                    if (response.has(Constant.DATA)) {
                        try {
                            Constant.PROFILE_IMG_LINK = response.getJSONObject(Constant.DATA).getString(Constant.PROFILE_IMAGE);
                            utils.setPreference(Constant.PREF_PROFILE_IMG_LINK, Constant.PROFILE_IMG_LINK);
                            if (!TextUtils.isEmpty(Constant.PROFILE_IMG_LINK)) {
                                Picasso.with(getActivity()).load(Constant.PROFILE_IMG_LINK).into(IMG_USERPROFILE);
                                tvCaptureImg.setVisibility(View.INVISIBLE);
                            }
                            dismissProgress();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            },"Profile");
        } catch (Exception e) {
            e.printStackTrace();
            dismissProgress();
        }
    }

//    private void rotateImage() {
//        try {
//            File f = new File(FILE_PATH);
//
//            int angle = 0;
//            ExifInterface exif = new ExifInterface(FILE_PATH);
//            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_UNDEFINED);
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 2;
//
//            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f),
//                    null, options);
//
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    angle = 90;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    angle = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    angle = 270;
//                    break;
//                case ExifInterface.ORIENTATION_NORMAL:
//                default:
//                    break;
//            }
//
//            Matrix matrix = new Matrix();
//            matrix.postRotate(angle);
//            _tmpBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
//                    true);
//
//            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            _tmpBitmap.compress(Bitmap.CompressFormat.PNG, 100,
//                    outputStream);
//
//            new AsyncTask<Void, Void, Void>() {
//
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    FileOutputStream fos = null;
//                    try {
//                        fos = new FileOutputStream(FILE_PATH);
//                        fos.write(outputStream.toByteArray());
//                        fos.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(Void aVoid) {
//                    super.onPostExecute(aVoid);
//                    SaveProfile();
//                }
//            }.execute();
//
//
//        } catch (IOException e) {
//            Log.w("TAG", "-- Error in setting image");
//        } catch (OutOfMemoryError oom) {
//            Log.w("TAG", "-- OOM Error in setting image");
//        }
//    }
public void showProgress(String message){
    mProgressDialog = new ProgressDialog(getActivity());
    mProgressDialog.setMessage(getString(R.string.progress_loading));
    mProgressDialog.setIndeterminate(false);
    mProgressDialog.setCanceledOnTouchOutside(false);
    mProgressDialog.show();
    }

    public void dismissProgress(){
        if(mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }


    }
}
