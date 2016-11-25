package percept.myplan.adapters;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import im.ene.lab.flvv.FloppyVideoView;
import im.ene.lab.flvv.ScaleType;
import percept.myplan.AppController;
import percept.myplan.POJO.HopeDetail;
import percept.myplan.R;

/**
 * Created by percept on 5/8/16.
 */

public class Basic3VideoViewHolder extends Basic3ViewHolder {

    // vh_toro_video_basic_3 is the updated version for vh_toro_video_basic, which has an extra
    // TextView to show the selective click event handling.
    public static final int LAYOUT_RES = R.layout.vh_toro_video_basic_3;
    final TextView dummyView, tvCardVideoEdit;
    final ScaleType[] SCALE_TYPES = {
            ScaleType.FIT_CENTER, ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_END,
            ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE
    };
    private ImageLoader imageLoader;
    public ImageView videoView;
    int scaleTypeIndex = 0;
    int videoIndex = 0;
    private HopeDetail video;
    public FrameLayout frameVideo;
    public LinearLayout llVideoView;
    private ImageView ivThumbVideo;

    public Basic3VideoViewHolder(View itemView) {
        super(itemView);

        frameVideo= (FrameLayout) itemView.findViewById(R.id.video);
        llVideoView = (LinearLayout) itemView.findViewById(R.id.llVideoView);
        dummyView = (TextView) itemView.findViewById(R.id.text);
        videoView = (ImageView) itemView.findViewById(R.id.video_view);
        tvCardVideoEdit = (TextView) itemView.findViewById(R.id.tvCardVideoEdit);
        ivThumbVideo= (ImageView) itemView.findViewById(R.id.ivThumbVideo);
        imageLoader = AppController.getInstance().getImageLoader();

    }

    @Override
    public void bind(RecyclerView.Adapter adapter, Object item, int position) {
        if (!(item instanceof HopeDetail)) {
            throw new IllegalArgumentException("Invalid Object: " + item);
        }

        this.video = (HopeDetail) item;
        String url = this.video.getMEDIA();
        dummyView.setText(this.video.getMEDIA_TITLE());
        imageLoader.get(video.getMEDIA_THUMB(), new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // load image into imageview
                    videoView.setImageBitmap(response.getBitmap());
                }
            }
        });
//        videoView.setScaleType(SCALE_TYPES[scaleTypeIndex % SCALE_TYPES.length]);

        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(url,
                MediaStore.Images.Thumbnails.MINI_KIND);

        videoView.setImageBitmap(thumb);

      /*  frameVideo.addView(hudWidget);
        hudWidget.bringToFront();*/

        //videoView.setVideoPath(url);
      /*  videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (videoView != null) {
//                    videoView.start();
//                    mp.setLooping(true);
                    //ivThumbVideo.setVisibility(View.GONE);
                }
            }
        });*/
      /*  videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });*/

        videoView.setTag(position);
        tvCardVideoEdit.setTag(position);
        dummyView.setTag(position);
        llVideoView.setTag(position);
    }

    @Override
    public void setOnItemClickListener(View.OnClickListener listener) {
        super.setOnItemClickListener(listener);
        videoView.setOnClickListener(listener);
        tvCardVideoEdit.setOnClickListener(listener);
    }

    @Override
    public void setOnItemLongClickListener(final View.OnLongClickListener listener) {
        super.setOnItemLongClickListener(listener);
        videoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return listener.onLongClick(view);
            }
        });
    }

}
