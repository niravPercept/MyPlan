package percept.myplan.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.tpa.tpalib.lifecycle.AppLifeCycle;
import percept.myplan.Global.Constant;
import percept.myplan.POJO.HopeDetail;
import percept.myplan.POJO.StrategyDetails;
import percept.myplan.R;

public class AddStrategyMusicActivity extends AppCompatActivity {

    private final int SET_LINK = 25, REQ_CODE_MUSIC_LIST = 789;
    private TextView TV_CHOOSEFROMPHONE, TV_CHOOSEFROMLINK;
    private String FROM = "";
    private String HOPE_TITLE = "";
    private String HOPE_ID = "";
    private String HOPE_ELEMENT_ID = "";
    private boolean FROM_EDIT = false;
    private boolean HOPE=false;
    public List<String> listmusic; //change by ketan
    public List<String> listmusics;//change by ketan
    private RecyclerView rvMusic;
    private SelectedMusicListAdapter selectedMusicListAdapter;
    private  AddStrategyActivity  addlink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_strategy_music);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(getResources().getString(R.string.addmusic));

        if (getIntent().hasExtra("FROM_HOPE")) {
            FROM = getIntent().getExtras().getString("FROM_HOPE");
            HOPE_TITLE = getIntent().getExtras().getString("HOPE_TITLE");
            HOPE_ID = getIntent().getExtras().getString("HOPE_ID");
            if (getIntent().hasExtra(Constant.DATA)) {
                HopeDetail _Detail = (HopeDetail) getIntent().getExtras().getSerializable(Constant.DATA);
                HOPE_ELEMENT_ID = _Detail.getID();
            }
        }
        if (getIntent().hasExtra("FROM_EDIT")) {
            FROM_EDIT = true;
        }
        rvMusic = (RecyclerView) findViewById(R.id.rvMusic);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AddStrategyMusicActivity.this);
        rvMusic.setLayoutManager(mLayoutManager);
        rvMusic.setItemAnimator(new DefaultItemAnimator());
        TV_CHOOSEFROMPHONE = (TextView) findViewById(R.id.tvChooseFromPhone);
        TV_CHOOSEFROMLINK = (TextView) findViewById(R.id.tvChooseFromLink);
        TV_CHOOSEFROMPHONE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                startActivityForResult(i, 1);

//                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//                chooseFile.setType("audio/*");
//                chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                startActivityForResult(Intent.createChooser(chooseFile, "Choose a file") , 2);

                Intent _intent = new Intent(AddStrategyMusicActivity.this, MusicListActivity.class);

                if (getIntent().hasExtra("FROM_HOPE")) {
                    _intent.putExtra("HOPE_TITLE", HOPE_TITLE);
                    _intent.putExtra("FROM_HOPE", "FROM_HOPE");
                    _intent.putExtra("HOPE_ID", HOPE_ID);
                    _intent.putExtra("HOPE_ELEMENT_ID", HOPE_ELEMENT_ID);

                }
                if (getIntent().hasExtra("FROM_EDIT"))
                    _intent.putExtra("FROM_EDIT", "TRUE");


                startActivityForResult(_intent, REQ_CODE_MUSIC_LIST);
            }
        });

        TV_CHOOSEFROMLINK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AddStrategyMusicActivity.this, AddStrategyLinksActivity.class), SET_LINK);
            }
        });

        // change by ketan
        // before edit show strategies  music List
       /* if (StrategyDetailsOwnActivity.LISTMUSIC !=null ){

            selectedMusicListAdapter = new SelectedMusicListAdapter(StrategyDetailsOwnActivity.LISTMUSIC);
            rvMusic.setAdapter(selectedMusicListAdapter);
        }*/

     /*   if (StrategyEditActivity.LIST_MUSIC !=null ||StrategyDetailsOwnActivity.LISTMUSIC !=null){

            listmusics=new ArrayList<>();
            listmusics.addAll(StrategyEditActivity.LIST_MUSIC);
            listmusics.addAll(StrategyDetailsOwnActivity.LISTMUSIC);
           // selectedMusicListAdapter = new SelectedMusicListAdapter(StrategyEditActivity.LIST_MUSIC);
            selectedMusicListAdapter = new SelectedMusicListAdapter(listmusics);
            rvMusic.setAdapter(selectedMusicListAdapter);
        }
*/

        if(StrategyEditActivity.LIST_MUSIC != null){
            selectedMusicListAdapter = new SelectedMusicListAdapter(StrategyEditActivity.LIST_MUSIC);
            selectedMusicListAdapter = new SelectedMusicListAdapter(listmusic);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLifeCycle.getInstance().resumed(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SET_LINK:
            if (resultCode == RESULT_OK) {

                if (FROM.equals("") || FROM_EDIT) {
                    if (FROM_EDIT) {
                        listmusic = new ArrayList<>();
                        String ar = data.getStringExtra("LINK");
                        StrategyEditActivity.LIST_MUSIC.add(ar);
                        listmusic.addAll(StrategyEditActivity.LIST_MUSIC);

                        selectedMusicListAdapter = new SelectedMusicListAdapter(listmusic);
                        Log.d("strategylist", StrategyEditActivity.LIST_MUSIC.toString());
                        rvMusic.setAdapter(selectedMusicListAdapter);
                    } else {
                        String ar = data.getStringExtra("LINK");
                        AddStrategyActivity.LIST_MUSIC.add(ar);
                        selectedMusicListAdapter = new SelectedMusicListAdapter(AddStrategyActivity.LIST_MUSIC);
                        rvMusic.setAdapter(selectedMusicListAdapter);
                    }
                }
            }else AddStrategyMusicActivity.this.finish();
                   /* Intent returnIntent = new Intent();
                    returnIntent.putExtra("LINK1", data.getStringExtra("LINK"));
                    setResult(Activity.RESULT_OK, returnIntent);
                    AddStrategyMusicActivity.this.finish();*/
                //} else
                break;


//            case SET_LINK:
//                if (resultCode == RESULT_OK) {
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("LINK", data.getStringExtra("LINK"));
//                    setResult(Activity.RESULT_OK, returnIntent);
//
//                   // AddStrategyMusicActivity.this.finish();
//                } else AddStrategyMusicActivity.this.finish();
//                break;
            case REQ_CODE_MUSIC_LIST:
                if (resultCode == RESULT_OK) {// change Method
                    if (FROM.equals("") || FROM_EDIT) {
                        if (FROM_EDIT) {
                            listmusic=new ArrayList<>();
//                            listmusic.addAll(StrategyEditActivity.LIST_MUSIC);
                            listmusic.addAll(StrategyEditActivity.LIST_MUSIC);
                            //listmusic.addAll(StrategyDetailsOwnActivity.LISTMUSIC);

                            Log.d("startegyeditActivity",StrategyEditActivity.LIST_MUSIC.toString());
                            Log.d("strategydetailActivity",StrategyEditActivity.LIST_MUSIC.toString());

//                      selectedMusicListAdapter = new SelectedMusicListAdapter(StrategyEditActivity.LIST_MUSIC);
                            selectedMusicListAdapter = new SelectedMusicListAdapter(listmusic);
                            Log.d("strategylist",StrategyEditActivity.LIST_MUSIC.toString());
                            rvMusic.setAdapter(selectedMusicListAdapter);
                        } else {
//                            selectedMusicListAdapter = new SelectedMusicListAdapter(AddStrategyActivity.LIST_MUSIC);
                            selectedMusicListAdapter=new SelectedMusicListAdapter(AddStrategyActivity.LIST_MUSIC);
                            rvMusic.setAdapter(selectedMusicListAdapter);
                        }
                    }

                } else AddStrategyMusicActivity.this.finish();
                break;
            default:
                AddStrategyMusicActivity.this.finish();
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (FROM.equals("") || FROM_EDIT) {
                if (FROM_EDIT) {
                    if (StrategyEditActivity.LIST_MUSIC.size() > 10) {
                        Snackbar.make(getWindow().getDecorView(), getString(R.string.max_5_music_files), Snackbar.LENGTH_LONG).show();
                    } else AddStrategyMusicActivity.this.finish();
                } else {
                    if (AddStrategyActivity.LIST_MUSIC.size() > 10) {
                        Snackbar.make(getWindow().getDecorView(), getString(R.string.max_5_music_files), Snackbar.LENGTH_LONG).show();
                    } else AddStrategyMusicActivity.this.finish();
                }

            } else AddStrategyMusicActivity.this.finish();

            return true;
        }
        return false;
    }

    private class SelectedMusicListAdapter extends RecyclerView.Adapter<SelectedMusicListAdapter.MusicViewHolder> {
        List<String> listMusic =new ArrayList<>();

        public SelectedMusicListAdapter(List<String> listMusic) {

            this.listMusic = listMusic;
        }

        @Override
        public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_selected_music, parent, false);
            return new MusicViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MusicViewHolder holder, int position) {
            holder.itemMusicName.setText(new File(listMusic.get(position)).getName());
            holder.itemRemove.setTag(position);
        }

        @Override
        public int getItemCount() {
            return listMusic.size();
        }

        class MusicViewHolder extends RecyclerView.ViewHolder {
            public TextView itemMusicName;
            public ImageView itemRemove;

            public MusicViewHolder(View itemView) {
                super(itemView);
                itemMusicName = (TextView) itemView.findViewById(R.id.itemMusicName);
                itemRemove = (ImageView) itemView.findViewById(R.id.itemRemove);
                itemRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int positon = (int) view.getTag();
                        listMusic.remove(positon);
                        notifyDataSetChanged();
                    }
                });
            }
        }


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
