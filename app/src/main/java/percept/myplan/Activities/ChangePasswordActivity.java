package percept.myplan.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.tpa.tpalib.TpaConfiguration;
import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.R;

/**
 * Created by percept on 20/8/16.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private EditText EDT_OLD_PASSWORD, EDT_NEW_PASSWORD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUI();
    }

    private void initializeUI() {
        setContentView(R.layout.activity_change_password);

        autoScreenTracking();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.change_password));

        EDT_OLD_PASSWORD = (EditText) findViewById(R.id.edtOldPassword);
        EDT_NEW_PASSWORD = (EditText) findViewById(R.id.edtNewPassword);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.change_password, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ChangePasswordActivity.this.finish();
            return true;
        } else if (item.getItemId() == R.id.action_change) {
            if (isValidate()) {
                Intent intent = new Intent();
                intent.putExtra("change_password", EDT_NEW_PASSWORD.getText().toString().trim());
                intent.putExtra("old_password", EDT_OLD_PASSWORD.getText().toString().trim());
                setResult(Activity.RESULT_OK,intent);
                ChangePasswordActivity.this.finish();
                return true;
            }
        }
        return false;
    }

    private boolean isValidate() {
        if (TextUtils.isEmpty(EDT_OLD_PASSWORD.getText().toString()) ||
                EDT_OLD_PASSWORD.getText().toString().length() < 4) {
//            EDT_OLD_PASSWORD.setError(getString(R.string.toast_old_password_invalid));
            Toast.makeText(this,getString(R.string.toast_old_password_invalid),Toast.LENGTH_LONG);
            EDT_OLD_PASSWORD.requestFocus();
            return false;

        }
        else if (TextUtils.isEmpty(EDT_NEW_PASSWORD.getText().toString()) ||
                EDT_NEW_PASSWORD.getText().toString().length() < 4){
//            EDT_NEW_PASSWORD.setError(getString(R.string.toast_new_password_invalid));
            Toast.makeText(this,getString(R.string.toast_new_password_invalid),Toast.LENGTH_LONG);
            EDT_NEW_PASSWORD.requestFocus();
            return false;
        }
    return true;
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
