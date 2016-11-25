package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import percept.myplan.Activities.StrategySubmittedDetailActivity;
import percept.myplan.Dialogs.dialogYesNoOption;
import percept.myplan.POJO.StrategyDetails;
import percept.myplan.R;

/**
 * Created by percept on 19/8/16.
 */
public class StrategySubmittedOthersAdapter extends RecyclerView.Adapter<StrategySubmittedOthersAdapter.StrategyAdapter> {

    private StrategySubmittedDetailActivity activity;
    private ArrayList<StrategyDetails> listStrategyDetail;

    public StrategySubmittedOthersAdapter(StrategySubmittedDetailActivity activity, ArrayList<StrategyDetails> listStrategyDetail) {
        this.activity = activity;
        this.listStrategyDetail = listStrategyDetail;
    }

    public class StrategyAdapter extends RecyclerView.ViewHolder {
        public ImageView ivAddStrategy;
        public TextView tvStrategyDetail, tvStrategyTitle;

        public StrategyAdapter(View itemView) {
            super(itemView);
            ivAddStrategy = (ImageView) itemView.findViewById(R.id.ivAddStrategy);
            tvStrategyDetail = (TextView) itemView.findViewById(R.id.tvStrategyDetail);
            tvStrategyTitle = (TextView) itemView.findViewById(R.id.tvStrategyTitle);
        }

        public void setOnItemClickListener(View.OnClickListener listener) {
            this.itemView.setOnClickListener(listener);
            ivAddStrategy.setOnClickListener(listener);
        }
    }


    @Override
    public StrategyAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View _itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_strategy_submitted_others, parent, false);
        final StrategyAdapter _adapter = new StrategyAdapter(_itemView);
        _adapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ivAddStrategy:
                        openConfimationDialog((Integer) view.getTag());
                        break;
                }
            }
        });
        return _adapter;
    }

    @Override
    public void onBindViewHolder(StrategyAdapter holder, int position) {
        StrategyDetails details = listStrategyDetail.get(position);

        holder.tvStrategyTitle.setText(details.getTitle());
        holder.tvStrategyDetail.setText(details.getDescription());
        holder.ivAddStrategy.setTag(position);

    }

    @Override
    public int getItemCount() {
        return listStrategyDetail.size();
    }

    private void openConfimationDialog(final int position) {
        dialogYesNoOption _dialog = new dialogYesNoOption(activity) {
            @Override
            public void onClickYes() {
                dismiss();
                activity.addMyStrategy(position);
            }

            @Override
            public void onClickNo() {
                dismiss();
            }
        };
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.show();

    }
}
