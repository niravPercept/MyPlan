package percept.myplan.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.R;
import percept.myplan.customviews.PinEntryEditText;

/**
 * Created by percept on 8/9/16.
 */

public class ExportMyPlanPasswordActivity extends AppCompatActivity {
    private PinEntryEditText editText;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_myplan_password);
        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.exportmyplan));

        editText = (PinEntryEditText) findViewById(R.id.txt_pin_entry);


        REL_COORDINET = (CoordinatorLayout) findViewById(R.id.snakeBar);

        editText.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence str) {
                try {
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    exportingPDF(str.toString().trim());
                } catch (Exception e) {
                    e.printStackTrace();
                    mProgressDialog.dismiss();
                }
            }
        });

    }

    private void exportingPDF(final String pdfPassword) {
        mProgressDialog = new ProgressDialog(ExportMyPlanPasswordActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("password", pdfPassword.toString().trim());

        try {
            if (!General.checkInternetConnection(ExportMyPlanPasswordActivity.this))
                return;
            new General().getJSONContentFromInternetService(ExportMyPlanPasswordActivity.this, General.PHPServices.GET_EXPORT_PDF, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.i("::::PDF", response.toString());
                    mProgressDialog.dismiss();
                    Toast.makeText(ExportMyPlanPasswordActivity.this, getString(R.string.pdf_success_msg), Toast.LENGTH_LONG).show();
                    ExportMyPlanPasswordActivity.this.finish();
                }
            },"");
        } catch (Exception e) {
            mProgressDialog.dismiss();
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(REL_COORDINET, getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exportingPDF(pdfPassword);
                }
            });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ExportMyPlanPasswordActivity.this.finish();
            return true;
        }
        return false;
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
