package percept.myplan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.R;

/**
 * Created by percept on 13/10/16.
 */

public class AddHelpContactActivity extends AppCompatActivity {

    TextView TV_PHONELIST, TV_NEWCONTACT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_add_contact);

        Log.e("Add help screen","Add help screen");
        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.title_activity_add_contact));

        TV_NEWCONTACT = (TextView) findViewById(R.id.tvNewContact);
        TV_PHONELIST = (TextView) findViewById(R.id.tvPhoneList);


        TV_NEWCONTACT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(AddHelpContactActivity.this, AddContactDetailActivity.class);
                if (getIntent().hasExtra("ADD_TO_HELP")) {
                    _intent.putExtra("ADD_TO_HELP", "true");
                }
                if (getIntent().hasExtra("FROM_EMERGENCY")) {
                    _intent.putExtra("FROM_EMERGENCY", "true");
                }
                if (getIntent().hasExtra(Constant.HELP_COUNT))
                    _intent.putExtra(Constant.HELP_COUNT, getIntent().getIntExtra(Constant.HELP_COUNT, 0));
                startActivity(_intent);
                AddHelpContactActivity.this.finish();
            }
        });

        TV_PHONELIST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(AddHelpContactActivity.this, AddContactFromMyPlan.class);
                if (getIntent().hasExtra(Constant.HELP_COUNT))
                    _intent.putExtra(Constant.HELP_COUNT, getIntent().getIntExtra(Constant.HELP_COUNT, 0));
                if (getIntent().hasExtra("ADD_TO_HELP")) {
                    _intent.putExtra("ADD_TO_HELP", "true");
                }
                if (getIntent().hasExtra("FROM_EMERGENCY")) {
                    _intent.putExtra("FROM_EMERGENCY", "true");
                }
                if (getIntent().hasExtra("FROM_QUICKMSG"))
                    _intent.putExtra("FROM_QUICKMSG", getIntent().getExtras().getString("FROM_QUICKMSG"));
                startActivity(_intent);
                AddHelpContactActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            AddHelpContactActivity.this.finish();
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
