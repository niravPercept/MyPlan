package percept.myplan.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.R;

public class FeedbackActivity extends AppCompatActivity {

    private EditText edtFeebbackEmail, edtFeebbackSubject, edtFeebbackDetail;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.feedback));

        edtFeebbackEmail = (EditText) findViewById(R.id.edtFeebbackEmail);
        edtFeebbackSubject = (EditText) findViewById(R.id.edtFeebbackSubject);
        edtFeebbackDetail = (EditText) findViewById(R.id.edtFeebbackDetail);
        utils = new Utils(FeedbackActivity.this);
        edtFeebbackEmail.setText(Constant.PROFILE_EMAIL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_send:
                if (!General.checkInternetConnection(FeedbackActivity.this))
                    return false;
                if (isValidate())
                    sendFeedback();
                break;
            case android.R.id.home:
                FeedbackActivity.this.finish();
                break;


        }
        return true;
    }

    private boolean isValidate() {
        String msg = null;
        if (TextUtils.isEmpty(edtFeebbackEmail.getText().toString()) && !
                utils.isEmailValid(edtFeebbackEmail.getText().toString())) {
            msg = getString(R.string.validate_email);
        } else if (TextUtils.isEmpty(edtFeebbackSubject.getText().toString().trim()))
            msg = getString(R.string.validate_subject);
        else if (TextUtils.isEmpty(edtFeebbackDetail.getText().toString().trim()))
            msg = getString(R.string.validate_detail_feedback);
        if (TextUtils.isEmpty(msg))
            return true;
        else {
            Toast.makeText(FeedbackActivity.this, msg, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void sendFeedback() {
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("email", edtFeebbackEmail.getText().toString());
        params.put("subject", edtFeebbackSubject.getText().toString());
        params.put("description", edtFeebbackDetail.getText().toString());
        try {

            new General().getJSONContentFromInternetService(FeedbackActivity.this, General.PHPServices.GIVE_FEEDBACK, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {

                }

                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(FeedbackActivity.this, getString(R.string.thank_feedback), Toast.LENGTH_LONG).show();
                    FeedbackActivity.this.finish();
                }
            },"");
        } catch (Exception e) {
            e.printStackTrace();
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
