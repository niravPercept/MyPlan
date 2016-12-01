package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import percept.myplan.POJO.Symptom;
import percept.myplan.R;

/**
 * Created by percept on 16/7/16.
 */

public class SymptomAdapter extends RecyclerView.Adapter<SymptomAdapter.SymptomHolder> {


    public List<Symptom> LIST_SYMPTOM;

    public class SymptomHolder extends RecyclerView.ViewHolder {
        public TextView TV_SYMPTOM;

        public SymptomHolder(View itemView) {
            super(itemView);
            TV_SYMPTOM = (TextView) itemView.findViewById(R.id.tvSymptom);
        }

//        public void setOnItemClickListener(View.OnClickListener listener) {
//            this.itemView.setOnClickListener(listener);
//            TV_SYMPTOM.setOnClickListener(listener);
//        }
    }

    public SymptomAdapter(List<Symptom> quickMessageList) {
        this.LIST_SYMPTOM = quickMessageList;
    }

    @Override
    public SymptomHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.symptom_list_item, parent, false);
        final SymptomHolder _holder = new SymptomHolder(itemView);
//        _holder.setOnItemClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (view == _holder.TV_SYMPTOM) {
//                    Toast.makeText(parent.getContext(), "CLICKED", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        return _holder;
    }

    @Override
    public void onBindViewHolder(SymptomHolder holder, int position) {
        Symptom _symptom = LIST_SYMPTOM.get(position);
        holder.TV_SYMPTOM.setText(_symptom.getTitle());
    }

    @Override
    public int getItemCount() {
        return this.LIST_SYMPTOM.size();
    }
}
