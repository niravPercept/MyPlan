package percept.myplan.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import percept.myplan.Activities.CreateQuickMsgActivity;
import percept.myplan.Activities.QuickMessageDetailsActivity;
import percept.myplan.CustomListener.RecyclerTouchListener;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.ClickListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.QuickMessage;
import percept.myplan.R;
import percept.myplan.adapters.QuickMessageAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentQuickMessage extends Fragment {

    public static final int INDEX = 7;
    public static List<QuickMessage> LIST_QUICKMSG = new ArrayList<>();
    private RecyclerView LSTQUICKMSG;
    private QuickMessageAdapter ADAPTER;

    private TextView TV_ADDNEWMSG;
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;


    public fragmentQuickMessage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the lay_help_info for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.quick_msg));

        View _view = inflater.inflate(R.layout.fragment_quick_message, container, false);

        TV_ADDNEWMSG = (TextView) _view.findViewById(R.id.tvAddNewMessage);

        LSTQUICKMSG = (RecyclerView) _view.findViewById(R.id.lstQuickMsg);

        REL_COORDINATE = (CoordinatorLayout) _view.findViewById(R.id.snakeBar);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        LSTQUICKMSG.setLayoutManager(mLayoutManager);
        LSTQUICKMSG.setItemAnimator(new DefaultItemAnimator());

        setHasOptionsMenu(true);

        TV_ADDNEWMSG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(getActivity().getApplicationContext(), CreateQuickMsgActivity.class);
                startActivity(_intent);

            }
        });
        LSTQUICKMSG.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), LSTQUICKMSG, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), QuickMessageDetailsActivity.class);
                intent.putExtra("QUICK_MSG", LIST_QUICKMSG.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessages();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.quickmessage, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_Quickmsg) {
            if (!General.checkInternetConnection(getActivity()))
                return false;
            Intent _intent = new Intent(getActivity().getApplicationContext(), CreateQuickMsgActivity.class);
            startActivity(_intent);

            return true;
        }
        return false;
    }

    private void getMessages() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        try {
            new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.GET_MESSAGE, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {

                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.e("::::", response.toString());
                    mProgressDialog.dismiss();
                    try {
                        LIST_QUICKMSG = new Gson().fromJson(response.getJSONArray(Constant.DATA).toString(), new TypeToken<List<QuickMessage>>() {
                        }.getType());
                        ADAPTER = new QuickMessageAdapter(LIST_QUICKMSG);
                        LSTQUICKMSG.setAdapter(ADAPTER);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },"");
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getMessages();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
