package quang.project2.view.playmusic;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

import quang.project2.R;

import static quang.project2.controller.playmusic.PlayMusic.mediaPlayer;
import static quang.project2.controller.playmusic.PlayMusic.listSongs;
import static quang.project2.controller.playmusic.PlayMusic.posit;

/**
 * Created by keban on 12/15/2016.
 */
public class PlayMusic extends AppCompatActivity {

    ScrollerViewPager viewPager;
    ImageView time;
    static TextView mTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_music);

        viewPager = (ScrollerViewPager) findViewById(R.id.view_pager);
        SpringIndicator springIndicator = (SpringIndicator) findViewById(R.id.indicator);

        PagerModelManager manager = new PagerModelManager();
        manager.addCommonFragment(Frag.class, getTitles());
        manager.addCommonFragment(GuideFragment.class);

        ModelPagerAdapter adapter = new ModelPagerAdapter(getSupportFragmentManager(), manager);
        viewPager.setAdapter(adapter);
        viewPager.fixScrollSpeed();

        // just set viewPager
        springIndicator.setViewPager(viewPager);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.play_music_action_bar, null);
        mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);

        if(listSongs.size() == 0)
            mTitleTextView.setText(R.string.app_name);
        else
            mTitleTextView.setText(listSongs.get(posit).getTitle());

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        ((Toolbar) mCustomView.getParent()).setContentInsetsAbsolute(0,0);

        time = (ImageView)mCustomView.findViewById(R.id.boom);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTime();
            }
        });
    }

    private Runnable Update = new Runnable() {
        public void run() {

            t = seek.getProgress();

            timer.setText(String.format("%d min",
                    TimeUnit.MILLISECONDS.toMinutes(t)

            ));


            myHandler.postDelayed(this, 100);
        }
    };

    int t = 0;
    TextView timer;
    SeekBar seek;
    Handler myHandler;
    Thread bamgio;

    public void onTime()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.time, null);

        seek = (SeekBar)alertLayout.findViewById(R.id.seekBar);
        seek.setMax(60 * 60 * 1000);

        timer = (TextView)alertLayout.findViewById(R.id.timer);
        myHandler = new Handler();
        myHandler.postDelayed(Update, 100);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.sleep) + " " + TimeUnit.MILLISECONDS.toMinutes(t) + " " +  getResources().getString(R.string.minutes), Toast.LENGTH_SHORT).show();
                bamgio = new Thread() {
                    public void run() {
                        try {

                            for(int m = 0; m < 3600000; m += 60 * 1000)
                            {
                                if(t < m)
                                {
                                    sleep(m - 60 * 1000);
                                    break;
                                }
                            }
                            mediaPlayer.pause();

                        } catch (Exception e) {

                        }
                    }
                };
                bamgio.start();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(bamgio != null)
                            bamgio.interrupt();

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.timer_off), Toast.LENGTH_SHORT).show();
                    }
                });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private List<String> getTitles(){

        String[] arrayOfString = new String[4];
        arrayOfString[0] = "1";
        arrayOfString[1] = "2";

        return Lists.newArrayList(arrayOfString);
    }

}
