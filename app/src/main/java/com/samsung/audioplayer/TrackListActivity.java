package com.samsung.audioplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class TrackListActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView btPlay, btPause;
    Button btCreatePL;
    EditText edName;
    List<Track> tracks = new ArrayList<>();
    List<PlayList> playLists = new ArrayList<>();
    ListView tracksListView;
    LinearLayout plLinearLayout;
    Spinner spinner;
    TrackAdapter mainAdapter;
    MediaPlayer player;
    int currentTrack = 0;
    final static int MY_PERMISSION_REQUEST = 1;

    MyBD dataBase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.track_list_activity);

        init();

        if (checkPermission()){
            getMusic();
            getPlayLists();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
        }
    }

    private void init(){
        tracksListView = findViewById(R.id.trackList);
        LinearLayout linearLayout = findViewById(R.id.player);
        btPlay = linearLayout.findViewById(R.id.play);
        btPause = linearLayout.findViewById(R.id.pause);
        btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btCreatePL = findViewById(R.id.bt_createPL);
        btCreatePL.setOnClickListener(this);
        edName = findViewById(R.id.ed_name_pl);

        mainAdapter = new TrackAdapter(this, tracks, true);

        plLinearLayout = findViewById(R.id.playList);

        PlayList allSongs = new PlayList("все песни", tracks);
        playLists.add(allSongs);
        addPLView(allSongs, true);

        spinner = findViewById(R.id.spinner);
        setSpinnerAdapter();

        dataBase = new MyBD(this);
    }

    private List<String> getPLName(){
        List<String> list = new ArrayList<>();
        for (int i = 1; i < playLists.size(); i++) {
            list.add(playLists.get(i).getName());
        }
        return list;
    }

    private void getPlayLists(){
        List<String> list = dataBase.getPLNames();
        for (int i = 0; i < list.size(); i++) {
            PlayList playList = new PlayList(list.get(i));
            playLists.add(playList);
            addPLView(playList, false);
        }

        List<Track> trackList = dataBase.selectAll();
        for (int i = 0; i < trackList.size(); i++) {
            Track track = trackList.get(i);
            Log.d("My", "Трек из базы: " + track.toString());
            for (int j = 0; j < tracks.size(); j++) {
                if (track.getInProv() == tracks.get(j).getId()){
                    for (int l = 0; l < playLists.size(); l++){
                        if (track.getTrackList().equals(playLists.get(l).getName())){
                            Track track1 = tracks.get(j);
                            track1.setInBase(track.getId());
                            playLists.get(l).getTracks().add(track1);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        setSpinnerAdapter();
    }

    private void setSpinnerAdapter(){
        if (playLists.size() < 2){
            return;
        }
        List<String> list = getPLName();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
    }

    private void addPLView(PlayList playList, boolean first){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.item_play_list, null);
        plLinearLayout.addView(view);
        TextView textView = view.findViewById(R.id.pl_text);
        textView.setText(playList.getName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainAdapter = new TrackAdapter(TrackListActivity.this, playList.getTracks(), first);
                tracksListView.setAdapter(mainAdapter);
            }
        });
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

    private boolean checkPermission(){
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
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
            } while (songCursor.moveToNext());
        }

        mainAdapter = new TrackAdapter(this, tracks, true);
        tracksListView.setAdapter(mainAdapter);
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
            case R.id.bt_createPL:
                createPL();
                break;
        }
    }

    public void addTrack(Track track){
        if (spinner.getCount() == 0){
            return;
        }
        track.setTrackList(spinner.getSelectedItem().toString());
        long count = dataBase.insert(track);
        Toast.makeText(this, "Было добавлено " + count + " записей", Toast.LENGTH_SHORT).show();

        playLists.get(spinner.getSelectedItemPosition() + 1).getTracks().add(track);
    }

    public void removeTrack(Track track){
        Log.d("My", "Трек на удаление: " + track.toString());
        long count = dataBase.delete(track.getInBase());
        Toast.makeText(this, "Было удалено " + count + " записей", Toast.LENGTH_SHORT).show();
    }

    private void createPL(){
        String name = edName.getText().toString();
        if (!name.equals("")){
            for (PlayList playList: playLists){
                if (playList.getName().equals(name)){
                    Toast.makeText(this, "Плейлист с таким именем уже существует", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            PlayList playList = new PlayList(name);
            playLists.add(playList);
            setSpinnerAdapter();
            addPLView(playList, false);
        }
    }

    public void selectPlayList(int position){
        mainAdapter = new TrackAdapter(this, playLists.get(position).getTracks(), position==0);
        tracksListView.setAdapter(mainAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:{
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
                    getMusic();
                    getPlayLists();
                }
                else {
                    Toast.makeText(this, "permission failed", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
}
