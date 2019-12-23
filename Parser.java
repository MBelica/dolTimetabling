import static edu.kit.dol.timetabling.structures.DataStructures.inputDataStructure;

import edu.kit.dol.timetabling.model.base.BaseParser;
import edu.kit.dol.timetabling.structures.EventType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("unused")
class BParser extends BaseParser {

    static List<inputDataStructure> parseDataSet(String fileToParse, String outputDateStringPattern, String outputTimeStringPattern) throws IOException, InvalidFormatException, ParseException {
        SimpleDateFormat myFormat = new SimpleDateFormat(outputDateStringPattern);
        String outputDateTimeStringPattern = outputDateStringPattern + " " + outputTimeStringPattern;
        Workbook workbook = WorkbookFactory.create(new File(fileToParse));
        List<inputDataStructure> Appointments = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            for (Row row : sheet) {
                if (row.getRowNum() > 0) {
                    String dayOfSubmit = retrieveXLSCell(row, 13, inputDateTimeStringPattern, outputDateTimeStringPattern);
                    if ((dayOfSubmit != null) && (!dayOfSubmit.equals(""))) {
                        String appointmentDate = retrieveXLSCell(row, 8, inputDateStringPattern, outputDateStringPattern);
                        String startTime = retrieveXLSCell(row, 9, inputTimeStringPattern, outputTimeStringPattern);
                        String eventLengthText = retrieveXLSCell(row, 12, null, null);
                        String[] eventTime = eventLengthText.split(":");
                        int hour = 0, min;
                        if (eventTime.length == 2) {
                            hour = Integer.parseInt(eventTime[0].trim());
                            min = Integer.parseInt(eventTime[1].trim());
                        } else min = Integer.parseInt(eventTime[0].trim());
                        int length = ((min >= 0) && (min <= 60) && (hour >= 0)) ? hour * 60 + min : 0;
                        Date date1 = myFormat.parse(dayOfSubmit);
                        Date date2 = myFormat.parse(appointmentDate);
                        long diffDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
                        int indirectWaitingPeriod = (int) diffDays;
                        int directWaitingPeriod = 0;
                        String patientID = retrieveXLSCell(row, 0, null, null);
                        String patientAG = retrieveXLSCell(row, 1, null, null);
                        int patientAge = ((patientAG == null) || (Objects.equals(patientAG, "")) || (Integer.valueOf(patientAG)) < 0) ? -1 : Integer.valueOf(0 + patientAG);
                        String eventType = retrieveXLSCell(row, 3, null, null);
                        String agentName = "Dr. B.";
                        String agentTag = "1";
                        @SuppressWarnings("UnnecessaryLocalVariable")
                        String askedAgent = agentTag;
                        String attendId = retrieveXLSCell(row, 4, null, null);
                        boolean attended = !attendId.equals("Not Visited");

                        boolean acceptableEvent = true;
                        if (agentTag.equals("-1") || (length > 60 * 12) || (length == 0)) {
                            acceptableEvent = false;
                        }

                        if (acceptableEvent) Appointments.add(new inputDataStructure(appointmentDate, startTime, length, patientID, patientAge, directWaitingPeriod, indirectWaitingPeriod, dayOfSubmit, eventType, setEventTypeCoding(), agentName, agentTag, askedAgent, attended));
                    }
                }
            }
        }

        workbook.close();

        return Appointments;
    }

    public static List<Set<EventType>> getEventWorkingHoursArray() {
        String[][][] data = {
                { // Doctor 1 - index 0
                        {"0", "2", "12:00", "15:00"},  {"0", "3", "12:00", "15:00"},  {"0", "4", "12:00", "15:00"}
                },
        };


        return createEventWorkingHoursArray(data);
    }

    public static List<String[][]> getWorkingHoursArray() {
        String[][][] data = {
                { // Doctor 1 - index 0
                        {"00:00", "00:00"}, {"08:00", "18:00"}, {"08:00", "18:00"}, {"08:00", "18:00"}, {"00:00", "00:00"}, {"08:00", "12:00"}, {"00:00", "00:00"}
                },
        };

        return createWorkingHoursArray(data);

    }
}

@SuppressWarnings("unused")
class WDParser extends BaseParser {

    static List<inputDataStructure> parseDataSet(String fileToParse, String outputDateStringPattern, String outputTimeStringPattern) throws IOException, InvalidFormatException, ParseException {
        SimpleDateFormat myFormat = new SimpleDateFormat(outputDateStringPattern);
        String outputDateTimeStringPattern = outputDateStringPattern + " " + outputTimeStringPattern;
        Workbook workbook = WorkbookFactory.create(new File(fileToParse));
        List<inputDataStructure> Appointments = new ArrayList<>();

        int i = 0;
        String inputTimeStringPattern = "HH:mm";
        String inputDateStringPattern = "M/d/yy";
        String inputDateTimeStringPattern = inputDateStringPattern + " " + inputTimeStringPattern;

        Sheet sheet = workbook.getSheetAt(i);
        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                String dayOfSubmit = retrieveXLSCell(row, 6, inputDateStringPattern, outputDateTimeStringPattern);
                if ( (dayOfSubmit != null) && (!dayOfSubmit.equals(""))) {
                    String appointmentDate = retrieveXLSCell(row, 1, inputDateStringPattern, outputDateStringPattern);
                    String startTime = retrieveXLSCell(row, 2, inputTimeStringPattern, outputTimeStringPattern);
                    String eventLengthText = retrieveXLSCell(row, 3, inputTimeStringPattern, outputTimeStringPattern);
                    String[] eventTime = eventLengthText.split(":");
                    int days = 0, hours = 0, mins = 0;
                    if  (eventTime.length == 3) {
                        days = Integer.parseInt(eventTime[0].trim());
                        hours = Integer.parseInt(eventTime[1].trim());
                        mins = Integer.parseInt(eventTime[2].trim());
                    } else if (eventTime.length == 2) {
                        hours = Integer.parseInt(eventTime[0].trim());
                        mins = Integer.parseInt(eventTime[1].trim());
                    } else if (eventTime.length == 1) mins = Integer.parseInt(eventTime[0].trim());
                    int length = ((mins >= 0) && (mins <= 60) && (hours >= 0) && (days >= 0)) ? days * 24 * 60 + hours * 60 + mins : 0;
                    Date date1 = myFormat.parse(dayOfSubmit);
                    Date date2 = myFormat.parse(appointmentDate);
                    long diffDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
                    int indirectWaitingPeriod = (int) diffDays;
                    int directWaitingPeriod = 0;
                    String patientID = retrieveXLSCell(row, 0, null, null);
                    String patientAG = retrieveXLSCell(row, 9, null, null);
                    int patientAge = ((patientAG == null) || (Objects.equals(patientAG, ""))) ? -1 : Integer.valueOf(0 + patientAG);
                    String eventType = retrieveXLSCell(row, 10, null, null);
                    String eventData = retrieveXLSCell(row, 12, null, null);
                    String agentName = eventData.contains("Drehmer") ? "Dr. Drehmer" : (eventData.contains("Weber") ? "Dr. Weber" : ("Unknown :" + eventData));
                    String agentTag = agentName.equals("Dr. Drehmer") ? "1" : (eventData.contains("Dr. Weber") ? "2" : "-1");
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    String askedAgent = agentTag;
                    String attendId = retrieveXLSCell(row, 13, null, null);
                    boolean attended = !attendId.equals("Nicht erschienen");

                    boolean acceptableEvent = true;
                    if (agentTag.equals("-1") || (length > 60 * 12) || (length == 0)) {
                        acceptableEvent = false;
                    }

                    if (acceptableEvent) Appointments.add(new inputDataStructure(appointmentDate, startTime, length, patientID, patientAge, directWaitingPeriod, indirectWaitingPeriod, dayOfSubmit, eventType, setEventTypeCoding(), agentName, agentTag, askedAgent, attended));
                }
            }
        }

        workbook.close();

        return Appointments;
    }

    public static List<Set<EventType>> getEventWorkingHoursArray() {
        String[][][] data = {
                { // Doctor 1 - index 0
                        {"0", "2", "12:00", "14:00"}, {"0", "5", "12:00", "14:00"}
                },
                { // Doctor 2 - index 1
                        {"0", "2", "13:00", "14:00"}, {"0", "3", "12:00", "14:00"}, {"0", "5", "12:00", "14:00"}
                },
        };


        return createEventWorkingHoursArray(data);
    }

    public static List<String[][]> getWorkingHoursArray() {
        String[][][] data = {
                { // Doctor 1 - index 0
                        {"00:00", "00:00"}, {"08:00", "18:00"}, {"00:00", "00:00"}, {"08:00", "12:00"}, {"08:00", "18:00"}, {"09:00", "13:00"}, {"00:00", "00:00"}
                },
                { // Doctor 2 - index 1
                        {"00:00", "00:00"}, {"08:00", "18:00"}, {"08:00", "18:00"}, {"08:00", "12:00"}, {"08:00", "18:00"}, {"09:00", "13:00"}, {"00:00", "00:00"}
                },
        };

        return createWorkingHoursArray(data);

    }
}


@SuppressWarnings("unused")
class IMLParser extends BaseParser {

    static List<inputDataStructure> parseDataSet(String fileToParse, String outputDateStringPattern, String outputTimeStringPattern) throws IOException, InvalidFormatException, ParseException {
        SimpleDateFormat myFormat = new SimpleDateFormat(outputDateStringPattern);
        String outputDateTimeStringPattern = outputDateStringPattern + " " + outputTimeStringPattern;
        Workbook workbook = WorkbookFactory.create(new File(fileToParse));
        Sheet sheet = workbook.getSheetAt(0);

        List<inputDataStructure> Appointments = new ArrayList<>();

        for (Row row : sheet) {
            if (row.getRowNum() > 0) {
                String dayOfSubmit = retrieveXLSCell(row, 4, inputDateTimeStringPattern, outputDateTimeStringPattern);
                if ((dayOfSubmit != null) && (!dayOfSubmit.equals(""))) {
                    String appointmentDate = retrieveXLSCell(row, 0, inputDateStringPattern, outputDateStringPattern);
                    String startTime = retrieveXLSCell(row, 1, inputTimeStringPattern, outputTimeStringPattern);
                    String eventLengthText = retrieveXLSCell(row, 2, inputTimeStringPattern, outputTimeStringPattern);
                    String[] eventTime = eventLengthText.split(":");
                    int hour = Integer.parseInt(eventTime[0].trim());
                    int min = Integer.parseInt(eventTime[1].trim());
                    int length = ((min >= 0) && (min <= 60) && (hour >= 0)) ? hour * 60 + min : 0;
                    Date date1 = myFormat.parse(dayOfSubmit);
                    Date date2 = myFormat.parse(appointmentDate);
                    long diffDays = (date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
                    int indirectWaitingPeriod = (int) diffDays;
                    int directWaitingPeriod = 0;
                    String patientID = retrieveXLSCell(row, 6, null, null);
                    String patientAG = retrieveXLSCell(row, 8, null, null);
                    int patientAge = ((patientAG == null) || (Objects.equals(patientAG, ""))) ? -1 : Integer.valueOf(0 + patientAG);
                    String eventType = retrieveXLSCell(row, 9, null, null);
                    String eventData = retrieveXLSCell(row, 10, null, null);
                    String agentName = eventData.substring(2).split(",", 2)[0]; // name of agent
                    String agentTag = eventData.substring(0, 1); // number identifying agent
                    String askedAgent = getAskedForAgent(agentTag, eventData, eventType);
                    String attendId = retrieveXLSCell(row, 11, null, null);
                    boolean attended = !attendId.equals("Nicht erschienen");

                    boolean acceptableEvent = true;
                    if (agentTag.equals("-1") || (length > 60 * 12) || (length == 0) || (Integer.parseInt(agentTag) > 4)) {
                        acceptableEvent = false;
                    }

                    if (acceptableEvent) Appointments.add(new inputDataStructure(appointmentDate, startTime, length, patientID, patientAge, directWaitingPeriod, indirectWaitingPeriod, dayOfSubmit, eventType, setEventTypeCoding(), agentName, agentTag, askedAgent, attended));
                }
            }
        }

        workbook.close();

        return Appointments;
    }

    public static List<Set<EventType>> getEventWorkingHoursArray() {
        String[][][] data = {
                { // Doctor 1 - index 0
                        {"0", "2", "13:00", "14:00"}, {"0", "3", "13:00", "14:00"}, {"0", "4", "13:00", "14:00"}, {"0", "5", "12:00", "14:00"}
                },
                { // Doctor 2 - index 1
                        {"0", "2", "13:00", "14:00"}, {"0", "3", "13:00", "14:00"}, {"0", "4", "13:00", "14:00"}, {"0", "5", "12:00", "14:00"}
                },
                { // Doctor 3 - index 2
                        {"0", "2", "13:00", "14:00"},  {"0", "3", "13:00", "14:00"}, {"0", "4", "13:00", "14:00"},  {"0", "5", "12:00", "14:00"}
                },
                { // Doctor 4 - index 3
                        //
                },
        };


        return createEventWorkingHoursArray(data);
    }

    public static List<String[][]> getWorkingHoursArray() {
        String[][][] data = {
                { // Doctor 1 - index 0
                        {"00:00", "00:00"}, {"08:00", "18:00"}, {"08:00", "18:00"}, {"08:00", "18:00"}, {"08:00", "18:00"}, {"08:00", "12:00"}, {"00:00", "00:00"}
                },
                { // Doctor 2 - index 1
                        {"00:00", "00:00"}, {"08:00", "16:00"}, {"08:00", "18:00"}, {"08:00", "18:00"}, {"08:00", "18:00"}, {"08:00", "13:00"}, {"00:00", "00:00"}
                },
                { // Doctor 3 - index 2
                        {"00:00", "00:00"}, {"08:00", "18:00"}, {"08:00", "18:00"}, {"08:00", "18:00"}, {"08:00", "16:00"}, {"08:00", "13:00"}, {"00:00", "00:00"}
                },
                { // Doctor 4 - index 3
                        {"00:00", "00:00"}, {"00:00", "00:00"}, {"08:00", "12:00"}, {"14:00", "17:00"}, {"09:00", "12:00"}, {"00:00", "00:00"}, {"00:00", "00:00"}
                },
        };


        return createWorkingHoursArray(data);

    }

    private static String getAskedForAgent(String agentTag, String eventData, String eventType) {
        String askedAgend = "-1";

        String[][] ressourceClassesArray = {
                {"Friedrichsen", "1"},
                {"Ley", "2"},
                {"Hartmann", "3"},
                {"Ischinger", "4"}
        };

        String[] ressourceClasses = null;
        for (String[] ressourceClass : ressourceClassesArray) {
            if (eventData.contains(ressourceClass[0])) {
                if (askedAgend.equals("-1")) {
                    askedAgend = "";
                } else if (!askedAgend.equals("")) askedAgend += ",";
                askedAgend += ressourceClass[1];
            }
        }

        return askedAgend;
    }
}