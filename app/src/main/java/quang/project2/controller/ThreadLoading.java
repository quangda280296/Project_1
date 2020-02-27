package quang.project2.controller;

import android.content.Context;

/*
 * Created by keban on 12/10/2016.
 */

public class ThreadLoading extends Thread {

    Context context;

    public ThreadLoading(Context c)
    {
        context = c;
    }

    @Override
    public void run()
    {
        CreateListSong.getListSongs(context);
    }

}
