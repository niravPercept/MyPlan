package percept.myplan.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import percept.myplan.AppController;
import percept.myplan.R;


/**
 * Created by percept on 2/5/15.
 */
public class dialogStrategyImg extends Dialog {
    private Context CONTEXT;
    private List<String> listImages;
    private ImageButton ibClose;
    private ViewPager vpPhotos;

    public dialogStrategyImg(Context context, List<String> listImages) {

        super(context, R.style.DialogThemeCustom);
        this.CONTEXT = context;
        this.listImages = listImages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image_detail);
        getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        vpPhotos = (ViewPager) findViewById(R.id.vpPhotos);
        vpPhotos.setAdapter(new PhotoAdapter());
        ibClose = (ImageButton) findViewById(R.id.ibClose);
        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogStrategyImg.this.dismiss();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class PhotoAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return listImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            final ImageView imageView = new ImageView(CONTEXT);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            ImageLoader imageLoader = AppController.getInstance().getImageLoader();

            imageLoader.get(listImages.get(position), new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                    if (response.getBitmap() != null) {
                        // load image into imageview
                        imageView.setImageBitmap(response.getBitmap());
                    }
                }
            });
            container.addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }
}
