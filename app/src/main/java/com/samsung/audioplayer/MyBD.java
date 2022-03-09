package com.samsung.audioplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyBD {
    private static final String DATABASE_NAME = "simple.db";
    private static final int DATABASE_VERSION = 4;
    private static final String TABLE_NAME = "trackList";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ID_IN_PROV = "in_provider";

    private static final int NUM_COLUMN_ID = 0;
    private static final int NUM_COLUMN_NAME = 1;
    private static final int NUM_COLUMN_IN_PROV = 2;

    private SQLiteDatabase dataBase;

    public MyBD(Context context) {
        OpenHelper openHelper = new OpenHelper(context);
        dataBase = openHelper.getWritableDatabase();
    }

    public List<String> getPLNames(){
        Cursor cursor = dataBase.query(true, TABLE_NAME, new String[] { COLUMN_NAME },
                null, null, COLUMN_NAME, null, null, null);
        List<String> list = new ArrayList<>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                String name = cursor.getString(0);
                list.add(name);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Track select(long id) {
        Cursor cursor = dataBase.query(TABLE_NAME, null, COLUMN_ID + " = " + id,
                null, null, null, null);
        Track track;
        if (cursor.moveToFirst()) {
            track = new Track();
            track.setId(cursor.getLong(NUM_COLUMN_ID));
            track.setTrackList(cursor.getString(NUM_COLUMN_NAME));
            track.setInProv(cursor.getLong(NUM_COLUMN_IN_PROV));
            cursor.close();
            return track;
        }
        cursor.close();
        return null;
    }

    public List<Track> selectAll() {
        Cursor cursor = dataBase.query(TABLE_NAME, null, null,
                null, null, null, null);
        ArrayList<Track> list = new ArrayList<>();
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            do {
                Track track = new Track();
                track.setId(cursor.getLong(NUM_COLUMN_ID));
                track.setTrackList(cursor.getString(NUM_COLUMN_NAME));
                track.setInProv(cursor.getLong(NUM_COLUMN_IN_PROV));
                list.add(track);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public long insert(List<Track> list) {
        if (list.size() == 0) {
            return 0;
        }
        long count = 0;
        for (Track track: list) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_NAME, track.getTrackList());
            cv.put(COLUMN_ID_IN_PROV, track.getInProv());
            dataBase.insert(TABLE_NAME, null, cv);
            count++;
        }
        return count;
    }

    public long insert(Track track) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, track.getTrackList());
        cv.put(COLUMN_ID_IN_PROV, track.getId());
        return dataBase.insert(TABLE_NAME, null, cv);
    }

    public long delete(long id){
        return dataBase.delete(TABLE_NAME,
                COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public long update(Track track){
        Log.d("My", track.toString());
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, track.getTrackList());
        contentValues.put(COLUMN_ID, track.getId());
        contentValues.put(COLUMN_ID_IN_PROV, track.getInProv());
        return dataBase.update(TABLE_NAME, contentValues,
                COLUMN_ID + "=?", new String[]{String.valueOf(track.getId())});
    }

    private class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String query = "create table " + TABLE_NAME + " (" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_NAME + " text not null, " +
                    COLUMN_ID_IN_PROV + " integer not null);";
            Log.d("My", query);
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            onCreate(db);
        }
    }
}
