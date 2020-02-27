package quang.project2.view.playmusic;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import quang.project2.R;
import quang.project2.controller.playmusic.PlayMusic;

import static quang.project2.controller.playmusic.PlayMusic.array;
import static quang.project2.controller.playmusic.PlayMusic.current_song;
import static quang.project2.controller.playmusic.PlayMusic.listSongs;
import static quang.project2.controller.playmusic.PlayMusic.mediaPlayer;
import static quang.project2.controller.playmusic.PlayMusic.music_play;
import static quang.project2.controller.playmusic.PlayMusic.posit;
import static quang.project2.controller.playmusic.PlayMusic.repeat;
import static quang.project2.controller.playmusic.PlayMusic.shuffle;
import static quang.project2.view.playmusic.PlayMusic.mTitleTextView;

/**
 * Created by keban on 1/12/2017.
 */
public class Frag extends Fragment {

    static String play_or_pause;
    TextView tvStartTime;
    TextView tvFinalTime;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    private Handler handler = new Handler();
    private SeekBar seekbar;
    ImageView playPause;
    ImageView rep;
    ImageView shuff;
    ImageView next;
    ImageView prev;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View row = inflater.inflate(R.layout.activity_full_player, container, false);
        tvStartTime = (TextView) row.findViewById(R.id.start_time);
        tvFinalTime = (TextView) row.findViewById(R.id.end_time);
        seekbar = (SeekBar) row.findViewById(R.id.seekBar);

        myHandler.postDelayed(UpdateSongTime, 100);
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();

        seekbar.setMax((int) finalTime);

        long min_start = TimeUnit.MILLISECONDS.toMinutes((long) startTime);
        long sec_start = TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime));

        long min_final = TimeUnit.MILLISECONDS.toMinutes((long) finalTime);
        long sec_final = TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime));

        String min_st = "";
        String sec_st = "";
        String min_fn = "";
        String sec_fn = "";

        if (min_start < 10)
            min_st = "0" + String.valueOf(min_start);
        else
            min_st = String.valueOf(min_start);

        if (sec_start < 10)
            sec_st = "0" + String.valueOf(sec_start);
        else
            sec_st = String.valueOf(sec_start);

        if (min_final < 10)
            min_fn = "0" + String.valueOf(min_final);
        else
            min_fn = String.valueOf(min_final);

        if (sec_final < 10)
            sec_fn = "0" + String.valueOf(sec_final);
        else
            sec_fn = String.valueOf(sec_final);

        tvFinalTime.setText(String.format("%s : %s", min_fn, sec_fn));
        tvStartTime.setText(String.format("%s : %s", min_st, sec_st));

        seekbar.setProgress((int) startTime);

        playPause = (ImageView) row.findViewById(R.id.play_pause);
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseAction();
            }
        });

        rep = (ImageView) row.findViewById(R.id.repeat);
        switch (repeat) {
            case "all":
                rep.setImageResource(R.mipmap.btn_playback_repeat_all);
                break;
            case "one":
                rep.setImageResource(R.mipmap.btn_playback_repeat_one);
                break;
            case "off":
                rep.setImageResource(R.mipmap.btn_playback_repeat);
                break;
        }

        shuff = (ImageView) row.findViewById(R.id.shuffle);
        switch (shuffle) {
            case "off":
                shuff.setImageResource(R.mipmap.btn_playback_shuffle);
                break;
            case "on":
                shuff.setImageResource(R.mipmap.btn_playback_shuffle_all);
                break;
        }

        rep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable r;
                switch (repeat) {
                    case "off":
                        repeat = "all";
                        rep.setImageResource(R.mipmap.btn_playback_repeat_all);
                        mediaPlayer.setLooping(false);
                        Toast.makeText(getContext(), getResources().getString(R.string.repeat_all), Toast.LENGTH_SHORT).show();
                        break;
                    case "all":
                        repeat = "one";
                        rep.setImageResource(R.mipmap.btn_playback_repeat_one);
                        mediaPlayer.setLooping(true);
                        Toast.makeText(getContext(), getResources().getString(R.string.repeat_one), Toast.LENGTH_SHORT).show();
                        break;
                    case "one":
                        repeat = "off";
                        rep.setImageResource(R.mipmap.btn_playback_repeat);
                        mediaPlayer.setLooping(false);
                        Toast.makeText(getContext(), getResources().getString(R.string.repeat_off), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        shuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable r;
                switch (shuffle) {
                    case "off":
                        shuffle = "on";
                        shuff.setImageResource(R.mipmap.btn_playback_shuffle_all);
                        Toast.makeText(getContext(), getResources().getString(R.string.shuffle_on), Toast.LENGTH_SHORT).show();
                        array = new int[listSongs.size()];
                        music_play = 0;
                        array[music_play] = posit;
                        music_play++;
                        break;
                    case "on":
                        shuffle = "off";
                        shuff.setImageResource(R.mipmap.btn_playback_shuffle);
                        Toast.makeText(getContext(), getResources().getString(R.string.shuffle_off), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        next = (ImageView) row.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardAction();
            }
        });

        prev = (ImageView) row.findViewById(R.id.prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backwardAction();
            }
        });

        if (mediaPlayer.isPlaying()) {
            play_or_pause = "play";
            playPause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
        } else {
            play_or_pause = "pause";
            playPause.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
        }

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        return row;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    play_or_pause = "pause";
                    playPause.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
                    PlayMusic.play();
                }
            });

            startTime = mediaPlayer.getCurrentPosition();
            long min_start = TimeUnit.MILLISECONDS.toMinutes((long) startTime);
            long sec_start = TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime));

            String min_st = "";
            String sec_st = "";

            if(min_start == 5250 && sec_start == 28)
            {
                min_start = 0;
                sec_start = 0;
            }

            if (min_start < 10)
                min_st = "0" + String.valueOf(min_start);
            else
                min_st = String.valueOf(min_start);

            if (sec_start < 10)
                sec_st = "0" + String.valueOf(sec_start);
            else
                sec_st = String.valueOf(sec_start);

            tvStartTime.setText(String.format("%s : %s", min_st, sec_st));

            finalTime = mediaPlayer.getDuration();
            long min_final = TimeUnit.MILLISECONDS.toMinutes((long) finalTime);
            long sec_final = TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime));

            String min_fn = "";
            String sec_fn = "";

            if (min_final < 10)
                min_fn = "0" + String.valueOf(min_final);
            else
                min_fn = String.valueOf(min_final);

            if (sec_final < 10)
                sec_fn = "0" + String.valueOf(sec_final);
            else
                sec_fn = String.valueOf(sec_final);

            tvFinalTime.setText(String.format("%s : %s", min_fn, sec_fn));

            seekbar.setMax((int) finalTime);

            if (current_song == null)
                mTitleTextView.setText("HUST Music Player");
            else
                mTitleTextView.setText(current_song.getTitle());

            if (mediaPlayer.isPlaying()) {
                play_or_pause = "play";
                playPause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
            } else {
                play_or_pause = "pause";
                playPause.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
            }

            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    public void playPauseAction() {

        if (play_or_pause.compareTo("pause") == 0) {
            mediaPlayer.start();
            play_or_pause = "play";
            playPause.setImageResource(R.drawable.uamp_ic_pause_white_48dp);
        } else if (play_or_pause.compareTo("play") == 0) {
            play_or_pause = "pause";
            mediaPlayer.pause();
            playPause.setImageResource(R.drawable.uamp_ic_play_arrow_white_48dp);
        }
    }

    public void forwardAction() {

        if (listSongs.size() != 0) {
            int po = posit;
            po++;
            if (po < listSongs.size()) {
                posit++;
                mediaPlayer.stop();
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(listSongs.get(posit).getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    current_song = listSongs.get(posit);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                posit = 0;
                mediaPlayer.stop();
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(listSongs.get(posit).getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    current_song = listSongs.get(posit);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void backwardAction() {

        if (listSongs.size() != 0) {
            int po = posit;
            po--;
            if (po > 0) {
                posit--;
                mediaPlayer.stop();
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(listSongs.get(posit).getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    current_song = listSongs.get(posit);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                posit = listSongs.size() - 1;
                mediaPlayer.stop();
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(listSongs.get(posit).getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    current_song = listSongs.get(posit);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}