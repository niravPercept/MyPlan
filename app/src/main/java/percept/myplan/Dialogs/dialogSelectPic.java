package percept.myplan.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import percept.myplan.R;


/**
 * Created by percept on 2/5/15.
 */
public abstract class dialogSelectPic extends Dialog implements View.OnClickListener {
    private Context CONTEXT;
    private TextView TV_GALLERY, TV_CAMERA;

    public dialogSelectPic(Context context) {

        super(context, R.style.DialogTheme);
        this.CONTEXT = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_image);

        TV_GALLERY = (TextView) findViewById(R.id.txtPhoneGallery);
        TV_GALLERY.setOnClickListener(this);
        TV_CAMERA = (TextView) findViewById(R.id.txtCapture);
        TV_CAMERA.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtPhoneGallery:
                fromGallery();
                break;
            case R.id.txtCapture:
                fromCamera();
                break;


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    abstract public void fromGallery();
    abstract public void fromCamera();
}
