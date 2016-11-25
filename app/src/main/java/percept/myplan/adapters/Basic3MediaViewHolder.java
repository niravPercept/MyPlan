package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import percept.myplan.POJO.HopeDetail;
import percept.myplan.R;

/**
 * Created by percept on 5/8/16.
 */

public class Basic3MediaViewHolder extends Basic3ViewHolder {

    public static final int LAYOUT_RES = R.layout.vh_image_view;

    private HopeDetail DETAILS;
    final ImageView imgCardMusic;
    final TextView tvCardMusic, tvCardMusicEdit;

    public Basic3MediaViewHolder(View itemView) {
        super(itemView);
        imgCardMusic = (ImageView) itemView.findViewById(R.id.imgCardImage);
        tvCardMusic = (TextView) itemView.findViewById(R.id.tvCardImage);
        tvCardMusicEdit = (TextView) itemView.findViewById(R.id.tvCardImageEdit);
    }

    @Override
    public void bind(RecyclerView.Adapter adapter, Object item, int posi) {

        this.DETAILS = (HopeDetail) item;
        tvCardMusic.setText(this.DETAILS.getMEDIA_TITLE());
        imgCardMusic.setTag(posi);
        tvCardMusicEdit.setTag(posi);
        imgCardMusic.setImageResource(R.drawable.media_hope);
    }

    @Override
    public void setOnItemClickListener(View.OnClickListener listener) {
        super.setOnItemClickListener(listener);
        tvCardMusicEdit.setOnClickListener(listener);
        imgCardMusic.setOnClickListener(listener);
    }
    @Override
    public void setOnItemLongClickListener(View.OnLongClickListener listener) {
        super.setOnItemLongClickListener(listener);
        imgCardMusic.setOnLongClickListener(listener);
    }
}