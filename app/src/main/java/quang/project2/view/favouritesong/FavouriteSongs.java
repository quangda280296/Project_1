package quang.project2.view.favouritesong;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import quang.project2.R;
import quang.project2.controller.ThreadLoad;
import quang.project2.controller.ThreadLoading;
import quang.project2.controller.language.Libs;
import quang.project2.model.ListMusic;
import quang.project2.model.Song;
import quang.project2.view.boommenu.BoomMenuButton;
import quang.project2.view.boommenu.Types.BoomType;
import quang.project2.view.boommenu.Types.ButtonType;
import quang.project2.view.boommenu.Types.PlaceType;
import quang.project2.view.boommenu.Util;

import static quang.project2.controller.CreateListSong.mListSongs;
import static quang.project2.controller.playmusic.PlayMusic.current_song;
import static quang.project2.controller.playmusic.PlayMusic.listSongs;
import static quang.project2.controller.playmusic.PlayMusic.mediaPlayer;
import static quang.project2.controller.playmusic.PlayMusic.play;
import static quang.project2.controller.playmusic.PlayMusic.posit;
import static quang.project2.controller.playmusic.PlayMusic.repeat;
import static quang.project2.controller.playmusic.PlayMusic.shuffle;
import static quang.project2.view.homebackground.MainActivity.favouriteSong;
import static quang.project2.view.homebackground.MainActivity.playlist;

/**
 * Created by keban on 12/15/2016.
 */
public class FavouriteSongs extends AppCompatActivity {

    static String mode = "li";
    RecyclerView recyclerView;
    MusicAdapter adapter;
    SwipeToAction swipeToAction;

    private BoomMenuButton boomMenuButtonInActionBar;
    private Context mContext;
    private View mCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_song);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new MusicAdapter(favouriteSong);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog();
            }
        });

        mContext = this;
        String readString = "";
        try {
            FileInputStream fIn = openFileInput("Theme.txt");
            InputStreamReader isr = new InputStreamReader(fIn);

            char[] inputBuffer = new char[100];

            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {

                readString = String.copyValueOf(inputBuffer, 0, charRead);
                mode = readString;
                inputBuffer = new char[100];
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        RelativeLayout home = (RelativeLayout)findViewById(R.id.home);
        LinearLayout root = (LinearLayout)findViewById(R.id.root);
        TextView text = (TextView)findViewById(R.id.shuffle);

        if(mode.compareTo("ni") == 0)
        {
            home.setBackgroundColor(getResources().getColor(R.color.dark_gray_pressed));
            root.setBackground(getResources().getDrawable(R.drawable.night_selector));
            text.setTextColor(getResources().getColor(android.R.color.white));
        }

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(R.string.favourite_song);

        boomMenuButtonInActionBar = (BoomMenuButton) mCustomView.findViewById(R.id.boom);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        ((Toolbar) mCustomView.getParent()).setContentInsetsAbsolute(0,0);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(favouriteSong.size() != 0) {

                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }

                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(favouriteSong.get(0).getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        posit = 0;
                        listSongs = favouriteSong;
                        shuffle = "off";
                        repeat = "off";
                        current_song = favouriteSong.get(0);
                        startActivity(new Intent("quang.project2.view.playmusic.PlayMusic"));
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                play();
                            }
                        };

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        initBoom();

        swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SwipeListener<Song>() {
            @Override
            public boolean swipeLeft(final Song itemData) {
                final int pos = removeSong(itemData);
                displaySnackbar(itemData.getTitle() + " " + getResources().getString(R.string.remove), getResources().getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addSong(pos, itemData);
                    }
                });
                return true;
            }

            @Override
            public boolean swipeRight(final Song itemData) {
                final int pos = removeSong(itemData);
                displaySnackbar(itemData.getTitle() + " " + getResources().getString(R.string.remove), getResources().getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addSong(pos, itemData);
                    }
                });
                return true;
            }

            @Override
            public void onClick(Song itemData) {
                if(mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(itemData.getPath());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                for(int i = 0; i < favouriteSong.size(); i++)
                {
                    if(favouriteSong.get(i)==itemData)
                        posit = i;
                }
                listSongs = favouriteSong;
                current_song = itemData;

                startActivity(new Intent("quang.project2.view.playmusic.PlayMusic"));
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        play();
                    }
                };
            }

            @Override
            public void onLongClick(Song itemData) {

            }
        });

        // use swipeLeft or swipeRight and the elem position to swipe by code
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToAction.swipeRight(2);
            }
        }, 3000);
    }

    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snack.getView();
        v.setBackgroundColor(getResources().getColor(R.color.secondary));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }

            private int removeSong(Song song) {
                int pos = favouriteSong.indexOf(song);
                favouriteSong.remove(song);
                adapter.notifyItemRemoved(pos);
                return pos;
            }

            private void addSong(int pos, Song song) {
                favouriteSong.add(pos, song);
                adapter.notifyItemInserted(pos);
            }

    String item[];
    AlertDialog input;

    public void displayDialog(){

        ls = new ArrayList();
        item = new String[mListSongs.size()];
        int i = 0;

        for(Song s : mListSongs)
        {
            item[i] = s.getName();
            i++;
        }

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.edit_playlist, null);

        ListView list = (ListView)alertLayout.findViewById(R.id.list);

        ArrayAdapter aa = new SetDS(this);
        list.setAdapter(aa);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                favouriteSong = ls;
                write_favourite_songs();
                startActivity(new Intent("quang.project2.view.favouritesong.FavouriteSongs"));
                finish();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                input.hide();
            }
        });

        input = alert.create();
        input.show();
    }

    List<Song> ls;

    public class SetDS extends ArrayAdapter<String> {

        Activity context;

        public SetDS(Activity context) {

            super(context, R.layout.list_music, item);
            this.context = context;

            for(Song song : mListSongs)
                for(int c = 0; c < favouriteSong.size(); c++)
                    if (song.getName().compareTo(favouriteSong.get(c).getName()) == 0)
                        ls.add(song);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.custom, null);

            TextView label = (TextView) row.findViewById(R.id.name);
            label.setText(mListSongs.get(position).getName());

            final CheckBox check = (CheckBox)row.findViewById(R.id.check);

            final Song song = mListSongs.get(position);

            for(int c = 0; c < favouriteSong.size(); c++) {
                if (song.getName().compareTo(favouriteSong.get(c).getName()) == 0) {

                    check.setChecked(true);
                    break;
                }
            }

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(check.isChecked() == true)
                    {
                        ls.add(song);
                    }
                    else
                    if(check.isChecked() == false)
                    {
                        ls.remove(song);
                    }
                }
            });

            return row;
        }
    }

    public void write_favourite_songs(){

        try {
            FileOutputStream fOut = openFileOutput("FavouriteSongs.txt", MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            //---Bắt đầu quá trình ghi file---
            for(Song k : favouriteSong)
            {
                String str = k.getPath();
                osw.write(str);
                osw.flush();
                osw.write("#####");
                osw.flush();
            }

            osw.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void initBoom() {
        int number = 6;

        Drawable[] drawables = new Drawable[number];
        int[] drawablesResource = new int[]{
                R.drawable.record,
                R.drawable.refresh,
                R.drawable.info,
                R.drawable.heart,
                R.drawable.mark,
                R.drawable.settings
        };
        for (int i = 0; i < number; i++)
            drawables[i] = ContextCompat.getDrawable(mContext, drawablesResource[i]);

        String[] STRINGS = new String[]{
                "Play Music",
                "Refresh",
                "Info",
                "Exit",
                "Language",
                "Theme"
        };
        String[] strings = new String[number];
        for (int i = 0; i < number; i++)
            strings[i] = STRINGS[i];

        int[][] colors = new int[number][2];
        for (int i = 0; i < number; i++) {
            colors[i][1] = GetRandomColor();
            colors[i][0] = Util.getInstance().getPressedColor(colors[i][1]);
        }

        // Now with Builder, you can init BMB more convenient
        new BoomMenuButton.Builder()
                .subButtons(drawables, colors, strings)
                .button(ButtonType.CIRCLE)
                .boom(BoomType.PARABOLA)
                .place(PlaceType.CIRCLE_6_4)
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                    @Override
                    public void onClick(int buttonIndex) {

                        switch (buttonIndex) {
                            case 0:
                                startActivity(new Intent("quang.project2.view.playmusic.PlayMusic"));
                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                        Manifest.permission.READ_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                }

                                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                        Manifest.permission.READ_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {

                                    Thread loadList = new ThreadLoading(getApplicationContext());
                                    loadList.start();

                                    try {

                                        loadList.join();

                                        Thread load = new ThreadLoad();
                                        load.start();

                                        favouriteSong = new ArrayList();
                                        String[] words;
                                        String readString = "";

                                        try {
                                            FileInputStream fIn = openFileInput("FavouriteSongs.txt");
                                            InputStreamReader isr = new InputStreamReader(fIn);

                                            char[] inputBuffer = new char[100];
                                            int i = 0;

                                            int charRead;
                                            while ((charRead = isr.read(inputBuffer)) > 0) {

                                                readString += String.copyValueOf(inputBuffer, 0, charRead);

                                                inputBuffer = new char[100];
                                            }

                                        } catch (IOException ioe) {
                                            ioe.printStackTrace();
                                        }

                                        if (readString.compareTo("") != 0) {
                                            words = readString.split("#####");
                                            for (String s : words) {
                                                if (s != "") {
                                                    for (Song song : mListSongs)
                                                        if (song.getPath().compareTo(s) == 0) {
                                                            favouriteSong.add(song);
                                                        }
                                                }
                                            }
                                        }

                                        playlist = new ArrayList();

                                        String[] result;
                                        String read = "";

                                        try {
                                            FileInputStream fin = openFileInput("Playlist.txt");
                                            InputStreamReader fisr = new InputStreamReader(fin);

                                            char[] inputBuffer = new char[100];
                                            int i = 0;

                                            int charRead;
                                            while ((charRead = fisr.read(inputBuffer)) > 0) {

                                                read += String.copyValueOf(inputBuffer, 0, charRead);

                                                inputBuffer = new char[100];
                                            }
                                        } catch (IOException ioe) {
                                            ioe.printStackTrace();
                                        }

                                        if (readString.compareTo("") != 0) {
                                            result = read.split("@@@@@");
                                            for (String s : result) {
                                                if (s != "") {
                                                    String[] chuoi = s.split("<<<<<");
                                                    List<Song> a = new ArrayList();
                                                    ListMusic list = new ListMusic(chuoi[0], a);
                                                    playlist.add(list);

                                                    String ket_qua[] = chuoi[1].split("###");

                                                    for (String str : ket_qua) {
                                                        for (Song song : mListSongs)
                                                            if (song.getPath().compareTo(str) == 0) {
                                                                a.add(song);
                                                                list.setListSong(a);
                                                            }
                                                    }
                                                }
                                            }
                                        }

                                        try {

                                            loadList.join();

                                            Intent activitymoi = new Intent("quang.project2.view.home.MainActivity");
                                            startActivity(activitymoi);
                                            finish();

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                break;

                            case 2:
                                startActivity(new Intent("quang.project2.view.AboutActivity"));
                                break;
                            case 3:
                                finish();
                                break;
                            case 4:
                                displayAlertDialog();
                                break;
                            case 5:
                                displayTheme();
                                break;
                        }
                    }
                })
                .init(boomMenuButtonInActionBar);
    }

    public void displayTheme()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_theme, null);

        View ni = alertLayout.findViewById(R.id.ni);
        View li = alertLayout.findViewById(R.id.li);

        final RadioButton night = (RadioButton) alertLayout.findViewById(R.id.night);
        final RadioButton light = (RadioButton) alertLayout.findViewById(R.id.light);

        switch (mode)
        {
            case "li":
                light.setChecked(true);
                night.setChecked(false);
                break;
            case "ni":
                light.setChecked(false);
                night.setChecked(true);
                break;
        }

        ni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                night.setChecked(true);
                light.setChecked(false);

                try {
                    FileOutputStream fOut = openFileOutput("Theme.txt", MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    //---Bắt đầu quá trình ghi file---

                    String string = "ni";
                    osw.write(string);
                    osw.flush();
                    osw.close();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                mode = "ni";
                startActivity(new Intent("quang.project2.view.home.MainActivity"));
            }
        });

        li.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                night.setChecked(false);
                light.setChecked(true);

                try {
                    FileOutputStream fOut = openFileOutput("Theme.txt", MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    //---Bắt đầu quá trình ghi file---

                    String string = "li";
                    osw.write(string);
                    osw.flush();
                    osw.close();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                mode = "li";
                startActivity(new Intent("quang.project2.view.home.MainActivity"));
            }
        });

        night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                night.setChecked(true);
                light.setChecked(false);

                try {
                    FileOutputStream fOut = openFileOutput("Theme.txt", MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    //---Bắt đầu quá trình ghi file---

                    String string = "ni";
                    osw.write(string);
                    osw.flush();
                    osw.close();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                mode = "ni";
                startActivity(new Intent("quang.project2.view.home.MainActivity"));
            }
        });

        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                night.setChecked(false);
                light.setChecked(true);

                try {
                    FileOutputStream fOut = openFileOutput("Theme.txt", MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    //---Bắt đầu quá trình ghi file---

                    String string = "li";
                    osw.write(string);
                    osw.flush();
                    osw.close();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                mode = "li";
                startActivity(new Intent("quang.project2.view.home.MainActivity"));
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private String[] Colors = {
            "#F44336",
            "#E91E63",
            "#9C27B0",
            "#2196F3",
            "#03A9F4",
            "#00BCD4",
            "#009688",
            "#4CAF50",
            "#8BC34A",
            "#CDDC39",
            "#FFEB3B",
            "#FFC107",
            "#FF9800",
            "#FF5722",
            "#795548",
            "#9E9E9E",
            "#607D8B"};

    public int GetRandomColor() {
        Random random = new Random();
        int p = random.nextInt(Colors.length);
        return Color.parseColor(Colors[p]);
    }

    public void displayAlertDialog()
    {
        String str = "en";
        String readString = "";

        try {
            FileInputStream fIn = openFileInput("Language.txt");
            InputStreamReader isr = new InputStreamReader(fIn);

            char[] inputBuffer = new char[100];
            int i = 0;

            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {

                readString = String.copyValueOf(inputBuffer, 0, charRead);
                str = readString;
                inputBuffer = new char[100];
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog, null);

        View vi = alertLayout.findViewById(R.id.vi);
        View en = alertLayout.findViewById(R.id.en);

        final RadioButton vietnamese = (RadioButton) alertLayout.findViewById(R.id.vietnamese);
        final RadioButton english = (RadioButton) alertLayout.findViewById(R.id.english);

        vi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vietnamese.setChecked(true);
                english.setChecked(false);

                try {
                    FileOutputStream fOut = openFileOutput("Language.txt", MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    //---Bắt đầu quá trình ghi file---

                    String string = "vi";
                    osw.write(string);
                    osw.flush();
                    osw.close();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                setLocale("vi");
                startActivity(new Intent("quang.project2.view.home.MainActivity"));
            }
        });

        en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vietnamese.setChecked(false);
                english.setChecked(true);

                try {
                    FileOutputStream fOut = openFileOutput("Language.txt", MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    //---Bắt đầu quá trình ghi file---

                    String string = "en";
                    osw.write(string);
                    osw.flush();
                    osw.close();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                setLocale("en");
                startActivity(new Intent("quang.project2.view.home.MainActivity"));
            }
        });

        if(str.compareTo("en")==0)
        {
            english.setChecked(true);
        }
        else
        if(str.compareTo("vi")==0)
        {
            vietnamese.setChecked(true);
        }

        vietnamese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vietnamese.setChecked(true);
                english.setChecked(false);

                try {
                    FileOutputStream fOut = openFileOutput("Language.txt", MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    //---Bắt đầu quá trình ghi file---

                    String string = "vi";
                    osw.write(string);
                    osw.flush();
                    osw.close();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                setLocale("vi");
                startActivity(new Intent("quang.project2.view.home.MainActivity"));
            }
        });

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vietnamese.setChecked(false);
                english.setChecked(true);

                try {
                    FileOutputStream fOut = openFileOutput("Language.txt", MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);

                    //---Bắt đầu quá trình ghi file---

                    String string = "en";
                    osw.write(string);
                    osw.flush();
                    osw.close();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                setLocale("en");
                startActivity(new Intent("quang.project2.view.home.MainActivity"));
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public void setLocale(String lang) {
        Libs.changeLang(lang, FavouriteSongs.this);
        Log.e("LANG", lang);
    }
}
