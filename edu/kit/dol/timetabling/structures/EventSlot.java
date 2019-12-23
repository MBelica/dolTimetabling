package edu.kit.dol.timetabling.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class EventSlot {

    private int eventType;
    private boolean eventBooked;
    private boolean eventOverbooked;
    private final List< UUID > eventIDs  = new ArrayList< >();
    private final List< Long > sDatesInMS = new ArrayList< >();

    public EventSlot(int type) {
        eventType = type;
        eventBooked = false;
        eventOverbooked = false;
    }

    public void setEventType(int type) {

        eventType = type;
    }

    public final int getEventType() {

        return eventType;
    }

    public final void setEventID(UUID id) {

        eventIDs.add(id);
    }

    public List< UUID > getEventIDs() {

        return eventIDs;
    }

    public final void setSDatesInMS(long sDateInMS) {
        sDatesInMS.add(sDateInMS);
    }

    public List< Long > getSDatesInMS () {

        return sDatesInMS;
    }

    public final void setEventBooked(boolean bkd) {

        eventBooked = bkd;
    }

    public final boolean getEventBooked() {

        return eventBooked;
    }

    public void setEventOverbooked(boolean bkd) {

        eventOverbooked = bkd;
    }

    public boolean getEventOverbooked() {

        return eventOverbooked;
    }
}