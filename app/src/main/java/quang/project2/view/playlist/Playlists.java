package quang.project2.view.playlist;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import android.support.design.widget.FloatingActionButton;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.AdapterView;
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

import static quang.project2.view.homebackground.MainActivity.favouriteSong;
import static quang.project2.view.homebackground.MainActivity.playlist;
import static quang.project2.controller.CreateListSong.mListSongs;

/**
 * Created by keban on 12/15/2016.
 */
public class Playlists extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static String theme = "li";
    private ExplosionField mExplosionField;
    private BoomMenuButton boomMenuButtonInActionBar;
    private Context mContext;
    private View mCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
        mExplosionField = ExplosionField.attach2Window(this);
        //addListener(findViewById(R.id.root));

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(this);

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
                theme = readString;
                inputBuffer = new char[100];
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        FrameLayout root = (FrameLayout)findViewById(R.id.root);
        if(theme.compareTo("ni") == 0)
        {
            root.setBackgroundColor(getResources().getColor(R.color.black));
        }

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText(R.string.playlist);

        boomMenuButtonInActionBar = (BoomMenuButton) mCustomView.findViewById(R.id.boom);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        ((Toolbar) mCustomView.getParent()).setContentInsetsAbsolute(0,0);

        initBoom();
    }

    List<Song> ls = new ArrayList();

    public class SetDS extends ArrayAdapter<String> {

        Activity context;

        public SetDS(Activity context) {

            super(context, R.layout.list_music, item);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.custom, null);

            TextView label = (TextView) row.findViewById(R.id.name);
            label.setText(mListSongs.get(position).getName());

            final CheckBox check = (CheckBox)row.findViewById(R.id.check);

            final Song song = mListSongs.get(position);

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(check.isChecked() == true)
                    {
                        ls.add(song);
                    }
                    else
                    {
                        ls.remove(song);
                    }
                }
            });

            return row;
        }
    }

    String item[];

    public void displayInput()
    {
        item = new String[mListSongs.size()];
        int i = 0;

        for(Song s : mListSongs)
        {
            item[i] = s.getName();
            i++;
        }

        dialog.hide();
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.new_playlist, null);
        final EditText edit = (EditText)alertLayout.findViewById(R.id.edit);

        ListView list = (ListView)alertLayout.findViewById(R.id.list);

        ArrayAdapter aa = new SetDS(this);
        list.setAdapter(aa);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(edit.getText().toString().compareTo("") == 0)
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.noti), Toast.LENGTH_SHORT).show();
                        else
                        {
                            int check = 1;
                            for(ListMusic p : playlist)
                                if(p.getName().compareTo(edit.getText().toString())==0)
                                {
                                    check = 0;
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.noti), Toast.LENGTH_SHORT).show();
                                }

                            if(check == 1)
                            {
                                ListMusic a = new ListMusic(edit.getText().toString(), ls);
                                playlist.add(a);
                                write_playlist();
                                startActivity(new Intent("quang.project2.view.playlist.Playlists"));
                                finish();
                            }
                        }
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

    AlertDialog input;
    AlertDialog dialog;

    public void displayEdit(final List<Song> list)
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.rename, null);
        final EditText edit = (EditText)alertLayout.findViewById(R.id.edit);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edit.getText().toString().compareTo("") == 0)
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.noti), Toast.LENGTH_SHORT).show();
                else
                {
                    int check = 1;
                    for(ListMusic p : playlist)
                        if(p.getName().compareTo(edit.getText().toString())==0)
                        {
                            check = 0;
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.noti), Toast.LENGTH_SHORT).show();
                        }

                    if(check == 1)
                    {
                        playlist.remove(po);
                        ListMusic lt = new ListMusic(edit.getText().toString(), list);
                        playlist.add(lt);
                        write_playlist();
                        startActivity(new Intent("quang.project2.view.playlist.Playlists"));
                        finish();
                    }
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rename.hide();
                    }
                });

        rename = alert.create();
        rename.show();
    }

    AlertDialog rename;

    public void displayDialog()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.edit, null);

        FrameLayout add = (FrameLayout)alertLayout.findViewById(R.id.add);
        FrameLayout edit = (FrameLayout)alertLayout.findViewById(R.id.edit);
        FrameLayout delete = (FrameLayout)alertLayout.findViewById(R.id.delete);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInput();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "rename";
                dialog.hide();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.tap_to_rename), Toast.LENGTH_SHORT).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = "delete";
                dialog.hide();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.tap_to_delete), Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        dialog = alert.create();
        dialog.show();
    }

    public class ImageAdapter extends BaseAdapter {
        private Activity mContext;

        public ImageAdapter(Activity c) {
            mContext = c;
        }

        public int getCount() {
            return playlist.size();
        }

        public Object getItem(int position) {

            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = mContext.getLayoutInflater();
            View row = inflater.inflate(R.layout.pl, null);
            TextView label = (TextView)row.findViewById(R.id.label);
            if(theme.compareTo("ni") == 0)
               label.setTextColor(row.getResources().getColor(R.color.white));

            label.setText(playlist.get(position).getName());

            return row;
        }
    }
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                switch (mode)
                {
                    case "normal":
                        //ps = position;
                        lsts = playlist.get(position).getListSong();
                        name = playlist.get(position).getName();
                        startActivity(new Intent("quang.project2.view.playlist.FavouriteSongs"));
                        break;
                    case "rename":
                        po = position;
                        displayEdit(playlist.get(position).getListSong());
                        break;
                    case "delete":
                        displayConfirm(v, position);
                        break;
                }
        }

    static String name;

    public void displayConfirm(final View v, final int position)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Terminator");
        alert   .setMessage(getResources().getString(R.string.confirm));
        alert     .setIcon(android.R.drawable.ic_delete);
        alert     .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            playlist.remove(position);
                            mExplosionField.explode(v);
                            write_playlist();

                        }
                    });
        final AlertDialog a = alert.create();
        alert    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        a.hide();
                    }
                });
        alert.create();
        alert.show();
    }
    String mode = "normal";
    static List<Song> lsts;
    static int ps;
    int po;

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

        switch (theme)
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

                theme = "ni";
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

                theme = "li";
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

                theme = "ni";
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

                theme = "li";
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
        Libs.changeLang(lang, Playlists.this);
        Log.e("LANG", lang);
    }
}
