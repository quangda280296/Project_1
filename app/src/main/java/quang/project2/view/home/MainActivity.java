/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package quang.project2.view.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.support.v7.widget.SearchView;

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

import static quang.project2.controller.CreateListAlbum.listAlbum;
import static quang.project2.controller.CreateListArtist.listArtist;
import static quang.project2.controller.CreateListFolder.listFolder;
import static quang.project2.controller.CreateListSong.mListSongs;
import static quang.project2.view.homebackground.MainActivity.favouriteSong;
import static quang.project2.view.homebackground.MainActivity.playlist;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    public static String mode = "li";
    static String[] items = new String [8];

    private SearchView searchView;
    private BoomMenuButton boomMenuButtonInActionBar;
    private Context mContext;
    private View mCustomView;

    ListView lst;
    ArrayAdapter a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        String readString = "";
        try {
            FileInputStream fIn = openFileInput("Theme.txt");
            InputStreamReader isr = new InputStreamReader(fIn);

            char[] inputBuffer = new char[100];
            int i = 0;

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
        if(mode.compareTo("ni") == 0)
            home.setBackgroundColor(getResources().getColor(R.color.dark_gray_pressed));

        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String lang = prefs.getString("Language", "en");
        Log.e("DEBUG", lang);
        Libs.loadLocale(this);

        items[0] = getResources().getString(R.string.song);
        items[1] = getResources().getString(R.string.album);
        items[2] = getResources().getString(R.string.artist);
        items[3] = getResources().getString(R.string.playlist);
        items[4] = getResources().getString(R.string.favourite_song);
        items[5] = getResources().getString(R.string.folder);
        items[6] = getResources().getString(R.string.about);
        items[7] = getResources().getString(R.string.exit);

        lst = (ListView)findViewById(R.id.list);
        a = new SetList(this);
        lst.setAdapter(a);
        lst.setOnItemClickListener(this);

        mContext = this;

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(R.string.app_name);

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                startActivity(new Intent("quang.project2.view.song.Songs"));
                break;
            case 1:
                startActivity(new Intent("quang.project2.view.album.Albums"));
                break;
            case 2:
                startActivity(new Intent("quang.project2.view.artist.Artists"));
                break;
            case 3:
                startActivity(new Intent("quang.project2.view.playlist.Playlists"));
                break;
            case 4:
                startActivity(new Intent("quang.project2.view.favouritesong.FavouriteSongs"));
                break;
            case 5:
                startActivity(new Intent("quang.project2.view.folder.Folders"));
                break;
            case 6:
                startActivity(new Intent("quang.project2.view.AboutActivity"));
                break;
            default:
                finish();
        }
    }

    public class SetList extends ArrayAdapter<String> {

        Activity context;

        public SetList(Activity context ) {
            super(context, R.layout.home_layout, items);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.home_layout, null);

            LinearLayout root = (LinearLayout)row.findViewById(R.id.root);
            ImageView icon = (ImageView)row.findViewById(R.id.icon);
            TextView label = (TextView)row.findViewById(R.id.label);
            TextView total = (TextView)row.findViewById(R.id.total);

            if(mode.compareTo("ni") == 0)
            {
                root.setBackgroundResource(R.drawable.night_selector);
                label.setTextColor(getResources().getColor(R.color.white));
                total.setTextColor(getResources().getColor(R.color.light_gray));
            }

            switch(position)
            {
                case 0:
                    icon.setImageResource(R.drawable.songs);
                    label.setText(R.string.song);
                    total.setText(String.valueOf(mListSongs.size()));
                    break;
                case 1:
                    icon.setImageResource(R.drawable.albums);
                    label.setText(R.string.album);
                    total.setText(String.valueOf(listAlbum.size()));
                    break;
                case 2:
                    icon.setImageResource(R.drawable.artists);
                    label.setText(R.string.artist);
                    total.setText(String.valueOf(listArtist.size()));
                    break;
                case 3:
                    icon.setImageResource(R.drawable.playlists);
                    label.setText(R.string.playlist);
                    total.setText("");
                    break;
                case 4:
                    icon.setImageResource(R.drawable.favouritesongs);
                    label.setText(R.string.favourite_song);
                    total.setText("");
                    break;
                case 5:
                    icon.setImageResource(R.drawable.folders);
                    label.setText(R.string.folder);
                    total.setText(String.valueOf(listFolder.size()));
                    break;
                case 6:
                    icon.setImageResource(R.drawable.about);
                    label.setText(R.string.about);
                    break;
                default:
                    icon.setImageResource(R.drawable.exit);
                    label.setText(R.string.exit);
                    break;
            }

        return (row);
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
                                                        for (Song song : mListSongs)
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
        Libs.changeLang(lang, MainActivity.this);
        Log.e("LANG", lang);
    }

}

