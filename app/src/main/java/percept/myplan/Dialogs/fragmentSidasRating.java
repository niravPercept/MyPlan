package percept.myplan.Dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import percept.myplan.R;

/**
 * Created by percept on 16/7/16.
 */

public class fragmentSidasRating extends DialogFragment {

    private TextView TV_SIDAPOINTS;
    private SeekBar SEEK_SIDA;

    public static fragmentSidasRating newInstance() {
        fragmentSidasRating fragment = new fragmentSidasRating();
        // Supply num input as an argument.
        Bundle args = new Bundle();
//        args.putString("NOTE", note);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View _view = inflater.inflate(R.layout.lay_moodrating_sidas, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TV_SIDAPOINTS = (TextView) _view.findViewById(R.id.tvSidaPoints);
        SEEK_SIDA = (SeekBar) _view.findViewById(R.id.seekBarSidas);

        SEEK_SIDA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                TV_SIDAPOINTS.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return _view;
    }
}
