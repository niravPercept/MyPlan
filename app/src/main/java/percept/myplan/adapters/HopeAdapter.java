package percept.myplan.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import percept.myplan.AppController;
import percept.myplan.POJO.Hope;
import percept.myplan.R;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class HopeAdapter extends RecyclerView.Adapter<HopeAdapter.MyViewHolder> {

    ImageLoader imageLoader;
    private Context CONTEXT;
    private List<Hope> LST_HOPE;
    private MyViewHolder holder;

    public HopeAdapter(Context mContext, List<Hope> hopeList) {
        this.CONTEXT = mContext;
        this.LST_HOPE = hopeList;
        imageLoader = AppController.getInstance().getImageLoader();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hope_card_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Hope album = LST_HOPE.get(position);

        if (position == LST_HOPE.size() - 1) {
            holder.IMG_ADDHOPE.setVisibility(View.VISIBLE);
            holder.TV_TITLE.setText(CONTEXT.getResources().getString(R.string.addnewbox));
            holder.IMG_COVER.setBackgroundColor(CONTEXT.getResources().getColor(android.R.color.white));
        } else {
            holder.IMG_ADDHOPE.setVisibility(View.GONE);
            holder.TV_TITLE.setText(album.getTITLE());
//            Picasso.with(CONTEXT).load(album.getIMG_COVER()).into(holder.ivSelectedImg);
            imageLoader.get(album.getTHUMB_COVER(), new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                    if (response.getBitmap() != null) {
                        // load image into imageview

                        Bitmap output = Bitmap.createBitmap(response.getBitmap().getWidth(), response.getBitmap().getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(output);
                        final float densityMultiplier = CONTEXT.getResources().getDisplayMetrics().density;

                        final int color = 0xff424242;
                        final Paint paint = new Paint();
                        final Rect rect = new Rect(0, 0, response.getBitmap().getWidth(), response.getBitmap().getHeight());
                        final RectF rectF = new RectF(rect);

                        //make sure that our rounded corner is scaled appropriately
                        final float roundPx = 10 * densityMultiplier;

                        paint.setAntiAlias(true);
                        canvas.drawARGB(0, 0, 0, 0);
                        paint.setColor(color);
                        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

                        int h = response.getBitmap().getHeight();
                        int w = response.getBitmap().getWidth();
                        boolean squareTL = false;
                        boolean squareTR = false;
                        boolean squareBL = false;
                        boolean squareBR = false;
                        //draw rectangles over the corners we want to be square
                        if (squareTL) {
                            canvas.drawRect(0, h / 2, w / 2, h, paint);
                        }
                        if (squareTR) {
                            canvas.drawRect(w / 2, h / 2, w, h, paint);
                        }
                        if (squareBL) {
                            canvas.drawRect(0, 0, w / 2, h / 2, paint);
                        }
                        if (squareBR) {
                            canvas.drawRect(w / 2, 0, w, h / 2, paint);
                        }


                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                        canvas.drawBitmap(response.getBitmap(), 0, 0, paint);


                        holder.IMG_COVER.setImageBitmap(output);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return LST_HOPE.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView TV_TITLE;
        public ImageView IMG_COVER, IMG_ADDHOPE;

        public MyViewHolder(View view) {
            super(view);
            TV_TITLE = (TextView) view.findViewById(R.id.title);
            IMG_COVER = (ImageView) view.findViewById(R.id.thumbnail);
            IMG_ADDHOPE = (ImageView) view.findViewById(R.id.imgAddHope);
        }
    }
}
