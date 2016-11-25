package percept.myplan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import percept.myplan.R;


/**
 * Created by Ravi Tamada on 12-03-2015.
 */
public class NavigationDrawerAdapter extends BaseAdapter {
    List<String> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public NavigationDrawerAdapter(Context context, List<String> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder _holder;
        if (view == null) {
            _holder = new ViewHolder();
            view = inflater.inflate(R.layout.side_menu_item, null);
            _holder.TV_ITEM = (TextView) view.findViewById(R.id.title);
            _holder.IMG_ARROW = (ImageView) view.findViewById(R.id.arrow);
            _holder.IMG_ICON = (ImageView) view.findViewById(R.id.imgIcon);
            _holder.VIEW_CUT = view.findViewById(R.id.viewCut);
            _holder.VIEW_FULL = view.findViewById(R.id.viewFull);
            view.setTag(_holder);
        } else {
            _holder = (ViewHolder) view.getTag();
        }

        _holder.TV_ITEM.setText(data.get(i));
        if (data.get(i).trim().equals("")) {
            _holder.IMG_ARROW.setVisibility(View.INVISIBLE);
            _holder.IMG_ICON.setVisibility(View.INVISIBLE);
        } else {
            _holder.IMG_ARROW.setVisibility(View.VISIBLE);
            _holder.IMG_ICON.setVisibility(View.VISIBLE);
        }
        switch (i) {
            case 0:
                // Symptom
                _holder.IMG_ICON.setImageResource(R.drawable.menu_icon_symptom);
                break;
            case 1:
                //Strategies
                _holder.IMG_ICON.setImageResource(R.drawable.menu_icon_strategies);
                break;
            case 2:
                //Contacts
                _holder.IMG_ICON.setImageResource(R.drawable.menu_icon_contact);
                break;
            case 3:
                //HopeBox
                _holder.IMG_ICON.setImageResource(R.drawable.menu_icon_hopebox);
                break;
            case 4:
                //Mood Ratings
                _holder.IMG_ICON.setImageResource(R.drawable.setting_moodrating_new);
                break;
            case 5:
                //Blank Row
                break;
            case 6:
                //Quick Message
                _holder.IMG_ICON.setImageResource(R.drawable.menu_icon_quickmsg);
                break;
            case 7:
                //Share My Location
                _holder.IMG_ICON.setImageResource(R.drawable.menu_icon_shareloc);
                break;
            case 8:
                //Nearest Emergency Room
                _holder.IMG_ICON.setImageResource(R.drawable.menu_icon_emergency);
                break;
            case 9:
                //Blank Row
                break;
            case 10:
                //Setting
                _holder.IMG_ICON.setImageResource(R.drawable.menu_icon_settings);
                break;
            case 11:
                //Logout
                _holder.IMG_ICON.setImageResource(R.drawable.menu_icon_logout);
                break;

        }

        if (i == 5 || i == 4 || i == 8 || i == 9 || i == 10 || i == 11) {
            _holder.VIEW_FULL.setVisibility(View.VISIBLE);
            _holder.VIEW_CUT.setVisibility(View.GONE);
        } else {
            _holder.VIEW_FULL.setVisibility(View.GONE);
            _holder.VIEW_CUT.setVisibility(View.VISIBLE);
        }
        return view;
    }

    class ViewHolder {
        private TextView TV_ITEM;
        private ImageView IMG_ARROW, IMG_ICON;
        private View VIEW_CUT, VIEW_FULL;
    }
}
