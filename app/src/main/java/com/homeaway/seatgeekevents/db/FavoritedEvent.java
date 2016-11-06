package com.homeaway.seatgeekevents.db;

/**
 * Created by Jegan Kabilan on 11/6/2016.
 */

public class FavoritedEvent {
    int _id;
    String _event_id;
    String _event_name;

    public FavoritedEvent() {

    }

    public FavoritedEvent(int id, String eventId, String eventName) {
        this._id = id;
        this._event_id = eventId;
        this._event_name = eventName;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getEventId() {
        return _event_id;
    }

    public void setEventId(String _event_id) {
        this._event_id = _event_id;
    }

    public String getEventName() {
        return _event_name;
    }

    public void setEventName(String _event_name) {
        this._event_name = _event_name;
    }
}
