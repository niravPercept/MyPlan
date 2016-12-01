package percept.myplan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import percept.myplan.POJO.AlarmRepeat;
import percept.myplan.R;

/**
 * Created by percept on 15/9/16.
 */

public class AlarmRepeatAdapter extends RecyclerView.Adapter<AlarmRepeatAdapter.AlarmRepeatHolder> {

    private Context context;
    private ArrayList<AlarmRepeat> listAlarmRepeat;

    public AlarmRepeatAdapter(Context context, ArrayList<AlarmRepeat> listAlarmRepeat) {
        this.context = context;
        this.listAlarmRepeat = listAlarmRepeat;
    }

    @Override
    public AlarmRepeatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lay_alarm_repeat_item, parent, false);

        return new AlarmRepeatHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlarmRepeatHolder holder, int position) {
        holder.tvAlarmRepeatItem.setText(listAlarmRepeat.get(position).getAlarmDay());
        holder.itemCheckBox.setChecked(listAlarmRepeat.get(position).isSelected());
    }

    @Override
    public int getItemCount() {
        return listAlarmRepeat.size();
    }

    class AlarmRepeatHolder extends RecyclerView.ViewHolder {

        TextView tvAlarmRepeatItem;
        CheckBox itemCheckBox;

        public AlarmRepeatHolder(View itemView) {
            super(itemView);
            tvAlarmRepeatItem = (TextView) itemView.findViewById(R.id.tvAlarmRepeatItem);
            itemCheckBox = (CheckBox) itemView.findViewById(R.id.itemCheckBox);
        }
    }
}
