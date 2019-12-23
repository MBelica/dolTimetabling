package edu.kit.dol.timetabling.utilities;

import java.sql.Array;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static edu.kit.dol.timetabling.utilities.Configuration.dateStringPattern;
import static edu.kit.dol.timetabling.utilities.Configuration.dateTimeStringPattern;
import static edu.kit.dol.timetabling.utilities.Configuration.lengthOfSession;

@SuppressWarnings("unused")
public class Dates {

    public static int stringToTimeSlot(String text) {
        int timeSlot = -1;

        String[] time = text.split(":");
        int min, hour;

        try {
            hour = Integer.parseInt(time[0].trim());
            min  = Integer.parseInt(time[1].trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("String for timeslot in wrong format: " + e);
        }

        if ((min >= 0) && (min <= 60) && (hour >= 0) && (hour <= 24)) {
            int minCorrection = min;
            minCorrection -= min % lengthOfSession;
            timeSlot = (hour * 60 + minCorrection) / lengthOfSession;
        }

        return timeSlot;
    }

    public static String timeSlotToString(int timeSlot) {
        String result = null;
        int mins = 0;
        int hours = 0;

        if (timeSlot >= 0) {
            int totalMins = timeSlot * lengthOfSession;

            mins = totalMins % 60;
            hours = totalMins / 60;
        }

        String minsString, hoursString;
        if (mins < 10) {
            minsString = "0" + Integer.toString(mins);
        } else { minsString = Integer.toString(mins); }
        if (hours < 10) {
            hoursString = "0" + Integer.toString(hours);
        } else { hoursString = Integer.toString(hours); }

        if (hours < 24) {
            result = hoursString + ":" + minsString;
        }

        return result;
    }

    public static String calendarToString(Calendar cal) {

        return calendarToString(cal, dateStringPattern);
    }


    private static String calendarToString(Calendar cal, String dateFormat) {

        String result;
        if (cal == null) {
            return "";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        try {
            result = simpleDateFormat.format(cal.getTime());
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    public static long calendarDifferenceInDays(Calendar cal1, Calendar cal2) {

        return cal2.getTimeInMillis() - cal1.getTimeInMillis();
    }

    public static LocalDateTime calendarToLocalDateTime(Calendar calendar) {

        if (calendar == null) {
            return null;
        }

        TimeZone tz = calendar.getTimeZone();
        ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();

        return LocalDateTime.ofInstant(calendar.toInstant(), zid);
    }

    public static Date stringToDate (String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;

        try {
            date = sdf.parse(stringDate);
        } catch (Exception e) {
            try {
                SimpleDateFormat standardDateFormat = new SimpleDateFormat(dateTimeStringPattern);
                date = standardDateFormat.parse(stringDate);
            } catch (Exception ex) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(dateStringPattern);
                    date = dateFormat.parse(stringDate);
                } catch (ParseException pe) {
                    date = null;
                }
            }
        }

        return date;
    }

    public static Calendar stringToCalendar(String stringDate) {

        if (stringDate == null) {
            return null;
        }

        Calendar calendar = new GregorianCalendar();
        try {
            Timestamp newDate = Timestamp.valueOf(stringDate);
            calendar.setTime(newDate);
        } catch (Exception e) {
            SimpleDateFormat standardDateFormat = new SimpleDateFormat(dateTimeStringPattern);
            try {
                calendar.setTime(standardDateFormat.parse(stringDate));
            } catch (Exception ex) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    calendar.setTime(simpleDateFormat.parse(stringDate));
                } catch (ParseException pe) {
                    SimpleDateFormat reparseDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    try {
                        calendar.setTime(reparseDateFormat.parse(stringDate));
                    } catch (ParseException peRP) {
                        calendar = null;
                    }
                }
            }
        }
        return calendar;
    }

    public static String saveStringDateAsString(String stringDate) {
        Date date = stringToCalendar(stringDate).getTime();

        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 == 0 && year % 400 == 0 || year % 100 != 0 || year % 400 == 0);
    }

    public static String longMsToTime(long ms) {
        String result = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(ms),
                        TimeUnit.MILLISECONDS.toMinutes(ms) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(ms) % TimeUnit.MINUTES.toSeconds(1));

        return result;
    }
}
