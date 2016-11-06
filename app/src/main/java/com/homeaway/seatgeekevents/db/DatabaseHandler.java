package com.homeaway.seatgeekevents.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.R.attr.id;

/**
 * Created by Jegan Kabilan on 11/6/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "eventsManager";

    // Contacts table name
    private static final String TABLE_FAVORITED_EVENTS = "favoritedEvents";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_EVENT_ID = "event_id";
    private static final String KEY_EVENT_NAME = "event_name";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITED_EVENTS_TABLE =  "CREATE TABLE " + TABLE_FAVORITED_EVENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EVENT_ID + " TEXT,"
                + KEY_EVENT_NAME + " TEXT" + ")";
        db.execSQL(CREATE_FAVORITED_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITED_EVENTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * Add favorited event to database
     * @param event
     */
    public void addFavoritedEvent(FavoritedEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_ID, event.getEventId());
        values.put(KEY_EVENT_NAME, event.getEventName());

        db.insert(TABLE_FAVORITED_EVENTS, null, values);
        db.close();
    }

    /**
     * Get the favorited event, if available
     * @param eventId
     * @return
     */
    public FavoritedEvent getFavoritedEvent(String eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITED_EVENTS, new String[] { KEY_ID,
                        KEY_EVENT_ID, KEY_EVENT_NAME }, KEY_EVENT_ID + "=?",
                new String[] {String.valueOf(eventId)}, null, null, null, null);

        if ((cursor != null) && cursor.moveToFirst()){

            FavoritedEvent favoritedEvent = new FavoritedEvent(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2));
            // return contact
            return favoritedEvent;
        } else {
            return null;
        }
    }

    /**
     * Update a favorited event
     * @param event
     * @return
     */
    public int updateFavoritedEvent(FavoritedEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_ID, event.getEventId());
        values.put(KEY_EVENT_NAME, event.getEventName());

        return db.update(TABLE_FAVORITED_EVENTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(event.getId()) });
    }

    /**
     * Delete a previously favorited event
     * @param event
     */
    public void deleteFavoritedEvent(FavoritedEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITED_EVENTS, KEY_EVENT_ID + " = ?",
                new String[] { String.valueOf(event.getEventId()) });
        db.close();
    }
}
