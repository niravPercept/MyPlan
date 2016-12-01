package percept.myplan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import percept.myplan.POJO.Mood;
import percept.myplan.R;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class MoodSummaryAdapter extends RecyclerView.Adapter<MoodSummaryAdapter.MyViewHolder> {

    private Context CONTEXT;
    private List<Mood> LST_MOOD;
    private TextView TV_MOODSUMTITLE, TV_MOODNOTE;
    private ImageView IMG_MOOD;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public MyViewHolder(View view) {
            super(view);

            TV_MOODSUMTITLE = (TextView) view.findViewById(R.id.tvMoodSymmaryTime);
            TV_MOODNOTE = (TextView) view.findViewById(R.id.tvMoodNote);
            IMG_MOOD = (ImageView) view.findViewById(R.id.imgMood);
        }

        @Override
        public void onClick(View view) {
            int i = (int) view.getTag();
            Log.d(":::: Pressed on ", String.valueOf(i));
        }
    }


    public MoodSummaryAdapter(Context mContext, List<Mood> hopeList) {
        this.CONTEXT = mContext;
        this.LST_MOOD = hopeList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mood_summary_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Mood _mood = LST_MOOD.get(position);
//        holder.TV_ALARMTITLE.setText(album.getAlarmName());
        TV_MOODSUMTITLE.setText(_mood.getMOOD_DATE_STRING());
        TV_MOODNOTE.setText(_mood.getNOTE());
        switch (_mood.getMEASUREMENT()) {
            case "1":
                IMG_MOOD.setImageResource(R.drawable.mood_very_happy_big);
                break;
            case "2":
                IMG_MOOD.setImageResource(R.drawable.mood_happy_big);
                break;
            case "3":
                IMG_MOOD.setImageResource(R.drawable.mood_ok_big);
                break;
            case "4":
                IMG_MOOD.setImageResource(R.drawable.mood_sad_big);
                break;
            case "5":
                IMG_MOOD.setImageResource(R.drawable.mood_very_sad_big);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return LST_MOOD.size();
    }
}
