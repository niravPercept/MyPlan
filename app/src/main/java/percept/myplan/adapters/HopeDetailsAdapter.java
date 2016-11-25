package percept.myplan.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import percept.myplan.AppController;
import percept.myplan.POJO.HopeDetail;
import percept.myplan.R;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class HopeDetailsAdapter extends RecyclerView.Adapter<HopeDetailsAdapter.MyViewHolder> {

    private Context CONTEXT;
    private List<HopeDetail> LST_HOPE;
    ImageLoader imageLoader;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView CARD_IMAGE, CARD_VIDEO;
        public ImageView IMG_HOPEDETAILS;
        public TextureView VID_HOPEDETAILS;
        public TextView TV_CARDIMG_TITLE, TV_CARDVID_TITLE;


        public MyViewHolder(View view) {
            super(view);

            CARD_IMAGE = (CardView) view.findViewById(R.id.cardImage);
            CARD_VIDEO = (CardView) view.findViewById(R.id.cardVideo);

            IMG_HOPEDETAILS = (ImageView) view.findViewById(R.id.imgCardImage);
            VID_HOPEDETAILS = (TextureView) view.findViewById(R.id.videoViewCardVideo);

            TV_CARDIMG_TITLE = (TextView) view.findViewById(R.id.tvCardImage);
            TV_CARDVID_TITLE = (TextView) view.findViewById(R.id.tvCardVideo);
            VID_HOPEDETAILS.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int i = (int) view.getTag();
            Log.d(":::: Pressed on ", String.valueOf(i));
            ((VideoView) view).start();
        }
    }


    public HopeDetailsAdapter(Context mContext, List<HopeDetail> hopeList) {
        this.CONTEXT = mContext;
        this.LST_HOPE = hopeList;
        imageLoader = AppController.getInstance().getImageLoader();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hope_details_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        HopeDetail album = LST_HOPE.get(position);
        switch (album.getTYPE()) {
            case "image":
                holder.CARD_IMAGE.setVisibility(View.VISIBLE);
                holder.CARD_VIDEO.setVisibility(View.GONE);
                imageLoader.get(album.getMEDIA(), new ImageLoader.ImageListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                        if (response.getBitmap() != null) {
                            // load image into imageview
                            holder.IMG_HOPEDETAILS.setImageBitmap(response.getBitmap());
                        }
                    }
                });

                holder.TV_CARDIMG_TITLE.setText(album.getMEDIA_TITLE());
                break;
            case "video":
                holder.CARD_IMAGE.setVisibility(View.GONE);
                holder.CARD_VIDEO.setVisibility(View.VISIBLE);
                holder.TV_CARDVID_TITLE.setText(album.getMEDIA_TITLE());
//                holder.VID_HOPEDETAILS.setVideoURI(Uri.parse(album.getMEDIA()));
//                holder.VID_HOPEDETAILS.setTag(position);
//                MediaController mc = new MediaController(CONTEXT);
//                mc.setAnchorView(holder.VID_HOPEDETAILS);
//                mc.setMediaPlayer(holder.VID_HOPEDETAILS);
//                Uri video = Uri.parse(album.getMEDIA());
//                holder.VID_HOPEDETAILS.setMediaController(mc);
//                holder.VID_HOPEDETAILS.setVideoURI(video);
//                holder.VID_HOPEDETAILS.start();
                break;
            default:
                holder.CARD_IMAGE.setVisibility(View.GONE);
                holder.CARD_VIDEO.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return LST_HOPE.size();
    }
}
