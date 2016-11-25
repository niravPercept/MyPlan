package percept.myplan.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import percept.myplan.POJO.ContactDisplay;
import percept.myplan.R;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class StrategyContactAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    private final Context mContext;
    private List<ContactDisplay> LIST_CONTACT;
    private int[] mSectionIndices;
    private Character[] mSectionLetters;
    private LayoutInflater mInflater;
    private boolean SINGLE_CHECK;

    public StrategyContactAdapter(Context context, List<ContactDisplay> lstContact, boolean SINGLE_CHECK) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        LIST_CONTACT = lstContact;
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
        this.SINGLE_CHECK = SINGLE_CHECK;
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        if (!TextUtils.isEmpty(LIST_CONTACT.get(0).getFirst_name())) {
            char lastFirstChar = LIST_CONTACT.get(0).getFirst_name().charAt(0);
            sectionIndices.add(0);
            for (int i = 1; i < LIST_CONTACT.size(); i++) {
                if (TextUtils.isEmpty(LIST_CONTACT.get(i).getFirst_name()))
                    continue;
                if (LIST_CONTACT.get(i).getFirst_name().charAt(0) != lastFirstChar) {
                    lastFirstChar = LIST_CONTACT.get(i).getFirst_name().charAt(0);
                    sectionIndices.add(i);
                }
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }

        return sections;
    }

    private Character[] getSectionLetters() {
        Character[] letters = new Character[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            letters[i] = LIST_CONTACT.get(mSectionIndices[i]).getFirst_name().charAt(0);
        }
        return letters;
    }

    @Override
    public int getCount() {
        return LIST_CONTACT.size();
    }

    @Override
    public Object getItem(int position) {
        return LIST_CONTACT.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.test_list_item_layout, parent, false);
            holder.TV_CONTACTNAME = (TextView) convertView.findViewById(R.id.tvContact);
            holder.IMG_CHK = (ImageView) convertView.findViewById(R.id.imgTick);

            holder.IMG_CHK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int _i = (int) view.getTag();
                    if (SINGLE_CHECK) {
                        for (int i = 0; i < LIST_CONTACT.size(); i++) {
                            LIST_CONTACT.get(i).setSelected(false);
                        }
                    }
                    LIST_CONTACT.get(_i).setSelected(!LIST_CONTACT.get(_i).isSelected());
                    notifyDataSetChanged();
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.isEmpty(LIST_CONTACT.get(position).getFirst_name()))
            holder.TV_CONTACTNAME.setText(LIST_CONTACT.get(position).getPhone());
        else
            holder.TV_CONTACTNAME.setText(LIST_CONTACT.get(position).getFirst_name()
                    + " " + LIST_CONTACT.get(position).getLast_name());

        if (LIST_CONTACT.get(position).isSelected()) {
            holder.IMG_CHK.setImageResource(R.drawable.tick);
        } else {
            holder.IMG_CHK.setImageResource(R.drawable.untick);
        }
        holder.IMG_CHK.setTag(position);
        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        if (!TextUtils.isEmpty(LIST_CONTACT.get(position).getFirst_name())) {
            CharSequence headerChar = LIST_CONTACT.get(position).getFirst_name().subSequence(0, 1);
            holder.text.setText(headerChar);
        } else holder.text.setText("#");

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        if (!TextUtils.isEmpty(LIST_CONTACT.get(position).getFirst_name()))
            return LIST_CONTACT.get(position).getFirst_name().subSequence(0, 1).charAt(0);
        else return "#".charAt(0);
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }


    class HeaderViewHolder {
        TextView text;
    }

    class ViewHolder {
        TextView TV_CONTACTNAME;
        ImageView IMG_CHK;
    }

}
