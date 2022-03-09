package com.samsung.audioplayer;

import java.util.ArrayList;
import java.util.List;

public class PlayList {

    private List<Track> tracks;
    private String name;

    public PlayList(String name, List<Track> list) {
        this.name = name;
        this.tracks = list;
    }

    public PlayList(String name) {
        this.name = name;
        tracks = new ArrayList<>();
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
