package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import percept.myplan.Activities.AlarmListActivity;
import percept.myplan.Activities.StrategyDetailsOwnActivity;
import percept.myplan.POJO.Alarm;
import percept.myplan.R;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {

    private AlarmListActivity activity;
    private List<Alarm> LST_HOPE;
    private boolean isFromDetail = false;

    public AlarmAdapter(AlarmListActivity activity, List<Alarm> hopeList) {
        this.activity = activity;
        if (hopeList == StrategyDetailsOwnActivity.LIST_ALARM)
            isFromDetail = true;

        this.LST_HOPE = hopeList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Alarm alarm = LST_HOPE.get(position);
        holder.TV_ALARMTITLE.setText(alarm.getAlarmName());
       /* Date date = new Date(Long.valueOf(alarm.getAlarmTime()));
        int _hour = date.getHours();
        int _min = date.getMinutes();*/
        holder.TV_ALARMTIME.setText(alarm.getAlarmTime());
        holder.TV_ALARMEDIT.setTag(position);
        holder.SWITCH_STATUS.setTag(position);
        holder.SWITCH_STATUS.setChecked(alarm.isStatus());
        if (isFromDetail) {
            holder.TV_ALARMEDIT.setEnabled(false);
            holder.SWITCH_STATUS.setEnabled(false);
        } else {
            holder.TV_ALARMEDIT.setEnabled(true);
            holder.SWITCH_STATUS.setEnabled(true);
        }

    }

    @Override
    public int getItemCount() {
        return LST_HOPE.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView TV_ALARMTITLE, TV_ALARMEDIT, TV_ALARMTIME;
        public SwitchCompat SWITCH_STATUS;


        public MyViewHolder(View view) {
            super(view);

            TV_ALARMTITLE = (TextView) view.findViewById(R.id.tvAlarmTitle);
            TV_ALARMEDIT = (TextView) view.findViewById(R.id.tvAlarmEdit);
            TV_ALARMTIME = (TextView) view.findViewById(R.id.tvAlarmTime);
            SWITCH_STATUS = (SwitchCompat) view.findViewById(R.id.switchAlarmStatus);
            TV_ALARMEDIT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = (int) view.getTag();
                    Log.d(":::: Pressed on ", String.valueOf(position));
                    activity.editAlarm(position);
                }
            });
            SWITCH_STATUS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    int position = (int) compoundButton.getTag();
                    LST_HOPE.get(position).setStatus(b);
                }
            });
        }
    }
}
