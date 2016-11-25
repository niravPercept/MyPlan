package percept.myplan.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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
import percept.myplan.Dialogs.dialogSelectPic;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.MultiPartParsing;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.POJO.NearestEmergencyRoom;
import percept.myplan.R;
import percept.myplan.adapters.PlacesAutocompleteAdapter;
import percept.myplan.fragments.fragmentNearestEmergencyRoom;

/**
 * Created by percept on 3/9/16.
 */

public class AddEmergencyRoomActivity extends AppCompatActivity {

    private static final int REQ_TAKE_PICTURE = 33;
    private static final int TAKE_PICTURE_GALLERY = 34;
    private static Uri IMG_URI;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    MapView mMapView;
    private String FILE_PATH = "";
    private GoogleMap googleMap;
    private AutoCompleteTextView edtAddress;
    private EditText edtEmergencyRoomName, edtPostCode, edtTelephoneNo, edtCountry, edtCity;
    private LatLng addressLatlng;

    private Utils utils;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;

    private ImageView imgRoomPhoto;
    private TextView tvAddPhoto;
    private NearestEmergencyRoom emergencyRoom;
    private Marker marker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emergency_room);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.add_room));

        imgRoomPhoto = (ImageView) findViewById(R.id.imgRoomPhoto);

        tvAddPhoto = (TextView) findViewById(R.id.tvAddPhoto);

        edtAddress = (AutoCompleteTextView) findViewById(R.id.edtAddress);
        edtEmergencyRoomName = (EditText) findViewById(R.id.edtEmergencyRoomName);
        edtCity = (EditText) findViewById(R.id.edtCity);
        edtCountry = (EditText) findViewById(R.id.edtCountry);
        edtTelephoneNo = (EditText) findViewById(R.id.edtTelephoneNo);
        edtPostCode = (EditText) findViewById(R.id.edtPostCode);
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);




        utils = new Utils(AddEmergencyRoomActivity.this);

        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(AddEmergencyRoomActivity.this.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getIntent().hasExtra("FROM_EDIT")) {

            emergencyRoom = (NearestEmergencyRoom) getIntent().getSerializableExtra(Constant.DATA);
            mTitle.setText(emergencyRoom.getRoomName());

        }

        imgRoomPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermission();
            }
        });
        edtAddress.setAdapter(new PlacesAutocompleteAdapter(this));
        edtAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Address", String.valueOf(adapterView.getAdapter().getItem(i)));
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ?
                        null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                addressLatlng = null;
                final String strAddress = ((PlacesAutocompleteAdapter) adapterView.getAdapter()).getItem(i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Geocoder geocoder = new Geocoder(AddEmergencyRoomActivity.this);
                            List<Address> addressList = geocoder.getFromLocationName(strAddress, 1);
                            if (addressList != null && addressList.size() > 0) {

                                addressLatlng = new LatLng(addressList.get(0).getLatitude(), addressList.get(0)
                                        .getLongitude());

                                Address address = addressList.get(0);
                                if (!TextUtils.isEmpty(address.getFeatureName()))
                                    edtEmergencyRoomName.setText(address.getFeatureName());
                                if (!TextUtils.isEmpty(address.getLocality()))
                                    edtCity.setText(address.getLocality());
                                if (!TextUtils.isEmpty(address.getPostalCode()))
                                    edtPostCode.setText(address.getPostalCode());
                                if (!TextUtils.isEmpty(address.getCountryName()))
                                    edtCountry.setText(address.getCountryName());
                                if (!TextUtils.isEmpty(address.getPhone()))
                                    edtTelephoneNo.setText(address.getPhone());
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(addressLatlng).zoom(12).build();
                                if (marker == null) {
                                    marker = googleMap.addMarker(new MarkerOptions()
                                            .position(addressLatlng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                                } else
                                    marker.setPosition(addressLatlng);
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(AddEmergencyRoomActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddEmergencyRoomActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(false);
                if (emergencyRoom != null) {
                    addressLatlng = new LatLng(Double.parseDouble(emergencyRoom.getLatitude()), Double.parseDouble(emergencyRoom.getLongitude()));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(addressLatlng).zoom(12).build();
                    marker = googleMap.addMarker(new MarkerOptions()
                            .position(addressLatlng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    edtAddress.setText(emergencyRoom.getAddress());
                    edtTelephoneNo.setText(emergencyRoom.getPhone());
                    edtEmergencyRoomName.setText(emergencyRoom.getRoomName());
                    edtPostCode.setText(emergencyRoom.getPostcode());
                    edtCity.setText(emergencyRoom.getCity());
                    edtCountry.setText(emergencyRoom.getCountry());
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        AppLifeCycle.getInstance().resumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        AppLifeCycle.getInstance().paused(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppLifeCycle.getInstance().stopped(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            AddEmergencyRoomActivity.this.finish();
            return true;
        } else if (item.getItemId() == R.id.action_Save) {
            if (!utils.isNetConnected()) {
                Snackbar snackbar = Snackbar
                        .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                saveEmergencyRoom();
                            }
                        });
                // Changing message text color
                snackbar.setActionTextColor(Color.RED);
                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);

                snackbar.show();
                return false;
            }
            if (isValidate())
                saveEmergencyRoom();
            return true;
        }
        return false;
    }

    private boolean isValidate() {
        String msg = null;
        if (TextUtils.isEmpty(edtEmergencyRoomName.getText().toString().trim()))
            msg = getString(R.string.validate_enter_emergency_room);
        if (addressLatlng == null)
            msg = getString(R.string.validate_address);
        if (!TextUtils.isEmpty(msg)) {
            Toast.makeText(AddEmergencyRoomActivity.this, msg, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void saveEmergencyRoom() {
        if (!utils.isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveEmergencyRoom();
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
        mProgressDialog = new ProgressDialog(AddEmergencyRoomActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ?
                null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        if (emergencyRoom != null)
            params.put("id", emergencyRoom.getId());
        if (FILE_PATH != null)
            params.put("image", FILE_PATH);
        params.put("name", edtEmergencyRoomName.getText().toString());
        params.put("address", edtAddress.getText().toString());
        params.put("postcode", edtPostCode.getText().toString());
        params.put("city", edtCity.getText().toString());
        params.put("country", edtCountry.getText().toString());
        params.put("phone", edtTelephoneNo.getText().toString());
        params.put("lat", String.valueOf(addressLatlng.latitude));
        params.put("long", String.valueOf(addressLatlng.longitude));
        params.put("current_lat", String.valueOf(HomeActivity.CURRENT_LAT));
        params.put("current_long", String.valueOf(HomeActivity.CURRENT_LONG));

        new MultiPartParsing(AddEmergencyRoomActivity.this, params, General.PHPServices.SAVE_EMERGENCY_ROOM, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                mProgressDialog.dismiss();
                if (emergencyRoom != null) {
                    try {
                        emergencyRoom = new Gson().fromJson(new JSONObject(response).getJSONObject(Constant.DATA).toString(),
                                new TypeToken<NearestEmergencyRoom>() {
                                }.getType());
                        Intent intent = new Intent();
                        intent.putExtra("EMERGENCY_ROOM_DETAIL", emergencyRoom);
                        setResult(RESULT_OK, intent);
                        Toast.makeText(AddEmergencyRoomActivity.this, getString(R.string.update_emergency_room), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(AddEmergencyRoomActivity.this, getString(R.string.added_emergency_room), Toast.LENGTH_LONG).show();
                AddEmergencyRoomActivity.this.finish();
                fragmentNearestEmergencyRoom.RELOAD_MAP = true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case TAKE_PICTURE_GALLERY:
                if (data == null)
                    return;
                Uri selectedImage = data.getData();

                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                FILE_PATH = c.getString(columnIndex);
                c.close();
//                IMG_USER.setImageURI(selectedImage);
                Picasso.with(AddEmergencyRoomActivity.this).load(selectedImage).into(imgRoomPhoto);
                tvAddPhoto.setVisibility(View.INVISIBLE);
                break;
            case REQ_TAKE_PICTURE:
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
//                        file.delete();
                    }
                    FILE_PATH = Constant.APP_MEDIA_PATH + File.separator + "IMAGES" + File.separator + name;

                    Picasso.with(AddEmergencyRoomActivity.this).load(IMG_URI).into(imgRoomPhoto);
                    tvAddPhoto.setVisibility(View.INVISIBLE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);

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
            } else
                OpenDialog();
        } else {
            OpenDialog();
        }
    }

    public void OpenDialog() {
        dialogSelectPic _dialogDate = new dialogSelectPic(AddEmergencyRoomActivity.this) {
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

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AddEmergencyRoomActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
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
                    Toast.makeText(AddEmergencyRoomActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
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

}
