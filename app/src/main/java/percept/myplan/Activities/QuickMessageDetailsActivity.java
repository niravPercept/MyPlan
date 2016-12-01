package percept.myplan.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.POJO.QuickMessage;
import percept.myplan.R;

public class QuickMessageDetailsActivity extends AppCompatActivity {

    private final int SMS_REQUEST_CODE = 101;
    private QuickMessage message;
    private Button btnSendAgain;
    private TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_message_details);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.app_name));


        btnSendAgain = (Button) findViewById(R.id.btnSendAgain);
        tvMsg = (TextView) findViewById(R.id.tvMsg);

        if (getIntent().hasExtra("QUICK_MSG")) {
            message = (QuickMessage) getIntent().getSerializableExtra("QUICK_MSG");
            tvMsg.setText(message.getNotificationMessage() + " " + message.getMessage());
            mTitle.setText(message.getFirstName());
        }
        btnSendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("sms:" + message.getContactN0()));
                sendIntent.putExtra("sms_body", tvMsg.getText().toString());
                sendIntent.putExtra("exit_on_sent", true);
                startActivityForResult(sendIntent, SMS_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quickmessagedetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_Quickmsg_details) {
            Intent _intent = new Intent(QuickMessageDetailsActivity.this, SendMessageActivity.class);
            _intent.putExtra("MSG_CONTACT", message);
            startActivity(_intent);
            QuickMessageDetailsActivity.this.finish();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            QuickMessageDetailsActivity.this.finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SMS_REQUEST_CODE:
                QuickMessageDetailsActivity.this.finish();
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
