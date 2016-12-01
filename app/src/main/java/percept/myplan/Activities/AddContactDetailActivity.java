package percept.myplan.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import org.apache.http.util.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.AppController;
import percept.myplan.Dialogs.dialogOk;
import percept.myplan.Dialogs.dialogSelectPic;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.MultiPartParsing;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.POJO.ContactDisplay;
import percept.myplan.R;
import percept.myplan.customviews.RoundedImageView;
import percept.myplan.fragments.fragmentContacts;

/**
 * Created by percept on 22/8/16.
 */

public class AddContactDetailActivity extends AppCompatActivity {

    private static final int REQ_TAKE_PICTURE = 33;
    private static final int TAKE_PICTURE_GALLERY = 34;
    private static Uri IMG_URI;
    private final int REQUEST_CODE_RINGTONE = 12;
    private final int REQUEST_CODE_PRIORITY = 13;
    private final int REQUEST_CODE_WRITE_CONTACT_PERMISSION = 123;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    RoundedImageView imgContact;
    private TextView tvAssignPriority, tvContactChar, tvAddRingTone;
    private ImageView imgAddPhoneNo, imgAddRingTone, imgAddEmail, imgAddUrl, imgAddAddress, imgAssignPriority;
    private EditText edtFirstName, edtLastName, edtCompany, edtAddAddress, edtAddUrl, edtAddEmail, edtAddPhoneNo;
    private ContactDisplay _contactDisplay;
    private String FILE_PATH = "";
    private CoordinatorLayout REL_COORDINATE;
    private String ADD_TO_HELP_LIST = "0";
    private String ADD_TO_EMERGENCY = "";
    private int contact_priority = 0;
    private Utils UTILS;
    private Ringtone ringtone = null;
    private boolean isForEdit = false;
    private boolean isEDIT = false;
    private TextView mTitle;
    private Uri uri;
    private LinearLayout llMain;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        autoScreenTracking();

        isForEdit = getIntent().getBooleanExtra("IS_FOR_EDIT", false);
        isEDIT = isForEdit;

        if (isForEdit) {
            _contactDisplay = (ContactDisplay) getIntent().getSerializableExtra("DATA");
            ADD_TO_HELP_LIST = _contactDisplay.getHelplist();
        }
        int helpCount = 0;
        if (getIntent().hasExtra(Constant.HELP_COUNT)) {
            helpCount = getIntent().getIntExtra(Constant.HELP_COUNT, 0);
//            contact_priority = 1;

        }

        initializeComponent();
        if (getIntent().hasExtra("ADD_TO_HELP")) {
            ADD_TO_HELP_LIST = "1";
            if (helpCount < 10) {
                tvAssignPriority.setText(getString(R.string.help));
                contact_priority = 1;
            }
        }
        if (getIntent().hasExtra("FROM_EMERGENCY")) {
            ADD_TO_EMERGENCY = "1";
            tvAssignPriority.setText(getString(R.string.emergency));
            contact_priority = 2;
        }

    }

    private void initializeComponent() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        UTILS = new Utils(AddContactDetailActivity.this);
        imgContact = (RoundedImageView) findViewById(R.id.imgContact);

        tvAssignPriority = (TextView) findViewById(R.id.tvAssignPriority);
        tvContactChar = (TextView) findViewById(R.id.tvContactChar);
        tvAddRingTone = (TextView) findViewById(R.id.tvAddRingTone);

        imgAddPhoneNo = (ImageView) findViewById(R.id.imgAddPhoneNo);
        imgAddRingTone = (ImageView) findViewById(R.id.imgAddRingTone);
        imgAddEmail = (ImageView) findViewById(R.id.imgAddEmail);
        imgAddUrl = (ImageView) findViewById(R.id.imgAddUrl);
        imgAddAddress = (ImageView) findViewById(R.id.imgAddAddress);
        imgAssignPriority = (ImageView) findViewById(R.id.imgAssignPriority);

        llMain = (LinearLayout) findViewById(R.id.llMain);
        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtCompany = (EditText) findViewById(R.id.edtCompany);
        edtAddPhoneNo = (EditText) findViewById(R.id.edtAddPhoneNo);
        edtAddEmail = (EditText) findViewById(R.id.edtAddEmail);
        edtAddUrl = (EditText) findViewById(R.id.edtAddUrl);
        edtAddAddress = (EditText) findViewById(R.id.edtAddAddress);

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);



        findViewById(R.id.lay1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermission();
            }
        });

        tvAssignPriority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!view.isEnabled())
                    return;
                Intent intent = new Intent(AddContactDetailActivity.this, AssignPriorityActivity.class);
                intent.putExtra("ADD_TO_HELP", ADD_TO_HELP_LIST);
                if (getIntent().hasExtra("FROM_EMERGENCY")) {
                    intent.putExtra("FROM_EMERGENCY", "true");
                }
                if (getIntent().hasExtra(Constant.HELP_COUNT))
                    intent.putExtra(Constant.HELP_COUNT, getIntent().getIntExtra(Constant.HELP_COUNT, 0));
                startActivityForResult(intent, REQUEST_CODE_PRIORITY);
            }
        });

        tvAddRingTone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!view.isEnabled())
                    return;
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        mProgressDialog = new ProgressDialog(AddContactDetailActivity.this);
                        mProgressDialog.setMessage(getString(R.string.progress_loading));
                        mProgressDialog.setIndeterminate(false);
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.show();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        final Intent ringtone = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                        ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                        ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                        ringtone.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
                                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                        startActivityForResult(ringtone, REQUEST_CODE_RINGTONE);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                       mProgressDialog.dismiss();
                    }
                }.execute();

            }
        });
        edtAddPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!view.isEnabled())
//                Toast.makeText(AddContactDetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        if (isForEdit) {
            mTitle.setText(_contactDisplay.getFirst_name() + " " + _contactDisplay.getLast_name());
            edtFirstName.setText(_contactDisplay.getFirst_name());
            edtLastName.setText(_contactDisplay.getLast_name());
            edtAddPhoneNo.setText(_contactDisplay.getPhone());
            edtAddEmail.setText(_contactDisplay.getEmail());
            if (_contactDisplay.getCon_image().equals("")) {
                imgContact.setImageResource(R.drawable.contact_noimage);
                tvContactChar.setTextColor(getResources().getColor(android.R.color.white));
//                if (!TextUtils.isEmpty(_contactDisplay.getFirst_name())) {
//                    tvContactChar.setText(_contactDisplay.getFirst_name().substring(0, 2));
//                } else if (!TextUtils.isEmpty(_contactDisplay.getLast_name())) {
//                    tvContactChar.setText(_contactDisplay.getLast_name().substring(0, 2));
//                }

                if (!TextUtils.isEmpty(_contactDisplay.getFirst_name()) && !TextUtils.isEmpty(_contactDisplay.getLast_name()) )
                {
                    tvContactChar.setText(_contactDisplay.getFirst_name().substring(0, 1)+""+_contactDisplay.getLast_name().substring(0,1));
                }else if (TextUtils.isEmpty(_contactDisplay.getLast_name()) ){
                    if (_contactDisplay.getFirst_name().length()!=0) {
                        tvContactChar.setText(_contactDisplay.getFirst_name().substring(0, 1));
                    }else
                        tvContactChar.setText("");
                }else {
                    tvContactChar.setText("");
                }



//            holder.IMG_CONTACT.setBackgroundColor(Color.rgb(169, 169, 169));
            } else {
                tvContactChar.setVisibility(View.GONE);
                AppController.getInstance().getImageLoader().get(_contactDisplay.getCon_image(),
                        new ImageLoader.ImageListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }

                            @Override
                            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                                if (response.getBitmap() != null) {
                                    // load image into imageview
                                    imgContact.setImageBitmap(response.getBitmap());
                                }
                            }
                        });
            }
            if (_contactDisplay.getHelplist().equals("1"))
                tvAssignPriority.setText(getString(R.string.help));
            disableAllCompontent();
        } else {

        }
    }

    private void disableAllCompontent() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus())
                ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        edtFirstName.setEnabled(false);
        edtLastName.setEnabled(false);
        edtCompany.setEnabled(false);
        edtAddAddress.setEnabled(false);
        edtAddUrl.setEnabled(false);
        edtAddEmail.setEnabled(false);
        edtAddPhoneNo.setEnabled(false);
        imgContact.setEnabled(false);
        tvAddRingTone.setEnabled(false);
        tvAssignPriority.setEnabled(false);
    }

    private void enableAllCompontent() {
        edtFirstName.setEnabled(true);
        edtLastName.setEnabled(true);
        edtCompany.setEnabled(true);
        edtAddAddress.setEnabled(true);
        edtAddUrl.setEnabled(true);
        edtAddEmail.setEnabled(true);
        edtAddPhoneNo.setEnabled(true);
        imgContact.setEnabled(true);
        tvAddRingTone.setEnabled(true);
        tvAssignPriority.setEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_save_details, menu);
        if (isEDIT) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            isEDIT = false;
        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            if (isForEdit)
                mTitle.setText(getString(R.string.editcontact));
            else
                mTitle.setText(getString(R.string.addcontact));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_save) {

            if (edtFirstName.getText().length()!=0 || edtAddPhoneNo.getText().length()!=0){
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                saveContact();
            }else {
                dialogOk _dialoglert=new dialogOk(AddContactDetailActivity.this, getString(R.string.contactadderror)) {
                    @Override
                    public void onClickOk() {
                        dismiss();
                    }
                };
                _dialoglert.setCancelable(false);
                _dialoglert.setCanceledOnTouchOutside(false);
                _dialoglert.show();
            }

            return true;
        } else if (item.getItemId() == R.id.action_edit) {
            imgAddPhoneNo.setVisibility(View.VISIBLE);
            imgAddRingTone.setVisibility(View.VISIBLE);
            imgAddEmail.setVisibility(View.VISIBLE);
            imgAddUrl.setVisibility(View.VISIBLE);
            imgAddAddress.setVisibility(View.VISIBLE);
            imgAssignPriority.setVisibility(View.VISIBLE);
            setMargins(llMain, 10, 20, 10, 5);
            setMargins(edtAddPhoneNo, 10, 0, 0, 0);
            setMargins(tvAddRingTone, 10, 0, 0, 0);
            setMargins(edtAddEmail, 10, 0, 0, 0);
            setMargins(edtAddUrl, 10, 0, 0, 0);
            setMargins(edtAddAddress, 10, 0, 0, 0);
            setMargins(tvAssignPriority, 10, 0, 0, 0);
            enableAllCompontent();
            invalidateOptionsMenu();

            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RINGTONE:
                if (resultCode == RESULT_OK) {
                    uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    ringtone = RingtoneManager.getRingtone(this, uri);
                    // Get your title here `ringtone.getTitle(this)
                    String Str = ringtone.getTitle(this);

                    tvAddRingTone.setText(Str);
                    Log.d("::::: ", Str);
                }
                break;
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
                Picasso.with(AddContactDetailActivity.this).load(selectedImage).into(imgContact);
                tvContactChar.setVisibility(View.INVISIBLE);
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

                    Picasso.with(AddContactDetailActivity.this).load(IMG_URI).into(imgContact);
                    tvContactChar.setVisibility(View.INVISIBLE);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case REQUEST_CODE_PRIORITY:
                // 0: Default 1:Help  2:Emergency
                if (resultCode == Activity.RESULT_OK) {
                    if (data.hasExtra("FROM_PRIORITY")) {
                        contact_priority = data.getIntExtra("FROM_PRIORITY", 0);
                        switch (contact_priority) {

                            case 1:
                                tvAssignPriority.setText(getString(R.string.help));
                                break;
                            case 2:
                                tvAssignPriority.setText(getString(R.string.emergency));
                                break;
                        }

//                        if (contact_priority ==2)
//                            ADD_TO_EMERGENCY="1";
//                        else
//                           ADD_TO_EMERGENCY="";
//

                        if (contact_priority == 2) {
                            ADD_TO_EMERGENCY = "1";
                            ADD_TO_HELP_LIST = "";
                        }
                        else if (contact_priority ==1) {
                            ADD_TO_HELP_LIST = "1";
                            ADD_TO_EMERGENCY = "";
                        }
                        else{
                            ADD_TO_EMERGENCY = "";
                            ADD_TO_HELP_LIST = "";
                            //ADD_TO_HELP_LIST = "";
                        }

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void saveContact() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getCurrentFocus())
                ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (!UTILS.isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveContact();
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


        mProgressDialog = new ProgressDialog(AddContactDetailActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
//        if (contact_priority ==2)
//            ADD_TO_EMERGENCY=String.valueOf(contact_priority);
//           // ADD_TO_HELP_LIST = String.valueOf(contact_priority);
//        else if (contact_priority == 1)
//            ADD_TO_HELP_LIST=String.valueOf(contact_priority);
//        else if (contact_priority==0){
//            ADD_TO_HELP_LIST=String.valueOf(contact_priority);
//            ADD_TO_EMERGENCY=String.valueOf(contact_priority);
//        }


//            ADD_TO_EMERGENCY=String.valueOf(contact_priority);
//            ADD_TO_HELP_LIST=String.valueOf(contact_priority);

        HashMap<String, String> params = new HashMap<>();
        // Adding file data to http body
        if (FILE_PATH != null)
            params.put(Constant.CON_IMAGE, FILE_PATH);
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        if (_contactDisplay != null)
            params.put(Constant.ID, _contactDisplay.getId());
        else
            params.put(Constant.ID, "");
        params.put(Constant.FIRST_NAME, edtFirstName.getText().toString().trim());
        params.put(Constant.LAST_NAME, edtLastName.getText().toString().trim());
        params.put(Constant.PHONE, edtAddPhoneNo.getText().toString().trim());
        params.put(Constant.SKYPE, "");
        params.put(Constant.EMAIL, edtAddEmail.getText().toString().trim());
        params.put(Constant.HELPLIST, ADD_TO_HELP_LIST);
        params.put(Constant.WEB_ADDRESS, edtAddUrl.getText().toString().trim());
        params.put(Constant.COMPANY_NAME, edtCompany.getText().toString().trim());
        params.put(Constant.ADDRESS, edtAddAddress.getText().toString().trim());
        params.put("emergency", ADD_TO_EMERGENCY);

        if (ringtone != null)
            params.put(Constant.RINGTONE, tvAddRingTone.getText().toString());
        else
            params.put(Constant.RINGTONE, "");
        checkPermissionForContact();
        new MultiPartParsing(AddContactDetailActivity.this, params, General.PHPServices.SAVE_CONTACT, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                if (contact_priority == 2) {
                    UTILS.setPreference("EMERGENCY_CONTACT_NAME", edtFirstName.getText().toString().trim() + " " + edtLastName.getText().toString().trim());
                    UTILS.setPreference("EMERGENCY_CONTACT_NO", edtAddPhoneNo.getText().toString().trim());

                }
                fragmentContacts.GET_CONTACTS = true;
                mProgressDialog.dismiss();
                AddContactDetailActivity.this.finish();
            }
        });


    }

    private void checkPermissionForContact() {
        if (!getIntent().getBooleanExtra("IS_FOR_EDIT", false)) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(AddContactDetailActivity.this,
                        Manifest.permission.WRITE_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddContactDetailActivity.this,
                            Manifest.permission.WRITE_CONTACTS)) {
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(AddContactDetailActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                REQUEST_CODE_WRITE_CONTACT_PERMISSION);
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    saveContactInPhone();
                }
            } else {
                saveContactInPhone();
            }
        }
    }

    private void saveContactInPhone() {
        if (!getIntent().getBooleanExtra("IS_FOR_EDIT", false)) {


            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//                    .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            edtFirstName.getText().toString().trim()).build());

            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                            edtLastName.getText().toString().trim()).build());

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY,
                            edtCompany.getText().toString().trim()).build());

            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                            edtAddPhoneNo.getText().toString().trim()).build());

            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Website.URL)
                    .withValue(ContactsContract.CommonDataKinds.Website.URL,
                            edtAddUrl.getText().toString().trim()).build());

            if (uri != null)
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Contacts.CUSTOM_RINGTONE, uri.getPath()).build());


            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, edtAddEmail.getText().toString().trim())
                    .build());

            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.DATA, edtAddAddress.getText().toString().trim())
                    .build());

            if (!TextUtils.isEmpty(FILE_PATH)) {
                File image = new File(FILE_PATH);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                bitmap = Bitmap.createBitmap(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (bitmap != null) {    // If an image is selected successfully
                    bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);


                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).
                                    withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1).
                                    withValue(ContactsContract.Data.MIMETYPE,
                                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                            .build());
                }
            }
            // Asking the Contact provider to create a new contact
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {
                e.printStackTrace();
                // Toast.makeText(AddContactDetailActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            } else
                OpenDialog();
        } else {
            OpenDialog();
        }
    }

    public void OpenDialog() {
        dialogSelectPic _dialogDate = new dialogSelectPic(AddContactDetailActivity.this) {
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
        new AlertDialog.Builder(AddContactDetailActivity.this)
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
                    Toast.makeText(AddContactDetailActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }

                break;
            case REQUEST_CODE_WRITE_CONTACT_PERMISSION:
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
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
