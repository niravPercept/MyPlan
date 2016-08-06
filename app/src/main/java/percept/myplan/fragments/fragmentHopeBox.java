package percept.myplan.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import percept.myplan.Activities.AddHopeBoxActivity;
import percept.myplan.Activities.HopeDetailsActivity;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.Hope;
import percept.myplan.R;
import percept.myplan.adapters.HopeAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentHopeBox extends Fragment {

    public static final int INDEX = 4;

    private RecyclerView LST_HOPE;
    private HopeAdapter ADAPTER;
    private List<Hope> LIST_HOPE;
    public static boolean ADDED_HOPEBOX = false;
    Map<String, String> params;
    private ProgressBar PB;

    public fragmentHopeBox() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Hope Box");
        View _View = inflater.inflate(R.layout.fragment_hope_box, container, false);
        LST_HOPE = (RecyclerView) _View.findViewById(R.id.recycler_hope);
        LIST_HOPE = new ArrayList<>();

        PB = (ProgressBar) _View.findViewById(R.id.pbGetHope);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        LST_HOPE.setLayoutManager(mLayoutManager);
        LST_HOPE.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        LST_HOPE.setItemAnimator(new DefaultItemAnimator());


        LST_HOPE.addOnItemTouchListener(new fragmentHopeBox.RecyclerTouchListener(getActivity(), LST_HOPE, new fragmentHopeBox.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LIST_HOPE.get(position);
                if (position == LIST_HOPE.size() - 1) {
                    Toast.makeText(getActivity(), LIST_HOPE.get(position).getTITLE(), Toast.LENGTH_SHORT).show();
                    Intent _intent = new Intent(getActivity(), AddHopeBoxActivity.class);
                    startActivity(_intent);
                } else {
                    Intent _intent = new Intent(getActivity(), HopeDetailsActivity.class);
                    _intent.putExtra("HOPE_TITLE", LIST_HOPE.get(position).getTITLE());
                    _intent.putExtra("HOPE_ID", LIST_HOPE.get(position).getID());
                    startActivity(_intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        try {
            PB.setVisibility(View.VISIBLE);
            LIST_HOPE.clear();
            new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.GET_HOPEBOXES, params, false, false, false, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    PB.setVisibility(View.GONE);
                }

                @Override
                public void onResponse(JSONObject response) {
                    PB.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    try {
                        LIST_HOPE = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<Hope>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    LIST_HOPE.add(new Hope("", "", "", "", "", "Add New Box", ""));
                    ADAPTER = new HopeAdapter(getActivity(), LIST_HOPE);
                    LST_HOPE.setAdapter(ADAPTER);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _View;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ADDED_HOPEBOX) {
            try {
                PB.setVisibility(View.VISIBLE);
                LIST_HOPE.clear();
                new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.GET_HOPEBOXES, params, false, false, false, new VolleyResponseListener() {
                    @Override
                    public void onError(VolleyError message) {
                        PB.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        PB.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        try {
                            LIST_HOPE = gson.fromJson(response.getJSONArray(Constant.DATA)
                                    .toString(), new TypeToken<List<Hope>>() {
                            }.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        LIST_HOPE.add(new Hope("", "", "", "", "", "Add New Box", ""));
                        ADAPTER = new HopeAdapter(getActivity(), LIST_HOPE);
                        LST_HOPE.setAdapter(ADAPTER);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            ADDED_HOPEBOX = false;
        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private fragmentHopeBox.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final fragmentHopeBox.ClickListener clickListener) {
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
