package quang.project2.view.folder;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
import quang.project2.view.song.FloatingActionMenu;
import quang.project2.view.song.SubActionButton;

import static quang.project2.controller.playmusic.PlayMusic.listSongs;
import static quang.project2.controller.playmusic.PlayMusic.mediaPlayer;
import static quang.project2.controller.playmusic.PlayMusic.play;
import static quang.project2.controller.playmusic.PlayMusic.posit;
import static quang.project2.controller.playmusic.PlayMusic.repeat;
import static quang.project2.controller.playmusic.PlayMusic.shuffle;
import static quang.project2.view.folder.Folders.ls_t;
import static quang.project2.view.folder.Folders.name;
import static quang.project2.view.homebackground.MainActivity.favouriteSong;
import static quang.project2.view.homebackground.MainActivity.playlist;
import static quang.project2.controller.playmusic.PlayMusic.current_song;

/**
 * Created by keban on 12/15/2016.
 */
public class Songs extends AppCompatActivity implements SearchView.OnQueryTextListener {

    static String mode = "li";
    String items[] = new String[ls_t.size()];
    private SearchView searchView;

    private BoomMenuButton boomMenuButtonInActionBar;
    private Context mContext;
    private View mCustomView;

    ListView lst;
    ArrayAdapter a = null;
    FloatingActionMenu[] fab = new FloatingActionMenu[ls_t.size() + 1000];
    int j = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song);

        int i = 0;

        for(Song s : ls_t)
        {
            items[i] = s.getTitle();
            i++;
        }

        lst = (ListView)findViewById(R.id.list);
        a = new SetList(this);
        lst.setAdapter(a);
//        lst.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//
//                for(int h = 0; h < j ; h++)
//                    if(fab[h] != null)
//                        fab[h].close(true);
//            }
//        });

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

        LinearLayout home = (LinearLayout)findViewById(R.id.home);
        LinearLayout root = (LinearLayout)findViewById(R.id.root);
        TextView text = (TextView)findViewById(R.id.shuffle);

        if(mode.compareTo("ni") == 0)
        {
            home.setBackgroundColor(getResources().getColor(R.color.dark_gray_pressed));
            root.setBackground(getResources().getDrawable(R.drawable.night_selector));
            text.setTextColor(getResources().getColor(android.R.color.white));
        }

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                }

                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(ls_t.get(0).getPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    posit = 0;
                    listSongs = ls_t;
                    shuffle = "off";
                    repeat = "off";
                    current_song = ls_t.get(0);
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
        });

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(name);

        boomMenuButtonInActionBar = (BoomMenuButton) mCustomView.findViewById(R.id.boom);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        ((Toolbar) mCustomView.getParent()).setContentInsetsAbsolute(0,0);

        initBoom();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // thêm search vào vào action bar
        getMenuInflater().inflate(R.menu.menu_main  , menu);
        MenuItem itemSearch = menu.findItem(R.id.search_view);
        searchView = (SearchView) itemSearch.getActionView();
        //set OnQueryTextListener cho search view để thực hiện search theo text
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    //phương thúc lọc khi search
    @Override
    public boolean onQueryTextChange(String newText)
    {
        if (TextUtils.isEmpty(newText)){
            a.getFilter().filter("");
            lst.clearTextFilter();
        }else {
            a.getFilter().filter(newText.toString());
        }
        return true;
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//        for(int i = 0; i < ls_t.size(); i++)
//        {
//            if(position == i)
//            {
//                startActivity(new Intent("quang.project2.view.playmusic.PlayMusic"));
//                break;
//            }
//        }
//
//    }

    public class SetList extends ArrayAdapter<String> {

        Activity context;

        public SetList(Activity context) {

            super(context, R.layout.list_song, items);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.list_song, null);

            LinearLayout root = (LinearLayout)row.findViewById(R.id.root);
            TextView label = (TextView) row.findViewById(R.id.label);
            TextView artist = (TextView) row.findViewById(R.id.artist);
            Button button = (Button) row.findViewById(R.id.button);

            label.setText(ls_t.get(position).getTitle());
            artist.setText(ls_t.get(position).getArtist());

            if(mode.compareTo("ni") == 0)
            {
                root.setBackgroundResource(R.drawable.night_selector);
                label.setTextColor(getResources().getColor(R.color.white));
                artist.setTextColor(getResources().getColor(R.color.light_gray));
            }

            root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            for(int h = 0; h < j ; h++)
                                if(fab[h] != null)
                                    fab[h].close(true);

                            for (int i = 0; i < ls_t.size(); i++) {
                                if (position == i) {

                                    if(mediaPlayer.isPlaying())
                                    {
                                        mediaPlayer.stop();
                                    }

                                    try {
                                        mediaPlayer = new MediaPlayer();
                                        mediaPlayer.setDataSource(ls_t.get(position).getPath());
                                        mediaPlayer.prepare();
                                        mediaPlayer.start();
                                        posit = i;
                                        listSongs = ls_t;
                                        shuffle = "off";
                                        repeat = "off";
                                        current_song = ls_t.get(position);
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

                                    break;
                                }
                            }
                        }
                    });

            ImageView rlIcon1 = new ImageView(getApplicationContext());
            ImageView rlIcon2 = new ImageView(getApplicationContext());
            ImageView rlIcon3 = new ImageView(getApplicationContext());
            ImageView rlIcon4 = new ImageView(getApplicationContext());

            rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.playlists));
            rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.favouritesongs));
            rlIcon3.setImageDrawable(getResources().getDrawable(R.mipmap.ic_action_chat_light));
            rlIcon4.setImageDrawable(getResources().getDrawable(R.mipmap.ic_action_headphones));

            int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
            int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);
            int redActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius);

            SubActionButton.Builder lCSubBuilder = new SubActionButton.Builder(getApplicationContext());

            SubActionButton.Builder rCSubBuilder = new SubActionButton.Builder(getApplicationContext());
            rCSubBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_action_blue_selector));

            FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            blueContentParams.setMargins(blueSubActionButtonContentMargin,
                    blueSubActionButtonContentMargin,
                    blueSubActionButtonContentMargin,
                    blueSubActionButtonContentMargin);

            FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
            lCSubBuilder.setLayoutParams(blueParams);

            SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(getApplicationContext());

            final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(getApplicationContext())
                    .addSubActionView(lCSubBuilder.setContentView(rlIcon1, blueContentParams).build())
                    .addSubActionView(rCSubBuilder.setContentView(rlIcon4).build())
                    .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                    .addSubActionView(lCSubBuilder.setContentView(rlIcon2, blueContentParams).build())
                    .setRadius(redActionMenuRadius)
                    .attachTo(button)
                    .build();

            List<FloatingActionMenu.Item> items = rightLowerMenu.getSubActionItems();
            for(int k = 0; k < items.size(); k++) {
                switch (k) {
                    case 0:
                        items.get(k).view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                displayDialog(position);
                                for(int h = 0; h < j ; h++)
                                    if(fab[h] != null)
                                        fab[h].close(true);
                            }
                        });
                        break;
                    case 1:
                        items.get(k).view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                listSongs.add(ls_t.get(position));
                                posit = listSongs.size() - 1;
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_queue), Toast.LENGTH_SHORT).show();

                                for(int h = 0; h < j ; h++)
                                    if(fab[h] != null)
                                        fab[h].close(true);
                            }
                        });
                        break;
                    case 2:
                        items.get(k).view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                displayDetail(position);
                                for(int h = 0; h < j ; h++)
                                    if(fab[h] != null)
                                        fab[h].close(true);
                            }
                        });
                        break;
                    case 3:
                        items.get(k).view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                write_data(position);
                                for(int h = 0; h < j ; h++)
                                    if(fab[h] != null)
                                        fab[h].close(true);
                            }
                        });
                        break;

                }
            }

            j++;
            fab[j] = rightLowerMenu;

            //Listen menu open and close events to animate the button content view

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for(int h = 0; h < j ; h++)
                        if(h != position)
                            if(fab[h] != null)
                                fab[h].close(true);

                    if(rightLowerMenu.isOpen())
                        rightLowerMenu.close(true);
                    else
                        rightLowerMenu.open(true);
                }
            });

//            rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
//                @Override
//                public void onMenuOpened(FloatingActionMenu menu) {
//
//                }
//
//                @Override
//                public void onMenuClosed(FloatingActionMenu menu) {
//
//                }
//            });

            return row;
        }
    }

    public void displayDetail(int position)
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.detail, null);

        TextView name = (TextView)alertLayout.findViewById(R.id.name);
        TextView artist = (TextView)alertLayout.findViewById(R.id.artist);
        TextView album = (TextView)alertLayout.findViewById(R.id.album);
        TextView duration = (TextView)alertLayout.findViewById(R.id.duration);
        TextView size = (TextView)alertLayout.findViewById(R.id.size);
        TextView path = (TextView)alertLayout.findViewById(R.id.path);
        TextView format = (TextView)alertLayout.findViewById(R.id.format);

        int startTime = ls_t.get(position).getDuration();

        name.setText(getResources().getString(R.string.name) + ": " + ls_t.get(position).getTitle());
        artist.setText(getResources().getString(R.string.artist) + ": " + ls_t.get(position).getArtist());
        album.setText(getResources().getString(R.string.album) + ": " + ls_t.get(position).getAlbum());
        duration.setText(getResources().getString(R.string.duration)  + ": " + String.format("%d min %d sec",

                TimeUnit.MILLISECONDS.toMinutes(startTime),
                TimeUnit.MILLISECONDS.toSeconds(startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes(startTime))));

        size.setText(getResources().getString(R.string.size) + ": " + String.valueOf(ls_t.get(position).getSize()/(1000)) + " KB");
        path.setText(getResources().getString(R.string.path) + ": " + ls_t.get(position).getPath());
        format.setText(getResources().getString(R.string.format) + ": " + ls_t.get(position).getFormat());

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        AlertDialog dialog_detail = alert.create();
        final AlertDialog finalDialog = dialog_detail;
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finalDialog.hide();
                    }
                });

        dialog_detail = alert.create();
        dialog_detail.show();
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

                        switch (buttonIndex)
                        {
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

                                        if(readString.compareTo("")!=0)
                                        {
                                            words = readString.split("#####");
                                            for (String s : words) {
                                                if (s != "")
                                                {
                                                    for (Song song : ls_t)
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

                                        if(readString.compareTo("")!=0)
                                        {
                                            result = read.split("@@@@@");
                                            for (String s : result) {
                                                if (s != "")
                                                {
                                                    String []chuoi = s.split("<<<<<");
                                                    List<Song> a = new ArrayList();
                                                    ListMusic list = new ListMusic(chuoi[0], a);
                                                    playlist.add(list);

                                                    String ket_qua[] = chuoi[1].split("###");

                                                    for(String str : ket_qua)
                                                    {
                                                        for (Song song : ls_t)
                                                            if (song.getPath().compareTo(str) == 0)
                                                            {
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

    static String item[];
    AlertDialog dialog;
    int pst;

    public void displayDialog(final int pst)
    {
        this.pst = pst;
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_playlist, null);

        int i = 0;
        item = new String[playlist.size()];

        for(ListMusic s : playlist)
        {
            item[i] = s.getName();
            i++;
        }

        LinearLayout ln = (LinearLayout)alertLayout.findViewById(R.id.root);
        ln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInput(pst);
            }
        });

        ListView ls = (ListView)alertLayout.findViewById(R.id.ls);
        ArrayAdapter aa = new SetDS(this);
        ls.setAdapter(aa);
        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i < playlist.size(); i++)
                {
                        if(position == i)
                        {
                            int check = 0;
                            ListMusic m = playlist.get(i);

                                if(m.getListSong().size() > 0) {

                                    List<Song> q = m.getListSong();

                                    for (Song s : q) {
                                        if (ls_t.get(pst).getTitle().compareTo(s.getTitle()) == 0)
                                            if (ls_t.get(pst).getArtist().compareTo(s.getArtist()) == 0) {
                                                check = 1;
                                                break;
                                            }
                                    }
                                }

                                if(check == 0) {
                                    List<Song> lst = m.getListSong();
                                    lst.add(ls_t.get(pst));
                                    m.setListSong(lst);
                                    write_playlist();
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_playlist), Toast.LENGTH_SHORT).show();
                                }
                                else
                                if(check == 1)
                                {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fail_pl), Toast.LENGTH_SHORT).show();
                                }
                        }
                    }
                dialog.hide();
                }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        dialog = alert.create();
        dialog.show();
    }

    public void displayInput(final int pst)
    {
        dialog.hide();
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.new_playlist, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        AlertDialog dialog123 = alert.create();
        final EditText edit = (EditText)alertLayout.findViewById(R.id.edit);

        final AlertDialog finalDialog = dialog123;
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finalDialog.hide();
            }
        });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                List<Song> a = new ArrayList();
                a.add(ls_t.get(pst));

                for(Song m : a)
                {
                    for(Song n : a) {

                        if (check(m.getTitle(), n.getTitle()) == true) {

                            Song temp;
                            temp = m;
                            m = n;
                            n = temp;
                        }
                    }
                }
                ListMusic list = new ListMusic(edit.getText().toString(), a);
                playlist.add(list);
                write_playlist();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_playlist), Toast.LENGTH_SHORT).show();

                item = new String[playlist.size()];
                int i = 0;

                for(ListMusic s : playlist)
                {
                    item[i] = s.getName();
                    i++;
                }
                finalDialog.hide();
            }
        });
        dialog123 = alert.create();
        dialog123.show();
    }

    public class SetDS extends ArrayAdapter<String> {

        Activity context;

        public SetDS(Activity context) {

            super(context, R.layout.list_music, item);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.list_playlist, null);

            TextView label = (TextView) row.findViewById(R.id.label);
            label.setText(playlist.get(position).getName());

            LinearLayout root = (LinearLayout)row.findViewById(R.id.root);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for(int i = 0; i < playlist.size(); i++)
                    {
                        if(position == i)
                        {
                            int check = 0;
                            ListMusic m = playlist.get(i);

                            if(m.getListSong().size() > 0) {

                                List<Song> q = m.getListSong();

                                for (Song s : q) {
                                    if (ls_t.get(pst).getTitle().compareTo(s.getTitle()) == 0)
                                        if (ls_t.get(pst).getArtist().compareTo(s.getArtist()) == 0) {
                                            check = 1;
                                            break;
                                        }
                                }
                            }

                            if(check == 0) {
                                List<Song> lst = m.getListSong();
                                lst.add(ls_t.get(pst));
                                m.setListSong(lst);
                                write_playlist();
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_playlist), Toast.LENGTH_SHORT).show();
                            }
                            else
                            if(check == 1)
                            {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.fail_pl), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    dialog.hide();

                }
            });


            return row;
        }
    }

    public void write_playlist()
    {
        try {
            FileOutputStream fOut = openFileOutput("Playlist.txt", MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            //---Bắt đầu quá trình ghi file---
            for(ListMusic k : playlist)
            {
                String str = k.getName();
                osw.write(str);
                osw.flush();
                osw.write("<<<<<");
                osw.flush();

                List<Song> s = k.getListSong();

                for(int n = 0; n < s.size() ; n++)
                {
                    String st = s.get(n).getPath();
                    osw.write(st);
                    osw.flush();

                    osw.write("###");
                    osw.flush();

                    if(n == s.size() - 1)
                    {
                        osw.write("@@@@@");
                        osw.flush();
                    }
                }
            }

            osw.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void write_data(int position)
    {
        Song s = ls_t.get(position);
        int check = 0;

        for(Song q : favouriteSong) {
            if (s.getTitle().compareTo(q.getTitle()) == 0)
                if(s.getArtist().compareTo(q.getArtist()) == 0)
                   {
                        check = 1;
                        break;
                   }
            }

            if(check == 0)
            {
                favouriteSong.add(s);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_favourite_song), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.fail), Toast.LENGTH_SHORT).show();
            }

        for(Song m : favouriteSong)
        {
            for(Song n : favouriteSong) {

                if (check(m.getTitle(), n.getTitle()) == true) {

                    Song temp;
                    temp = m;
                    m = n;
                    n = temp;
                }
            }
        }

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

    public static boolean check(String str1, String str2)
    {
        int len;
        if(str1.length() < str2.length())
            len = str1.length();
        else
            len = str2.length();

        for(int i = 0; i < len; i++) {

            if (str1.charAt(i) > str2.charAt(i))
                return true;
            else
            if(str1.charAt(i) < str2.charAt(i))
                return false;
        }

        if(len == str1.length())
            return true;
        else
            return false;
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
        Libs.changeLang(lang, Songs.this);
        Log.e("LANG", lang);
    }
}
