package quang.project2.controller.playmusic;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import quang.project2.model.Song;

/**
 * Created by keban on 1/15/2017.
 */
public class PlayMusic extends AppCompatActivity {

    public static int posit = 0;
    public static String repeat = "off";
    public static String shuffle = "off";
    public static List<Song> listSongs = new ArrayList();
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static Song current_song;
    public static int music_play;
    public static int array[];

    public static void play() {

        if (repeat.compareTo("all") == 0 && shuffle.compareTo("off") == 0) {

            if (posit == listSongs.size() - 1)
                posit = -1;

            posit++;
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(listSongs.get(posit).getPath());
                mediaPlayer.prepare();
                current_song = listSongs.get(posit);
                mediaPlayer.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (repeat.compareTo("all") == 0 && shuffle.compareTo("on") == 0) {

            if (music_play < listSongs.size()) {

                boolean check = false;
                int rand = 0;

                while (check == false) {
                    rand = rand(0, listSongs.size() - 1);

                    boolean kt = true;
                    for (int a : array)
                        if (rand == a) {
                            kt = false;
                            break;
                        }

                    if (kt == true) {

                        check = true;
                        array[music_play] = rand;
                        music_play++;
                    }
                }

                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(listSongs.get(rand).getPath());
                    mediaPlayer.prepare();
                    current_song = listSongs.get(rand);
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (music_play == listSongs.size()) {
                    array = new int[listSongs.size()];
                    music_play = 0;
                }
            }
        }

        if (repeat.compareTo("off") == 0 && shuffle.compareTo("on") == 0) {

            if (music_play < listSongs.size()) {

                boolean check = false;
                int rand = 0;

                while (check == false) {
                    rand = rand(0, listSongs.size() - 1);

                    boolean kt = true;
                    for (int a : array)
                        if (rand == a) {
                            kt = false;
                            break;
                        }

                    if (kt == true) {

                        check = true;
                        array[music_play] = rand;
                        music_play++;
                    }
                }

                music_play++;

                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(listSongs.get(rand).getPath());
                    mediaPlayer.prepare();
                    current_song = listSongs.get(rand);
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (repeat.compareTo("off") == 0 && shuffle.compareTo("off") == 0) {

            if (posit < listSongs.size() - 1) {

                posit++;
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(listSongs.get(posit).getPath());
                    mediaPlayer.prepare();
                    current_song = listSongs.get(posit);
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int rand(int min, int max) {
        try {
            Random rn = new Random();
            int range = max - min + 1;
            int randomNum = min + rn.nextInt(range);
            return randomNum;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
