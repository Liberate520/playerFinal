package com.samsung.audioplayer;

public class Track {
    private long id;
    private String title;
    private String author;
    private String path;
    private long duration;
    private String trackList;
    private long inProv;
    private long inBase;

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", path='" + path + '\'' +
                ", duration=" + duration +
                ", trackList='" + trackList + '\'' +
                ", inProv=" + inProv +
                '}';
    }

    public long getInBase() {
        return inBase;
    }

    public void setInBase(long inBase) {
        this.inBase = inBase;
    }

    public long getInProv() {
        return inProv;
    }

    public void setInProv(long inProv) {
        this.inProv = inProv;
    }

    public String getTrackList() {
        return trackList;
    }

    public void setTrackList(String trackList) {
        this.trackList = trackList;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
