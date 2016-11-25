package percept.myplan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import percept.myplan.R;

/**
 * Created by percept on 5/11/16.
 */

public class SidasDetailActivity extends AppCompatActivity {
    private TextView TV_CALENDAR, TV_TEST;
    private final int REQ_CODE_SIDAS = 23;
    private final int REQ_CODE_TEST = 23;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sidas_detail_activity);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.sidas));

        TV_CALENDAR = (TextView)findViewById(R.id.tvViewCalendar);
        TV_TEST = (TextView)findViewById(R.id.tvTakeFullTest);

        TV_CALENDAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), SidasActivity.class), REQ_CODE_SIDAS);
            }
        });

        TV_TEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), SidaTestActivity.class), REQ_CODE_TEST);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SidasDetailActivity.this.finish();
            return true;
        }
        return false;
    }
}
