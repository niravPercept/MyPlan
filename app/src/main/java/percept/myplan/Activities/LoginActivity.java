package percept.myplan.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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

import io.tpa.tpalib.TPA;
import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.ext.CrashHandling;
import io.tpa.tpalib.ext.TpaLog;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.HelpVideos;
import percept.myplan.POJO.info;
import percept.myplan.R;
import percept.myplan.adapters.getHelpinfoadapter;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button BTN_LOGIN, BTN_SIGNUP, BTN_INFO, BTN_SHOWINFOINSIDE;
    private TextView TV_FORGOTPWD;
    private RelativeLayout LAY_INFO, REL_MAIN;
    private Utils UTILS;

    private  ImageView imagesupport;

    private ProgressBar pbHelpVideo;
    private ArrayList<HelpVideos> listHelpVideos;
    private TextView tvTitle1, tvTitle2, tvTitle3, tvTitle4;
    private ImageView ivThumb1, ivThumb2, ivThumb3, ivThumb4;
    private ListView lstmenu;
    private List<info> infos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        chekcVesion();
        autoScreenTracking();


        UTILS = new Utils(LoginActivity.this);
        if (UTILS.getPreference(Constant.PREF_LOGGEDIN).equals("true")) {
            Constant.SID = UTILS.getPreference(Constant.PREF_SID);
            Constant.SNAME = UTILS.getPreference(Constant.PREF_SNAME);
            Constant.PROFILE_IMG_LINK = UTILS.getPreference(Constant.PREF_PROFILE_IMG_LINK);
            Constant.PROFILE_EMAIL = UTILS.getPreference(Constant.PREF_PROFILE_EMAIL);
            Constant.PROFILE_USER_NAME = UTILS.getPreference(Constant.PREF_PROFILE_USER_NAME);
            Constant.PROFILE_NAME = UTILS.getPreference(Constant.PREF_PROFILE_FNAME) + " " + UTILS.getPreference(Constant.PREF_PROFILE_LNAME);

            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            LoginActivity.this.finish();
        }

        LAY_INFO = (RelativeLayout) findViewById(R.id.layInfo);
        REL_MAIN = (RelativeLayout) findViewById(R.id.relMainLogin);

        LAY_INFO.setVisibility(View.GONE);

        TV_FORGOTPWD = (TextView) findViewById(R.id.tvForgotPwd);

        BTN_LOGIN = (Button) findViewById(R.id.btnLogin);
        BTN_INFO = (Button) findViewById(R.id.btnShowInfo);
        BTN_SHOWINFOINSIDE = (Button) findViewById(R.id.btnShowInfoInside);

//        tvTitle1 = (TextView) findViewById(R.id.tvTitle1);
//        tvTitle2 = (TextView) findViewById(R.id.tvTitle2);
//        tvTitle3 = (TextView) findViewById(R.id.tvTitle3);
//        tvTitle4 = (TextView) findViewById(R.id.tvTitle4);
//        ivThumb1 = (ImageView) findViewById(R.id.ivThumb1);
//        ivThumb2 = (ImageView) findViewById(R.id.ivThumb2);
//        ivThumb3 = (ImageView) findViewById(R.id.ivThumb3);
//        ivThumb4 = (ImageView) findViewById(R.id.ivThumb4);
        lstmenu= (ListView) findViewById(R.id.listmenuinfo);

        infos=new ArrayList<>();

        // supported by image show logo
        imagesupport= (ImageView) findViewById(R.id.imgsuportedby_login);
        String a= getResources().getConfiguration().locale.getDisplayLanguage();

        if (a.equals("English")){
            imagesupport.setImageResource(R.drawable.tryglogo_eng);
        }else {
            imagesupport.setImageResource(R.drawable.tryglogo);
        }

//        tvTitle1.setOnClickListener(this);
//        tvTitle2.setOnClickListener(this);
//        tvTitle3.setOnClickListener(this);
//        tvTitle4.setOnClickListener(this);
//        ivThumb1.setOnClickListener(this);
//        ivThumb2.setOnClickListener(this);
//        ivThumb3.setOnClickListener(this);
//        ivThumb4.setOnClickListener(this);

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
                                    pbHelpVideo=new ProgressBar(getApplicationContext());

                                  if (infos.size()==0) {
                                      getHelpinfo();
                                  }

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


        BTN_SIGNUP = (Button) findViewById(R.id.btnSignUp);
        BTN_LOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LoginActivity_1.class));
                LoginActivity.this.finish();
            }
        });

        BTN_SIGNUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                LoginActivity.this.finish();
            }
        });

        TV_FORGOTPWD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPwdActivity.class));
                LoginActivity.this.finish();
            }
        });


        lstmenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listHelpVideos != null && listHelpVideos.size() > 2) {
                    watchVideoOnYouTube(listHelpVideos.get(2).getVideoLink());

                }
            }
        });

    }

    private void getHelpinfo() {
        pbHelpVideo.setVisibility(View.VISIBLE);
        try {
            new General().getJSONContentFromInternetService(LoginActivity.this, General.PHPServices.GET_HELP_INFO, new HashMap<String, String>(), true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    pbHelpVideo.setVisibility(View.GONE);
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.i(":::Help Videos", "" + response);
                    try {
                        listHelpVideos = new Gson().fromJson(response.getJSONArray(Constant.DATA).toString(), new TypeToken<ArrayList<HelpVideos>>() {
                        }.getType());


                        infos.clear();
                        for (int i=0;i<listHelpVideos.size();i++){
                            info in=new info(listHelpVideos.get(i).getVideoLink(),listHelpVideos.get(i).getVideoTitle());
                            infos.add(in);
                        }

                        getHelpinfoadapter adapter=new getHelpinfoadapter(getApplicationContext(),infos);
                        lstmenu.setAdapter(adapter);


//                        tvTitle1.setText(listHelpVideos.get(0).getVideoTitle());
//                        tvTitle2.setText(listHelpVideos.get(1).getVideoTitle());
//                        tvTitle3.setText(listHelpVideos.get(2).getVideoTitle());
//                        tvTitle4.setText(listHelpVideos.get(3).getVideoTitle());
//                        Picasso.with(LoginActivity.this)
//                                .load("http://img.youtube.com/vi/" + listHelpVideos.get(0).getVideoLink() + "/1.jpg")
//                                .into(ivThumb1);
//                        Picasso.with(LoginActivity.this)
//                                .load("http://img.youtube.com/vi/" + listHelpVideos.get(1).getVideoLink() + "/1.jpg")
//                                .into(ivThumb2);
//                        Picasso.with(LoginActivity.this)
//                                .load("http://img.youtube.com/vi/" + listHelpVideos.get(2).getVideoLink() + "/1.jpg")
//                                .into(ivThumb3);
//                        Picasso.with(LoginActivity.this)
//                                .load("http://img.youtube.com/vi/" + listHelpVideos.get(3).getVideoLink() + "/1.jpg")
//                                .into(ivThumb4);
                        pbHelpVideo.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },"");
        } catch (Exception e) {
            pbHelpVideo.setVisibility(View.GONE);
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
//            case R.id.tvTitle1:
//            case R.id.ivThumb1:
//                if (listHelpVideos != null && listHelpVideos.size() > 0) {
//                    watchVideoOnYouTube(listHelpVideos.get(0).getVideoLink());
//
//                }
//                break;
//            case R.id.tvTitle2:
//            case R.id.ivThumb2:
//                if (listHelpVideos != null && listHelpVideos.size() > 1) {
//                    watchVideoOnYouTube(listHelpVideos.get(1).getVideoLink());
//
//                }
//                break;
//            case R.id.tvTitle3:
//            case R.id.ivThumb3:
//                if (listHelpVideos != null && listHelpVideos.size() > 2) {
//                    watchVideoOnYouTube(listHelpVideos.get(2).getVideoLink());
//
//                }
//                break;
//            case R.id.tvTitle4:
//            case R.id.ivThumb4:
//                if (listHelpVideos != null && listHelpVideos.size() > 3) {
//                    watchVideoOnYouTube(listHelpVideos.get(3).getVideoLink());
//
//                }
//                break;
        }
    }


    public void autoScreenTracking(){
        TpaConfiguration config =
                new TpaConfiguration.Builder("d3baf5af-0002-4e72-82bd-9ed0c66af31c", "https://weiswise.tpa.io/")
                        // other config settings
                        .enableAutoTrackScreen(true)
                        .build();
    }
    @Override
    public void onResume() {
        super.onResume();
        AppLifeCycle.getInstance().resumed(this);
        chekcVesion();
    }

    @Override
    public void onPause() {
        super.onPause();
        AppLifeCycle.getInstance().paused(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        AppLifeCycle.getInstance().stopped(this);
    }
    private void chekcVesion() {
        TpaConfiguration config =
                new TpaConfiguration.Builder("d3baf5af-0002-4e72-82bd-9ed0c66af31c", "https://weiswise.tpa.io/")
                        .setLogType(TpaLog.Type.BOTH)           // Default
                        .setCrashHandling(CrashHandling.SILENT) // Default
                        .enableAnalytics(true)                 // Default
                        .useShakeFeedback(false, null)          // Default
                        .updateInterval(60)                     // Default
                        .useApi14(true)                         // Default
                        .build();

        TPA.initialize(this, config);
    }




   }




