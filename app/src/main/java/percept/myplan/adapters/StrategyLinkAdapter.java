package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import percept.myplan.R;

/**
 * Created by percept on 16/7/16.
 */

public class StrategyLinkAdapter extends RecyclerView.Adapter<StrategyLinkAdapter.SymptomHolder> {


    public List<String> listLink;

    public StrategyLinkAdapter(List<String> quickMessageList) {
        this.listLink = quickMessageList;
    }

    @Override
    public SymptomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_strategy_list_item, parent, false);

        return new SymptomHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SymptomHolder holder, int position) {
        holder.tvLink.setText(listLink.get(position));
    }

    @Override
    public int getItemCount() {
        return this.listLink.size();
    }

    public class SymptomHolder extends RecyclerView.ViewHolder {
        public TextView tvLink;

        public SymptomHolder(View itemView) {
            super(itemView);
            tvLink = (TextView) itemView.findViewById(R.id.tvSymptomStrategy);
        }
    }
}
