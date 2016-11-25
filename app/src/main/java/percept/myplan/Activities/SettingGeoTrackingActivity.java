package percept.myplan.Activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Dialogs.dialogYesNoOption;
import percept.myplan.Global.Constant;
import percept.myplan.Global.Utils;
import percept.myplan.R;


public class SettingGeoTrackingActivity extends AppCompatActivity {

    private SwitchCompat SWITCH_LOCATIONSHARE;
    private Utils UTILS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geotracking);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.title_activity_geotracking));
        UTILS = new Utils(SettingGeoTrackingActivity.this);
        SWITCH_LOCATIONSHARE = (SwitchCompat) findViewById(R.id.switchLocationSharing);
//        if (TextUtils.isEmpty(UTILS.getBoolPref(Constant.PREF_LOCATION)))
//            SWITCH_LOCATIONSHARE.setChecked(true);
//        else
        SWITCH_LOCATIONSHARE.setChecked(UTILS.getBoolPref(Constant.PREF_LOCATION));

        SWITCH_LOCATIONSHARE.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                UTILS.setBoolPrefrences(Constant.PREF_LOCATION, b);
                if (!isGPSEnabled() && b) {
                    dialogYesNoOption dialog = new dialogYesNoOption(SettingGeoTrackingActivity.this, getString(R.string.dialog_gps_msg)) {
                        @Override
                        public void onClickYes() {
                            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(viewIntent);
                            dismiss();

                            SettingGeoTrackingActivity.this.finish();
                        }

                        @Override
                        public void onClickNo() {
                            dismiss();
                        }
                    };
                    dialog.show();
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            UTILS.setBoolPrefrences(Constant.PREF_LOCATION, SWITCH_LOCATIONSHARE.isChecked());
            SettingGeoTrackingActivity.this.finish();
            return true;
        }
        return false;
    }

    public boolean isGPSEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return true;
        else return false;

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
