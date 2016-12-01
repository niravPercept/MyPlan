package percept.myplan.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.CustomListener.RecyclerTouchListener;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.ClickListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.ContactDisplay;
import percept.myplan.POJO.QuickMessage;
import percept.myplan.R;
import percept.myplan.adapters.ContactHelpListAdapter;
import percept.myplan.fragments.fragmentContacts;

import static percept.myplan.fragments.fragmentContacts.CONTACT_NAME;
import static percept.myplan.fragments.fragmentContacts.LIST_CONTACTS;
import static percept.myplan.fragments.fragmentContacts.LIST_HELPCONTACTS;

public class CreateQuickMsgActivity extends AppCompatActivity {


    private TextView TV_EDIT_HELPLIST, TV_ADD_CONTACT;
    private RecyclerView LST_HELP, LST_CONTACTS;


    private ContactHelpListAdapter ADPT_CONTACTHELPLIST;
    private List<ContactDisplay> LIST_ALLCONTACTS;
    private ContactHelpListAdapter ADPT_CONTACTLIST;
    private ProgressDialog mProgressDialog;


    private CoordinatorLayout REL_COORDINATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quick_msg);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.title_activity_create_quick_msg));

        LIST_ALLCONTACTS = new ArrayList<>();
        LIST_HELPCONTACTS = new ArrayList<>();
        LIST_CONTACTS = new ArrayList<>();
        CONTACT_NAME = new HashMap<>();

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        TV_EDIT_HELPLIST = (TextView) findViewById(R.id.tvEditHelpList);
        TV_EDIT_HELPLIST.setVisibility(View.INVISIBLE);
        LST_HELP = (RecyclerView) findViewById(R.id.lstHelpList);
        LST_CONTACTS = (RecyclerView) findViewById(R.id.lstContacts);


        TV_ADD_CONTACT = (TextView) findViewById(R.id.tvAddContact);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CreateQuickMsgActivity.this);
        LST_HELP.setLayoutManager(mLayoutManager);
        LST_HELP.setItemAnimator(new DefaultItemAnimator());
        LST_HELP.setAdapter(ADPT_CONTACTHELPLIST);

        RecyclerView.LayoutManager mLayoutManagerContact = new LinearLayoutManager(CreateQuickMsgActivity.this);
        LST_CONTACTS.setLayoutManager(mLayoutManagerContact);
        LST_CONTACTS.setItemAnimator(new DefaultItemAnimator());
        LST_CONTACTS.setAdapter(ADPT_CONTACTHELPLIST);

        TV_ADD_CONTACT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(CreateQuickMsgActivity.this, AddContactActivity.class);
                _intent.putExtra("FROM_QUICKMSG", "FROM_QUICKMSG");
                _intent.putExtra(Constant.HELP_COUNT, LIST_HELPCONTACTS.size());
                startActivity(_intent);
            }
        });

        LST_HELP.addOnItemTouchListener(new RecyclerTouchListener(CreateQuickMsgActivity.this, LST_HELP, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent _intent = new Intent(CreateQuickMsgActivity.this, SendMessageActivity.class);
                ContactDisplay _ContactDisplay = LIST_HELPCONTACTS.get(position);
                QuickMessage _Message = new QuickMessage(_ContactDisplay.getId(), _ContactDisplay.getFirst_name(),
                        _ContactDisplay.getLast_name(), _ContactDisplay.getPhone(), "", "");
                _intent.putExtra("MSG_CONTACT", _Message);
                startActivity(_intent);
                CreateQuickMsgActivity.this.finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        LST_CONTACTS.addOnItemTouchListener(new RecyclerTouchListener(CreateQuickMsgActivity.this, LST_HELP, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent _intent = new Intent(CreateQuickMsgActivity.this, SendMessageActivity.class);
                ContactDisplay _ContactDisplay = LIST_CONTACTS.get(position);
                QuickMessage _Message = new QuickMessage(_ContactDisplay.getId(), _ContactDisplay.getFirst_name(),
                        _ContactDisplay.getLast_name(), _ContactDisplay.getPhone(), "", "");
                _intent.putExtra("MSG_CONTACT", _Message);
                startActivity(_intent);
                CreateQuickMsgActivity.this.finish();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        getContacts();

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLifeCycle.getInstance().resumed(this);
        if (fragmentContacts.GET_CONTACTS) {
            getContacts();
            fragmentContacts.GET_CONTACTS = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            CreateQuickMsgActivity.this.finish();
            return true;
        } else if (item.getItemId() == R.id.action_addContact) {
            Intent _intent = new Intent(CreateQuickMsgActivity.this, AddContactActivity.class);
            if (getIntent().hasExtra(Constant.HELP_COUNT))
                _intent.putExtra(Constant.HELP_COUNT, getIntent().getIntExtra(Constant.HELP_COUNT, 0));
            _intent.putExtra("FROM_QUICKMSG", "FROM_QUICKMSG");
            startActivity(_intent);

            return true;
        }
        return false;
    }

    private void getContacts() {
        mProgressDialog = new ProgressDialog(CreateQuickMsgActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        try {
            LIST_HELPCONTACTS.clear();
            CONTACT_NAME.clear();
            LIST_CONTACTS.clear();
            new General().getJSONContentFromInternetService(CreateQuickMsgActivity.this, General.PHPServices.GET_CONTACTS, params, true, false, false, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    mProgressDialog.dismiss();
                    Log.d(":::::::::::::: ", response.toString());

                    Gson gson = new Gson();
                    try {
                        LIST_ALLCONTACTS = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<ContactDisplay>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for (ContactDisplay _obj : LIST_ALLCONTACTS) {
                        if (_obj.getHelplist().equals("0")) {
                            CONTACT_NAME.put(_obj.getId(), _obj.getFirst_name());
                            LIST_CONTACTS.add(_obj);
                        } else {
                            LIST_HELPCONTACTS.add(_obj);
                        }
                    }

                    ADPT_CONTACTHELPLIST = new ContactHelpListAdapter(LIST_HELPCONTACTS, "HELP");
                    LST_HELP.setAdapter(ADPT_CONTACTHELPLIST);

                    ADPT_CONTACTLIST = new ContactHelpListAdapter(LIST_CONTACTS, "CONTACT");
                    LST_CONTACTS.setAdapter(ADPT_CONTACTLIST);
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
                            getContacts();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
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
