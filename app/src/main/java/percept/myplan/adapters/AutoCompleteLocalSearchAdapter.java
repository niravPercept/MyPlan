package percept.myplan.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import percept.myplan.POJO.NearestEmergencyRoom;
import percept.myplan.R;


public class AutoCompleteLocalSearchAdapter extends ArrayAdapter<NearestEmergencyRoom> {
    private final String MY_DEBUG_TAG = "AutoCompleteLocalSearchAdapter";
    private ArrayList<NearestEmergencyRoom> items;
    private ArrayList<NearestEmergencyRoom> itemsAll;
    private ArrayList<NearestEmergencyRoom> suggestions;
    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((NearestEmergencyRoom) (resultValue)).getRoomName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (NearestEmergencyRoom customer : itemsAll) {
                    if (customer.getRoomName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<NearestEmergencyRoom> filteredList = (ArrayList<NearestEmergencyRoom>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (NearestEmergencyRoom c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

    public AutoCompleteLocalSearchAdapter(Context context, ArrayList<NearestEmergencyRoom> items) {
        super(context, R.layout.item_autocomplete, items);
        this.items = items;
        this.itemsAll = (ArrayList<NearestEmergencyRoom>) items.clone();
        this.suggestions = new ArrayList<NearestEmergencyRoom>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_autocomplete, null);
        }
        NearestEmergencyRoom emergencyRoom = items.get(position);
        if (emergencyRoom != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.tvText);
            if (customerNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView NearestEmergencyRoom Name:"+emergencyRoom.getName());
                customerNameLabel.setText(emergencyRoom.getRoomName());
            }
        }
        return v;
    }

    @Nullable
    @Override
    public NearestEmergencyRoom getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

}