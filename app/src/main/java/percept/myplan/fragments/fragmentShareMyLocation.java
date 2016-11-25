package percept.myplan.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

import percept.myplan.Activities.AddStrategyContactActivity;
import percept.myplan.Activities.HomeActivity;
import percept.myplan.Dialogs.dialogOk;
import percept.myplan.Global.Constant;
import percept.myplan.Global.Utils;
import percept.myplan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentShareMyLocation extends Fragment {

    public static final int INDEX = 8;
    MapView mMapView;
    private GoogleMap googleMap;
    private Button BTN_SHARELOC;
    private LatLng _CurrentPos;
    private HomeActivity activity;
    private Utils utils;

    public fragmentShareMyLocation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (HomeActivity) getActivity();
        // Inflate the lay_help_info for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Share My Location");
        View _view = inflater.inflate(R.layout.fragment_share_my_location, container, false);

        BTN_SHARELOC = (Button) _view.findViewById(R.id.btnShareMyLoc);
        mMapView = (MapView) _view.findViewById(R.id.mapView);
        utils = new Utils(getActivity());
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
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
        BTN_SHARELOC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = new Intent(getActivity(), AddStrategyContactActivity.class);
                _intent.putExtra("FROM_SHARELOC", "True");
                _intent.putExtra("CURRENT_LOCATION", _CurrentPos);
                startActivity(_intent);
            }
        });
        return _view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                googleMap.setMyLocationEnabled(false);
                if (activity.isGPSEnabled()) {
                    updateUI();
                } else {
                    activity.buildGoogleApiClient();
                }
            }
        });
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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void updateUI() {
        // For dropping a marker at a point on the Map
        _CurrentPos = new LatLng(HomeActivity.CURRENT_LAT, HomeActivity.CURRENT_LONG);
//                googleMap.addMarker(new MarkerOptions().position(_CurrentPos).title("Marker Title").snippet("Marker Description"));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(_CurrentPos).zoom(12).build();
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(_CurrentPos)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
