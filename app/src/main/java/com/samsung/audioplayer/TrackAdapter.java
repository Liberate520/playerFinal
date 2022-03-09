package com.samsung.audioplayer;

import android.Manifest;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TrackAdapter extends ArrayAdapter<Track> {

    List<Track> list;
    TrackListActivity trackListActivity;
    boolean first;

    public TrackAdapter(@NonNull Context context, @NonNull List<Track> objects, boolean first) {
        super(context, R.layout.item_track_list, objects);
        list = objects;
        trackListActivity = (TrackListActivity) context;
        this.first = first;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_track_list, null);
        }

        LinearLayout clickZone = convertView.findViewById(R.id.clickZone);
        clickZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Track track = list.get(position);
                trackListActivity.play(track);
            }
        });

        Track track = list.get(position);

        ImageView addTrack = convertView.findViewById(R.id.addTrack);
        if (!first) {
            addTrack.setImageResource(R.drawable.ic_baseline_clear_24);
            addTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trackListActivity.removeTrack(track);
                    list.remove(track);
                    notifyDataSetChanged();
                }
            });
        } else {
            addTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trackListActivity.addTrack(track);
                }
            });
        }


        TextView title = convertView.findViewById(R.id.trackTitle);
        title.setText(track.getTitle());

        TextView author = convertView.findViewById(R.id.trackAuthor);
        author.setText(track.getAuthor());

        TextView duration = convertView.findViewById(R.id.trackDuration);
        duration.setText(MainActivity.timeFormatter((int) track.getDuration()));

        return convertView;
    }
}
