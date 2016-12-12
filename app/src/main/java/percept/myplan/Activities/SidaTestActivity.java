package percept.myplan.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Dialogs.dialogYesNoOption;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.ContactDisplay;
import percept.myplan.POJO.SidaQuestion;
import percept.myplan.R;

public class SidaTestActivity extends AppCompatActivity {

    final private int REQUEST_CODE_CALL_PERMISSIONS = 123;
    Map<String, String> params;
    private TextView TV_TESTQUESTION, TV_TESTANSWER;
    private List<SidaQuestion> LST_SIDAQUES;
    private SeekBar SEEK_SIDA;
    private Button BTN_NEXT_QUES;
    private int CURR_QUES = 0, TOTAL_QUES = 0;
    private String STRANSWER = "";
    private ProgressDialog mProgressDialog;
    private CoordinatorLayout REL_COORDINATE;
    private TextView tvLabelLeft, tvLabelRight;
    private Utils UTILS;
    private String _phoneNo = "112";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sida_test);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.sidasfulltest));

        TV_TESTQUESTION = (TextView) findViewById(R.id.tvQuestions);
        TV_TESTANSWER = (TextView) findViewById(R.id.tvSidaPoints);
        BTN_NEXT_QUES = (Button) findViewById(R.id.btnNextQues);
        tvLabelLeft = (TextView) findViewById(R.id.tvLabelLeft);
        tvLabelRight = (TextView) findViewById(R.id.tvLabelRight);
        UTILS = new Utils(SidaTestActivity.this);
        REL_COORDINATE = (CoordinatorLayout) findViewById(R.id.snakeBar);

        SEEK_SIDA = (SeekBar) findViewById(R.id.seekBarSidas);
       /* Drawable thumb = getResources().getDrawable(R.drawable.thumb_image);
        thumb.setBounds(0,0,thumb.getIntrinsicWidth(),
                thumb.getIntrinsicHeight());
        SEEK_SIDA.setThumb(thumb);
*/
        SEEK_SIDA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                TV_TESTANSWER.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        GetSidaTest();
        InfoDialog _dialog = new InfoDialog(SidaTestActivity.this,
                getString(R.string.test_start_warning_msg), getString(R.string.cont), "") {
            @Override
            public void onClickFirstButton() {
                super.onClickFirstButton();

            }
        };
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.setCancelable(false);
        _dialog.show();


        BTN_NEXT_QUES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CURR_QUES + 1 < TOTAL_QUES) {
                    CURR_QUES = CURR_QUES + 1;
                    TV_TESTQUESTION.setText(LST_SIDAQUES.get(CURR_QUES).getQuestion());
                    String arr[] = TextUtils.split(LST_SIDAQUES.get(CURR_QUES).getLabelLeft(), " - ");
                    tvLabelLeft.setText(arr[1]);
                    arr = TextUtils.split(LST_SIDAQUES.get(CURR_QUES).getLabelRight(), " - ");
                    tvLabelRight.setText(arr[1]);
                    if (CURR_QUES + 1 == TOTAL_QUES)
                        BTN_NEXT_QUES.setText(getString(R.string.complete_test));// +
//                                " (" + String.valueOf(CURR_QUES + 1) + "/" + String.valueOf(TOTAL_QUES) + ")");
                    else
                        BTN_NEXT_QUES.setText(getResources().getString(R.string.nextques) +
                                " (" + String.valueOf(CURR_QUES + 1) + "/" + String.valueOf(TOTAL_QUES) + ")");
                    if (STRANSWER.equals(""))
                        STRANSWER = TV_TESTANSWER.getText().toString();
                    else
                        STRANSWER += "," + TV_TESTANSWER.getText().toString();
                } else {
                    SubmitSidaTest();

                }
                SEEK_SIDA.setProgress(0);
            }
        });
    }

    private void SubmitSidaTest() {
        try {
            if (!General.checkInternetConnection(SidaTestActivity.this))
                return;
            STRANSWER += "," + TV_TESTANSWER.getText().toString();
            params.put("answer", STRANSWER);
            mProgressDialog = new ProgressDialog(SidaTestActivity.this);
            mProgressDialog.setMessage(getString(R.string.progress_loading));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            new General().getJSONContentFromInternetService(SidaTestActivity.this, General.PHPServices.SUBMIT_SIDATEST, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Log.i(":::SidasTest::", response.toString());
                    mProgressDialog.dismiss();
                    try {
                        int score= response.getJSONObject(Constant.DATA).getInt("score");
                       // int score=20;
                        if (score >=15 && score < 21) {
                            InfoDialog infoDialog = new InfoDialog(SidaTestActivity.this,getString(R.string.test_result_btwn_15to21) ,
                                    getString(R.string.help), getString(R.string.inspiration)) {


                                @Override
                                public void onClickFirstButton() {
//                                    Intent _intent = new Intent(SidaTestActivity.this, HelpListActivity.class);
//                                    startActivity(_intent);
                                    Intent _intent = new Intent(SidaTestActivity.this, HelpListActivity.class);
                                    startActivity(_intent);
                                }

                                @Override
                                public void onClickSecondButton() {
//                                    Intent _iIntent = new Intent();
//                                    _iIntent.putExtra("SIDAS_OPEN_STRATEGIES", true);
//                                    setResult(RESULT_OK, _iIntent);
                                    Intent _intent = new Intent(SidaTestActivity.this, InspirationCategoryActivity.class);
                                    startActivity(_intent);
                                    SidaTestActivity.this.finish();
                                }
                            };
                            infoDialog.setCanceledOnTouchOutside(true);
                            infoDialog.show();
                            infoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {

                                }
                            });
                        } else if (score >= 21) {
                            InfoDialog infoDialog = new InfoDialog(SidaTestActivity.this, getString(R.string.test_result_greater_21),
                                    getString(R.string.help), getString(R.string.emergency)) {

                                @Override
                                public void onClickFirstButton() {

                                    Intent _intent = new Intent(SidaTestActivity.this, HelpListActivity.class);
                                    startActivity(_intent);
                                    SidaTestActivity.this.finish();
                                }

                                @Override
                                public void onClickSecondButton() {
                                    getEmergencyContact();
                                }
                            };
                            infoDialog.setCanceledOnTouchOutside(true);
                            infoDialog.show();

                            infoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {

                                }
                            });
                        } else {
                            InfoDialog infoDialog = new InfoDialog(SidaTestActivity.this, getString(R.string.test_result_less_15),
                                    getString(R.string.ok), "") {
                                @Override
                                public void onClickFirstButton() {
                                    super.onClickFirstButton();
                                    SidaTestActivity.this.finish();
                                }
                            };
                            infoDialog.setCanceledOnTouchOutside(true);
                            infoDialog.show();
                            infoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {

                                }
                            });
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


//                    SidaTestActivity.this.finish();
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
                            SubmitSidaTest();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void GetSidaTest() {
        mProgressDialog = new ProgressDialog(SidaTestActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        try {
           // Locale.getDefault().getLanguage()

            new General().getJSONContentFromInternetService(SidaTestActivity.this, General.PHPServices.GET_SIDATEST, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {

                    Gson gson = new Gson();
                    try {
                        LST_SIDAQUES = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<SidaQuestion>>() {
                        }.getType());
                        TOTAL_QUES = LST_SIDAQUES.size();
//                        Collections.sort(LST_SIDAQUES, new Comparator<SidaQuestion>() {
//                            @Override
//                            public int compare(SidaQuestion sidaQuestion, SidaQuestion t1) {
//                                return sidaQuestion.compareTo(t1);
//                            }
//                        });
//                        Log.i("SortedList",gson.toJsonTree(LST_SIDAQUES).getAsJsonArray().toString());
                        CURR_QUES = 0;
                        TV_TESTQUESTION.setText(LST_SIDAQUES.get(CURR_QUES).getQuestion());
                        String arr[] = TextUtils.split(LST_SIDAQUES.get(CURR_QUES).getLabelLeft(), " - ");
                        tvLabelLeft.setText(arr[1]);
                        arr = TextUtils.split(LST_SIDAQUES.get(CURR_QUES).getLabelRight(), " - ");
                        tvLabelRight.setText(arr[1]);
                        BTN_NEXT_QUES.setText(getResources().getString(R.string.nextques) +
                                "(" + String.valueOf(CURR_QUES + 1) + "/" + String.valueOf(TOTAL_QUES) + ")");
                        mProgressDialog.dismiss();
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
                            GetSidaTest();
                        }
                    });
            snackbar.setActionTextColor(Color.RED);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SidaTestActivity.this.finish();
            return true;
        }
        return false;
    }

    private void getEmergencyContact() {
        mProgressDialog = new ProgressDialog(SidaTestActivity.this);
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("helplist", "2");
        try {
            new General().getJSONContentFromInternetService(SidaTestActivity.this, General.PHPServices.GET_CONTACTS, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        mProgressDialog.dismiss();
                        if (response.getJSONArray(Constant.DATA) != null && response.getJSONArray(Constant.DATA).length() > 0) {
                            ContactDisplay _contactDisplay = new Gson().fromJson(response.getJSONArray(Constant.DATA).get(0)
                                    .toString(), new TypeToken<ContactDisplay>() {
                            }.getType());
                            UTILS.setPreference("EMERGENCY_CONTACT_NAME", _contactDisplay.getFirst_name());
                            UTILS.setPreference("EMERGENCY_CONTACT_NO", _contactDisplay.getPhone());
                            onCall();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },"_2");
        } catch (Exception e) {
            mProgressDialog.dismiss();
            e.printStackTrace();
        }
    }

    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(SidaTestActivity.this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SidaTestActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_CALL_PERMISSIONS);
        } else {

            if (!TextUtils.isEmpty(new Utils(SidaTestActivity.this).getPreference("EMERGENCY_CONTACT_NAME"))) {
                _phoneNo = new Utils(SidaTestActivity.this).getPreference("EMERGENCY_CONTACT_NO");
            }
            new dialogYesNoOption(SidaTestActivity.this, getString(R.string.confimation_call) + "\n" + _phoneNo) {
                @Override
                public void onClickYes() {
                    dismiss();
                    try {
                        Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + _phoneNo));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            phoneIntent.setPackage("com.android.server.telecom");
                        else
                            phoneIntent.setPackage("com.android.phone");
                        if (ActivityCompat.checkSelfPermission(SidaTestActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SidaTestActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    REQUEST_CODE_CALL_PERMISSIONS);
                            return;
                        }
                        startActivity(phoneIntent);
                        SidaTestActivity.this.finish();

                    } catch (ActivityNotFoundException e) {
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + _phoneNo));
                        startActivity(phoneIntent);

                    }
                }

                @Override
                public void onClickNo() {
                    dismiss();
                    SidaTestActivity.this.finish();
                }
            }.show();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_CALL_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onCall();
                break;
        }
    }

    public class InfoDialog extends Dialog {

        private TextView TV_NOTE, tvInfoBtn1;
        private String note, strFistTitle, strSecondTitle;
        private TextView tvInfoBtn2;

        InfoDialog(Context context, String note, String strFistTitle, String strSecondTitle) {
            super(context, R.style.DialogTheme);
            this.note = note;
            this.strFistTitle = strFistTitle;
            this.strSecondTitle = strSecondTitle;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.lay_info);

            setCanceledOnTouchOutside(false);
            TV_NOTE = (TextView) findViewById(R.id.tvNote);
            tvInfoBtn1 = (TextView) findViewById(R.id.tvInfoBtn1);
            tvInfoBtn2 = (TextView) findViewById(R.id.tvInfoBtn2);
            View viewSeperator = findViewById(R.id.viewSeperator);
            TV_NOTE.setText(Html.fromHtml(note));
            tvInfoBtn1.setText(strFistTitle);

            if (TextUtils.isEmpty(strSecondTitle)) {
                tvInfoBtn2.setVisibility(View.GONE);
                viewSeperator.setVisibility(View.GONE);
            } else {
                tvInfoBtn2.setVisibility(View.VISIBLE);
                viewSeperator.setVisibility(View.VISIBLE);
            }
            tvInfoBtn2.setText(strSecondTitle);

            TV_NOTE.setMovementMethod(LinkMovementMethod.getInstance());
//            TV_NOTE.setText(Html.fromHtml("<b>" + "SIDAS" + "</b>" + " consist of 5 questions"
//                    + "<br />" + "<br />" + "<br />" + "You can use" + "<b>" + " SIDAS " + "</b>"
//                    + "to measure your suicidal thoughts." + "<br />" + "<br />" + "<br />" +
//                    "<b>" + "IMPORTANT " + "</b>" + "if you have a high note, you will get an automate " +
//                    "notification to seek help" + "<br />" + "<br />" + "<br />"
//                    + "It is important that you act and follow your crisis plan"));


            tvInfoBtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    onClickFirstButton();
                }
            });
            tvInfoBtn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                    onClickSecondButton();
                }
            });
        }

        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            super.setOnDismissListener(listener);
            //SidaTestActivity.this.finish();
        }

        public void onClickFirstButton() {
        }

        public void onClickSecondButton() {
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
}
