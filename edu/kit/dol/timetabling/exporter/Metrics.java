package edu.kit.dol.timetabling.exporter;

import edu.kit.dol.timetabling.model.Event;

import static edu.kit.dol.timetabling.exporter.Commons.saveExport;
import static edu.kit.dol.timetabling.utilities.Dates.timeSlotToString;
import static edu.kit.dol.timetabling.utilities.Verbose.printProgress;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


public interface Metrics {

    interface DailyAppointmentMetrics {
        static void exportMultipleDailyAppointmentMetrics (String filename, List< String[] > Metrics, boolean perAgent) {
            if (Metrics != null) {
                System.out.println("Exporting Metrics " + Metrics.size() + ":");
                long start = System.currentTimeMillis();

                StringBuilder content = new StringBuilder();
                content
                        .append("AgentTag").append(';')
                        .append("EventSubmitDay").append(';')
                        .append("EventDay").append(';')
                        .append("EventTimestamp").append(';')
                        .append("PatientID").append(';');
                        if (perAgent) {
                            content
                                .append("CountOfDailyAppointmentsPerAgent").append(';')
                                .append("SumOfFutureAppointmentsPerAgent").append(';');
                        } else {
                            content
                                .append("CountOfDailyAppointmentsPerAgency").append(';')
                                .append("SumOfFutureAppointmentsPerAgency").append(';');
                        }
                content.append('\n');

                int k = 1;
                for (String[] metric : Metrics) {
                    for (String metricEntry: metric) {
                        content.append(metricEntry).append(';');
                    }
                    content.append('\n');
                    printProgress(start, Metrics.size(), k++);
                }

                System.out.println("\n");
                saveExport(filename, content);
            } else throw new IllegalArgumentException("Metrics null");
        }
    }

    interface EventMetrics {
        static void exportMultipleEventMetrics(String filename, List<String[]> Metrics, List<Event> Events, boolean detailed) {

            if ((Metrics != null) && (Events != null)) {

                StringBuilder content = new StringBuilder();

                content
                        .append("AgentTag").append(';')
                        .append("EventSubmitDay").append(';')
                        .append("EventDay").append(';')
                        .append("EventTimestamp").append(';')
                        .append("TimeSlotAsInt").append(';')
                        .append("TimeSlotAsString").append(';')
                        .append("PatientID").append(';')
                        .append("PatientAge").append(';')
                        .append("DirectWaitingPeriod").append(';')
                        .append("IndirectWaitingPeriod").append(';')
                        .append("Metrik1").append(';')
                        .append("Metrik2").append(';');

                if (detailed) {
                    content
                        .append("freeSlots").append(';')
                        .append("shortSlots").append(';')
                        .append("bookedSlots").append(';')
                        .append("CountOfCurrentAppointments").append(';')
                        .append("msToNextSDateFromPrevDDate").append(';')
                        .append("msToNextDDateFromPrevDDate").append(';')
                        .append("numberOfSlots").append(';')
                        .append("slotsToNextPossibleEventDate").append(';')
                        .append("minsToNextPossibleEventDate").append(';');
                }

                content
                        .append("Attended").append(';')
                        .append("Stammpatient").append(';')
                        .append("WalkIn").append(';')
                        .append('\n');

                if (Metrics.size() == Events.size()) {

                    System.out.println("Exporting Metrics " + Metrics.size() + ":");
                    long start = System.currentTimeMillis();
                    for (int k = 0; k < Metrics.size(); k++) {
                        String[] metric = Metrics.get(k);
                        Event event = Events.get(k);

                        addSingleEventMetricsToExport(content, metric, event, detailed);
                        printProgress(start, Metrics.size(), k + 1);
                    }
                }
                System.out.println("\n");
                saveExport(filename, content);
            } else throw new IllegalStateException("Appointments and Metrics length mismatch");
        }

        static void addSingleEventMetricsToExport(StringBuilder content, String[] metrics, Event event, boolean detailed) {
            if (metrics != null) {
                int stammpatient = event.getPatientID().equals("") ? 0 : 1;
                int attended = event.getAttended() ? 1 : 0;
                int walkin = (event.getDayOfSubmitAsString() == event.getDayOfEventAsString()) ? 1 : 0;
                DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.FRENCH);
                format.setMaximumFractionDigits(12);

                content
                        .append(event.getAgentTag()).append(';')
                        .append(event.getDayOfSubmitAsString()).append(';')
                        .append(event.getDayOfEventAsString()).append(';')
                        .append(Long.toString(event.getDDateInMS())).append(';')
                        .append(event.getTimeSlot()).append(';')
                        .append(timeSlotToString(event.getTimeSlot())).append(';')
                        .append(event.getPatientID()).append(';')
                        .append(Integer.toString(event.getPatientAge())).append(';')
                        .append(Integer.toString(event.getDirectWaitingPeriod())).append(';')
                        .append(Integer.toString(event.getIndirectWaitingPeriod())).append(';')
                        .append(metrics[0]).append(';')
                        .append(metrics[1]).append(';');

                if (detailed) {
                    for (int i = 2; i < metrics.length; i++) {
                        content.append(metrics[i]).append(';');
                    }
                }

                content
                        .append(Integer.toString(attended)).append(';')
                        .append(Integer.toString(stammpatient)).append(';')
                        .append(Integer.toString(walkin)).append(';')
                        .append('\n');
            }
        }
    }
}
