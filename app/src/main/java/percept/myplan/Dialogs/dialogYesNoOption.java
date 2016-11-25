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
public abstract class dialogYesNoOption extends Dialog implements View.OnClickListener {
    private Context CONTEXT;
    private TextView TV_YES, TV_NO, TV_DIALOGMSG;
    private String dialogMsg;

    public dialogYesNoOption(Context context) {
        super(context, R.style.DialogTheme);
        this.CONTEXT = context;
    }

    public dialogYesNoOption(Context context, String dialogMsg) {

        super(context, R.style.DialogTheme);
        this.CONTEXT = context;
        this.dialogMsg = dialogMsg;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_strategy);

        TV_DIALOGMSG = (TextView) findViewById(R.id.tvDialogMsg);
        if (!TextUtils.isEmpty(dialogMsg))
            TV_DIALOGMSG.setText(dialogMsg);

        TV_YES = (TextView) findViewById(R.id.tvYes);
        TV_YES.setOnClickListener(this);
        TV_NO = (TextView) findViewById(R.id.tvNo);
        TV_NO.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvYes:
                onClickYes();
                break;
            case R.id.tvNo:
                onClickNo();
                break;


        }
    }

    public abstract void onClickYes();

    public abstract void onClickNo();
}
