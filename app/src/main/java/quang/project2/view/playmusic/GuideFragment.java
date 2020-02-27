/*
 * Copyright 2015 chenupt
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

package quang.project2.view.playmusic;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.Snackbar;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.support.design.widget.CoordinatorLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import quang.project2.R;
import quang.project2.model.Song;

import static quang.project2.controller.CreateListSong.mListSongs;
import static quang.project2.view.home.MainActivity.mode;

import static quang.project2.controller.playmusic.PlayMusic.current_song;
import static quang.project2.controller.playmusic.PlayMusic.listSongs;
import static quang.project2.controller.playmusic.PlayMusic.mediaPlayer;
import static quang.project2.controller.playmusic.PlayMusic.play;
import static quang.project2.controller.playmusic.PlayMusic.posit;

/**
 * Created by chenupt@gmail.com on 2015/1/31.
 * Description TODO
 */
public class GuideFragment extends Fragment {

    RecyclerView recyclerView;
    MusicAdapter adapter;
    SwipeToAction swipeToAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View row = inflater.inflate(R.layout.fragment_guide, container, false);

        recyclerView = (RecyclerView) row.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton)row.findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayDialog();
            }
        });

        final CoordinatorLayout root = (CoordinatorLayout)row.findViewById(R.id.coordinatorLayout);
        LinearLayout home = (LinearLayout)row.findViewById(R.id.home);
        LinearLayout parent = (LinearLayout)row.findViewById(R.id.root);

        if(mode.compareTo("ni") == 0)
        {
            home.setBackgroundColor(getResources().getColor(R.color.black));
            parent.setBackgroundColor(getResources().getColor(R.color.dark_gray_pressed));
        }

        adapter = new MusicAdapter(listSongs);
        recyclerView.setAdapter(adapter);

        swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SwipeListener<Song>() {
            @Override
            public boolean swipeLeft(final Song itemData) {
                final int pos = removeSong(itemData);
                displaySnackbar(itemData.getTitle() + " removed", "Undo", root, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addSong(pos, itemData);
                    }
                });
                return true;
            }

            @Override
            public boolean swipeRight(final Song itemData) {
                final int pos = removeSong(itemData);
                displaySnackbar(itemData.getTitle() + " removed", "Undo", root, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addSong(pos, itemData);
                    }
                });
                return true;
            }

            @Override
            public void onClick(Song itemData) {
                if(mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(itemData.getPath());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                for(int i = 0; i < listSongs.size(); i++)
                {
                    if(listSongs.get(i)==itemData)
                        posit = i;
                }

                current_song = itemData;

                startActivity(new Intent("quang.project2.view.playmusic.PlayMusic"));
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        play();
                    }
                };
            }

            @Override
            public void onLongClick(Song itemData) {

            }
        });

        // use swipeLeft or swipeRight and the elem position to swipe by code
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeToAction.swipeRight(2);
            }
        }, 3000);

        return row;
    }

    String item[];
    AlertDialog input;

    public void displayDialog(){

        ls = new ArrayList();
        item = new String[mListSongs.size()];
        int i = 0;

        for(Song s : mListSongs)
        {
            item[i] = s.getName();
            i++;
        }

        LayoutInflater inflater = getLayoutInflater(getArguments());
        View alertLayout = inflater.inflate(R.layout.edit_playlist, null);

        ListView list = (ListView)alertLayout.findViewById(R.id.list);

        ArrayAdapter aa = new SetDS(getActivity());
        list.setAdapter(aa);

        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setView(alertLayout);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                listSongs = ls;
                startActivity(new Intent("quang.project2.view.playmusic.PlayMusic"));
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

    List<Song> ls;

    public class SetDS extends ArrayAdapter<String> {

        Activity context;

        public SetDS(Activity context) {

            super(context, R.layout.list_music, item);
            this.context = context;

            for(Song song : mListSongs)
                for(int c = 0; c < listSongs.size(); c++)
                    if (song.getName().compareTo(listSongs.get(c).getName()) == 0)
                        ls.add(song);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View row = inflater.inflate(R.layout.custom, null);

            TextView label = (TextView) row.findViewById(R.id.name);
            label.setText(mListSongs.get(position).getName());

            final CheckBox check = (CheckBox)row.findViewById(R.id.check);

            final Song song = mListSongs.get(position);

            for(int c = 0; c < listSongs.size(); c++) {
                if (song.getName().compareTo(listSongs.get(c).getName()) == 0) {

                    check.setChecked(true);
                    break;
                }
            }

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(check.isChecked() == true)
                    {
                        ls.add(song);
                    }
                    else
                    if(check.isChecked() == false)
                    {
                        ls.remove(song);
                    }
                }
            });

            return row;
        }
    }

    private void displaySnackbar(String text, String actionName, CoordinatorLayout root, View.OnClickListener action) {

        Snackbar snack = Snackbar.make(root, text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snack.getView();
        v.setBackgroundColor(root.getResources().getColor(R.color.secondary));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }

    private int removeSong(Song song) {
        int pos = listSongs.indexOf(song);
        listSongs.remove(song);
        adapter.notifyItemRemoved(pos);
        return pos;
    }

    private void addSong(int pos, Song song) {
        listSongs.add(pos, song);
        adapter.notifyItemInserted(pos);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
