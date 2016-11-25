package percept.myplan.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.R;


public class SharePositionActivity extends AppCompatActivity {

    private static final int SMS_REQUEST_CODE = 101;
    private Button BTN_SEND;
    private TextView tvShareMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_position);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.sharepos));

        tvShareMsg = (TextView) findViewById(R.id.tvShareMsg);
        BTN_SEND = (Button) findViewById(R.id.btnSend);

        final LatLng latLng = getIntent().getParcelableExtra("CURRENT_LOCATION");
        final String strContactNos = getIntent().getStringExtra("CONTACT_NOs");
        String msg = " ";

//        tvShareMsg.setText(Html.fromHtml("&lt;a href=\"http://www.google.com\"&gt;URL&lt;/a&gt;"));
        tvShareMsg.setText(Html.fromHtml(getString(R.string.share_location_msg) + "<br/>" +
                "<a href=\"http://maps.google.com/?q="
                + latLng.latitude + "," + latLng.longitude + "\">" + getString(R.string.url) + "</a>"));
        tvShareMsg.setMovementMethod(LinkMovementMethod.getInstance());
        BTN_SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("sms:" + strContactNos));
                sendIntent.putExtra("sms_body", getString(R.string.share_location_msg) +
                        " " + "http://maps.google.com/?q=" + latLng.latitude + "," + latLng.longitude);
                sendIntent.putExtra("exit_on_sent", true);
                startActivityForResult(sendIntent, SMS_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SharePositionActivity.this.finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SMS_REQUEST_CODE:
                SharePositionActivity.this.finish();
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
