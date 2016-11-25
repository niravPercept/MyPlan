package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import percept.myplan.POJO.Strategy;
import percept.myplan.R;

/**
 * Created by percept on 16/7/16.
 */

public class StrategyAdapter extends RecyclerView.Adapter<StrategyAdapter.SymptomHolder> {


    public List<Strategy> LIST_STRATEGY;

    public class SymptomHolder extends RecyclerView.ViewHolder {
        public TextView TV_STRATEGY;

        public SymptomHolder(View itemView) {
            super(itemView);
            TV_STRATEGY = (TextView) itemView.findViewById(R.id.tvStrategy);
        }

//        public void setOnItemClickListener(View.OnClickListener listener) {
//            this.itemView.setOnClickListener(listener);
//            TV_STRATEGY.setOnClickListener(listener);
//        }
    }

    public StrategyAdapter(List<Strategy> quickMessageList) {
        this.LIST_STRATEGY = quickMessageList;
    }


    @Override
    public SymptomHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.strategy_list_item, parent, false);

        final SymptomHolder _holder = new SymptomHolder(itemView);
//        _holder.setOnItemClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (view == _holder.TV_STRATEGY) {
//                    Toast.makeText(parent.getContext(), "CLICKED", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        return _holder;
    }

    @Override
    public void onBindViewHolder(SymptomHolder holder, int position) {
        Strategy _strategy = LIST_STRATEGY.get(position);
        holder.TV_STRATEGY.setText(_strategy.getTitle());
    }

    @Override
    public int getItemCount() {
        return this.LIST_STRATEGY.size();
    }
}
