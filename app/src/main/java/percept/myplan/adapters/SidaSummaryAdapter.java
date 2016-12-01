package percept.myplan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import percept.myplan.Graph.PieGraph;
import percept.myplan.Graph.PieSlice;
import percept.myplan.POJO.SidaSummary;
import percept.myplan.R;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class SidaSummaryAdapter extends RecyclerView.Adapter<SidaSummaryAdapter.MyViewHolder> {

    private Context CONTEXT;
    private List<SidaSummary> LST_MOOD;
    private TextView TV_MOODSUMTITLE;
    private PieGraph SIDA_PIE_GRAPH;

    public SidaSummaryAdapter(Context mContext, List<SidaSummary> hopeList) {
        this.CONTEXT = mContext;
        this.LST_MOOD = hopeList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sida_summary_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        SidaSummary _mood = LST_MOOD.get(position);
//        holder.TV_ALARMTITLE.setText(album.getAlarmName());

        if (TextUtils.isEmpty(_mood.getWeek_Number()))
            TV_MOODSUMTITLE.setText(new DateFormatSymbols().getMonths()[Integer.parseInt(_mood.getMonthNumber()) - 1] + " " + _mood.getYear());
//            TV_MOODSUMTITLE.setText(_mood.getMonthNumber() + " " + CONTEXT.getString(R.string.month_of) + " " + _mood.getYear());
        else
            TV_MOODSUMTITLE.setText(_mood.getWeek_Number() + " " + CONTEXT.getString(R.string.week_of) + " " + _mood.getYear());
        ArrayList<PieSlice> LST_PIEDATA = new ArrayList<>();
        PieSlice slice = new PieSlice();
        double avgScore = Float.parseFloat(_mood.getAvg_Score());
        double avgPer = Float.parseFloat(_mood.getAvg_perc());
        if (avgPer <= 30) {
            slice.setColor(CONTEXT.getResources().getColor(R.color.pie_color1));
//            $color = '#04a060';
        } else if (avgPer > 30 && avgPer <= 42) {
            slice.setColor(CONTEXT.getResources().getColor(R.color.pie_color2));
//            $color = '#fed304';
        } else if (avgPer > 42) {
            slice.setColor(CONTEXT.getResources().getColor(R.color.pie_color3));
//            $color = '#ed1c24';
        }

        slice.setValue(Float.parseFloat(_mood.getAvg_Score()));
        slice.setTitle(new DecimalFormat("0").format(Float.parseFloat(_mood.getAvg_Score())));

        LST_PIEDATA.add(slice);
        slice = new PieSlice();
        slice.setColor(CONTEXT.getResources().getColor(android.R.color.transparent));
        slice.setValue(100 - Float.parseFloat(_mood.getAvg_Score()));
//        slice.setTitle(new DecimalFormat("0.00").format(100 - Float.parseFloat(_mood.getAvg_Score())) + "%");
        LST_PIEDATA.add(slice);
        SIDA_PIE_GRAPH.removeSlices();
        SIDA_PIE_GRAPH.addSlice(LST_PIEDATA.get(0));
        SIDA_PIE_GRAPH.addSlice(LST_PIEDATA.get(1));
        SIDA_PIE_GRAPH.setPadding(2);


    }

    @Override
    public int getItemCount() {
        return LST_MOOD.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public MyViewHolder(View view) {
            super(view);

            TV_MOODSUMTITLE = (TextView) view.findViewById(R.id.tvMoodSymmaryTime);
            SIDA_PIE_GRAPH = (PieGraph) view.findViewById(R.id.pieSidagraph);
        }

        @Override
        public void onClick(View view) {
            int i = (int) view.getTag();
            Log.d(":::: Pressed on ", String.valueOf(i));
        }
    }
}
