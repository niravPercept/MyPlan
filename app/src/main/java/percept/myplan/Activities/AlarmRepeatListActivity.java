package percept.myplan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.CustomListener.RecyclerTouchListener;
import percept.myplan.Interfaces.ClickListener;
import percept.myplan.POJO.AlarmRepeat;
import percept.myplan.R;
import percept.myplan.adapters.AlarmRepeatAdapter;


//interface ClickListener {
//    void onClick(View view, int position);
//
//    void onLongClick(View view, int position);
//}
//
//class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
//
//    private GestureDetector gestureDetector;
//    private ClickListener clickListener;
//
//    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
//        this.clickListener = clickListener;
//        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return true;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                if (child != null && clickListener != null) {
//                    clickListener.onLongClick(child, recyclerView.getChildPosition(child));
//                }
//            }
//        });
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//
//        View child = rv.findChildViewUnder(e.getX(), e.getY());
//        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
//            clickListener.onClick(child, rv.getChildPosition(child));
//        }
//        return false;
//    }
//
//    @Override
//    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//    }
//
//    @Override
//    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//    }
//}


public class AlarmRepeatListActivity extends AppCompatActivity {

    private RecyclerView lstAlarmRepeat;
    private AlarmRepeatAdapter adapter;
    private ArrayList<AlarmRepeat> listAlarmRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_repeat_list);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.repeat));

        lstAlarmRepeat = (RecyclerView) findViewById(R.id.lstAlarmRepeat);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AlarmRepeatListActivity.this);
        lstAlarmRepeat.setLayoutManager(mLayoutManager);
        lstAlarmRepeat.setItemAnimator(new DefaultItemAnimator());
        listAlarmRepeat = new ArrayList<>();

        String[] repeat = getResources().getStringArray(R.array.alarm_repeat);

        String ids = getIntent().getStringExtra("ALARM_REPEAT");
        for (int i = 0; i < repeat.length; i++) {
            AlarmRepeat alarmRepeat = new AlarmRepeat();
            alarmRepeat.setId(i);
            alarmRepeat.setAlarmDay(repeat[i]);
            if (ids.contains(String.valueOf(i)))
                alarmRepeat.setSelected(true);
            else alarmRepeat.setSelected(false);
            listAlarmRepeat.add(alarmRepeat);
        }

        adapter = new AlarmRepeatAdapter(AlarmRepeatListActivity.this, listAlarmRepeat);
        lstAlarmRepeat.setAdapter(adapter);

        lstAlarmRepeat.addOnItemTouchListener(new RecyclerTouchListener(AlarmRepeatListActivity.this, lstAlarmRepeat, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position == 0) {
                    for (int i = 0; i < listAlarmRepeat.size(); i++) {
                        listAlarmRepeat.get(i).setSelected(false);
                    }
                    listAlarmRepeat.get(position).setSelected(true);
                } else {
                    listAlarmRepeat.get(0).setSelected(false);
                    listAlarmRepeat.get(position).setSelected(!listAlarmRepeat.get(position).isSelected());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ArrayList<Integer> listInt = new ArrayList<>();
            for (int i = 0; i < listAlarmRepeat.size(); i++) {
                if (listAlarmRepeat.get(i).isSelected())
                    listInt.add(listAlarmRepeat.get(i).getId());
            }
            Intent intent = new Intent();
            intent.putExtra("ALARM_REPEAT", TextUtils.join(",", listInt));
            setResult(RESULT_OK, intent);
            AlarmRepeatListActivity.this.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ArrayList<Integer> listInt = new ArrayList<>();
        for (int i = 0; i < listAlarmRepeat.size(); i++) {
            if (listAlarmRepeat.get(i).isSelected())
                listInt.add(listAlarmRepeat.get(i).getId());
        }
        Intent intent = new Intent();
        intent.putExtra("ALARM_REPEAT", TextUtils.join(",", listInt));
        setResult(RESULT_OK, intent);
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
