package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import percept.myplan.POJO.InspirationWiseStrategy;
import percept.myplan.R;

/**
 * Created by percept on 16/7/16.
 */

public class InspirationWiseStrategyAdapter extends RecyclerView.Adapter<InspirationWiseStrategyAdapter.SymptomHolder> {


    public List<InspirationWiseStrategy> LIST_INSPIRATIONWISESTRATEGY;

    public class SymptomHolder extends RecyclerView.ViewHolder {
        public TextView TV_INSPIRATIONWISESTR_NAME, TV_INSPIRATIONWISESTR_DESC;

        public SymptomHolder(View itemView) {
            super(itemView);
            TV_INSPIRATIONWISESTR_NAME = (TextView) itemView.findViewById(R.id.tvInspirationWiseStrategyName);
            TV_INSPIRATIONWISESTR_DESC = (TextView) itemView.findViewById(R.id.tvInspirationWiseStrategyDesc);
        }
    }

    public InspirationWiseStrategyAdapter(List<InspirationWiseStrategy> quickMessageList) {
        this.LIST_INSPIRATIONWISESTRATEGY = quickMessageList;
    }

    @Override
    public SymptomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inspiration_wise_strategy_list_item, parent, false);

        return new SymptomHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SymptomHolder holder, int position) {
        InspirationWiseStrategy _symptom = LIST_INSPIRATIONWISESTRATEGY.get(position);
        holder.TV_INSPIRATIONWISESTR_NAME.setText(_symptom.getStrategyName());
        holder.TV_INSPIRATIONWISESTR_DESC.setText(_symptom.getStrategyDesc());
    }

    @Override
    public int getItemCount() {
        return this.LIST_INSPIRATIONWISESTRATEGY.size();
    }
}
