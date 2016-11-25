package percept.myplan.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import percept.myplan.Activities.AddEmergencyRoomActivity;
import percept.myplan.Activities.EmergencyRoomDetailActivity;
import percept.myplan.Activities.HomeActivity;
import percept.myplan.Dialogs.dialogOk;
import percept.myplan.Global.Constant;
import percept.myplan.Global.General;
import percept.myplan.Global.Utils;
import percept.myplan.Interfaces.VolleyResponseListener;
import percept.myplan.POJO.NearestEmergencyRoom;
import percept.myplan.R;
import percept.myplan.adapters.AutoCompleteLocalSearchAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentNearestEmergencyRoom extends Fragment {

    public static final int INDEX = 9;
    public static boolean RELOAD_MAP = false;
    private final int REQ_CODE_ADD_ROOM = 392;
    MapView mMapView;
    private GoogleMap googleMap;
    private HomeActivity activity;
    private AutoCompleteTextView edtLocationSearch;
    private TextView mTitle;
    private ArrayList<NearestEmergencyRoom> listNearestEmergencyRoom = new ArrayList<>();
    private Button btnNearestDistance;
    private AutoCompleteLocalSearchAdapter adapter;
    private Utils utils;


    public fragmentNearestEmergencyRoom() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the lay_help_info for this fragment
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.nearest_emergency_room));
        activity = (HomeActivity) getActivity();

        View _view = inflater.inflate(R.layout.fragment_nearest_emergency_room, container, false);

        Toolbar toolbar = activity.toolbar;
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setVisibility(View.GONE);
        edtLocationSearch = (AutoCompleteTextView) toolbar.findViewById(R.id.edtLocationSearch);
        edtLocationSearch.setVisibility(View.VISIBLE);
        utils = new Utils(getActivity());
        mMapView = (MapView) _view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        btnNearestDistance = (Button) _view.findViewById(R.id.btnNearestDistance);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!utils.getBoolPref(Constant.PREF_LOCATION)) {
            new dialogOk(getActivity()) {
                @Override
                public void onClickOk() {
                    dismiss();
                    ((HomeActivity) getActivity()).selectItem(fragmentSettings.INDEX);
                }
            }.show();
        }
        edtLocationSearch.setText("");
        setHasOptionsMenu(true);
        btnNearestDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listNearestEmergencyRoom != null && listNearestEmergencyRoom.size() > 0) {
                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus())
                            ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    Intent _intent = new Intent(getActivity(), EmergencyRoomDetailActivity.class);
                    _intent.putExtra("EMERGENCY_ROOM_DETAIL", listNearestEmergencyRoom.get(0));
                    startActivity(_intent);
                }
            }
        });
        adapter = new AutoCompleteLocalSearchAdapter(getActivity(), listNearestEmergencyRoom);
        edtLocationSearch.setAdapter(adapter);
        edtLocationSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(":::::::Info", "Position of AutoComplete:" + i + " Name of Array:" +
                        ((NearestEmergencyRoom) adapter.getItem(i)).getRoomName());
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus())
                        ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                Intent _intent = new Intent(getActivity(), EmergencyRoomDetailActivity.class);
                _intent.putExtra("EMERGENCY_ROOM_DETAIL", adapter.getItem(i));
                startActivity(_intent);
            }
        });

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
              /*  if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    activity.buildGoogleApiClient();

                }*/
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION }, 1);
                }
               // googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map

//                googleMap.addMarker(new MarkerOptions().position(_CurrentPos).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                if (activity.isGPSEnabled()) {
                    getNearestEmergencyRoom();
                } else activity.buildGoogleApiClient();
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        NearestEmergencyRoom emergencyRoom = (NearestEmergencyRoom) marker.getTag();
                        View view = View.inflate(getActivity(), R.layout.lay_marker_infowindow, null);
                        ImageView ivRoomThumb = (ImageView) view.findViewById(R.id.ivRoomThumb);
                        Log.e("Image thumb","Image Thumb="+emergencyRoom.getImageThumb());
                       // Picasso.with(getActivity()).load(emergencyRoom.getImageThumb()).into(ivRoomThumb);
                        TextView tvEmergencyRoomName = (TextView) view.findViewById(R.id.tvEmergencyRoomName);
                        tvEmergencyRoomName.setText(emergencyRoom.getRoomName());
                        TextView tvEmergencyRoomNo = (TextView) view.findViewById(R.id.tvEmergencyRoomNo);
                        tvEmergencyRoomNo.setText(emergencyRoom.getPhone());
                        return view;
                    }
                });
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent _intent = new Intent(getActivity(), EmergencyRoomDetailActivity.class);
                        _intent.putExtra("EMERGENCY_ROOM_DETAIL", (NearestEmergencyRoom) marker.getTag());
                        startActivity(_intent);
//                        marker.hideInfoWindow();
                    }
                });

            }
        });
        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (RELOAD_MAP) {
            RELOAD_MAP = false;
            if (activity.isGPSEnabled()) {
                googleMap.clear();
                getNearestEmergencyRoom();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mTitle.setVisibility(View.VISIBLE);
        edtLocationSearch.setVisibility(View.GONE);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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
            startActivityForResult(new Intent(getActivity(), AddEmergencyRoomActivity.class), REQ_CODE_ADD_ROOM);

            return true;
        }
        return false;
    }

    public void getNearestEmergencyRoom() {
        HashMap<String, String> params = new HashMap<>();
        params.put("sid", Constant.SID);
        params.put("sname", Constant.SNAME);
        params.put("lat", String.valueOf(activity.CURRENT_LAT));
        params.put("long", String.valueOf(activity.CURRENT_LONG));
        try {
            new General().getJSONContentFromInternetService(getActivity(), General.PHPServices.GET_EMERGENCY_ROOMS,
                    params, true, false, true, new VolleyResponseListener() {
                        @Override
                        public void onError(VolleyError message) {

                        }

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(":::Nearest Room::", response.toString());
                            Gson gson = new Gson();
                            try {
                                listNearestEmergencyRoom = gson.fromJson(response.getJSONArray(Constant.DATA).toString(),
                                        new TypeToken<ArrayList<NearestEmergencyRoom>>() {
                                        }.getType());
                                if (listNearestEmergencyRoom.size() > 0) {
                                    addMarkersOnMap();
                                    adapter = new AutoCompleteLocalSearchAdapter(getActivity(), listNearestEmergencyRoom);
                                    edtLocationSearch.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMarkersOnMap() {
        if (listNearestEmergencyRoom.size() > 0) {
//            if (Double.parseDouble(listNearestEmergencyRoom.get(0).getDistance()) > 0)
//                btnNearestDistance.setText(getString(R.string.distance_nearest) + new DecimalFormat("0.00").
//                        format(Double.parseDouble(listNearestEmergencyRoom.get(0).getDistance())) + " " + getString(R.string.km));
//            else if (listNearestEmergencyRoom.size() > 1)
//                btnNearestDistance.setText(getString(R.string.distance_nearest) + new DecimalFormat("0.00").
//                        format(Double.parseDouble(listNearestEmergencyRoom.get(1).getDistance())) + " " + getString(R.string.km));
//            else
//                btnNearestDistance.setText(getString(R.string.nearest_emergency));
            btnNearestDistance.setText(getString(R.string.distance_nearest) +
                    new DecimalFormat("0.00").format(Double.parseDouble(listNearestEmergencyRoom.get(0).
                            getDistance())) + " " + getString(R.string.km));
//            Collections.sort(listNearestEmergencyRoom, new Comparator<NearestEmergencyRoom>() {
//                @Override
//                public int compare(NearestEmergencyRoom nearestEmergencyRoom, NearestEmergencyRoom nearestEmergencyRoom1) {
//                    return nearestEmergencyRoom1.getDistance().compareTo(nearestEmergencyRoom.getDistance());
//                }
//            });

            for (NearestEmergencyRoom emergencyRoom : listNearestEmergencyRoom) {
                LatLng latLng = new LatLng(Double.parseDouble(emergencyRoom.getLatitude()),
                        Double.parseDouble(emergencyRoom.getLongitude()));
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                marker.setTag(emergencyRoom);

            }
            if (TextUtils.isEmpty(listNearestEmergencyRoom.get(0).getLatitude()) || TextUtils.isEmpty(listNearestEmergencyRoom.get(0).getLatitude()))
                return;
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(listNearestEmergencyRoom.get(0).getLatitude()),
                    Double.parseDouble(listNearestEmergencyRoom.get(0).getLongitude()))).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } else {
            LatLng _CurrentPos = new LatLng(activity.CURRENT_LAT, activity.CURRENT_LONG);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(_CurrentPos).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            btnNearestDistance.setText(getString(R.string.no_emergency_room));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_ADD_ROOM:
                getNearestEmergencyRoom();
                break;

        }
    }

}
