package com.example.mymusic.Utilities;

import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.mymusic.Activity.MainActivity;
import com.example.mymusic.Models.Song;

import java.util.ArrayList;
import java.util.Collections;

public class SongManagement {
    public static MediaPlayer mediaPlayer;
    public static ArrayList<Integer> orderSong;
    public static int currentPos = -1;
    public static ArrayList<Song> listSong;
    private static SongManagement instance;

    private SongManagement() {
        listSong = new ArrayList<>();
        orderSong = new ArrayList<>();
    }

    public static SongManagement getInstance() {
        if (instance == null) {
            instance = new SongManagement();
        }
        return instance;
    }

    public void setListSong() {
        if (listSong == null) listSong = new ArrayList<>();
        listSong.clear();
        performSearch();
        for (int i = 0; i < listSong.size(); i++) {
            orderSong.add(i);
        }
    }

    private void performSearch()
    {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.ALBUM
        };

        Cursor music = MainActivity.getInstance().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null, null);

        while ( music.moveToNext() )
        {
            String id = music.getString(0);
            String artist =  music.getString(1);
            String title = music.getString(2);
            String src =  music.getString(3);
            String fullName = music.getString(4);
            String album = music.getString(5);
            Uri path = Uri.parse(MediaStore.Audio.Media.getContentUri("external").toString() + "/" + id);
            SongManagement.listSong.add(new Song(id, title, artist, src, fullName, album, path));
        }
    }

    public void shuffle() {
        Collections.shuffle(orderSong);
    }

    public void restore() {
        Collections.sort(orderSong);
    }

    public int getPosRoot(int oderPos) {
        for (int i = 0; i < SongManagement.orderSong.size(); i++) {
            if (SongManagement.orderSong.get(i) == oderPos) {
                return i;
            }
        }
        return 0;
    }

    public Song getSong(int pos) {
        return listSong.get(orderSong.get(pos));
    }

}
