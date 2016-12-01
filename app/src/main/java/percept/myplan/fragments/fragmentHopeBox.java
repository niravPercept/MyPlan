package percept.myplan.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import percept.myplan.Activities.CreateQuickMsgActivity;
import percept.myplan.Activities.HopeDetailsActivity;
import percept.myplan.CustomListener.RecyclerTouchListener;
import percept.myplan.Dialogs.dialogDeleteAlert;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.ClickListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.Hope;
import percept.myplan.R;
import percept.myplan.adapters.HopeAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentHopeBox extends Fragment {

    public static final int INDEX = 4;
    public static boolean ADDED_HOPEBOX = false;
    Map<String, String> params;
    private RecyclerView LST_HOPE;
    private HopeAdapter ADAPTER;
    private List<Hope> LIST_HOPE;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;
    private int deletePosition = -1;

    public fragmentHopeBox() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the lay_help_info for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Hope Box");
        View _View = inflater.inflate(R.layout.fragment_hope_box, container, false);
        REL_COORDINATE = (CoordinatorLayout) _View.findViewById(R.id.snakeBar);
        setHasOptionsMenu(true);

        LST_HOPE = (RecyclerView) _View.findViewById(R.id.recycler_hope);
        LIST_HOPE = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        LST_HOPE.setLayoutManager(mLayoutManager);
        LST_HOPE.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        LST_HOPE.setItemAnimator(new DefaultItemAnimator());


        LST_HOPE.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), LST_HOPE, new ClickListener() {
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
                deletePosition = position;
                new dialogDeleteAlert(getActivity(), getString(R.string.delete_hope_box)) {
                    @Override
                    public void onClickYes() {
                        dismiss();
                        deleteHopeBox(LIST_HOPE.get(deletePosition).getID());


                    }

                    @Override
                    public void onClickNo() {
                        dismiss();
                        deletePosition = -1;
                    }
                }.show();
            }
        }));

        GetHopeBox();
        return _View;
    }

    private void deleteHopeBox(String hopeBoxId) {
        if (!General.checkInternetConnection(getActivity()))
            return;
        params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("id", hopeBoxId);
        try {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.progress_loading));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.DELETE_HOPE_BOX, params, true, false, false, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                    deletePosition = -1;
                }

                @Override
                public void onResponse(JSONObject response) {
                    mProgressDialog.dismiss();
                    try {
                        if (response.has(Constant.DATA)) {

                            if (response.getJSONObject(Constant.DATA).getString(Constant.STATUS).equals("Success")) {
                                LIST_HOPE.remove(deletePosition);
                                deletePosition = -1;
                                ADAPTER = new HopeAdapter(getActivity(), LIST_HOPE);
                                LST_HOPE.setAdapter(ADAPTER);
                                ADAPTER.notifyDataSetChanged();
                                Toast.makeText(getActivity(), getString(R.string.delete_hope_box_success), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },hopeBoxId);
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            final Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteHopeBox(LIST_HOPE.get(deletePosition).getID());
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();
        }
    }

    private void GetHopeBox() {

        params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        try {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.progress_loading));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            LIST_HOPE.clear();
            new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.GET_HOPEBOXES, params, true, false, false, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    mProgressDialog.dismiss();
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
            },"");
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            final Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GetHopeBox();
                        }
                    });

            // Changing message text color
            snackbar.setActionTextColor(Color.RED);

            // Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);

            snackbar.show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contact, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_addContact) {
            if (!General.checkInternetConnection(getActivity()))
                return false;
            Intent _intent = new Intent(getActivity().getApplicationContext(), AddHopeBoxActivity.class);
            startActivity(_intent);

            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ADDED_HOPEBOX) {
            GetHopeBox();
            ADDED_HOPEBOX = false;
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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

}
