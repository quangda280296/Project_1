package quang.project2.controller;

/**
 * Created by keban on 12/24/2016.
 */
public class ThreadLoad extends Thread {

    @Override
    public void run()
    {
        CreateListArtist.getListAtist();
        CreateListAlbum.getListAlbum();
        CreateListFolder.getListFolder();

    }

}
