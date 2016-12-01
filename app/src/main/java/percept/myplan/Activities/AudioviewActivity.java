package percept.myplan.Activities;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import percept.myplan.Dialogs.dialogOk;
import percept.myplan.R;

public class AudioviewActivity extends AppCompatActivity {

    private Button btnstart,btnpuase,btnstop;
    private TextView txtaudio;
    final MediaPlayer mediaPlayer=new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audioview);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.app_name));


        btnstart= (Button) findViewById(R.id.btn_audiostart);
        btnpuase= (Button) findViewById(R.id.btn_audiopuase);
        btnstop= (Button) findViewById(R.id.btn_audiostop);
        txtaudio=(TextView)findViewById(R.id.txt_audio);

        String audio=getIntent().getStringExtra("Audio");


        try{
           mediaPlayer.setDataSource(audio);
            mediaPlayer.prepare();
        }catch (IOException e) {
            e.printStackTrace();

            dialogOk _dialoglert=new dialogOk(AudioviewActivity.this, getString(R.string.audioerror)) {
                @Override
                public void onClickOk() {
                    dismiss();
                    AudioviewActivity.this.finish();
                }
            };
            _dialoglert.setCancelable(false);
            _dialoglert.setCanceledOnTouchOutside(false);
            _dialoglert.show();


        }catch (Exception es){
            es.printStackTrace();
        }

        txtaudio.setText(audio);


        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             mediaPlayer.start();
            }
        });


        btnpuase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               mediaPlayer.pause();

            }
        });


        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        if (item.getItemId()==android.R.id.home){
            AudioviewActivity.this.finish();
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
        }
        return false;
    }
}
