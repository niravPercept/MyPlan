package percept.myplan.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
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

import percept.myplan.Activities.AddContactActivity;
import percept.myplan.Activities.AddContactDetailActivity;
import percept.myplan.Activities.CreateQuickMsgActivity;
import percept.myplan.Activities.EmergencyContactActivity;
import percept.myplan.Activities.HelpListEditActivity;
import percept.myplan.CustomListener.RecyclerTouchListener;
import percept.myplan.Dialogs.dialogYesNoOption;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.ClickListener;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.ContactDisplay;
import percept.myplan.R;
import percept.myplan.adapters.ContactHelpListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentContacts extends Fragment {

    public static final int INDEX = 3;
    public static List<ContactDisplay> LIST_HELPCONTACTS;
    public static List<ContactDisplay> LIST_CONTACTS;
    public static HashMap<String, String> CONTACT_NAME = new HashMap<>();
    public static HashMap<String, String> HELP_CONTACT_NAME;
    public static String EMERGENCY_CONTACT_NAME;
    public static boolean GET_CONTACTS = false;
    final private int REQUEST_CODE_CALL_PERMISSIONS = 123;
    private TextView TV_EMERGENCYNO, TV_EDIT_EMERGENCYNO, TV_EDIT_HELPLIST, TV_ADDCONTACT;
    private RecyclerView LST_HELP, LST_CONTACTS;
    private List<ContactDisplay> LIST_ALLCONTACTS;
    private ContactHelpListAdapter ADPT_CONTACTHELPLIST;
    private ContactHelpListAdapter ADPT_CONTACTLIST;
    private Utils UTILS;
    private CoordinatorLayout REL_COORDINATE;
    private ProgressDialog mProgressDialog;


    public fragmentContacts() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the lay_help_info for this fragment
        View _View = inflater.inflate(R.layout.fragment_contacts, container, false);


        TV_EMERGENCYNO = (TextView) _View.findViewById(R.id.tvEmergencyNo);
        TV_EDIT_EMERGENCYNO = (TextView) _View.findViewById(R.id.tvEditEmergencyContact);
        TV_EDIT_HELPLIST = (TextView) _View.findViewById(R.id.tvEditHelpList);
        TV_ADDCONTACT = (TextView) _View.findViewById(R.id.tvAddContact);

        LST_HELP = (RecyclerView) _View.findViewById(R.id.lstHelpList);
        LST_CONTACTS = (RecyclerView) _View.findViewById(R.id.lstContacts);

        REL_COORDINATE = (CoordinatorLayout) _View.findViewById(R.id.snakeBar);

        LIST_ALLCONTACTS = new ArrayList<>();
        LIST_HELPCONTACTS = new ArrayList<>();
        LIST_CONTACTS = new ArrayList<>();
        CONTACT_NAME = new HashMap<>();
        HELP_CONTACT_NAME = new HashMap<>();
        UTILS = new Utils(getActivity());

        GET_CONTACTS = true;

        initSwipe(LST_CONTACTS);
        initSwipe(LST_HELP);

        ADPT_CONTACTHELPLIST = new ContactHelpListAdapter(LIST_HELPCONTACTS, "HELP");

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        LST_HELP.setLayoutManager(mLayoutManager);
        LST_HELP.setItemAnimator(new DefaultItemAnimator());
        LST_HELP.setAdapter(ADPT_CONTACTHELPLIST);


        ADPT_CONTACTLIST = new ContactHelpListAdapter(LIST_CONTACTS, "CONTACT");

        RecyclerView.LayoutManager mLayoutManagerContact = new LinearLayoutManager(getActivity());
        LST_CONTACTS.setLayoutManager(mLayoutManagerContact);
        LST_CONTACTS.setItemAnimator(new DefaultItemAnimator());
        LST_CONTACTS.setAdapter(ADPT_CONTACTLIST);
        setHasOptionsMenu(true);

        TV_EDIT_EMERGENCYNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(getActivity(), EmergencyContactActivity.class);
                startActivity(_intent);
            }
        });

        TV_EDIT_HELPLIST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(getActivity(), HelpListEditActivity.class);
                startActivity(_intent);
            }
        });

        TV_ADDCONTACT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(getActivity().getApplicationContext(), AddContactActivity.class);
                startActivity(_intent);
            }
        });
        LST_HELP.setNestedScrollingEnabled(false);
        LST_HELP.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), LST_HELP, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Toast.makeText(getActivity(), LIST_HELPCONTACTS.get(position).getFirst_name(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AddContactDetailActivity.class);
                intent.putExtra("IS_FOR_EDIT", true);
                intent.putExtra(Constant.HELP_COUNT, LIST_HELPCONTACTS.size());
                intent.putExtra("DATA", LIST_HELPCONTACTS.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        LST_CONTACTS.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), LST_CONTACTS, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                Toast.makeText(getActivity(), LIST_CONTACTS.get(position).getFirst_name(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AddContactDetailActivity.class);
                intent.putExtra("IS_FOR_EDIT", true);
                intent.putExtra("HELP_COUNT", LIST_HELPCONTACTS.size());
                intent.putExtra("DATA", LIST_CONTACTS.get(position));
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        TV_EMERGENCYNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCall();
            }
        });
        return _View;
    }

    private void GetContacts() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_loading));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        try {
            clearData();
            new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.GET_CONTACTS, params, true, false, true, new VolleyResponseListener() {
                @Override
                public void onError(VolleyError message) {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResponse(JSONObject response) {
                    Gson gson = new Gson();
                    try {
                        LIST_ALLCONTACTS = gson.fromJson(response.getJSONArray(Constant.DATA)
                                .toString(), new TypeToken<List<ContactDisplay>>() {
                        }.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (ContactDisplay _obj : LIST_ALLCONTACTS) {
                        if (_obj.getEmergency().equals("1")) {
                            if (!TextUtils.isEmpty(_obj.getFirst_name()))
                                TV_EMERGENCYNO.setText(_obj.getFirst_name());
                            else
                                TV_EMERGENCYNO.setText(_obj.getPhone());

                            UTILS.setPreference("EMERGENCY_CONTACT_NAME", _obj.getFirst_name());
                            UTILS.setPreference("EMERGENCY_CONTACT_NO", _obj.getPhone());
                        } else if (_obj.getHelplist().equals("0")) {
                            String name=_obj.getFirst_name()+_obj.getLast_name();
                            CONTACT_NAME.put(_obj.getId(), name);
                            LIST_CONTACTS.add(_obj);
                        } else {
                            String name=_obj.getFirst_name()+_obj.getLast_name();
                            HELP_CONTACT_NAME.put(_obj.getId(), name);
                            LIST_HELPCONTACTS.add(_obj);
                        }
                    }
                    if (TextUtils.isEmpty(TV_EMERGENCYNO.getText().toString())) {
                        TV_EMERGENCYNO.setText("112");
                    }
                    ADPT_CONTACTHELPLIST = new ContactHelpListAdapter(LIST_HELPCONTACTS, "HELP");
                    LST_HELP.setAdapter(ADPT_CONTACTHELPLIST);

                    ADPT_CONTACTLIST = new ContactHelpListAdapter(LIST_CONTACTS, "CONTACT");
                    LST_CONTACTS.setAdapter(ADPT_CONTACTLIST);
                    mProgressDialog.dismiss();
                }
            },"");
        } catch (Exception e) {
            mProgressDialog.dismiss();
            e.printStackTrace();
            Snackbar snackbar = Snackbar
                    .make(REL_COORDINATE, getResources().getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE)
                    .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GetContacts();
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

    private void clearData() {
        LIST_ALLCONTACTS.clear();
        LIST_CONTACTS.clear();
        LIST_HELPCONTACTS.clear();
        CONTACT_NAME.clear();
        HELP_CONTACT_NAME.clear();
    }

    @Override
    public void onResume() {
        super.onResume();


        if (GET_CONTACTS) {
            GetContacts();
            GET_CONTACTS = false;
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
            Intent _intent = new Intent(getActivity().getApplicationContext(), AddContactActivity.class);
            _intent.putExtra("HELP_COUNT", LIST_HELPCONTACTS.size());
            startActivity(_intent);

            return true;
        }
        return false;
    }

    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_CALL_PERMISSIONS);
        } else {
            String _phoneNo = "112";
            if (!TextUtils.isEmpty(new Utils(getActivity()).getPreference("EMERGENCY_CONTACT_NAME"))) {
                _phoneNo = new Utils(getActivity()).getPreference("EMERGENCY_CONTACT_NO");
            }
            try {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + _phoneNo));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    phoneIntent.setPackage("com.android.server.telecom");
                else
                    phoneIntent.setPackage("com.android.phone");
                startActivity(phoneIntent);

            } catch (ActivityNotFoundException e) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + _phoneNo));
                startActivity(phoneIntent);
            }
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

    private void initSwipe(final RecyclerView recyclerView) {
        final Paint p = new Paint();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
//                if (direction == ItemTouchHelper.LEFT){
//                    adapter.removeItem(position);
//                } else {
                // Snackbar.make(getView(), "Swipe Right", Snackbar.LENGTH_LONG).show();
//                }
                if (recyclerView == LST_CONTACTS)
                    deleteContact(position, false);
                else if (recyclerView == LST_HELP)
                    deleteContact(position, true);

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
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void deleteContact(final int position, final boolean isFromHelp) {
        dialogYesNoOption _dialog = new dialogYesNoOption(getActivity(), getString(R.string.delete_contact)) {

            @Override
            public void onClickYes() {
                if (!General.checkInternetConnection(getActivity()))
                    return;
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getString(R.string.progress_loading));
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                HashMap<String, String> params = new HashMap<>();
                params.put("sid", Constant.SID);
                params.put("sname", Constant.SNAME);
                if (isFromHelp)
                    params.put("id", LIST_HELPCONTACTS.get(position).getId());
                else
                    params.put("id", LIST_CONTACTS.get(position).getId());
                try {
                    new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.DELETE_CONTACT, params, true, false, true, new VolleyResponseListener() {
                        @Override
                        public void onError(VolleyError message) {
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            mProgressDialog.dismiss();
                            if (isFromHelp) {
                                LIST_HELPCONTACTS.remove(position);
                            } else {
                                LIST_CONTACTS.remove(position);
                            }
                            ADPT_CONTACTLIST.notifyDataSetChanged();
                            ADPT_CONTACTHELPLIST.notifyDataSetChanged();
                        }
                    },"");
                } catch (Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                }
                dismiss();


                findViewById(android.R.id.content).invalidate();
            }

            @Override
            public void onClickNo() {
                ADPT_CONTACTLIST.notifyDataSetChanged();
                ADPT_CONTACTHELPLIST.notifyDataSetChanged();
                dismiss();
            }
        };
        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);
        _dialog.show();
    }

}
