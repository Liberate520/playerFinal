package com.samsung.audioplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TrackListActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView btPlay, btPause;
    List<Track> tracks = new ArrayList<>();
    ListView listView;
    MediaPlayer player;
    int currentTrack = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.track_list_activity);

        getMusic();
        listView = findViewById(R.id.trackList);
        TrackAdapter trackAdapter = new TrackAdapter(this, tracks);
        listView.setAdapter(trackAdapter);

        LinearLayout linearLayout = findViewById(R.id.player);
        btPlay = linearLayout.findViewById(R.id.play);
        btPause = linearLayout.findViewById(R.id.pause);
        btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);
    }

    private void inverseButton(boolean visible){
        if (visible){
            btPlay.setVisibility(View.GONE);
            btPause.setVisibility(View.VISIBLE);
        } else {
            btPlay.setVisibility(View.VISIBLE);
            btPause.setVisibility(View.GONE);
        }
    }

    public void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        Log.d("no Tag", "1");
        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do {
                Track track = new Track();
                track.setTitle(songCursor.getString(songTitle));
                track.setAuthor(songCursor.getString(songArtist));
                track.setId(songCursor.getLong(songId));
                track.setPath(songCursor.getString(songPath));
                track.setDuration(songCursor.getLong(songDuration));
                tracks.add(track);
                Log.d("My", track.toString());
            } while (songCursor.moveToNext());
        }
        Log.d("My", String.valueOf(tracks.size()));
    }

    public void play(Track track){
        if (player != null && player.isPlaying()){
            player.stop();
        }
        Uri uri = Uri.parse(track.getPath());
        player = MediaPlayer.create(this, uri);
        player.start();
        inverseButton(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play:
                if (tracks.size() == 0){
                    return;
                }
                if (player == null){
                    play(tracks.get(currentTrack));
                } else if (!player.isPlaying()){
                    player.start();
                    inverseButton(true);
                }
                break;
            case R.id.pause:
                if (player != null){
                    player.pause();
                    inverseButton(false);
                }
                break;
        }
    }
}
