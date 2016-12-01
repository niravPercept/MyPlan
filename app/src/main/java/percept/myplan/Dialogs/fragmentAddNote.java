package percept.myplan.Dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import percept.myplan.R;

/**
 * Created by percept on 16/7/16.
 */

public class fragmentAddNote extends DialogFragment {

    private EditText EDT_NOTE;
    private Button BTN_SAVENOTE;

    void fragmentAddNote() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View _view = inflater.inflate(R.layout.lay_moodratings_savenote, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        EDT_NOTE = (EditText) _view.findViewById(R.id.edtNote);
        BTN_SAVENOTE = (Button) _view.findViewById(R.id.btnSaveNote);


        BTN_SAVENOTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _intent = getActivity().getIntent();
                _intent.putExtra("NOTE", EDT_NOTE.getText().toString());
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, _intent);
                fragmentAddNote.this.dismiss();
            }
        });
        return _view;
    }

}
