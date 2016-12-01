package percept.myplan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import percept.myplan.AppController;
import percept.myplan.POJO.HopeDetail;
import percept.myplan.R;

/**
 * Created by percept on 5/8/16.
 */

public class Basic3ImageViewHolder extends Basic3ViewHolder {

    public static final int LAYOUT_RES = R.layout.vh_image_view;

    private HopeDetail DETAILS;
    final ImageView imgCardImage;
    final TextView tvCardImage, tvCardImageEdit;
    ImageLoader imageLoader;

    public Basic3ImageViewHolder(View itemView) {
        super(itemView);
        imgCardImage = (ImageView) itemView.findViewById(R.id.imgCardImage);
        tvCardImage = (TextView) itemView.findViewById(R.id.tvCardImage);
        tvCardImageEdit = (TextView) itemView.findViewById(R.id.tvCardImageEdit);
        imageLoader = AppController.getInstance().getImageLoader();
    }

    @Override
    public void bind(RecyclerView.Adapter adapter, Object item, int posi) {

        this.DETAILS = (HopeDetail) item;
        tvCardImage.setText(this.DETAILS.getMEDIA_TITLE());
        tvCardImage.setTag(posi);
        imgCardImage.setTag(posi);
        tvCardImageEdit.setTag(posi);
        imageLoader.get(DETAILS.getMEDIA_THUMB(), new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // load image into imageview
                    imgCardImage.setImageBitmap(response.getBitmap());
                }
            }
        });
    }

    @Override
    public void setOnItemClickListener(View.OnClickListener listener) {
        super.setOnItemClickListener(listener);
        tvCardImageEdit.setOnClickListener(listener);
        imgCardImage.setOnClickListener(listener);
    }

    @Override
    public void setOnItemLongClickListener(View.OnLongClickListener listener) {
        super.setOnItemLongClickListener(listener);
        imgCardImage.setOnLongClickListener(listener);
    }
}