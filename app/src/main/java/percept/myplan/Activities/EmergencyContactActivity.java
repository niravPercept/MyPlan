package percept.myplan.Activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Utils;
import percept.myplan.R;

public class EmergencyContactActivity extends AppCompatActivity {

    final private int REQUEST_CODE_CALL_PERMISSIONS = 123;
    private TextView TV_ADDEMERGENCYCONTACT, TV_EMERGENCYCONTACT;
    private Utils UTILS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.title_activity_emergency_contact));

        UTILS = new Utils(EmergencyContactActivity.this);
        TV_ADDEMERGENCYCONTACT = (TextView) findViewById(R.id.tvAddNewContactEmergency);
        TV_EMERGENCYCONTACT = (TextView) findViewById(R.id.tvEmergencyContact);
        TV_EMERGENCYCONTACT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCall();
            }
        });
        TV_ADDEMERGENCYCONTACT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(EmergencyContactActivity.this, AddContactFromPhoneActivity.class);
                _intent.putExtra("FROM", "emergency");
                startActivity(_intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLifeCycle.getInstance().resumed(this);
        if (!UTILS.getPreference("EMERGENCY_CONTACT_NAME").equals("")) {
            TV_EMERGENCYCONTACT.setText(UTILS.getPreference("EMERGENCY_CONTACT_NAME"));
        } else {
            TV_EMERGENCYCONTACT.setText("112");
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            EmergencyContactActivity.this.finish();
            return true;
        } else if (item.getItemId() == R.id.action_addContact) {
            Intent _intent = new Intent(EmergencyContactActivity.this, AddContactActivity.class);
            _intent.putExtra("FROM_EMERGENCY", "emergency");
            startActivity(_intent);
            return true;
        }

        return false;
    }

    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(EmergencyContactActivity.this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EmergencyContactActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_CALL_PERMISSIONS);
        } else {
            String _phoneNo = "112";
            if (!TextUtils.isEmpty(new Utils(EmergencyContactActivity.this).getPreference("EMERGENCY_CONTACT_NAME"))) {
                _phoneNo = new Utils(EmergencyContactActivity.this).getPreference("EMERGENCY_CONTACT_NO");
            }
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
