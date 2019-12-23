package edu.kit.dol.timetabling.exporter;

import edu.kit.dol.timetabling.model.Event;
import edu.kit.dol.timetabling.model.TimeTable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static edu.kit.dol.timetabling.exporter.Commons.saveExport;
import static edu.kit.dol.timetabling.utilities.Configuration.lengthOfSession;

public interface Timetables {

    static void exportCSVTable(String tag, TimeTable table, List<Event> eventLog) {
        int year = table.getYear();
        int daysInYear = table.getDaysInYear();

        StringBuilder builder = new StringBuilder();
        builder.append("Timetable ").append(year).append(":").append("\n\n;;");
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE (dd.MM.yyyy)");

        for (int i = 1; i <= daysInYear; i++) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.DAY_OF_YEAR, i);
            int dIW = table.getDayInWeek(i);
            if ((i == 1) || ((i > 1) && (dIW == 1))) {
                builder.append("\n;;");
                for (int j = 0; j < (1440 / lengthOfSession); j++) {
                    int m = j * lengthOfSession;
                    builder.append(m / 60).append(":").append(m % 60).append(";");
                }
                builder.append("\n");
            }

            builder.append(";").append(sdf.format(cal.getTime()));
            for (int k = 0; k < (1440 / lengthOfSession); k++) {
                if (table.timeTable[i - 1][k] != null) {
                    StringBuilder res = new StringBuilder();
                    if (table.timeTable[i - 1][k].getEventBooked()) {
                        if( table.timeTable[i - 1][k].getEventIDs() != null ) {
                            int eventCounter = 0;
                            for (UUID eventID : table.timeTable[i - 1][k].getEventIDs()) {
                                eventCounter++;
                                if (eventCounter > 1) res.append(", ");
                                res.append("[");
                                Event event = null;
                                for (Event e : eventLog) {
                                    if (e.getUniqueId() == eventID) {
                                        event = e;
                                        break;
                                    }
                                }
                                if (event != null) {
                                    res.append(event.getType()).append(": ").append(eventID.toString());
                                } else throw new IllegalStateException("An event in the time table was expected to be in log but not found.");
                                res.append("]");
                            }
                        }
                    } else res = new StringBuilder("-");
                    builder.append(";").append(table.timeTable[i - 1][k].getEventType()).append(": ").append(res);
                } else builder.append(";null");
            }
            builder.append("\n\n");
        }

        String agentIdentifier;
        if ( (tag != null) && (!"".equals(tag)) ) {
            agentIdentifier = tag;
        } else agentIdentifier = "Unnamed";

        saveExport(agentIdentifier + " - " + year + ".csv", builder);
    }
}
