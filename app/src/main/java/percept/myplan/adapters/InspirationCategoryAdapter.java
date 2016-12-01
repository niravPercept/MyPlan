package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import percept.myplan.POJO.InspirationCategory;
import percept.myplan.R;

/**
 * Created by percept on 16/7/16.
 */

public class InspirationCategoryAdapter extends RecyclerView.Adapter<InspirationCategoryAdapter.SymptomHolder> {


    public List<InspirationCategory> LIST_INSPIRATIONCATEGORY;

    public class SymptomHolder extends RecyclerView.ViewHolder {
        public TextView TV_INSPIRATIONCATEGORY;

        public SymptomHolder(View itemView) {
            super(itemView);
            TV_INSPIRATIONCATEGORY = (TextView) itemView.findViewById(R.id.tvInspirationCategory);
        }
    }

    public InspirationCategoryAdapter(List<InspirationCategory> quickMessageList) {
        this.LIST_INSPIRATIONCATEGORY = quickMessageList;
    }

    @Override
    public SymptomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inspiration_category_list_item, parent, false);

        return new SymptomHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SymptomHolder holder, int position) {
        InspirationCategory _symptom = LIST_INSPIRATIONCATEGORY.get(position);
        holder.TV_INSPIRATIONCATEGORY.setText(_symptom.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return this.LIST_INSPIRATIONCATEGORY.size();
    }
}
