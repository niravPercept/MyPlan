package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import percept.myplan.POJO.SymptomStrategy;
import percept.myplan.R;

/**
 * Created by percept on 16/7/16.
 */

public class SymptomStrategyAdapter extends RecyclerView.Adapter<SymptomStrategyAdapter.SymptomHolder> {


    public List<SymptomStrategy> LIST_SYMPTOMSTRATEGY;

    public class SymptomHolder extends RecyclerView.ViewHolder {
        public TextView TV_SYMPTOMSTRATEGY;

        public SymptomHolder(View itemView) {
            super(itemView);
            TV_SYMPTOMSTRATEGY = (TextView) itemView.findViewById(R.id.tvSymptomStrategy);
        }
    }

    public SymptomStrategyAdapter(List<SymptomStrategy> quickMessageList) {
        this.LIST_SYMPTOMSTRATEGY = quickMessageList;
    }

    @Override
    public SymptomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_strategy_list_item, parent, false);

        return new SymptomHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SymptomHolder holder, int position) {
        SymptomStrategy _symptom = LIST_SYMPTOMSTRATEGY.get(position);
        holder.TV_SYMPTOMSTRATEGY.setText(_symptom.getTitle());
    }

    @Override
    public int getItemCount() {
        return this.LIST_SYMPTOMSTRATEGY.size();
    }
}
