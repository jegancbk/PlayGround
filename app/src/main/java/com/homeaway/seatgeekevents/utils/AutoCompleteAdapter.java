package com.homeaway.seatgeekevents.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.homeaway.seatgeekevents.R;
import com.homeaway.seatgeekevents.customviews.CircularNetworkImageView;
import com.homeaway.seatgeekevents.data.Event;
import com.homeaway.seatgeekevents.data.ISeatGeekResponseLoader;
import com.homeaway.seatgeekevents.data.VolleySingleton;
import com.homeaway.seatgeekevents.db.DatabaseHandler;
import com.homeaway.seatgeekevents.db.FavoritedEvent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jegan Kabilan on 11/4/2016.
 */

public class AutoCompleteAdapter extends ArrayAdapter<Event> implements Filterable{

    private ArrayList<Event> mSeatGeekEvents;
    private Context mContext;
    private ImageLoader mImageLoader;

    public AutoCompleteAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        mSeatGeekEvents = new ArrayList<Event>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.autocomplete_list_row, parent, false);
        }

        Event currentItem = getItem(position);
        String location = currentItem.getCity();
        String state = currentItem.getState();
        if (state != null && !state.equalsIgnoreCase("null")) {
            location = location + ", " + currentItem.getState();
        }

        String dateTimeLocal = currentItem.getDatetimeLocal();
        String formattedDate = "";
        DateFormat originalFormat = new SimpleDateFormat(SeatGeekApiConstants.API_RESP_DATE_FORMAT);
        DateFormat targetFormat = new SimpleDateFormat(SeatGeekApiConstants.DISPLAY_DATE_FORMAT);
        try {
            Date date = originalFormat.parse(dateTimeLocal);
            formattedDate = targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ((TextView) convertView.findViewById(R.id.eventTitleText)).setText(currentItem.getTitle());
        ((TextView) convertView.findViewById(R.id.venueText)).setText(location);
        ((TextView) convertView.findViewById(R.id.timeText)).setText(formattedDate);

        if(currentItem.getImage() != null) {
            mImageLoader = VolleySingleton.getInstance(mContext).getImageLoader();
            ((CircularNetworkImageView) convertView.findViewById(R.id.eventImage)).
                    setImageUrl(currentItem.getImage(), mImageLoader);
        } else {
            ((CircularNetworkImageView) convertView.findViewById(R.id.eventImage)).
                    setImageResource(R.drawable.noimage);
        }

        DatabaseHandler db = new DatabaseHandler(mContext);

        FavoritedEvent favoritedEvent = db.getFavoritedEvent(currentItem.getId());

        if(favoritedEvent != null) {
            convertView.findViewById(R.id.isFavorite).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.isFavorite).setVisibility(View.GONE);
        }

        db.close();

        return convertView;
    }

    @Override
    public int getCount() {
        return mSeatGeekEvents.size();
    }

    @Override
    public Event getItem(int index) {
        return mSeatGeekEvents.get(index);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter eventsFilter = new Filter() {
            public List<Event> eventsList;

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {

                    mSeatGeekEvents = ((ISeatGeekResponseLoader) mContext).
                            loadSeatGeekEvents(constraint.toString());

                    filterResults.values = mSeatGeekEvents;
                    filterResults.count = mSeatGeekEvents.size();
                }
                return  filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    eventsList = (List<Event>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return eventsFilter;
    }

}
