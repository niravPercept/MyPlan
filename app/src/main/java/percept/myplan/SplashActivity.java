package percept.myplan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import percept.myplan.Activities.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // supported by image show logo
        imageView= (ImageView) findViewById(R.id.imgsuportedby);
        String a= getResources().getConfiguration().locale.getDisplayLanguage();

        if (a.equals("English")){
            imageView.setImageResource(R.drawable.tryglogo_eng);
        }else {
            imageView.setImageResource(R.drawable.tryglogo);
        }

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //start your activity here
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                SplashActivity.this.finish();
            }

        }, 1000L);
    }

}
