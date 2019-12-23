package edu.kit.dol.timetabling.model;

import edu.kit.dol.timetabling.structures.EventSlot;
import edu.kit.dol.timetabling.structures.EventType;

import java.util.*;

import static edu.kit.dol.timetabling.utilities.Configuration.*;
import static edu.kit.dol.timetabling.utilities.Dates.*;

public class TimeTable {

    private final int year;
    private final int daysInYear;
    public final EventSlot[][] timeTable;

    TimeTable(int y) {
        year = y;

        if(isLeapYear(year)) {
            daysInYear = 366;
        } else daysInYear = 365;

        timeTable = new EventSlot[daysInYear][numberOfSlotsPerDay];
    }

    public int getYear() { return year; }
    public int getDaysInYear () {  return daysInYear; }

    public void initializeWorkingTime(String[][] workingHours) {

        for (int i = 0; i < daysInYear; i++) {
            int k = getDayInWeek(i + 1);
            for (int j = 0; j < timeTable[i].length; j++) {

                if ((j >= stringToTimeSlot(workingHours[k][0])) && (j < stringToTimeSlot(workingHours[k][1])) && (stringToTimeSlot(workingHours[k][0]) > -1)) {
                    timeTable[i][j] = new EventSlot(1);
                } else timeTable[i][j] = new EventSlot(0);
            }
        }
    }

    public void setWorkingTime(String[][] workingHours) {

        for (int i = 0; i < daysInYear; i++) {
            int k = getDayInWeek(i + 1);

            for (int j = 0; j < timeTable[i].length; j++) {

                if (timeTable[i][j] != null) {

                    if ((j >= stringToTimeSlot(workingHours[k - 1][0])) && (j < stringToTimeSlot(workingHours[k - 1][1])) && (stringToTimeSlot(workingHours[k - 1][0]) > -1)) {

                        if (!(timeTable[i][j].getEventType() > 0)) timeTable[i][j].setEventType(1);

                    } else  timeTable[i][j].setEventType(0);
                }
            }
        }
    }

    public void setWeeklyEventSchedule(Set <EventType> eventTypes) {

        if (eventTypes != null) {
            for (EventType type : eventTypes) {
                for (int i = 0; i < daysInYear; i++) {

                    if ((stringToTimeSlot(type.getEventPeriod()[0]) > -1) && ( getDayInWeek( i ) == (type.getEventDay() - 1)) ) {
                        for (int j = stringToTimeSlot(type.getEventPeriod()[0]); j < stringToTimeSlot(type.getEventPeriod()[1]); j++) {
                            if (timeTable[i][j] != null) {
                                timeTable[i][j].setEventType(type.getEventType());
                            }
                        }
                    }
                }
            }
        }
    }

    public int getDayInWeek(int i) {
        Calendar fj = Calendar.getInstance();
        fj.set(Calendar.DAY_OF_YEAR, i);
        fj.set(Calendar.YEAR, year);

        return fj.get(Calendar.DAY_OF_WEEK) - 1;
    }
}