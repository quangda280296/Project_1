package quang.project2.model;

/**
 * Created by keban on 12/10/2016.
 */
public class Song {

    private int id;
    private String name ;
    private String title;
    private String album;
    private String artist;
    private int duration;
    private int size;
    private String path;
    private String format;

    public Song(int id, String name, String title, String album, String artist, int duration, int size, String path, String format) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.size = size;
        this.path = path;
        this.format = format;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

}
