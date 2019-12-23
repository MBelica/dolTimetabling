import java.util.*;
import java.io.IOException;
import java.text.ParseException;

import edu.kit.dol.timetabling.utilities.CalendarViewer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import edu.kit.dol.timetabling.model.Agency;
import edu.kit.dol.timetabling.structures.EventType;

import static edu.kit.dol.timetabling.utilities.Configuration.*;
import static edu.kit.dol.timetabling.structures.DataStructures.inputDataStructure;
import static edu.kit.dol.timetabling.utilities.Dates.longMsToTime;

class Main {

    public static void main(String[] args) throws IOException, InvalidFormatException, ParseException {

        long initialTime = System.currentTimeMillis();

        processAgencyWD(initialTime, args);
        processAgencyIML(initialTime);
        processAgencyB(initialTime);

        System.out.println("Total time of execution: " + longMsToTime(System.currentTimeMillis() - initialTime) + "\n");
    }


    private static void processAgencyWD(long initialTime, String[] args) throws IOException, InvalidFormatException, ParseException {
        Agency wd = new Agency("WD");

        List<String[][]> wdWorkingHoursArray    = WDParser.getWorkingHoursArray();
        List<Set <EventType>> wdEventTypesArray = WDParser.getEventWorkingHoursArray();
        List<inputDataStructure> wdAppointments = WDParser.parseDataSet("data/wd.xlsx", dateStringPattern, timeStringPattern);

        System.out.println("Finished parsing 'wd' data after " + longMsToTime(System.currentTimeMillis() - initialTime) + "\n");

        wd.addMultipleAppointmentsToAgents(wdAppointments, wdWorkingHoursArray, wdEventTypesArray);

        wd.exportAllEventMetrics("eventMetricsWD.csv");
        wd.exportAllCountOfDailyAppointmentMetrics("dailyAppointmentMetricsWD.csv");
        wd.exportAllCountOfDailyAppointmentPerAgentMetrics("dailyPerAgentAppointmentMetricsWD.csv");

        //CalendarViewer.launch(args, wd, WDParser.eventTypeDescriptionClassesList);

        wd = null;
    }

    private static void processAgencyIML(long initialTime) throws IOException, InvalidFormatException, ParseException {
        Agency iml = new Agency("IML");

        List<String[][]> imlWorkingHoursArray    = IMLParser.getWorkingHoursArray();
        List<Set <EventType>> imlEventTypesArray = IMLParser.getEventWorkingHoursArray();
        List<inputDataStructure> imlAppointments = IMLParser.parseDataSet("data/l.xlsx", dateStringPattern, timeStringPattern);

        System.out.println("Finished parsing 'iml' data after " + longMsToTime(System.currentTimeMillis() - initialTime) + "\n");

        iml.addMultipleAppointmentsToAgents(imlAppointments, imlWorkingHoursArray, imlEventTypesArray);

        iml.exportAllEventMetrics("eventMetricsIML.csv");
        iml.exportAllCountOfDailyAppointmentMetrics("dailyAppointmentMetricsIML.csv");
        iml.exportAllCountOfDailyAppointmentPerAgentMetrics("dailyPerAgentAppointmentMetricsIML.csv");

        //CalendarViewer.launch(args, iml, IML.eventTypeDescriptionClassesList)

        iml = null;
    }

    private static void processAgencyB(long initialTime) throws IOException, InvalidFormatException, ParseException {
        Agency b = new Agency("IML");

        List<String[][]> bWorkingHoursArray    = BParser.getWorkingHoursArray();
        List<Set <EventType>> bEventTypesArray = BParser.getEventWorkingHoursArray();
        List<inputDataStructure> bAppointments = BParser.parseDataSet("data/b.xlsx", dateStringPattern, timeStringPattern);

        System.out.println("Finished parsing 'b' data after " + longMsToTime(System.currentTimeMillis() - initialTime) + "\n");

        b.addMultipleAppointmentsToAgents(bAppointments, bWorkingHoursArray, bEventTypesArray);

        b.exportAllEventMetrics("eventMetricsB.csv");
        b.exportAllCountOfDailyAppointmentMetrics("dailyAppointmentMetricsB.csv");
        b.exportAllCountOfDailyAppointmentPerAgentMetrics("dailyPerAgentAppointmentMetricsB.csv");

        //CalendarViewer.launch(args, b, B.eventTypeDescriptionClassesList)

        b = null;
    }
}

