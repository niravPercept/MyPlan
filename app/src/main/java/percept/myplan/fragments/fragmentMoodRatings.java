package percept.myplan.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import percept.myplan.Activities.HomeActivity;
import percept.myplan.Activities.MoodActivity;
import percept.myplan.Activities.SidasActivity;
import percept.myplan.Activities.SidasDetailActivity;
import percept.myplan.Graph.Bar;
import percept.myplan.Graph.BarGraph;
import percept.myplan.Graph.PieGraph;
import percept.myplan.Graph.PieSlice;
import percept.myplan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentMoodRatings extends Fragment {


    public static final int INDEX = 5;
    private final int REQ_CODE_SIDAS = 23;
    BarGraph BAR_GRAPH;
    PieGraph PIE_GRAPH;
    ArrayList<PieSlice> LST_PIEDATA;
    ArrayList<Bar> LST_BARDATA;

    private TextView TV_SIDAS, TV_MOOD;

    public fragmentMoodRatings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the lay_help_info for this fragment
        View _View = inflater.inflate(R.layout.fragment_mood_ratings, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mood Ratings");

        TV_SIDAS = (TextView) _View.findViewById(R.id.tvSidas);
        TV_MOOD = (TextView) _View.findViewById(R.id.tvMood);

        BAR_GRAPH = (BarGraph) _View.findViewById(R.id.bargraph);
        PIE_GRAPH = (PieGraph) _View.findViewById(R.id.piegraph);
        LST_PIEDATA = new ArrayList<>();
        LST_BARDATA = new ArrayList<>();
        setBarData();
        setPieData();

        TV_SIDAS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), SidasDetailActivity.class), REQ_CODE_SIDAS);
            }
        });

        TV_MOOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MoodActivity.class));
            }
        });
        return _View;
    }

    private void setPieData() {
        final Resources resources = getResources();
        PieSlice slice = new PieSlice();
        slice.setColor(resources.getColor(R.color.green_light));
        slice.setSelectedColor(resources.getColor(R.color.transparent_orange));
        slice.setValue(2);
        slice.setTitle("first");
        LST_PIEDATA.add(slice);
        slice = new PieSlice();
        slice.setColor(resources.getColor(R.color.orange));
        slice.setValue(3);
        slice.setTitle("Second");
        LST_PIEDATA.add(slice);
        slice = new PieSlice();
        slice.setColor(resources.getColor(R.color.purple));
        slice.setValue(8);
        slice.setTitle("Third");
        LST_PIEDATA.add(slice);

        for (int i = 0; i < LST_PIEDATA.size(); i++) {
            PIE_GRAPH.addSlice(LST_PIEDATA.get(i));
        }
//        PIE_GRAPH.addSlice(slice);
        PIE_GRAPH.setOnSliceClickedListener(new PieGraph.OnSliceClickedListener() {

            @Override
            public void onClick(int index) {
                Toast.makeText(getActivity(),
                        LST_PIEDATA.get(index).getTitle(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void setBarData() {
        final Resources resources = getResources();

        Bar bar = new Bar();
        bar.setColor(resources.getColor(R.color.green_light));
        bar.setSelectedColor(resources.getColor(R.color.transparent_orange));
        bar.setName("Test1");
        bar.setValue(1000);
        bar.setValueString("$1,000");
        LST_BARDATA.add(bar);
        bar = new Bar();
        bar.setColor(resources.getColor(R.color.orange));
        bar.setName("Test2");
        bar.setValue(3000);
        bar.setValueString("$3,000");
        LST_BARDATA.add(bar);
        bar = new Bar();
        bar.setColor(resources.getColor(R.color.orange));
        bar.setName("Test2");
        bar.setValue(4000);
        bar.setValueString("$4,000");
        LST_BARDATA.add(bar);
        bar = new Bar();
        bar.setColor(resources.getColor(R.color.orange));
        bar.setName("Test2");
        bar.setValue(2000);
        bar.setValueString("$2,000");
        LST_BARDATA.add(bar);
        bar = new Bar();
        bar.setColor(resources.getColor(R.color.purple));
        bar.setName("Test3");
        bar.setValue(1500);
        bar.setValueString("$1,500");
        LST_BARDATA.add(bar);

        BAR_GRAPH.setBars(LST_BARDATA);

        BAR_GRAPH.setOnBarClickedListener(new BarGraph.OnBarClickedListener() {

            @Override
            public void onClick(int index) {
                Toast.makeText(getActivity(),
                        "Bar " + index + " clicked " + String.valueOf(BAR_GRAPH.getBars().get(index).getValue()),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SIDAS:
                if (resultCode == Activity.RESULT_OK)
                    ((HomeActivity) getActivity()).selectItem(fragmentStrategies.INDEX);
                break;
        }
    }
}
