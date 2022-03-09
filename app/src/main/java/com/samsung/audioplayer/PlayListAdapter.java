package com.samsung.audioplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class PlayListAdapter extends ArrayAdapter<PlayList> {

    List<PlayList> list;

    public PlayListAdapter(@NonNull Context context, @NonNull List<PlayList> objects) {
        super(context, R.layout.item_play_list, objects);
        list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_play_list, null);
        }

        TextView textView = convertView.findViewById(R.id.pl_text);
        textView.setText(list.get(position).getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackListActivity trackListActivity = (TrackListActivity) getContext();
                trackListActivity.selectPlayList(position);
            }
        });

        return convertView;
    }
}
