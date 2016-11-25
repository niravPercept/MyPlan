package percept.myplan.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.QuickMessage;
import percept.myplan.R;

public class SendMessageActivity extends AppCompatActivity {

    private static final int SMS_REQUEST_CODE = 101;
    private Button BTN_SEND;
    private EditText EDT_MSG, edtNotificationMsg;
    private String NUMBER = "";
    private QuickMessage message;
    private CoordinatorLayout REL_COORDINATE;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.title_activity_send_message));

        BTN_SEND = (Button) findViewById(R.id.btnSendMessage);
        EDT_MSG = (EditText) findViewById(R.id.edtMsg);
        edtNotificationMsg = (EditText) findViewById(R.id.edtNotificationMsg);
        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);


        EDT_MSG.setText("");

        if (getIntent().hasExtra("MSG_CONTACT")) {
            message = (QuickMessage) getIntent().getSerializableExtra("MSG_CONTACT");
            NUMBER = message.getContactN0();
            if (!TextUtils.isEmpty(message.getNotificationMessage()))
                edtNotificationMsg.setText(message.getNotificationMessage());
            if (!TextUtils.isEmpty(message.getMessage()))
                EDT_MSG.setText(message.getMessage());
        }

        BTN_SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("sms:" + NUMBER));
                sendIntent.putExtra("sms_body", edtNotificationMsg.getText().toString()
                        + " " + EDT_MSG.getText().toString());
                sendIntent.putExtra("exit_on_sent", true);
                startActivityForResult(sendIntent, SMS_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SendMessageActivity.this.finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SMS_REQUEST_CODE:
//                LIST_QUICKMSG = new Gson().fromJson(new Utils(SendMessageActivity.this).
//                        getPreference(Constant.PREF_QUICK_MSG), new TypeToken<List<QuickMessage>>() {
//                }.getType());
//                if (LIST_QUICKMSG == null)
//                    LIST_QUICKMSG = new ArrayList<QuickMessage>();
//                boolean isAdd = false;
//                if (LIST_QUICKMSG.size() > 0) {
//                    for (int i = 0; i < LIST_QUICKMSG.size(); i++) {
//                        QuickMessage _message = LIST_QUICKMSG.get(i);
//                        if (_message.getContactID().equals(message.getContactID())) {
//                            LIST_QUICKMSG.set(i, new QuickMessage(message.getContactID(), message.getContactName(), message.getContactN0(),
//                                    edtNotificationMsg.getText().toString().trim(), EDT_MSG.getText().toString().trim()));
//                            isAdd = true;
//                            break;
//                        }
//                    }
//                }
//                if (!isAdd) {
//                LIST_QUICKMSG.add(new QuickMessage(message.getContactID(), message.getFirstName(), message.getLastName(), message.getContactN0(),
//                        edtNotificationMsg.getText().toString().trim(), EDT_MSG.getText().toString().trim()));
//                }
//                Gson gson = new Gson();
//                new Utils(SendMessageActivity.this).setPreference(Constant.PREF_QUICK_MSG, gson.toJson(LIST_QUICKMSG, new TypeToken<List<QuickMessage>>() {
//                }.getType()));
                saveMessages(message.getContactID(),
                        EDT_MSG.getText().toString().trim(), edtNotificationMsg.getText().toString().trim());
                break;
        }

    }

    private void saveMessages(final String contactID, final String msg, final String notificationMsg) {
        mProgressDialog = new ProgressDialog(SendMessageActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("c_id", contactID);
        params.put("msg", msg);
        params.put("n_msg", notificationMsg);
        try {
            if (!General.checkInternetConnection(SendMessageActivity.this))
                return;
            new General().getJSONContentFromInternetService(SendMessageActivity.this, General.PHPServices.SAVE_MESSAGE, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    mProgressDialog.dismiss();
                    SendMessageActivity.this.finish();
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
                            saveMessages(contactID, msg, notificationMsg);
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
