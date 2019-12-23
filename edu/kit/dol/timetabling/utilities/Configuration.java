package edu.kit.dol.timetabling.utilities;

public final class Configuration {

    public final static boolean silentMod = true;
    public final static boolean returnDetailedMetrics = true;
    public final static String dateStringPattern = "dd.MM.yyyy";
    public final static String timeStringPattern = "HH:mm";
    public final static String dateTimeStringPattern = dateStringPattern + " " + timeStringPattern;
    public final static String exportFilePath = "results/";

    public final static int UUIDTruncationLength = 8;
    public final static int lengthOfSession = 5;
    public final static boolean allowGeneralOvertime = true;
    public final static boolean allowGeneralOverbooking = true;
    public final static String[][] standardWorkingHours = {
            {"00:00", "23:59"},
            {"00:00", "23:59"},
            {"00:00", "23:59"},
            {"00:00", "23:59"},
            {"00:00", "23:59"},
            {"00:00", "23:59"},
            {"00:00", "23:59"}
    };

    public static String[] computeMetrics(int freeSlots, int shortSlots, int bookedSlots) {
        double metric1, metric2;

        int totalSlots = bookedSlots + freeSlots + shortSlots;

        if ( totalSlots > 0)  {
            metric1 = ( ( (double) bookedSlots ) / totalSlots );
            metric2 = ( ( (double) ( bookedSlots + shortSlots ) ) / totalSlots );
        } else {
            metric1 = 0;
            metric2 = 0;
        }

        return new String[] { Double.toString(metric1), Double.toString(metric2) };
    }

    // Do not change
    public final static int numberOfSlotsPerDay = (24 * 60 / lengthOfSession);
}