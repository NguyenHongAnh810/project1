package com.example.mymusic.Models;

import android.net.Uri;

public class Song {
    private String id;
    private String title;
    private String artist;
    private String src;
    private String fullName;
    private String album;
    private Uri File;

    public Song(String id, String title, String artist, String src, String fullName, String album, Uri file) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.src = src;
        this.fullName = fullName;
        this.album = album;
        File = file;
    }

    public String getSrc() {
        return src;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getFullName() {
        return fullName;
    }

    public Uri getFile() {
        return File;
    }
}
