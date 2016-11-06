package com.homeaway.seatgeekevents;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.homeaway.seatgeekevents.data.Event;
import com.homeaway.seatgeekevents.data.ISeatGeekResponseLoader;
import com.homeaway.seatgeekevents.utils.SeatGeekApiConstants;
import com.homeaway.seatgeekevents.data.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EventsChooserActivity extends AppCompatActivity implements ISeatGeekResponseLoader,
        EventsListFragment.IEventsListInteractionListener {


    public static ArrayList<Event> eventsList;
    private View eventsView;
    private EventsListFragment eventsListFragment;

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_chooser);
        eventsView = findViewById(R.id.frame_layout);
        loadEventsListFragment();

    }

    /**
     * Loads Events List Fragment
     */
    private void loadEventsListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        eventsListFragment = EventsListFragment.newInstance();
        fragmentTransaction.replace(R.id.frame_layout, eventsListFragment);
        fragmentTransaction.commit();

    }

    /**
     * Loads EventDetails fragment
     *
     * @param event
     */
    @Override
    public void loadEventDetailsFragment(Event event) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        EventDetailsFragment eventDetailsFragment = EventDetailsFragment.newInstance(event);
        fragmentTransaction.replace(R.id.frame_layout, eventDetailsFragment,
                SeatGeekApiConstants.EVENT_LIST_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(SeatGeekApiConstants.EVENT_LIST_FRAGMENT_TAG);
        fragmentTransaction.commit();

    }

    /**
     * Interacts with seat geek api and process the response
     *
     * @param constraintString
     * @return
     */
    public ArrayList<Event> loadSeatGeekEvents(String constraintString) {

        String encodedConstraintString = null;
        try {
            encodedConstraintString = URLEncoder.encode(constraintString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(SeatGeekApiConstants.API_URL
                + encodedConstraintString, null, future, future);

        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);

        try {
            JSONObject response = future.get(10, TimeUnit.SECONDS); // Blocks for at most 10 seconds.

            Log.d(TAG, "Response from Seat Geek API");
            Log.d(TAG, response.toString());

            JSONArray eventsJson = response.getJSONArray("events");
            ArrayList<Event> events = Event.fromJson(eventsJson);
            eventsList = events;

        } catch (JSONException e) {
            Log.d(TAG, "Json Exception");
            Log.d(TAG, e.getMessage());
        } catch (InterruptedException e) {
            Log.d(TAG, "Interrupted Exception");
            Log.d(TAG, e.getMessage());
        } catch (ExecutionException e) {
            Log.d(TAG, "Execution Exception");
            Log.d(TAG, e.getMessage());
        } catch (TimeoutException e) {
            Log.d(TAG, "Timeout Exception");
            Log.d(TAG, e.getMessage());
        }
        return eventsList;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
