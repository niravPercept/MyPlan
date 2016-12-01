package percept.myplan.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import percept.myplan.Activities.AddNewSymptomActivity;
import percept.myplan.Activities.DangerSignalsActivity;
import percept.myplan.Activities.SymptomDetailsActivity;
import percept.myplan.CustomListener.RecyclerTouchListener;
import percept.myplan.Dialogs.dialogYesNoOption;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Interfaces.ClickListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.HelpVideos;
import percept.myplan.POJO.Symptom;
import percept.myplan.R;
import percept.myplan.adapters.SymptomAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentSymptoms extends Fragment implements View.OnClickListener {

    public static final int INDEX = 1;
    public static Boolean GET_STRATEGY = false;
    private RecyclerView LST_SYMPTOM;
    private List<Symptom> LIST_SYMPTOM;
    private SymptomAdapter ADAPTER;
    private Button BTN_DANGERSIGNAL;
    private TextView TV_ADDNEW_SYMPTOM;
    private CoordinatorLayout REL_COORDINATE;
    private RelativeLayout LAY_INFO;
    private RelativeLayout REL_MAIN;
    private Button BTN_INFO;
    private Button BTN_SHOWINFOINSIDE;
    private ArrayList<HelpVideos> listHelpVideos;
    private TextView tvTitle1, tvTitle2, tvTitle3, tvTitle4;
    private ImageView ivThumb1, ivThumb2, ivThumb3, ivThumb4;
    private ProgressDialog mProgressDialog;
    public fragmentSymptoms() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the lay_help_info for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Symptoms");
        View _View = inflater.inflate(R.layout.fragment_symptoms, container, false);
        LST_SYMPTOM = (RecyclerView) _View.findViewById(R.id.lstSymptom);
        BTN_DANGERSIGNAL = (Button) _View.findViewById(R.id.btnDangerSignal);
        REL_COORDINATE = (CoordinatorLayout) _View.findViewById(R.id.snakeBar);
        LIST_SYMPTOM = new ArrayList<>();


        setHasOptionsMenu(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        LST_SYMPTOM.setLayoutManager(mLayoutManager);
        LST_SYMPTOM.setItemAnimator(new DefaultItemAnimator());

        TV_ADDNEW_SYMPTOM = (TextView) _View.findViewById(R.id.tvAddNewSymptom);
        TV_ADDNEW_SYMPTOM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddNewSymptomActivity.class));
            }
        });

        GetSymptom();

        LST_SYMPTOM.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), LST_SYMPTOM, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                LIST_SYMPTOM.get(position);
                Intent _intent = new Intent(getActivity(), SymptomDetailsActivity.class);
                _intent.putExtra("SYMPTOM_ID", LIST_SYMPTOM.get(position).getId());
                startActivity(_intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        BTN_DANGERSIGNAL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DangerSignalsActivity.class));
            }
        });


        LAY_INFO = (RelativeLayout) _View.findViewById(R.id.layInfo);
        REL_MAIN = (RelativeLayout) _View.findViewById(R.id.relMainLogin);

        LAY_INFO.setVisibility(View.GONE);

        BTN_INFO = (Button) _View.findViewById(R.id.btnShowInfo);
        BTN_SHOWINFOINSIDE = (Button) _View.findViewById(R.id.btnShowInfoInside);
        tvTitle1 = (TextView) _View.findViewById(R.id.tvTitle1);
        tvTitle2 = (TextView) _View.findViewById(R.id.tvTitle2);
        tvTitle3 = (TextView) _View.findViewById(R.id.tvTitle3);
        tvTitle4 = (TextView) _View.findViewById(R.id.tvTitle4);
        ivThumb1 = (ImageView) _View.findViewById(R.id.ivThumb1);
        ivThumb2 = (ImageView) _View.findViewById(R.id.ivThumb2);
        ivThumb3 = (ImageView) _View.findViewById(R.id.ivThumb3);
        ivThumb4 = (ImageView) _View.findViewById(R.id.ivThumb4);
        //android:background="#55000000"
        BTN_INFO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTN_INFO.setVisibility(View.INVISIBLE);
                if (LAY_INFO.getVisibility() == View.GONE) {
                    REL_MAIN.setBackgroundColor(getResources().getColor(R.color.shadowback));
                    LAY_INFO.requestLayout();
                    LAY_INFO.setVisibility(View.VISIBLE);
                    LAY_INFO.animate()
                            .translationX(0)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    LAY_INFO.setVisibility(View.VISIBLE);
                                    if (listHelpVideos == null)
                                        getHelpinfo();
                                }
                            });

                } else {
//                    LAY_INFO.setVisibility(View.GONE);
                    LAY_INFO.animate()
                            .translationX(LAY_INFO.getWidth())
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    LAY_INFO.setVisibility(View.GONE);

                                }
                            });
                }
            }
        });

        BTN_SHOWINFOINSIDE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LAY_INFO.animate()
                        .translationX(LAY_INFO.getWidth())
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                LAY_INFO.setVisibility(View.GONE);
                                BTN_INFO.setVisibility(View.VISIBLE);
                                REL_MAIN.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                            }
                        });
            }
        });
        tvTitle1.setOnClickListener(this);
        tvTitle2.setOnClickListener(this);
        tvTitle3.setOnClickListener(this);
        tvTitle4.setOnClickListener(this);
        ivThumb1.setOnClickListener(this);
        ivThumb2.setOnClickListener(this);
        ivThumb3.setOnClickListener(this);
        ivThumb4.setOnClickListener(this);
        initSwipe();
        return _View;
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
            Intent _intent = new Intent(getActivity().getApplicationContext(), AddNewSymptomActivity.class);
            startActivity(_intent);

            return true;
        }
        return false;
    }

    public void GetSymptom() {
        try {
            showProgress(getString(R.string.progress_loading));
            Map<String, String> params = new HashMap<String, String>();
            params.put("sid", Constant.SID);
            params.put("sname", Constant.SNAME);
            new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.GET_SYMPTOMS, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                   dismissProgress();
                }

                @Override
                public void onResponse(JSONObject response) {
                    dismissProgress();
                    Log.d(":::: ", response.toString());
                    Gson gson = new Gson();
                    try {
                        LIST_SYMPTOM = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<Symptom>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ADAPTER = new SymptomAdapter(LIST_SYMPTOM);
                    LST_SYMPTOM.setAdapter(ADAPTER);
                }
            },"");
        } catch (Exception e) {
            dismissProgress();
            e.printStackTrace();
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GetSymptom();
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
    public void onResume() {
        super.onResume();
        if (GET_STRATEGY) {
            GetSymptom();
            GET_STRATEGY = false;
        }
    }

    private void getHelpinfo() {
        showProgress(getString(R.string.progress_loading));
        try {
            new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.GET_HELP_INFO, new HashMap<String, String>(), true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    dismissProgress();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.i(":::Help Videos", "" + response);
                    try {
                        listHelpVideos = new Gson().fromJson(response.getJSONArray(Constant.DATA).toString(), new TypeToken<ArrayList<HelpVideos>>() {
                        }.getType());
                        tvTitle1.setText(listHelpVideos.get(0).getVideoTitle());
                        tvTitle2.setText(listHelpVideos.get(1).getVideoTitle());
                        tvTitle3.setText(listHelpVideos.get(2).getVideoTitle());
                        tvTitle4.setText(listHelpVideos.get(3).getVideoTitle());
                        Picasso.with(getActivity())
                                .load("http://img.youtube.com/vi/" + listHelpVideos.get(0).getVideoLink() + "/1.jpg")
                                .into(ivThumb1);
                        Picasso.with(getActivity())
                                .load("http://img.youtube.com/vi/" + listHelpVideos.get(1).getVideoLink() + "/1.jpg")
                                .into(ivThumb2);
                        Picasso.with(getActivity())
                                .load("http://img.youtube.com/vi/" + listHelpVideos.get(2).getVideoLink() + "/1.jpg")
                                .into(ivThumb3);
                        Picasso.with(getActivity())
                                .load("http://img.youtube.com/vi/" + listHelpVideos.get(3).getVideoLink() + "/1.jpg")
                                .into(ivThumb4);
                        dismissProgress();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },"");
        } catch (Exception e) {
            dismissProgress();
            e.printStackTrace();
        }
    }

    private void watchVideoOnYouTube(String videoName) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoName));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + videoName));
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvTitle1:
            case R.id.ivThumb1:
                if (listHelpVideos != null && listHelpVideos.size() > 0) {
                    watchVideoOnYouTube(listHelpVideos.get(0).getVideoLink());

                }
                break;
            case R.id.tvTitle2:
            case R.id.ivThumb2:
                if (listHelpVideos != null && listHelpVideos.size() > 1) {
                    watchVideoOnYouTube(listHelpVideos.get(1).getVideoLink());

                }
                break;
            case R.id.tvTitle3:
            case R.id.ivThumb3:
                if (listHelpVideos != null && listHelpVideos.size() > 2) {
                    watchVideoOnYouTube(listHelpVideos.get(2).getVideoLink());

                }
                break;
            case R.id.tvTitle4:
            case R.id.ivThumb4:
                if (listHelpVideos != null && listHelpVideos.size() > 3) {
                    watchVideoOnYouTube(listHelpVideos.get(3).getVideoLink());

                }
                break;
        }
    }

    private void initSwipe() {
        final Paint p = new Paint();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
//                if (direction == ItemTouchHelper.LEFT){
//                    adapter.removeItem(position);
//                } else {
                // Snackbar.make(getView(), "Swipe Right", Snackbar.LENGTH_LONG).show();
//                }
                dialogYesNoOption _dialog = new dialogYesNoOption(getActivity(), getString(R.string.delete_symptom)) {

                    @Override
                    public void onClickYes() {
                        if (!General.checkInternetConnection(getActivity()))
                            return;
                        HashMap<String, String> params = new HashMap<>();
                        params.put("sid",Constant.SID);
                        params.put("sname",Constant.SNAME);
                        params.put("id",LIST_SYMPTOM.get(position).getId());

                        try {
                           showProgress(getString(R.string.progress_loading));
                            dismiss();
                            new General().getJSONContentFromInternetService(getActivity(),
                                    General.PHPServices.DELETE_SYMPTOM, params,
                                    true, false, true, new VolleyResponseListener() {

                                        @Override
                                        public void onError(VolleyError message) {
                                            dismissProgress();
                                            ADAPTER.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onResponse(JSONObject response) {
                                            dismissProgress();
                                            LIST_SYMPTOM.remove(position);
                                            ADAPTER.notifyDataSetChanged();
                                        }
                                    },LIST_SYMPTOM.get(position).getId());
                        } catch (Exception e) {
                            dismissProgress();
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onClickNo() {
                        ADAPTER.notifyDataSetChanged();
                        dismiss();
                    }
                };
                _dialog.setCancelable(false);
                _dialog.setCanceledOnTouchOutside(false);
                _dialog.show();

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

//                    if(dX > 0){
//                        p.setColor(Color.parseColor("#388E3C"));
//                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
//                        c.drawRect(background,p);
//                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
//                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
//                        c.drawBitmap(icon,null,icon_dest,p);
//                    } else {
                    p.setColor(getResources().getColor(android.R.color.white));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_delete);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);
//                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(LST_SYMPTOM);
    }

    public void showProgress(String message){
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    public void dismissProgress(){
        if(mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }


    }

}
