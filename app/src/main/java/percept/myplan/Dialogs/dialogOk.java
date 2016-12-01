package percept.myplan.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import percept.myplan.R;


/**
 * Created by percept on 2/5/15.
 */
public abstract class dialogOk extends Dialog {
    private Context CONTEXT;
    private TextView tvOk, TV_DIALOGMSG;
    private String dialogMsg;

    public dialogOk(Context context) {
        super(context, R.style.DialogTheme);
        this.CONTEXT = context;
    }

    public dialogOk(Context context, String dialogMsg) {

        super(context, R.style.DialogTheme);
        this.CONTEXT = context;
        this.dialogMsg = dialogMsg;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_ok);

        TV_DIALOGMSG = (TextView) findViewById(R.id.tvDialogMsg);
        if (!TextUtils.isEmpty(dialogMsg))
            TV_DIALOGMSG.setText(dialogMsg);
        tvOk = (TextView) findViewById(R.id.tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOk();
            }
        });


    }



    public abstract void onClickOk();
}
