package quang.project2.view.homebackground;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import quang.project2.R;
import quang.project2.controller.ThreadLoad;
import quang.project2.controller.ThreadLoading;
import quang.project2.model.ListMusic;
import quang.project2.model.Song;

import static quang.project2.controller.CreateListSong.mListSongs;

/**
 * Created by keban on 12/11/2016.
 */
public class MainActivity extends Activity {

    public static List<Song> favouriteSong;
    public static List<ListMusic> playlist;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homebackground);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this,
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

                    int charRead;
                    while ((charRead = fisr.read(inputBuffer)) > 0) {

                        read += String.copyValueOf(inputBuffer, 0, charRead);

                        inputBuffer = new char[100];
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                if(read.compareTo("")!=0)
                {
                    result = read.split("@@@@@");
                    for (String s : result) {
                        if (s != "")
                        {
                            String []chuoi = s.split("<<<<<");
                            List<Song> a = new ArrayList();
                            ListMusic list = new ListMusic(chuoi[0], a);
                            playlist.add(list);

                            //if(chuoi.length > 1) {
                                String ket_qua[] = chuoi[1].split("###");

                                for (String str : ket_qua) {
                                    for (Song song : mListSongs)
                                        if (song.getPath().compareTo(str) == 0) {
                                            a.add(song);
                                            list.setListSong(a);
                                        }
                                }
                            //}
                        }
                    }
                }

                try {

                    loadList.join();

                    Thread bamgio = new Thread() {
                        public void run() {
                            try {
                                sleep(1000);
                            } catch (Exception e) {

                            } finally {
                                Intent activitymoi = new Intent("quang.project2.view.home.MainActivity");
                                startActivity(activitymoi);
                                finish();
                            }
                        }
                    };
                    bamgio.start();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


