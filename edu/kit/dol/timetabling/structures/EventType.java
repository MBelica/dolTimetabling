package edu.kit.dol.timetabling.structures;

import java.util.Arrays;

public class EventType {

    private final int eventDay;
    private final int eventType;
    private final String[] eventPeriod;

    public EventType(int type, int day, String[] prd) {
        eventDay = day;
        eventPeriod = prd;
        eventType = type;
    }

    public int getEventDay() {
        return eventDay;
    }

    public int getEventType() {
        return eventType;
    }

    public String[] getEventPeriod() {
        return eventPeriod;
    }

    @Override
    public String toString() {

        return "Day: " + eventDay + ", Type: " + eventType + ", Period: " + Arrays.toString(eventPeriod);
    }
}
