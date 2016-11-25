package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import percept.myplan.POJO.Strategy;
import percept.myplan.R;

/**
 * Created by percept on 16/7/16.
 */

public class StrategySelectionAdapter extends RecyclerView.Adapter<StrategySelectionAdapter.SymptomHolder> {


    public List<Strategy> LIST_STRATEGY;
    private boolean onBind;

    public class SymptomHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView TV_STRATEGY;
        public ImageView IMG_TICK;

        public SymptomHolder(View itemView) {
            super(itemView);
            TV_STRATEGY = (TextView) itemView.findViewById(R.id.tvStrategy);
            IMG_TICK = (ImageView) itemView.findViewById(R.id.imgTick);
            IMG_TICK.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (!onBind) {
                int _i = (int) IMG_TICK.getTag();
                LIST_STRATEGY.get(_i).setSelected(!LIST_STRATEGY.get(_i).isSelected());
//                notifyDataSetChanged();
                notifyDataSetChanged();
            }
        }
    }

    public StrategySelectionAdapter(List<Strategy> quickMessageList) {
        this.LIST_STRATEGY = quickMessageList;
    }

    @Override
    public SymptomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.strategy_list_item_select, parent, false);

        return new SymptomHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SymptomHolder holder, int position) {
        Strategy _strategy = LIST_STRATEGY.get(position);
        holder.TV_STRATEGY.setText(_strategy.getTitle());
        if (_strategy.isSelected()) {
            onBind = true;
            holder.IMG_TICK.setImageResource(R.drawable.tick); //setChecked(true);
            onBind = false;
        } else {
            onBind = true;
            holder.IMG_TICK.setImageResource(R.drawable.untick); //setChecked(false);
            onBind = false;
        }
        holder.IMG_TICK.setTag(position);
    }

    @Override
    public int getItemCount() {
        return this.LIST_STRATEGY.size();
    }
}
