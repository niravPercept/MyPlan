package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import percept.myplan.Activities.StrategyDetailsOwnActivity;
import percept.myplan.R;

/**
 * Created by percept on 16/7/16.
 */

public class StrategyMusicAdapter extends RecyclerView.Adapter<StrategyMusicAdapter.SymptomHolder> {


    public List<String> listMusic;
    private StrategyDetailsOwnActivity activity;

    public StrategyMusicAdapter(StrategyDetailsOwnActivity strategyDetailsOwnActivity, List<String> listmusic) {
        this.activity=strategyDetailsOwnActivity;
        this.listMusic = listmusic;
    }


    public class SymptomHolder extends RecyclerView.ViewHolder {
        public TextView tvMusicName;
      //  public ImageView tvMusicremove;

        public SymptomHolder(final View itemView) {
            super(itemView);
            tvMusicName = (TextView) itemView.findViewById(R.id.tvSymptomStrategy);
          /*  tvMusicremove=(ImageView)itemView.findViewById(R.id.tvSymptomStrategyremove);
            tvMusicremove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int positon = (int) view.getTag();
//                    listMusic.remove(positon);
//                    notifyDataSetChanged();
                    activity.deleteAudio(positon);
//                    activity.deleteAudio((Integer) view.getTag());
                }
            });*/
        }
    }

//    public StrategyMusicAdapter(List<String> quickMessageList) {
//
//    }

    @Override
    public SymptomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_strategy_list_item, parent, false);

        return new SymptomHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SymptomHolder holder, int position) {
        holder.tvMusicName.setText(listMusic.get(position).substring(listMusic.get(position).lastIndexOf('/') + 1));
//        holder.tvMusicremove.setTag(position);
    }

    @Override
    public int getItemCount() {
        return this.listMusic.size();
    }
}
