package com.homeaway.seatgeekevents.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jegan Kabilan on 11/3/2016.
 */

public class Event implements Parcelable {
    private String id;
    private String title;
    private String datetimeLocal;
    private String city;
    private String state;
    private String image;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected Event(Parcel in) {
        id = in.readString();
        title = in.readString();
        datetimeLocal = in.readString();
        city = in.readString();
        state = in.readString();
        image = in.readString();
        name = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public Event() {

    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDatetimeLocal() {
        return datetimeLocal;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getImage() {
        return image;
    }

    public static Event fromJson(JSONObject jsonObject) {
        Event event = new Event();
        try {
            event.id = jsonObject.getString("id");
            event.title = jsonObject.getString("title");
            event.datetimeLocal = jsonObject.getString("datetime_local");
            event.city = jsonObject.getJSONObject("venue").getString("city");
            event.state = jsonObject.getJSONObject("venue").getString("state");
            event.image = jsonObject.getJSONArray("performers").getJSONObject(0).getString("image");
            event.name = jsonObject.getJSONArray("performers").getJSONObject(0).getString("name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

    public static ArrayList<Event> fromJson(JSONArray jsonArray) {
        JSONObject eventJson = null;
        ArrayList<Event> events = new ArrayList<Event>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                eventJson = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Event event = Event.fromJson(eventJson);

            if(event != null) {
                events.add(event);
            }
        }

        return events;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(datetimeLocal);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(image);
        dest.writeString(name);
    }
}
