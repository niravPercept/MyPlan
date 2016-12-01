package percept.myplan.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import percept.myplan.Activities.FeedbackActivity;
import percept.myplan.Activities.SettingExportMyPlanActivity;
import percept.myplan.Activities.SettingGeoTrackingActivity;
import percept.myplan.Activities.SettingMoodRatingsActivity;
import percept.myplan.Activities.SettingNotificationActivity;
import percept.myplan.Activities.SettingProfileActivity;
import percept.myplan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentSettings extends Fragment {

    public static final int INDEX = 11;
    private TextView TV_PROFILE, TV_MOODRATINGS, TV_GEOTRACKING, TV_EXPORTMYPLAN, TV_NOTIFICATIONS, TV_FEEDBACK, TV_SHARE;

    public fragmentSettings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the lay_help_info for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
        View _View = inflater.inflate(R.layout.fragment_settings, container, false);
        TV_PROFILE = (TextView) _View.findViewById(R.id.tvProfile);
        TV_MOODRATINGS = (TextView) _View.findViewById(R.id.tvMoodRatings);
        TV_GEOTRACKING = (TextView) _View.findViewById(R.id.tvGeoTracking);
        TV_EXPORTMYPLAN = (TextView) _View.findViewById(R.id.tvExportPlan);
        TV_NOTIFICATIONS = (TextView) _View.findViewById(R.id.tvNotification);
        TV_FEEDBACK = (TextView) _View.findViewById(R.id.tvFeedBack);
        TV_SHARE = (TextView) _View.findViewById(R.id.tvShare);

        TV_PROFILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingProfileActivity.class));

            }
        });

        TV_MOODRATINGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingMoodRatingsActivity.class));
            }
        });

        TV_GEOTRACKING.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingGeoTrackingActivity.class));
            }
        });

        TV_EXPORTMYPLAN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingExportMyPlanActivity.class));
            }
        });

        TV_NOTIFICATIONS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingNotificationActivity.class));
            }
        });

        TV_FEEDBACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
            }
        });

        TV_SHARE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.share_body)));
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_on)));
            }
        });
        return _View;
    }

}
