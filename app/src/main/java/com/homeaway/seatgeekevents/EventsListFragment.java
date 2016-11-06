package com.homeaway.seatgeekevents;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.homeaway.seatgeekevents.utils.AutoCompleteAdapter;
import com.homeaway.seatgeekevents.customviews.DelayAutoCompleteTextView;
import com.homeaway.seatgeekevents.data.Event;


/**
 * Events List Fragment which provides the Type ahead
 */
public class EventsListFragment extends Fragment {

    private IEventsListInteractionListener mListener;
    private DelayAutoCompleteTextView searchEventsView;

    public EventsListFragment() {
        // Required empty public constructor
    }

    /**
     * Create new instance of Events List fragment
     *
     * @return A new instance of fragment EventsListFragment.
     */
    public static EventsListFragment newInstance() {
        EventsListFragment fragment = new EventsListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_list, container, false);

        searchEventsView = (DelayAutoCompleteTextView) view.findViewById(R.id.searchEventsView);
        searchEventsView.setThreshold(3);
        searchEventsView.setAdapter(new AutoCompleteAdapter(getActivity(),
                R.layout.autocomplete_list_row));
        searchEventsView.setLoadingIndicator(
                (android.widget.ProgressBar) view.findViewById(R.id.pb_loading_indicator));
        searchEventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Event event = (Event) adapterView.getItemAtPosition(position);
                searchEventsView.setText(event.getTitle());
                mListener.loadEventDetailsFragment(event);

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchEventsView != null) {
            searchEventsView.setText("");
            searchEventsView.requestFocus();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IEventsListInteractionListener) {
            mListener = (IEventsListInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement IEventsListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interfce for interaction with activity
     */
    public interface IEventsListInteractionListener {
        // TODO: Update argument type and name
        void loadEventDetailsFragment(Event event);
    }
}
