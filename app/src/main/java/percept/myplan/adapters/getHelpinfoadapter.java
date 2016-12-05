package percept.myplan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import percept.myplan.POJO.info;
import percept.myplan.R;

/**
 * Created by percept on 2/12/16.
 */

public class getHelpinfoadapter extends BaseAdapter {
    Context context;
    List<info> alist;
    ImageView imginfo;
    TextView txtitle;

    public getHelpinfoadapter(Context applicationContext, List<info> infos) {
        this.context=applicationContext;
        this.alist=infos;
    }

    @Override
    public int getCount() {
        return alist.size();
    }

    @Override
    public Object getItem(int i) {
        return alist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.layout_helpinfo,null,false);

        if (v!=null){
            imginfo= (ImageView) v.findViewById(R.id.ivThumb1);
            txtitle= (TextView) v.findViewById(R.id.tvTitle1);
        }

        txtitle.setText(alist.get(i).getInfoTitle());

        Picasso.with(context)
                .load("http://img.youtube.com/vi/" + alist.get(i).getInfoLink() + "/1.jpg")
                .into(imginfo);

     /*   txtitle.setText(alist.get(i).get(map.get("title")));


//map.get("link")
        Picasso.with(context)
                .load("http://img.youtube.com/vi/" + alist.get(i).get(map.get("link")) + "/1.jpg")
                .into(imginfo);*/


        return v;
    }
}
