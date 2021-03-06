package percept.myplan.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.MultiPartParsing;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.AsyncTaskCompletedListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.Contact;
import percept.myplan.POJO.ContactDisplay;
import percept.myplan.R;
import percept.myplan.adapters.ContactFromPhoneAdapter;
import percept.myplan.adapters.ContactHelpListAdapter;
import percept.myplan.adapters.ContactNewHelpAdapter;
import percept.myplan.fragments.fragmentContacts;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class AddContactFromPhoneActivity extends AppCompatActivity implements
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener {
    private final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 13;
    //    private RecyclerView LST_CONTACT;
    private List<Contact> LIST_CONTACTS;
    public static HashMap<String, String> CONTACT_NAME = new HashMap<>();
    //    private ContactAdapter ADAPTER;
    private ContactFromPhoneAdapter ADAPTER;
    private String FROM_PAGE = "";
    private boolean SINGLE_CHECK = false;
    private StickyListHeadersListView LST_CONTACT;
    private EditText EDT_SEARCHTEXT;
    private int HELP_COUNT = 0;
    private int SAVED_NO_COUNT = 0;
    private ProgressDialog mProgressDialog;
    private ProgressDialog mProgressDialog1;
    private String ADD_TO_HELP_LIST = "0";
    private Utils UTILS;
    private CoordinatorLayout REL_COORDINATE;
    private List<ContactDisplay> LIST_ALLCONTACTS;

    public static List<ContactDisplay> LIST_CONTACTS1;
    public static HashMap<String, String> HELP_CONTACT_NAME;
    private ContactNewHelpAdapter CONTACT_ADAPTER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        autoScreenTracking();
        if (getIntent().hasExtra("FROM")) {
            FROM_PAGE = getIntent().getStringExtra("FROM");
        }

        if (getIntent().hasExtra("FROM_EMERGENCY")) {
            SINGLE_CHECK = true;

        }

        if (getIntent().hasExtra(Constant.HELP_COUNT)) {
            HELP_COUNT = getIntent().getIntExtra(Constant.HELP_COUNT, 0);
        }
        if (getIntent().hasExtra("FROM_QUICKMSG")) {

        }
        if (getIntent().hasExtra("FROM_SHARELOC")) {

        }

        setContentView(R.layout.add_contact_from_phone);

        UTILS = new Utils(AddContactFromPhoneActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.title_activity_add_contact_from_phone));

       /* PB_SAVECONTACT = (ProgressBar) findViewById(R.id.pbSaveContact);
        PB_SAVECONTACT.setVisibility(View.GONE);
        PB_GETCONTACT = (ProgressBar) findViewById(R.id.pbGetContact);
        PB_GETCONTACT.setVisibility(View.VISIBLE);*/

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        EDT_SEARCHTEXT = (EditText) findViewById(R.id.edtSearchContact);
//        LST_CONTACT = (RecyclerView) findViewById(R.id.lstContact);
        LIST_CONTACTS = new ArrayList<>();


        LST_CONTACT = (StickyListHeadersListView) findViewById(R.id.list);
        LST_CONTACT.setOnStickyHeaderChangedListener(this);
        LST_CONTACT.setOnStickyHeaderOffsetChangedListener(this);
        LST_CONTACT.setDrawingListUnderStickyHeader(true);


        if (getIntent().hasExtra("ADD_TO_HELP")) {
            ADD_TO_HELP_LIST = "1";
            LIST_ALLCONTACTS = new ArrayList<>();
            LIST_CONTACTS1 = new ArrayList<>();
            CONTACT_NAME = new HashMap<>();
            HELP_CONTACT_NAME = new HashMap<>();
            GetContacts();

        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(AddContactFromPhoneActivity.this,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddContactFromPhoneActivity.this,
                            Manifest.permission.READ_CONTACTS)) {
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(AddContactFromPhoneActivity.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {

                    readContacts();


                }
            } else {

                readContacts();

            }


        }

//        LST_CONTACT.setAreHeadersSticky(true);


        EDT_SEARCHTEXT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {

                if (CONTACT_ADAPTER != null)
                     CONTACT_ADAPTER.getFilter().filter(s.toString());
                else
                    ADAPTER.getFilter().filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (CONTACT_ADAPTER != null)
                    CONTACT_ADAPTER.getFilter().filter(editable.toString());
                else
                    ADAPTER.getFilter().filter(editable.toString());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLifeCycle.getInstance().resumed(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    readContacts();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact, menu);
        if (getIntent().hasExtra("FROM_SHARELOC")) {
            menu.getItem(1).setVisible(true);
            menu.getItem(0).setVisible(false);
        } else {
            menu.getItem(1).setVisible(false);
            menu.getItem(0).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            AddContactFromPhoneActivity.this.finish();
            return true;
        } else if (item.getItemId() == R.id.action_InsertContact) {
            if (getIntent().hasExtra("ADD_TO_HELP")) {
                if (SINGLE_CHECK) {
                    for (ContactDisplay _obj : LIST_CONTACTS1) {
                        if (_obj.isSelected()) {
                            saveContactHelp(_obj);
                            break;
                        }
                    }

                    AddContactFromPhoneActivity.this.finish();
                    return true;
                }
//            Toast.makeText(AddContactFromPhoneActivity.this, "Add Contact Pressed", Toast.LENGTH_SHORT).show();

                UpdateContactsHelp();

            } else {
                if (SINGLE_CHECK) {
                    for (Contact _obj : LIST_CONTACTS) {
                        if (_obj.isSelected()) {
                            saveContact(_obj);
                            break;
                        }
                    }

                    AddContactFromPhoneActivity.this.finish();
                    return true;
                }
//            Toast.makeText(AddContactFromPhoneActivity.this, "Add Contact Pressed", Toast.LENGTH_SHORT).show();

                UpdateContacts();

            }

            return true;
        } else if (item.getItemId() == R.id.action_Next) {
            startActivity(new Intent(AddContactFromPhoneActivity.this, SharePositionActivity.class));
        }
        return false;
    }

    private void saveContact(final Contact _contact) {
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
                            saveContact(_contact);
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

        mProgressDialog1 = new ProgressDialog(AddContactFromPhoneActivity.this);
        mProgressDialog1.setMessage(getString(R.string.progress_loading));
        mProgressDialog1.setIndeterminate(false);
        mProgressDialog1.setCanceledOnTouchOutside(false);
        mProgressDialog1.show();
        //Get Email
        ContentResolver cr = getContentResolver();
        Cursor emailCur = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{_contact.getContactID()}, null);
        String email = null;
        while (emailCur.moveToNext()) {
            // This would allow you get several email addresses
            // if the email addresses were stored in an array
            email = emailCur.getString(
                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
            if (!TextUtils.isEmpty(email))
                break;
            System.out.println("Email " + email + " Email Type : " + emailType);
        }
        emailCur.close();

        // Get note.......
        String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] noteWhereParams = new String[]{_contact.getContactID(),
                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
        Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
        String note = null;
        if (noteCur.moveToFirst()) {
            note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
            System.out.println("Note " + note);
        }
        noteCur.close();

// Get Organizations.........

        String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] orgWhereParams = new String[]{_contact.getContactID(),
                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
        Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                null, orgWhere, orgWhereParams, null);
        String orgName = null;
        if (orgCur.moveToFirst()) {
            orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
            String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
        }
        orgCur.close();

        // Get Instant Messenger.........
        String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] imWhereParams = new String[]{_contact.getContactID(),
                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
        Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                null, imWhere, imWhereParams, null);
        String _skypeName = null;
        if (imCur.moveToFirst()) {
            int type = imCur
                    .getInt(imCur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
            String imName = imCur.getString(imCur
                    .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));

            switch (type) {
                case ContactsContract.CommonDataKinds.Im.PROTOCOL_SKYPE:
                    _skypeName = imName;

                    break;

                default:

                    break;
            }
        }
        imCur.close();

        // Get firstname and all names
        String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
        String[] whereNameParams = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, _contact.getContactID()};

        Cursor _lNameCur = cr.query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
        String _lName = null;
        while (_lNameCur.moveToNext()) {
            _lName = _lNameCur.getString(_lNameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
        }
        _lNameCur.close();

        Cursor webURLCur = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{_contact.getContactID()}, null);
        String _webURL = null;
        while (webURLCur.moveToNext()) {
            _webURL = webURLCur.getString(webURLCur.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
        }

        HashMap<String, String> params = new HashMap<>();
        // Adding file data to http body
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put(Constant.ID, "");
        params.put(Constant.FIRST_NAME, _contact.getFirstName());
        params.put(Constant.LAST_NAME, _lName);
        params.put(Constant.PHONE, _contact.getPhoneNo());
        params.put(Constant.SKYPE, _skypeName);
        params.put(Constant.EMAIL, email);
        params.put(Constant.HELPLIST, ADD_TO_HELP_LIST);
        params.put(Constant.NOTE, note);
        params.put(Constant.COMPANY_NAME, orgName);
        params.put(Constant.RINGTONE, "");
        params.put(Constant.WEB_ADDRESS, _webURL);
        params.put("emergency", "1");
        params.put(Constant.RINGTONE, "");
        String _filePath = getPathFromURI(_contact.getContactID());
        if (!TextUtils.isEmpty(_filePath)) {
            params.put(Constant.CON_IMAGE, _filePath);
        }

        new MultiPartParsing(AddContactFromPhoneActivity.this, params, General.PHPServices.SAVE_CONTACT, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                UTILS.setPreference("EMERGENCY_CONTACT_NAME", _contact.getFirstName());
                UTILS.setPreference("EMERGENCY_CONTACT_NO", _contact.getPhoneNo());
                fragmentContacts.GET_CONTACTS = true;
                mProgressDialog1.dismiss();
                AddContactFromPhoneActivity.this.finish();
            }
        });


    }

    private void UpdateContactsHelp() {
        if (!UTILS.isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UpdateContacts();
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
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        if (SINGLE_CHECK) {
            params.put("emergency", "1");
        }
        List<HashMap<String, String>> _tmpConList = addContactListHelp();
        String deleteIds = deleteContactIds();

        if (_tmpConList.size() <= 0 && TextUtils.isEmpty(deleteIds))
            return;
        mProgressDialog1 = new ProgressDialog(AddContactFromPhoneActivity.this);
        mProgressDialog1.setMessage(getString(R.string.progress_loading));
        mProgressDialog1.setIndeterminate(false);
        mProgressDialog1.setCanceledOnTouchOutside(false);
        mProgressDialog1.show();
        for (int i = 0; i < _tmpConList.size(); i++) {
            params.put("add_" + i, new Gson().toJson(_tmpConList.get(i)));
        }
        params.put("count", String.valueOf(_tmpConList.size()));
        params.put("delete", deleteIds);
        int img_count = 0;
        for (int i = 0; i < LIST_CONTACTS1.size(); i++) {

            if (LIST_CONTACTS1.get(i).isSelected() && !LIST_CONTACTS1.get(i).isOriginalSelection()) {
                // Adding file data to http body
                ContactDisplay _contact = LIST_CONTACTS1.get(i);
                if (_contact.getCon_image() != null) {
                    String _filePath = getPathFromURI(_contact.getId());
                    if (!TextUtils.isEmpty(_filePath)) {
                        params.put(Constant.CON_IMAGE + "_" + img_count, _filePath);
                        ++img_count;
                    }
                }
            }
        }


        new MultiPartParsing(this, params, General.PHPServices.SAVE_CONTACTS, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                try {
                    Log.d(":::::: ", response);
                    JSONObject _object = new JSONObject(response);
                    JSONObject _ObjData = _object.getJSONObject(Constant.DATA);
//                    SAVED_NO_COUNT = SAVED_NO_COUNT + 1;
//                    if (NO_COUNT == SAVED_NO_COUNT) {
                    mProgressDialog1.dismiss();
                    Toast.makeText(AddContactFromPhoneActivity.this,
                            getResources().getString(R.string.contactsaved), Toast.LENGTH_SHORT).show();
                    AddContactFromPhoneActivity.this.finish();
                    fragmentContacts.GET_CONTACTS = true;
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//       Gson gson = new Gson();
//        Log.d("JSON:::", "::" + gson.toJson(params));
//        for (int i = 0; i < LIST_CONTACTS.size(); i++) {
//            if (LIST_CONTACTS.get(i).isSelected() && !LIST_CONTACTS.get(i).isOriginalSelection()) {
//                PB_SAVECONTACT.setVisibility(View.VISIBLE);
//                NO_COUNT = NO_COUNT + 1;
////                getContactInfoFromID(LIST_CONTACTS.get(i).getContactID(), LIST_CONTACTS.get(i).getPhoneNo());
//                addContactList(LIST_CONTACTS, ADD_TO_HELP_LIST);
//
//            } else if (!LIST_CONTACTS.get(i).isSelected() && LIST_CONTACTS.get(i).isOriginalSelection()) {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("sid", Constant.SID);
//                params.put("sname", Constant.SNAME);
//                params.put(Constant.ID, LIST_CONTACTS.get(i).getWEB_ID());
//                PB_SAVECONTACT.setVisibility(View.VISIBLE);
//                NO_COUNT = NO_COUNT + 1;
//                *//*try {
//                    new General().getJSONContentFromInternetService(AddContactFromPhoneActivity.this,
//                            General.PHPServices.DELETE_CONTACT, params, false, false, true, new VolleyResponseListener() {
//                                @Override
//                                public void onError(VolleyError message) {
//                                    PB_SAVECONTACT.setVisibility(View.GONE);
//                                }
//
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    Log.d("::::::::::::: ", response.toString());
//                                    SAVED_NO_COUNT = SAVED_NO_COUNT + 1;
//                                    if (NO_COUNT == SAVED_NO_COUNT) {
//                                        PB_SAVECONTACT.setVisibility(View.GONE);
//                                        Toast.makeText(AddContactFromPhoneActivity.this,
//                                                getResources().getString(R.string.contactsaved), Toast.LENGTH_SHORT).show();
//                                        AddContactFromPhoneActivity.this.finish();
//                                        fragmentContacts.GET_CONTACTS = true;
//                                    }
//                                }
//                            });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    private void UpdateContacts() {
        if (!UTILS.isNetConnected()) {
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UpdateContacts();
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
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        if (SINGLE_CHECK) {
            params.put("emergency", "1");
        }
        List<HashMap<String, String>> _tmpConList = addContactList();
        String deleteIds = deleteContactIds();

        if (_tmpConList.size() <= 0 && TextUtils.isEmpty(deleteIds))
            return;
        mProgressDialog1 = new ProgressDialog(AddContactFromPhoneActivity.this);
        mProgressDialog1.setMessage(getString(R.string.progress_loading));
        mProgressDialog1.setIndeterminate(false);
        mProgressDialog1.setCanceledOnTouchOutside(false);
        mProgressDialog1.show();
        for (int i = 0; i < _tmpConList.size(); i++) {
            params.put("add_" + i, new Gson().toJson(_tmpConList.get(i)));
        }
        params.put("count", String.valueOf(_tmpConList.size()));
        params.put("delete", deleteIds);
        int img_count = 0;
        for (int i = 0; i < LIST_CONTACTS.size(); i++) {

            if (LIST_CONTACTS.get(i).isSelected() && !LIST_CONTACTS.get(i).isOriginalSelection()) {
                // Adding file data to http body
                Contact _contact = LIST_CONTACTS.get(i);
                if (_contact.getContactImgURI() != null) {
                    String _filePath = getPathFromURI(_contact.getContactID());
                    if (!TextUtils.isEmpty(_filePath)) {
                        params.put(Constant.CON_IMAGE + "_" + img_count, _filePath);
                        ++img_count;
                    }
                }
            }
        }


        new MultiPartParsing(this, params, General.PHPServices.SAVE_CONTACTS, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                try {
                    Log.d(":::::: ", response);
                    JSONObject _object = new JSONObject(response);
                    JSONObject _ObjData = _object.getJSONObject(Constant.DATA);
//                    SAVED_NO_COUNT = SAVED_NO_COUNT + 1;
//                    if (NO_COUNT == SAVED_NO_COUNT) {
                    mProgressDialog1.dismiss();
                    Toast.makeText(AddContactFromPhoneActivity.this,
                            getResources().getString(R.string.contactsaved), Toast.LENGTH_SHORT).show();
                    AddContactFromPhoneActivity.this.finish();
                    fragmentContacts.GET_CONTACTS = true;
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//       Gson gson = new Gson();
//        Log.d("JSON:::", "::" + gson.toJson(params));
//        for (int i = 0; i < LIST_CONTACTS.size(); i++) {
//            if (LIST_CONTACTS.get(i).isSelected() && !LIST_CONTACTS.get(i).isOriginalSelection()) {
//                PB_SAVECONTACT.setVisibility(View.VISIBLE);
//                NO_COUNT = NO_COUNT + 1;
////                getContactInfoFromID(LIST_CONTACTS.get(i).getContactID(), LIST_CONTACTS.get(i).getPhoneNo());
//                addContactList(LIST_CONTACTS, ADD_TO_HELP_LIST);
//
//            } else if (!LIST_CONTACTS.get(i).isSelected() && LIST_CONTACTS.get(i).isOriginalSelection()) {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("sid", Constant.SID);
//                params.put("sname", Constant.SNAME);
//                params.put(Constant.ID, LIST_CONTACTS.get(i).getWEB_ID());
//                PB_SAVECONTACT.setVisibility(View.VISIBLE);
//                NO_COUNT = NO_COUNT + 1;
//                *//*try {
//                    new General().getJSONContentFromInternetService(AddContactFromPhoneActivity.this,
//                            General.PHPServices.DELETE_CONTACT, params, false, false, true, new VolleyResponseListener() {
//                                @Override
//                                public void onError(VolleyError message) {
//                                    PB_SAVECONTACT.setVisibility(View.GONE);
//                                }
//
//                                @Override
//                                public void onResponse(JSONObject response) {
//                                    Log.d("::::::::::::: ", response.toString());
//                                    SAVED_NO_COUNT = SAVED_NO_COUNT + 1;
//                                    if (NO_COUNT == SAVED_NO_COUNT) {
//                                        PB_SAVECONTACT.setVisibility(View.GONE);
//                                        Toast.makeText(AddContactFromPhoneActivity.this,
//                                                getResources().getString(R.string.contactsaved), Toast.LENGTH_SHORT).show();
//                                        AddContactFromPhoneActivity.this.finish();
//                                        fragmentContacts.GET_CONTACTS = true;
//                                    }
//                                }
//                            });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    private void saveContactHelp(final ContactDisplay _contact) {
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
                            saveContactHelp(_contact);
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

        mProgressDialog1 = new ProgressDialog(AddContactFromPhoneActivity.this);
        mProgressDialog1.setMessage(getString(R.string.progress_loading));
        mProgressDialog1.setIndeterminate(false);
        mProgressDialog1.setCanceledOnTouchOutside(false);
        mProgressDialog1.show();
        HashMap<String, String> params = new HashMap<>();
        // Adding file data to http body
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put(Constant.ID, "");
        params.put(Constant.FIRST_NAME, _contact.getFirst_name());
        params.put(Constant.LAST_NAME, _contact.getLast_name());
        params.put(Constant.PHONE, _contact.getPhone());
        params.put(Constant.SKYPE, _contact.getSkype());
        params.put(Constant.EMAIL, _contact.getEmail());
        params.put(Constant.HELPLIST, ADD_TO_HELP_LIST);
        params.put(Constant.NOTE, _contact.getNote());
        params.put(Constant.COMPANY_NAME, _contact.getOrgName());
        params.put(Constant.RINGTONE, "");
        params.put(Constant.WEB_ADDRESS, _contact.getWeb_address());
        params.put("emergency", "1");
        params.put(Constant.RINGTONE, "");
        String _filePath = getPathFromURI(_contact.getId());
        if (!TextUtils.isEmpty(_filePath)) {
            params.put(Constant.CON_IMAGE, _filePath);
        }

        new MultiPartParsing(AddContactFromPhoneActivity.this, params, General.PHPServices.SAVE_CONTACT, new AsyncTaskCompletedListener() {
            @Override
            public void onTaskCompleted(String response) {
                UTILS.setPreference("EMERGENCY_CONTACT_NAME", _contact.getFirst_name());
                UTILS.setPreference("EMERGENCY_CONTACT_NO", _contact.getPhone());
                fragmentContacts.GET_CONTACTS = true;
                mProgressDialog1.dismiss();
                AddContactFromPhoneActivity.this.finish();
            }
        });


    }

//    private void getContactInfoFromID(String _contactID, String _phoneno) {
//        String _fname = "", _lname = "", _email = "", _note = "";
//        ContentResolver cr = getContentResolver();
//
//        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//                ContactsContract.CommonDataKinds.Phone.NUMBER,
//                ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
//                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,};
//        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
//        //Get Email...
//        Cursor emailCur = cr.query(
//                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//                null,
//                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                new String[]{_contactID}, null);
//        while (emailCur.moveToNext()) {
//            // This would allow you get several email addresses
//            // if the email addresses were stored in an array
//            _email = emailCur.getString(
//                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//            String emailType = emailCur.getString(
//                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
//
//            System.out.println("Email " + _email + " Email Type : " + emailType);
//        }
//        emailCur.close();
//
//        // Get note.......
//        String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//        String[] noteWhereParams = new String[]{_contactID,
//                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
//        Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
//        if (noteCur.moveToFirst()) {
//            _note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
//            System.out.println("Note " + _note);
//        }
//        noteCur.close();
//
//        // Get firstname and all names
//        String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
//        String[] whereNameParams = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, _contactID};
//        Cursor nameCur = cr.query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
//        while (nameCur.moveToNext()) {
//            _fname = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
//            _lname = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
//            String display = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
//            if (_lname == null || _lname.equals("null")) {
//                _lname = "";
//            }
//            System.out.println("Given " + _fname);
//            System.out.println("family " + _lname);
//            System.out.println("Display " + display);
//        }
//        nameCur.close();
//
////        try {
////            Cursor curPhoto = getContentResolver().query(
////                    ContactsContract.Data.CONTENT_URI,
////                    null,
////                    ContactsContract.Data.CONTACT_ID + "=" + _contactID + " AND "
////                            + ContactsContract.Data.MIMETYPE + "='"
////                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
////                    null);
////            if (curPhoto != null) {
////                if (!curPhoto.moveToFirst()) {
////                    //return null; // no photo
////                }
////            } else {
////                //return null; // error in cursor process
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////            //return null;
////        }
////        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long
////                .parseLong(_contactID));
////        Uri _imageURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
////
////        File _file = new File(_imageURI.getPath());
//
//        Bitmap photo = null;
//        File _file = null;
//        try {
//            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
//                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(_contactID)));
//
//            if (inputStream != null) {
//                photo = BitmapFactory.decodeStream(inputStream);
//                _file = new File(getCacheDir(), "_temp.png");
//                _file.createNewFile();
//
////Convert bitmap to byte array
//                Bitmap bitmap = photo;
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
//                byte[] bitmapdata = bos.toByteArray();
//
////write the bytes in file
//                FileOutputStream fos = new FileOutputStream(_file);
//                fos.write(bitmapdata);
//                fos.flush();
//                fos.close();
//            }
//
//            if (inputStream != null)
//                inputStream.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    private List<HashMap<String, String>> addContactList() {

        List<HashMap<String, String>> contactList = new ArrayList<>();
        for (int i = 0; i < LIST_CONTACTS.size(); i++) {
            if (LIST_CONTACTS.get(i).isSelected() && !LIST_CONTACTS.get(i).isOriginalSelection()) {
                // Adding file data to http body
                Contact _contact = LIST_CONTACTS.get(i);
                HashMap<String, String> map = new HashMap<>();
                /*if (_contact.getContactImgURI() != null) {
                    String _filePath = getPathFromURI(_contact.getContactID());
                    if (!TextUtils.isEmpty(_filePath))
                        map.put(Constant.CON_IMAGE + "_" + i, _filePath);
                }*/
                //Get Email
                ContentResolver cr = getContentResolver();
                Cursor emailCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{_contact.getContactID()}, null);
                String email = null;
                while (emailCur.moveToNext()) {
                    // This would allow you get several email addresses
                    // if the email addresses were stored in an array
                    email = emailCur.getString(
                            emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                    if (!TextUtils.isEmpty(email))
                        break;
                    System.out.println("Email " + email + " Email Type : " + emailType);
                }
                emailCur.close();

                // Get note.......
                String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] noteWhereParams = new String[]{_contact.getContactID(),
                        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                String note = null;
                if (noteCur.moveToFirst()) {
                    note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                    System.out.println("Note " + note);
                }
                noteCur.close();

// Get Organizations.........

                String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] orgWhereParams = new String[]{_contact.getContactID(),
                        ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                        null, orgWhere, orgWhereParams, null);
                String orgName = null;
                if (orgCur.moveToFirst()) {
                    orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                    String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                }
                orgCur.close();

                // Get Instant Messenger.........
                String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                String[] imWhereParams = new String[]{_contact.getContactID(),
                        ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                        null, imWhere, imWhereParams, null);
                String _skypeName = null;
                if (imCur.moveToFirst()) {
                    int type = imCur
                            .getInt(imCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
                    String imName = imCur.getString(imCur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));

                    switch (type) {
                        case ContactsContract.CommonDataKinds.Im.PROTOCOL_SKYPE:
                            _skypeName = imName;

                            break;

                        default:

                            break;
                    }
                }
                imCur.close();

                // Get firstname and all names
                String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
                String[] whereNameParams = new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, _contact.getContactID()};

                Cursor _lNameCur = cr.query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
                String _lName = null;
                while (_lNameCur.moveToNext()) {
                    _lName = _lNameCur.getString(_lNameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
                }
                _lNameCur.close();

                Cursor webURLCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{_contact.getContactID()}, null);
                String _webURL = null;
                while (webURLCur.moveToNext()) {
                    _webURL = webURLCur.getString(webURLCur.getColumnIndex(ContactsContract.CommonDataKinds.Website.URL));
                }
                map.put(Constant.ID, "");
                map.put(Constant.FIRST_NAME, _contact.getFirstName());
                map.put(Constant.LAST_NAME,_lName);
                map.put(Constant.PHONE, _contact.getPhoneNo());
                map.put(Constant.SKYPE, _skypeName);
                map.put(Constant.EMAIL, email);
                map.put(Constant.HELPLIST, ADD_TO_HELP_LIST);
                map.put(Constant.NOTE, note);
                map.put(Constant.COMPANY_NAME, orgName);
                map.put(Constant.RINGTONE, "");
                map.put(Constant.WEB_ADDRESS, _webURL);


                contactList.add(map);
                Log.d("Position:::", "Added " + i);
            }
        }
        return contactList;
    }

    private List<HashMap<String, String>> addContactListHelp() {

        List<HashMap<String, String>> contactList = new ArrayList<>();
        for (int i = 0; i < LIST_CONTACTS1.size(); i++) {
            if (LIST_CONTACTS1.get(i).isSelected() && !LIST_CONTACTS1.get(i).isOriginalSelection()) {
                // Adding file data to http body
                ContactDisplay _contact = LIST_CONTACTS1.get(i);
                HashMap<String, String> map = new HashMap<>();
                /*if (_contact.getContactImgURI() != null) {
                    String _filePath = getPathFromURI(_contact.getContactID());
                    if (!TextUtils.isEmpty(_filePath))
                        map.put(Constant.CON_IMAGE + "_" + i, _filePath);
                }*/

                map.put(Constant.ID, "");
                map.put(Constant.FIRST_NAME, _contact.getFirst_name());
                map.put(Constant.LAST_NAME, _contact.getLast_name());
                map.put(Constant.PHONE, _contact.getPhone());
                map.put(Constant.SKYPE, _contact.getSkype());
                map.put(Constant.EMAIL, _contact.getEmail());
                map.put(Constant.HELPLIST, ADD_TO_HELP_LIST);
                map.put(Constant.NOTE, _contact.getNote());
                map.put(Constant.COMPANY_NAME, _contact.getOrgName());
                map.put(Constant.RINGTONE, "");
                map.put(Constant.WEB_ADDRESS, _contact.getWeb_address());


                contactList.add(map);
                Log.d("Position:::", "Added " + i);
            }
        }
        return contactList;
    }

    private String deleteContactIds() {
        List<String> listContacts = new ArrayList<>();
        for (int i = 0; i < LIST_CONTACTS.size(); i++) {
            Contact _contact = LIST_CONTACTS.get(i);
            if (!_contact.isSelected() && _contact.isOriginalSelection()) {
                listContacts.add(_contact.getWEB_ID());
            }

        }
        return TextUtils.join(",", listContacts);
    }

    public String getPathFromURI(String _contactID) {
        Bitmap photo = null;
        File _file = null;
        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(_contactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                _file = new File(getCacheDir(), "_temp.png");
                _file.createNewFile();

//Convert bitmap to byte array
                Bitmap bitmap = photo;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(_file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            }

            if (inputStream != null)
                inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (_file != null)
            return _file.getAbsolutePath();
        else return "";

    }

//    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
//        private String FNAME, LNAME, EMAIL, NOTE, PHONENO, HELPLIST;
//        private File IMG_FILE;
//
//        public UploadFileToServer(String fname, String lname, String email, String note, String phoneno, String helplist,
//                                  File image) {
//            this.FNAME = fname;
//            this.LNAME = lname;
//            this.EMAIL = email;
//            this.NOTE = note;
//            this.PHONENO = phoneno;
//            this.HELPLIST = helplist;
//            this.IMG_FILE = image;
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
//
//
//            String responseString = null;
//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(getResources().getString(R.string.server_url) + ".addContactList");
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
////                if (!FILE_PATH.equals("")) {
////                    File sourceFile = new File(FILE_PATH);
//
//                // Adding file data to http body
//                if (IMG_FILE != null)
//                    entity.addPart(Constant.CON_IMAGE, new FileBody(IMG_FILE));
////                }
//                // Extra parameters if you want to pass to server
//                try {
//
//                    entity.addPart("sid", new StringBody(Constant.SID));
//                    entity.addPart("sname", new StringBody(Constant.SNAME));
//                    entity.addPart(Constant.ID, new StringBody(""));
//                    entity.addPart(Constant.FIRST_NAME, new StringBody(this.FNAME));
//                    entity.addPart(Constant.LAST_NAME, new StringBody(this.LNAME));
//                    entity.addPart(Constant.PHONE, new StringBody(this.PHONENO));
//                    entity.addPart(Constant.SKYPE, new StringBody(""));
//                    entity.addPart(Constant.EMAIL, new StringBody(this.EMAIL));
//                    entity.addPart(Constant.HELPLIST, new StringBody(this.HELPLIST));
//                    entity.addPart(Constant.NOTE, new StringBody(this.NOTE));
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
//            try {
//                Log.d(":::::: ", result);
//                JSONObject _object = new JSONObject(result);
//                JSONObject _ObjData = _object.getJSONObject(Constant.DATA);
//                SAVED_NO_COUNT = SAVED_NO_COUNT + 1;
//                if (NO_COUNT == SAVED_NO_COUNT) {
//                    PB_SAVECONTACT.setVisibility(View.GONE);
//                    Toast.makeText(AddContactFromPhoneActivity.this,
//                            getResources().getString(R.string.contactsaved), Toast.LENGTH_SHORT).show();
//                    AddContactFromPhoneActivity.this.finish();
//                    fragmentContacts.GET_CONTACTS = true;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }

    private void readContacts() {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(AddContactFromPhoneActivity.this);
                mProgressDialog.setMessage(getString(R.string.progress_loading));
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String _firstName = cur.getString(cur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String _phoneNo = cur.getString(cur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String _contactID = cur.getString(cur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

//                        Log.d(":::::::::::: ", _contactID);
                        Boolean _hasContact = false;
                        String _WEB_ID = "";
                        if (TextUtils.isEmpty(_firstName))
                            continue;
                        if (!getIntent().hasExtra("FROM_SHARELOC")) {
                            if (!SINGLE_CHECK) {
                                if (ADD_TO_HELP_LIST.equals("1")) {
                                    ArrayList<String> lst = new ArrayList<String>(fragmentContacts.HELP_CONTACT_NAME.values());
                                    if (lst.contains(_firstName)) {
                                        _hasContact = true;
                                        _WEB_ID = getKey(fragmentContacts.HELP_CONTACT_NAME, _firstName);
                                    }
                                } else {
                                    ArrayList<String> lst = new ArrayList<String>(fragmentContacts.CONTACT_NAME.values());
                                    if (lst.contains(_firstName)) {
                                        _hasContact = true;
                                        _WEB_ID = getKey(fragmentContacts.CONTACT_NAME, _firstName);
                                    }
                                }
                            }
                        }
                        LIST_CONTACTS.add(new Contact(_firstName, "", _phoneNo, "", "", "", "", "",
                                "", "", "", _contactID, _WEB_ID, _hasContact, _hasContact));

                    }
                }
                Collections.sort(LIST_CONTACTS);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mProgressDialog.dismiss();
                boolean _isFromHelp = false;
                if (ADD_TO_HELP_LIST.equals("1"))
                    _isFromHelp = true;
                ADAPTER = new ContactFromPhoneAdapter(AddContactFromPhoneActivity.this,
                        LIST_CONTACTS, SINGLE_CHECK, HELP_COUNT, _isFromHelp);
                LST_CONTACT.setAdapter(ADAPTER);
            }
        }.execute();


    }

    private void GetContacts() {
        mProgressDialog = new ProgressDialog(AddContactFromPhoneActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        try {

            clearData();
            new General().getJSONContentFromInternetService(AddContactFromPhoneActivity.this, General.PHPServices.GET_CONTACTS, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    try {
                        LIST_ALLCONTACTS = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<ContactDisplay>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (ContactDisplay _obj : LIST_ALLCONTACTS) {
                        if (_obj.getEmergency().equals("1")) {

                            UTILS.setPreference("EMERGENCY_CONTACT_NAME", _obj.getFirst_name());
                            UTILS.setPreference("EMERGENCY_CONTACT_NO", _obj.getPhone());
                        } else if (_obj.getHelplist().equals("0")) {
                            CONTACT_NAME.put(_obj.getId(), _obj.getFirst_name());
                            LIST_CONTACTS1.add(_obj);
                        } else {
                            HELP_CONTACT_NAME.put(_obj.getId(), _obj.getFirst_name());

                        }
                    }
                    boolean _isFromHelp = true;
                    Collections.sort(LIST_CONTACTS1);
                    CONTACT_ADAPTER = new ContactNewHelpAdapter(AddContactFromPhoneActivity.this,
                            LIST_CONTACTS1, SINGLE_CHECK, HELP_COUNT, _isFromHelp);

                    LST_CONTACT.setAdapter(CONTACT_ADAPTER);

                    mProgressDialog.dismiss();
                }
            }, "");
        } catch (Exception e) {
            mProgressDialog.dismiss();
            e.printStackTrace();
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GetContacts();
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
    }


    String getKey(HashMap<String, String> map, String value) {
        String key = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if ((value == null && entry.getValue() == null) || (value != null && value.equals(entry.getValue()))) {
                key = entry.getKey();
                break;
            }
        }
        return key;
    }

    private void clearData() {
        LIST_ALLCONTACTS.clear();
        LIST_CONTACTS1.clear();
        CONTACT_NAME.clear();
        HELP_CONTACT_NAME.clear();
    }

    @Override
    public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {

    }

    @Override
    public void onStickyHeaderOffsetChanged(StickyListHeadersListView l, View header, int offset) {

    }

    public void autoScreenTracking() {
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
