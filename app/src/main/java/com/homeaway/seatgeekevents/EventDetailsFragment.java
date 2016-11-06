package com.homeaway.seatgeekevents;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.homeaway.seatgeekevents.db.DatabaseHandler;
import com.homeaway.seatgeekevents.db.FavoritedEvent;
import com.homeaway.seatgeekevents.customviews.CircularNetworkImageView;
import com.homeaway.seatgeekevents.data.Event;
import com.homeaway.seatgeekevents.utils.SeatGeekApiConstants;
import com.homeaway.seatgeekevents.data.VolleySingleton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment {

    private Event mEventObj;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Creates new instance of Event details fragment
     *
     * @param eventObj Parameter 2.
     * @return A new instance of fragment EventDetailsFragment.
     */
    public static EventDetailsFragment newInstance(Event eventObj) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(SeatGeekApiConstants.EVENT_PARAM, eventObj);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEventObj = getArguments().getParcelable(SeatGeekApiConstants.EVENT_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        TextView constraintText = (TextView) view.findViewById(R.id.constraintText);
        constraintText.setText(mEventObj.getName());
        constraintText.setShadowLayer(1, 0, 0, Color.BLACK);

        String location = mEventObj.getCity();
        String state = mEventObj.getState();
        if (state != null && !state.equalsIgnoreCase("null")) {
            location = location + ", " + mEventObj.getState();
        }

        String dateTimeLocal = mEventObj.getDatetimeLocal();
        String formattedDate = "";
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat targetFormat = new SimpleDateFormat("EEE, dd MMM yyyy h:mm a");
        try {
            Date date = originalFormat.parse(dateTimeLocal);
            formattedDate = targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ((TextView) view.findViewById(R.id.eventTitleText)).setText(mEventObj.getTitle());
        ((TextView) view.findViewById(R.id.venueText)).setText(location);
        ((TextView) view.findViewById(R.id.timeText)).setText(formattedDate);

        if (mEventObj.getImage() != null) {
            ImageLoader imageLoader = VolleySingleton.getInstance(getActivity()).getImageLoader();
            ((CircularNetworkImageView) view.findViewById(R.id.eventImage)).
                    setImageUrl(mEventObj.getImage(), imageLoader);
        } else {
            ((CircularNetworkImageView) view.findViewById(R.id.eventImage)).
                    setImageResource(R.drawable.noimage);
        }

        view.findViewById(R.id.backbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        final ImageView favoriteImageView = (ImageView) view.findViewById(R.id.favorite);
        final DatabaseHandler db = new DatabaseHandler(getActivity());

        final FavoritedEvent favoritedEvent = db.getFavoritedEvent(mEventObj.getId());

        if (favoritedEvent != null) {
            favoriteImageView.setImageResource(R.drawable.ic_action_favorite);
            favoriteImageView.setTag(R.drawable.ic_action_favorite);
        } else {
            favoriteImageView.setTag(R.drawable.ic_action_favorite_outline);
        }

        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer) favoriteImageView.getTag() == R.drawable.ic_action_favorite_outline) {

                    FavoritedEvent insertFavortiedEvent = new FavoritedEvent();
                    insertFavortiedEvent.setEventId(mEventObj.getId());
                    insertFavortiedEvent.setEventName(mEventObj.getTitle());
                    db.addFavoritedEvent(insertFavortiedEvent);
                    favoriteImageView.setImageResource(R.drawable.ic_action_favorite);
                    favoriteImageView.setTag(R.drawable.ic_action_favorite);

                    Toast.makeText(getActivity(), "Event added to Favorites",
                            Toast.LENGTH_LONG).show();
                } else {
                    FavoritedEvent deleteFavoritedEvent = favoritedEvent;
                    db.deleteFavoritedEvent(deleteFavoritedEvent);

                    favoriteImageView.setImageResource(R.drawable.ic_action_favorite_outline);
                    favoriteImageView.setTag(R.drawable.ic_action_favorite_outline);

                    Toast.makeText(getActivity(), "Event removed from Favorites",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        db.close();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_detail_menu, menu);
    }


}
