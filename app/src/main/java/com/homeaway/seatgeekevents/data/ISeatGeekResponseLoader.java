package com.homeaway.seatgeekevents.data;

import java.util.ArrayList;

/**
 * Created by Jegan Kabilan on 11/4/2016.
 */

public interface ISeatGeekResponseLoader {

    ArrayList<Event> loadSeatGeekEvents(String constraintString);
}
