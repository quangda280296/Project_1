package quang.project2.controller;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import quang.project2.model.Song;

/**
 * Created by keban on 12/10/2016.
 */

public class CreateListSong {

    public static List<Song> mListSongs;

    public static void getListSongs(Context context) {

        mListSongs = new ArrayList<Song>();
        List<Song> listSongs = new ArrayList<Song>();
        Uri uri;
        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] m_data = {MediaStore.Audio.Media.DISPLAY_NAME,
                           MediaStore.Audio.Media.TITLE,
                           MediaStore.Audio.Media.ALBUM,
                           MediaStore.Audio.Media.ARTIST,
                           MediaStore.Audio.Media.DURATION,
                           MediaStore.Audio.Media.SIZE,
                           MediaStore.Audio.Media.DATA};

        Cursor c = context.getContentResolver().query(uri, m_data, MediaStore.Audio.Media.IS_MUSIC + "=1", null,
                MediaStore.Audio.Media.TITLE + " ASC");

        int id = 0;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            String name, title, album, artist, path, format;
            int duration, size;

            name = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            title = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
            artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            duration = (int) (c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
            size = (int) (c.getInt(c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
            path = c.getString(c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));

            String words[] = path.split("[.]");
            format = words[1];

            Song song = new Song(id, name, title, album, artist, duration, size, path, format);
            listSongs.add(song);

            id++;
        }

        for(Song s : listSongs)
        {
            int check = 0;
            for(Song q : mListSongs) {
                if (s.getTitle().compareTo(q.getTitle()) == 0)
                    if(s.getArtist().compareTo(q.getArtist()) == 0)
                    {
                        check = 1;
                        break;
                    }
            }

            if(check == 0)
                mListSongs.add(s);
        }

        for(Song m : mListSongs)
        {
            for(Song n : mListSongs) {

                if (check(m.getTitle(), n.getTitle()) == true) {

                    Song temp;
                    temp = m;
                    m = n;
                    n = temp;
                }
            }
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

}
