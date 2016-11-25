package percept.myplan.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.R;

public class ForgetPwdActivity extends AppCompatActivity {


    private Button BTN_SEND;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;
    private EditText edtForgotPwdEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.forgetpwdtitle));

        edtForgotPwdEmail = (EditText) findViewById(R.id.edtForgotPwdEmail);

        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        BTN_SEND = (Button) findViewById(R.id.btnSend);

        BTN_SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, LoginActivity.class);
                startActivity(homeIntent);
                ForgetPwdActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void forgotPassword() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", edtForgotPwdEmail.getText().toString().trim());
        mProgressDialog = new ProgressDialog(ForgetPwdActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        try {
            if (!General.checkInternetConnection(ForgetPwdActivity.this))
                return;
            new General().getJSONContentFromInternetService(ForgetPwdActivity.this,
                    General.PHPServices.FORGOT_PASSWORD, params, true, false, true, new VolleyResponseListener() {

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
                                        Toast.makeText(ForgetPwdActivity.this,
                                                getString(R.string.forgot_success), Toast.LENGTH_LONG).show();
                                        Intent homeIntent = new Intent(ForgetPwdActivity.this, LoginActivity.class);
                                        startActivity(homeIntent);
                                        ForgetPwdActivity.this.finish();
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
                            forgotPassword();
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
