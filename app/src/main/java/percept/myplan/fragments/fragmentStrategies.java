package percept.myplan.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import percept.myplan.Activities.SymptomDetailsActivity;
import percept.myplan.POJO.Strategy;
import percept.myplan.POJO.Symptom;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.VolleyResponseListener;
import percept.myplan.R;
import percept.myplan.adapters.StrategyAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentStrategies extends Fragment {

    public static final int INDEX = 2;
    private RecyclerView LST_STRATEGY;
    private List<Strategy> LIST_STRATEGY;
    private StrategyAdapter ADAPTER;
    private Button BTN_INSPIRATION;

    public fragmentStrategies() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Strategies");
        View _View = inflater.inflate(R.layout.fragment_strategies, container, false);

        LST_STRATEGY = (RecyclerView) _View.findViewById(R.id.lstStrategy);
        BTN_INSPIRATION = (Button) _View.findViewById(R.id.btnInspiration);

        LIST_STRATEGY = new ArrayList<>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        LST_STRATEGY.setLayoutManager(mLayoutManager);
        LST_STRATEGY.setItemAnimator(new DefaultItemAnimator());


        try {
            new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.GET_STRATEGIES, params, false, false, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {

                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(":::: ", response.toString());
                    Gson gson = new Gson();
                    try {
                        LIST_STRATEGY = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<Strategy>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ADAPTER = new StrategyAdapter(LIST_STRATEGY);
                    LST_STRATEGY.setAdapter(ADAPTER);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        LST_STRATEGY.addOnItemTouchListener(new fragmentStrategies.RecyclerTouchListener(getActivity(), LST_STRATEGY, new fragmentStrategies.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LIST_STRATEGY.get(position);
                Intent _intent = new Intent(getActivity(), SymptomDetailsActivity.class);
                _intent.putExtra("STRATEGY_ID", LIST_STRATEGY.get(position).getId());
                startActivity(_intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return _View;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private fragmentStrategies.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final fragmentStrategies.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
