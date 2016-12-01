package percept.myplan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import percept.myplan.POJO.SidaSchedule;
import percept.myplan.R;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class SidaScheduleAdapter extends RecyclerView.Adapter<SidaScheduleAdapter.MyViewHolder> {

    private Context CONTEXT;
    private List<SidaSchedule> LST_SIDAS_SCHEDULE;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvSidasSchedule;
        public ImageView IMG_TICK;

        public MyViewHolder(View view) {
            super(view);
            tvSidasSchedule = (TextView) itemView.findViewById(R.id.tvSidasSchedule);
            IMG_TICK = (ImageView) itemView.findViewById(R.id.imgTick);
            IMG_TICK.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int _i = (int) IMG_TICK.getTag();
            for (int i = 0; i < LST_SIDAS_SCHEDULE.size(); i++) {
                LST_SIDAS_SCHEDULE.get(i).setSelected(false);
            }
            LST_SIDAS_SCHEDULE.get(_i).setSelected(true);
            notifyDataSetChanged();
        }
    }


    public SidaScheduleAdapter(Context mContext, ArrayList<SidaSchedule> lstSidasSchedule) {
        this.CONTEXT = mContext;
        this.LST_SIDAS_SCHEDULE = lstSidasSchedule;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sidas_schedule_sidas_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.tvSidasSchedule.setText(LST_SIDAS_SCHEDULE.get(position).getName());
        holder.IMG_TICK.setTag(position);
        if (LST_SIDAS_SCHEDULE.get(position).isSelected()) {
            holder.IMG_TICK.setImageResource(R.drawable.tick);

        } else {
            holder.IMG_TICK.setImageResource(R.drawable.untick);
        }
    }

    @Override
    public int getItemCount() {
        return LST_SIDAS_SCHEDULE.size();
    }
}
