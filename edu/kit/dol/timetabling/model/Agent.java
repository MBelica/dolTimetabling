package edu.kit.dol.timetabling.model;

import edu.kit.dol.timetabling.structures.EventSlot;
import edu.kit.dol.timetabling.structures.EventType;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

import static edu.kit.dol.timetabling.exporter.Timetables.exportCSVTable;
import static edu.kit.dol.timetabling.utilities.Configuration.*;
import static edu.kit.dol.timetabling.utilities.Dates.*;
import static edu.kit.dol.timetabling.utilities.Miscellaneous.truncateUUID;
import static edu.kit.dol.timetabling.utilities.Verbose.print;

@SuppressWarnings("unused")
public class Agent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String agentTag;
    private String[][] currentWorkingHours;
    private UUID uniqueID;
    private boolean calcMetrics = true;
    private boolean allowOvertime = allowGeneralOvertime;
    private boolean allowOverbooking = allowGeneralOverbooking;

    private final Set<EventType> eventTypesLog = new HashSet< >();
    private final List<Event> eventLog = new ArrayList< > ();
    private final List< Event > overtimeLog = new ArrayList< > ();
    private final List< Event > overbookedLog = new ArrayList< >();
    private final List<TimeTable> timeTables = new ArrayList< >();

    public Agent() {
        initialize("", null, null);
    }

    public Agent(String tag) {
        initialize(tag, null, null);
    }

    public Agent(String tag, String[][] workingHours) { initialize(tag, workingHours, null); }

    public Agent(String tag, String[][] workingHours, Set <EventType> eventTypes) {
        initialize(tag, workingHours, eventTypes);
    }

    private void initialize(String tag, String[][] workingHours, Set <EventType> eventTypes) {
        agentTag = tag;
        uniqueID = UUID.randomUUID();

        if (workingHours != null) {
            currentWorkingHours = workingHours;
        } else currentWorkingHours = standardWorkingHours;
        if (eventTypes != null) {
            eventTypesLog.addAll(eventTypes);
        }
        setWeeklyEventTypes(eventTypesLog);
    }

    public boolean getCalcMetrics() {
        return calcMetrics;
    }

    public void setCalcMetrics(boolean status) {
        calcMetrics = status;
    }

    public UUID getUniqueId() {
        return uniqueID;
    }

    public void setUniqueId(UUID ID) {
        uniqueID = ID;
    }

    public List< Event > getEventLog() { return eventLog; }

    public String getTag() {
        return agentTag;
    }

    public boolean getOvertimeStatus() {
        return allowOvertime;
    }

    public void setOvertimeStatus(boolean policy) {
        allowOvertime = policy;
    }

    public boolean getOverbookingStatus() {
        return allowOverbooking;
    }

    public void setOverbookingStatus(boolean policy) {
        allowOverbooking = policy;
    }

    private void setWeeklyEventTypes(Set<EventType> eventTypes) {

        eventTypesLog.addAll(eventTypes);
        for (TimeTable table: timeTables) {
            table.setWeeklyEventSchedule(eventTypes);
        }
    }

    public long getOverbooked(Calendar date) {
        long result = 0;

        for (Event event : overbookedLog) {
            if (event.getDDateInMS() > date.getTimeInMillis()) {
                result = result + 1;
            }
        }

        return result;
    }

    public long getOvertime(Calendar date) {
        long result = 0;

        for (Event event : overtimeLog) {
            if (event.getDDateInMS() > date.getTimeInMillis()) {
                result = result + 1;
            }
        }

        return result;
    }

    public void setWeeklyWorkingHours(String[][] workingHours) {

        if (workingHours != null) {
            currentWorkingHours = workingHours;

            for (TimeTable table: timeTables) {
                table.setWorkingTime(workingHours);
            }
        }
    }

    public void createMissingCalendars(Event event) {
        Calendar eventDate  = event.getDate();
        Calendar submitDate = event.getSubmitDate();

        createIfNecessaryFirstTimetableForYearAndReturn(submitDate.get(Calendar.YEAR));
        createIfNecessaryFirstTimetableForYearAndReturn(eventDate.get(Calendar.YEAR) + 1);
    }

    public void addEvent(Event event) {

        Calendar eventDate = event.getDate();
        createMissingCalendars(event);

        boolean[] bookingResponse = checkSlotForBooking(event);

        if ( bookingResponse[0] ) {
            addSessions(event, eventDate, bookingResponse[1], bookingResponse[2]);
        }

        event.print();
    }


    public void exportAllSchedulesAsCSV() {

        for (TimeTable table: timeTables) {
            exportCSVTable(agentTag, table, eventLog);
        }
    }

    public void exportScheduleAsCSV(int year) {

        exportCSVTable(agentTag, createIfNecessaryFirstTimetableForYearAndReturn(year), eventLog);
    }

    private boolean[] checkSlotForBooking (Event event) {

        boolean successfulBooking = false,
                overtimeBooking   = false,
                overbookedBooking = false;

        int counter = 0;
        Calendar eventDate = event.getDate();
        int[] slot = new int[] {eventDate.get(Calendar.YEAR), eventDate.get(Calendar.DAY_OF_YEAR), event.getTimeSlot()};
        while (counter < event.getNumberOfSessions()) {
            TimeTable currentTable = createIfNecessaryFirstTimetableForYearAndReturn(slot[0]);

            EventSlot timeSlot = Objects.requireNonNull(currentTable).timeTable[slot[1] - 1][slot[2]];

            if (timeSlot == null) {
                throw new IllegalArgumentException("An unknown error occurred adding the following event: " + event.toString());
            } else {
                if ( timeSlot.getEventBooked() && (!allowOverbooking) ) {
                    print("Following event has not been booked as overbooking is disabled and an existing event is blocking the time slot:");
                    successfulBooking = false;
                    break;
                }
                else if ( calendarDifferenceInDays( event.getSubmitDate(), event.getDate() ) < event.getWaitingPeriodInMs() ) {
                    print("Following event has not been booked as the waiting period isn't over:");
                    successfulBooking = false;
                    break;
                }
                else if ( (timeSlot.getEventType() != event.getType()) && (timeSlot.getEventType() > 0) ) {
                    print("Following event has not been booked due mismatch of event type:");
                    successfulBooking = false;
                    break;
                }
                else if ( (timeSlot.getEventType() != event.getType()) && (timeSlot.getEventType() == 0)  && (!allowOvertime) ) {
                    print("Following event has not been booked as overtime is not allowed:");
                    successfulBooking = false;
                    break;
                }
                else if ( calendarDifferenceInDays( event.getSubmitDate(), event.getDate() ) >= event.getWaitingPeriodInMs() ) {
                    if ( (timeSlot.getEventType() == 0) && allowOvertime ) {
                        if ( timeSlot.getEventBooked() && allowOverbooking ) overbookedBooking = true;
                        successfulBooking = true;
                        overtimeBooking = true;
                    }
                    else if ( timeSlot.getEventType() == event.getType() ) {
                        if ( timeSlot.getEventBooked() && allowOverbooking ) {
                            successfulBooking = true;
                            overbookedBooking = true;
                        }
                        else if ( !timeSlot.getEventBooked() ) {
                            successfulBooking = true;
                        }
                    }
                }
                else {
                    throw new RuntimeException("An unknown error occurred adding the following event: " + event.toString());
                }
            }

            slot = findSuccessor(slot);
            counter++;
        }

        return new boolean[] { successfulBooking, overbookedBooking, overtimeBooking };
    }

    private void addSessions(Event event, Calendar eventDate, boolean overbooked, boolean overtime) {

        int[] slot = new int[] {eventDate.get(Calendar.YEAR), eventDate.get(Calendar.DAY_OF_YEAR), event.getTimeSlot()};
        for (int i = 0; i < event.getNumberOfSessions(); i++) {
            TimeTable currentTable = createIfNecessaryFirstTimetableForYearAndReturn(slot[0]);
            EventSlot timeSlot = Objects.requireNonNull(currentTable).timeTable[slot[1] - 1][slot[2]];

            timeSlot.setEventID(event.getUniqueId());
            timeSlot.setSDatesInMS(event.getSDateInMS());
            timeSlot.setEventBooked(true);
            if (overbooked) timeSlot.setEventOverbooked(true);
            if (i < event.getNumberOfSessions()) slot = findSuccessor(slot);
        }

        event.setSlotsToNextPossibleEventDate( calcSlotsToNextPossibleEventDate(event) );

        if (overbooked) {
            if (overtime) {
                print("Following event has been booked despite overbooking and overtime:");
            } else print("Following event has been booked despite overbooking:");
            overbookedLog.add(event);
        } else if (overtime) {
            print("Following event has been booked as overtime:");
            overtimeLog.add(event);
        } else print("Following event has been successfully booked:");

        eventLog.add(event);
    }

    private int calcSlotsToNextPossibleEventDate(Event currentEventToShift) {
        int counter;

        if ( currentEventToShift.getLength() <= ( 24*60 / lengthOfSession) ) {

            counter = 0;
            Event copy = new Event(calendarToString(currentEventToShift.getSubmitDate()), currentWorkingHours[(((currentEventToShift.getSubmitDate()).get(Calendar.DAY_OF_WEEK)) % 7)][0], currentEventToShift.getLength(), currentEventToShift.getType(), calendarToString(currentEventToShift.getSubmitDate()), currentEventToShift.getAgentTag(), currentEventToShift.getPatientID(), currentEventToShift.getPatientAge(), currentEventToShift.getAskedAgent(), currentEventToShift.getAttended(), currentEventToShift.getDirectWaitingPeriod(), currentEventToShift.getIndirectWaitingPeriod());

            boolean[] bookingResponseArray;
            boolean bookingResponse = false;

            while (!bookingResponse) {
                copy.shiftEventForNSlots(1);

                createMissingCalendars(copy);
                bookingResponseArray = checkSlotForBooking(copy);
                bookingResponse = ((bookingResponseArray[0]) && (!bookingResponseArray[1]) && (!bookingResponseArray[2]));

                counter++;
            }
        } else { counter = -1; }

        return counter;
    }

    private TimeTable createIfNecessaryFirstTimetableForYearAndReturn(int year) {

        TimeTable result = null;

        for (TimeTable table : timeTables) {
            if (table.getYear() == year) {
                result = table;
                break;
            }
        }

        if (result == null) {
            result = createMissingTables(year);
        }


        return result;
    }

    private TimeTable createMissingTables (int year) {
        int sYear;
        TimeTable result = null;
        TimeTable table;

        TimeTable lastYear = timeTables.stream().max(Comparator.comparing(TimeTable::getYear)).orElse(null);
        if (lastYear == null) {
            sYear = year - 1;
        } else {
            sYear = lastYear.getYear();
        }

        if (sYear <= year) {
            for (int sy = (sYear + 1); sy <= year + 1; sy++) {
                table = new TimeTable(sy);
                timeTables.add(table);
                table.initializeWorkingTime(currentWorkingHours);
                table.setWeeklyEventSchedule(eventTypesLog);
            }
        } else {
            TimeTable firstYear = timeTables.stream().min(Comparator.comparing(TimeTable::getYear)).orElse(null);
            for (int fy = (Objects.requireNonNull(firstYear).getYear() - 1); fy >= year; fy--) {
                table = new TimeTable(fy);
                timeTables.add(table);
                table.initializeWorkingTime(currentWorkingHours);
                table.setWeeklyEventSchedule(eventTypesLog);
            }
        }

        for (TimeTable tbl : timeTables) {
            if (tbl.getYear() == year) {
                result = tbl;
                break;
            }
        }

        if (result == null) {
            throw new InvalidStateException("No yearly table found even though it should have been created.");
        }

        return result;
    }

    private void createMissingTables (Calendar eventDate) {
        createMissingTables (eventDate.get(Calendar.YEAR));
    }

    public int[] getSlotCount(Event event) {
        int[] result = {0, 0, 0};

        int freeSlots = 0, shortSlots = 0;
        int bookedSlots = getBookedSlots(event);
        List<Integer> emptyBlocks = getEmptyBlocks(event);

        if (emptyBlocks != null) {
            for (Integer block : emptyBlocks) {
                if (event.getNumberOfSessions() != 0) {
                    freeSlots += (block / event.getNumberOfSessions()) * event.getNumberOfSessions();
                    shortSlots += block % event.getNumberOfSessions();
                } else {
                    System.out.println("NumberOfSessions() returns zero for " + event.toString());
                    break;
                }
            }
        }
        result[0] = freeSlots;
        result[1] = shortSlots;
        result[2] = bookedSlots;

        return result;
    }

    public long getCountOfCurrentAppointments(Event event) {

        long result = 0;

        if ( (eventLog != null) && (event != null) ) {
            result = eventLog.stream().filter(p -> ((p.getSDateInMS() <= event.getSDateInMS()) && (p.getDDateInMS() > event.getSDateInMS()))).count();
        }

        return result;
    }

    private int getBookedSlots (Event event) {

        int countBookedSlots = 0;

        Calendar sDate = Calendar.getInstance();
        sDate.setTime(event.getSubmitDate().getTime());
        sDate.add(Calendar.DATE, event.getDirectWaitingPeriod());
        SimpleDateFormat df = new SimpleDateFormat(timeStringPattern);
        int[] currentSlot = new int[] {sDate.get(Calendar.YEAR), sDate.get(Calendar.DAY_OF_YEAR), stringToTimeSlot(df.format(sDate.getTime()))};
        if (event != null) {

            while (isWithinRange(currentSlot, event)) {
                TimeTable yearlyTable = createIfNecessaryFirstTimetableForYearAndReturn(currentSlot[0]);

                if ( (yearlyTable != null) && (yearlyTable.timeTable[currentSlot[1] - 1][currentSlot[2]] != null) ) {
                    EventSlot slot = yearlyTable.timeTable[currentSlot[1] - 1][currentSlot[2]];

                    if ((slot.getEventType() == event.getType())) {
                        if ( (slot.getEventBooked()) && (anyEventInSlotAddedInThePast(slot, event.getSDateInMS())) )
                            countBookedSlots++;
                    }

                } else throw new InvalidStateException("No yearly table " + currentSlot[0] + " for event " + event.toString() + " # " + Objects.requireNonNull(yearlyTable).getYear() + ", " + (currentSlot[1] - 1) + ", " + currentSlot[2]);

                currentSlot = findSuccessor(currentSlot);
            }
        } else throw new InvalidParameterException("Event " + event.toString() + " non-existent.");

        return countBookedSlots;
    }

    private boolean anyEventInSlotAddedInThePast (EventSlot slot, long sDateInMS) {
        boolean result = false;
        List < Long > sDatesInMS = slot.getSDatesInMS();

        if (sDatesInMS != null) {
            for (Long date : sDatesInMS) {
                if (date  <= sDateInMS) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    private boolean allEventsInSlotAddedInTheFuture (EventSlot slot, long sDateInMS) {
        boolean result = false;
        List < Long > sDatesInMS = slot.getSDatesInMS();

        if (sDatesInMS != null) {
            int i = 0;

            for (Long date : sDatesInMS) {
                if (date  > sDateInMS)  i++;
            }

            if (i == sDatesInMS.size()) result = true;
        }

        return result;
    }
    private List<Integer> getEmptyBlocks (Event event) {

        int temp = 0;
        int[] lastCountedSlot = new int[] {0, 0, 0};
        List<Integer> emptyBlocks = new ArrayList< > ();

        Calendar sDate = Calendar.getInstance();
        sDate.setTime(event.getSubmitDate().getTime());
        sDate.add(Calendar.DATE, event.getDirectWaitingPeriod());
        SimpleDateFormat df = new SimpleDateFormat(timeStringPattern);

        int[] currentSlot = new int[] {sDate.get(Calendar.YEAR), sDate.get(Calendar.DAY_OF_YEAR), stringToTimeSlot(df.format(sDate.getTime()))};
        if (event != null) {

            while (isWithinRange(currentSlot, event)) {

                TimeTable yearlyTable = createIfNecessaryFirstTimetableForYearAndReturn(currentSlot[0]);


                if ( (yearlyTable != null) && (yearlyTable.timeTable[currentSlot[1] - 1][currentSlot[2]] != null) ) {
                    EventSlot slot = yearlyTable.timeTable[currentSlot[1] - 1][currentSlot[2]];

                    if ((slot.getEventType() == event.getType()) &&
                            ((!slot.getEventBooked()) || (allEventsInSlotAddedInTheFuture(slot, event.getSDateInMS())))) {

                        if (!slotIsPredecessor(lastCountedSlot, currentSlot)) {
                            emptyBlocks.add(temp);
                            temp = 1;
                        } else temp++;

                        lastCountedSlot = currentSlot;
                    }
                } else throw new InvalidStateException("No yearly table " + currentSlot[0] + " for event " + event.toString() + " # " + yearlyTable.getYear() + ", " + (currentSlot[1] - 1) + ", " + currentSlot[2]);

                currentSlot = findSuccessor(currentSlot);
            }

        } else throw new InvalidParameterException("Event " + event.toString() + " non-existent.");
        emptyBlocks.add(temp);

        return emptyBlocks;
    }

    private boolean slotIsPredecessor (int[] lastSlot, int[] currentSlot) {

        boolean result = false;

        if (lastSlot[0] ==  currentSlot[0]) {
            if ( lastSlot[1] == currentSlot[1] ) {
                if ( lastSlot[2] ==  (currentSlot[2] - 1) ) result = true;
            } else if  ( lastSlot[1] ==  (currentSlot[1] - 1) ) {
                if ( (lastSlot[2] == (numberOfSlotsPerDay - 1)) && (currentSlot[2] == 0) ) result = true;
            }
        } else if ( lastSlot[0] ==  (currentSlot[0] - 1) ) {
            if ( (lastSlot[1] == createIfNecessaryFirstTimetableForYearAndReturn(lastSlot[0]).getDaysInYear()) && (currentSlot[1] == 1) ) {
                if ( (lastSlot[2] == (numberOfSlotsPerDay - 1)) && (currentSlot[2] == 0) ) result = true;
            }
        }

        return result;
    }


    private boolean isWithinRange (int[] slot, Event event) {

        boolean result = false;

        Calendar eDate = event.getDate();

        int[] endSlot = new int[] {eDate.get(Calendar.YEAR), eDate.get(Calendar.DAY_OF_YEAR), event.getTimeSlot()};

        if ( slot[0] < endSlot[0] ) {
            result = true;
        } else if (slot[0] == endSlot[0]) {
            if ( slot[1] < endSlot[1] ) {
                result = true;
            } else if ( (slot[1] == endSlot[1]) && (slot[2] <= endSlot[2]) ) result = true;
        }

        return result;
    }

    private int[] findSuccessor(int[] slot) {

        int[] result;

        if ( slot[2] < (numberOfSlotsPerDay - 1) ) {
            result = new int[] {slot[0], slot[1], slot[2] + 1};
        }
        else if ( slot[1] < createIfNecessaryFirstTimetableForYearAndReturn(slot[0]).getDaysInYear() ) {
            result = new int[] {slot[0], slot[1] + 1, 0};
        }
        else result = new int[] {slot[0] + 1, 1, 0};

        return result;
    }

    @Override
    public String toString() {

        return getTag() + " (" + truncateUUID(getUniqueId().toString()) + ")";
    }
}