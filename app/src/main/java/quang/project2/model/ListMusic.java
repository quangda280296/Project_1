package quang.project2.model;

import java.util.List;

/**
 * Created by keban on 12/21/2016.
 */
public class ListMusic {

    String name;
    List<Song> listSong;

    public ListMusic(String name, List<Song> listSong) {
        this.name = name;
        this.listSong = listSong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getListSong() {
        return listSong;
    }

    public void setListSong(List<Song> listSong) {
        this.listSong = listSong;
    }
}
