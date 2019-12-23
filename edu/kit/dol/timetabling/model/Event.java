package edu.kit.dol.timetabling.model;

import edu.kit.dol.timetabling.utilities.Verbose;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import static edu.kit.dol.timetabling.utilities.Dates.*;
import static edu.kit.dol.timetabling.utilities.Configuration.*;
import static edu.kit.dol.timetabling.utilities.Miscellaneous.truncateUUID;

@SuppressWarnings("unused")
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID uniqueID;
    private int obligatoryWaitingPeriod;

    private int directWaitingPeriod;
    private int indirectWaitingPeriod;
    private int patientAge;
    private String patientID;

    private Calendar sDate;
    private Calendar dDate;
    private String agentTag;
    private int timeSlot;
    private int eventLength;
    private int eventType;
    private int numberOfSessions;
    private int slotsToNextPossibleEventDate;

    private long dDateInMS = -1;
    private long sDateInMS = -1;
    private long waitingPeriodInMs = -1;
    private String askedAgent = null;
    private String dayOfEventAsString = null;
    private String dayOfSubmitAsString = null;

    private final boolean attended;

    private String formattedEventDay = null;
    private String formattedDayOfSubmit = null;

    public Event(String date, String startTime, int length, int type, String dayOfSubmit, String agentTag, String patientID, int patientAge, String askedAgent, boolean attended, int directWaitingPeriod, int indirectWaitingPeriod) {

        this.uniqueID = UUID.randomUUID();

        this.agentTag = agentTag;
        this.patientID = patientID;
        this.sDate = stringToCalendar(dayOfSubmit);
        this.dDate = stringToCalendar(date + " " + startTime);
        this.timeSlot = stringToTimeSlot(startTime);
        this.eventLength = length;
        this.numberOfSessions = eventLength / lengthOfSession;
        this.eventType = type;

        this.askedAgent = askedAgent;
        this.attended = attended;
        this.patientAge = patientAge;
        this.directWaitingPeriod = directWaitingPeriod;
        this.indirectWaitingPeriod = indirectWaitingPeriod;
    }

    public void shiftEventForNSlots(int n) {
        dDate.add(Calendar.MINUTE, n * lengthOfSession);
        this.timeSlot = stringToTimeSlot(dDate.get(Calendar.HOUR_OF_DAY) + ":" + dDate.get(Calendar.MINUTE));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Event && getUniqueId() != null && ((Event) o).getUniqueId() != null && getUniqueId().equals(((Event) o).getUniqueId());
    }

    public Calendar getDate() {
        return dDate;
    }

    public void setDate(String date, String startTime) {
        this.dDate = stringToCalendar(date + " " + startTime);
    }

    public Calendar getSubmitDate() {

        return sDate;
    }

    public void setSubmitDate( String dayOfSubmit) {
        this.sDate = stringToCalendar(dayOfSubmit);
    }

    public String getAgentTag() {
        return agentTag;
    }

    public String getAskedAgent() {
        return this.askedAgent;
    }

    public String getPatientID() {
        return patientID;
    }

    public int getPatientAge() {
        return this.patientAge;
    }

    public boolean getAttended () {
        return this.attended;
    }

    public String getDayOfSubmitAsString() {

        if (dayOfSubmitAsString == null) {
            Date date = sDate.getTime();
            dayOfSubmitAsString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        }

        return dayOfSubmitAsString;
    }

    public int getSlotsToNextPossibleEventDate() {
        return this.slotsToNextPossibleEventDate;
    }

    public void setSlotsToNextPossibleEventDate(int numberOfSlots) {
        this.slotsToNextPossibleEventDate = numberOfSlots;
    }

    public String getDayOfEventAsString() {

        if (dayOfEventAsString == null) {
            Date date = dDate.getTime();
            dayOfEventAsString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        }

        return dayOfEventAsString;
    }

    public long getDDateInMS() {

        if (dDateInMS == -1) {
            dDateInMS = dDate.getTimeInMillis();
        }

        return dDateInMS;
    }

    public long getSDateInMS() {

        if (sDateInMS == -1) {
            sDateInMS = sDate.getTimeInMillis();
        }

        return sDateInMS;
    }

    public int getLength() {

        return eventLength;
    }

    public int getNumberOfSessions () {

        return numberOfSessions;
    }

    public int getTimeSlot() {

        return timeSlot;
    }

    public int getType() {

        return eventType;
    }

    public UUID getUniqueId() {

        return uniqueID;
    }

    public int getDirectWaitingPeriod() {

        return this.directWaitingPeriod;
    }

    public int getIndirectWaitingPeriod() {
        return this.indirectWaitingPeriod;
    }

    public long getWaitingPeriodInMs() {

        if (waitingPeriodInMs == -1) {
            waitingPeriodInMs = (long) (obligatoryWaitingPeriod * (1000*60*60*24));
        }

        return waitingPeriodInMs;
    }

    public void print() {

        Verbose.print(" - " + toString());
    }

    @Override
    public String toString() {

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateStringPattern);
        SimpleDateFormat timeFormat = new SimpleDateFormat(timeStringPattern);

        return "Event " + truncateUUID(uniqueID.toString()) + " ["
                + dateFormat.format(getDate().getTime()) + " at "
                + timeFormat.format(getDate().getTime()) + ", "
                + "timeslot: " + getTimeSlot() + ", "
                + "length: " + getLength() + " min, "
                + "type: " + getType() + "]";
    }
}