package quang.project2.controller;

/*
 * Created by keban on 12/12/2016.
 */

import java.util.ArrayList;
import java.util.List;

import quang.project2.model.ListMusic;
import quang.project2.model.Song;

import static quang.project2.controller.CreateListSong.mListSongs;

public class CreateListArtist {

    public static List<ListMusic> listArtist;

    public static void getListAtist() {

        listArtist = new ArrayList();

        for (Song s : mListSongs) {

            int check = 0;

            for (ListMusic m : listArtist) {
                if(m.getListSong().size() > 0)
                    if (s.getArtist().compareTo(m.getName()) == 0) {
                        List<Song> lst = m.getListSong();
                        lst.add(s);
                        m.setListSong(lst);
                        check = 1;
                        break;
                }
            }

            if(check == 0) {

                List<Song> a = new ArrayList();
                a.add(s);
                ListMusic list = new ListMusic(s.getArtist(), a);
                listArtist.add(list);
            }
        }

        for(ListMusic m : listArtist)
        {
            for(ListMusic n : listArtist) {

                if (check(m.getName(), n.getName()) == true) {

                    ListMusic temp;
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
