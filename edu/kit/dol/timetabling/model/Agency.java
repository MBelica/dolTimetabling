package edu.kit.dol.timetabling.model;

import edu.kit.dol.timetabling.structures.DataStructures;
import edu.kit.dol.timetabling.structures.EventType;

import java.util.*;
import java.util.stream.Collectors;

import static edu.kit.dol.timetabling.exporter.Metrics.DailyAppointmentMetrics.exportMultipleDailyAppointmentMetrics;
import static edu.kit.dol.timetabling.exporter.Metrics.EventMetrics.exportMultipleEventMetrics;
import static edu.kit.dol.timetabling.utilities.Configuration.computeMetrics;
import static edu.kit.dol.timetabling.utilities.Configuration.lengthOfSession;
import static edu.kit.dol.timetabling.utilities.Configuration.returnDetailedMetrics;
import static edu.kit.dol.timetabling.utilities.Verbose.printProgress;

@SuppressWarnings("unused")
public class Agency {

    private final String agencyTag;
    private final Set < Agent > agentList;

    private List<String[][]> workingHoursArray = null;
    private List<Set <EventType>> eventTypesArray = null;
    private List<Event> eventLog = new ArrayList< > ();

    public Agency() {
        agencyTag = "";
        agentList = new HashSet < > ();
    }

    public Agency(String tag) {
        agencyTag = tag;
        agentList = new HashSet < > ();
    }

    public Agent createAgent() {
        return createAgent(null, null, null);
    }

    public Agent createAgent(String tag) {
        return createAgent(tag, null, null);
    }

    public Agent createAgent(String tag, String[][] workingHours) {
        return createAgent(tag, workingHours, null);
    }

    private Agent createAgent(String tag, String[][] workingHours, Set<EventType> eventTypes) {

        Agent agent = new Agent(tag, workingHours, eventTypes);
        agentList.add(agent);

        return agent;
    }

    public Agent getAgentByTag(String tag) {
        Agent result = null;

        for (Agent agent : agentList) {
            if (agent.getTag().equals(tag)) {
                result = agent;
                break;
            }
        }

        return result;
    }

    public Agent getOrCreateAgentByTag(String tag, String[][] workingHours, Set <EventType> eventTypes) {
        Agent result = null;

        for (Agent agent : agentList) {
            if (agent.getTag().equals(tag)) {
                result = agent;
                break;
            }
        }

        if ( result == null ) {
            result = createAgent(tag, workingHours, eventTypes);
        }

        return result;
    }

    public List<String[]> computeMultipleMetricsForAgents(List<Event> Events, List<String[][]> workingHoursArray, List<Set <EventType>> eventTypesArray) {
        List< String[] > result = new ArrayList< > ();


        System.out.println("Calculating metrics for " + Events.size() + " appointments at agency " + agencyTag + ":");
        if (Events != null) {
            int i = 1;
            long start = System.currentTimeMillis();
            for (Event event : Events) {
                String[] metrics = computeMetricsForAgents( event, event.getAskedAgent(), workingHoursArray, eventTypesArray );
                result.add(metrics);

                printProgress(start, Events.size(), i++);
            }
            System.out.println("\n");
        } else throw new IllegalArgumentException("Appointments null");
        return result;
    }

    public String[] computeMetricsForAgents(Event event, String agentID, List<String[][]> workingHoursArray, List<Set <EventType>> eventTypesArray) {
        int freeSlots = 0,
            shortSlots = 0,
            bookedSlots = 0,
            countOfCurrentAppointments = 0;

        String[] result;
        Set < Agent > agentsToComputeMetricsFor = new HashSet < > ();

        if (!agentID.equals("all")) {
            String[] tokens = agentID.split(",");
            for (String token : tokens) {
                int indexOfAgent = Integer.parseInt(token) - 1;
                agentsToComputeMetricsFor.add( getOrCreateAgentByTag( token, workingHoursArray.get( indexOfAgent ),  eventTypesArray.get( indexOfAgent ) ) );
            }
        } else agentsToComputeMetricsFor = agentList;

        for (Agent agent : agentsToComputeMetricsFor) {
            if (agent != null) {

                agent.createMissingCalendars(event);
                int[] metricData = agent.getSlotCount(event);

                freeSlots   += metricData[0];
                shortSlots  += metricData[1];
                bookedSlots += metricData[2];

                countOfCurrentAppointments += agent.getCountOfCurrentAppointments(event);
            }
        }

        result = computeMetrics(freeSlots, shortSlots, bookedSlots);

        long msToNextSDateFromPrevDDate = 0, msToNextDDateFromPrevDDate = 0;
        if (!event.getPatientID().equals("")) {
            List<Event> eventsInQuestion = eventLog.stream().filter(p -> ((p.getSDateInMS() > event.getSDateInMS()) && (p.getPatientID().equals(event.getPatientID())))).collect(Collectors.toList());
            Event nextEventInQuestion = eventsInQuestion.stream().min(Comparator.comparing(Event::getSDateInMS)).orElse(null);
            if (nextEventInQuestion != null) {
                msToNextSDateFromPrevDDate = (nextEventInQuestion.getSDateInMS() - event.getDDateInMS());
                msToNextDDateFromPrevDDate = (nextEventInQuestion.getDDateInMS() - event.getDDateInMS());
            }
        }


        int slots = event.getNumberOfSessions();
        int slotsToNextPossibleEventDate = event.getSlotsToNextPossibleEventDate();
        int minsToNextPossibleEventDate  = slotsToNextPossibleEventDate * lengthOfSession;

        if (returnDetailedMetrics) {
            return new String[]{ result[0], result[1], Integer.toString(freeSlots), Integer.toString(shortSlots), Integer.toString(bookedSlots), Integer.toString(countOfCurrentAppointments), Long.toString(msToNextSDateFromPrevDDate), Long.toString(msToNextDDateFromPrevDDate), Integer.toString(slots), Integer.toString(slotsToNextPossibleEventDate), Integer.toString(minsToNextPossibleEventDate) };
        } else return result;
    }

    public List< String[] > getCountOfDailyAppointmentMetrics() {
        List< String[] > result = new ArrayList<>();

        long start = System.currentTimeMillis();
        System.out.println("Retrieving daily appointment metrics for " + eventLog.size() + " appointments in agency " + agencyTag + ":");

        int i = 1;
        for (Event event : eventLog) {

            String eventSubmitDay = event.getDayOfSubmitAsString();
            long sumOfFutureAppointments = 0;
            for (Agent agent : agentList) sumOfFutureAppointments += agent.getCountOfCurrentAppointments(event);
            long countOfDailyAppointments = eventLog.stream().filter(p -> (p.getDayOfSubmitAsString().equals(eventSubmitDay))).count();

            result.add( new String[] { event.getAgentTag(), eventSubmitDay,  event.getDayOfEventAsString(), Long.toString(event.getDDateInMS()), event.getPatientID(), Long.toString(countOfDailyAppointments), Long.toString(sumOfFutureAppointments) } );

            printProgress(start, eventLog.size(), i++);
        }
        System.out.println("\n");

        return result;
    }

    public List< String[] > getCountOfDailyAppointmentPerAgentMetrics() {
        List< String[] > result = new ArrayList<>();

        int i = 1;
        long start = System.currentTimeMillis();
        System.out.println("Retrieving daily appointment metrics per agent for " + eventLog.size() + " appointments at agency " + agencyTag + ":");
        for (Agent agent : agentList) {
            List< Event > events = agent.getEventLog();

            for (Event event : events) {

                String eventSubmitDate = event.getDayOfSubmitAsString();
                long sumOfFutureAppointments  = agent.getCountOfCurrentAppointments(event);
                long countOfDailyAppointments = events.stream().filter(p -> (p.getDayOfSubmitAsString().equals(eventSubmitDate))).count();

                result.add( new String[] { event.getAgentTag(), eventSubmitDate, event.getDayOfEventAsString(), Long.toString(event.getDDateInMS()), event.getPatientID(), Long.toString(countOfDailyAppointments), Long.toString(sumOfFutureAppointments) } );

                printProgress(start, eventLog.size(), i++);
            }
        }
        System.out.println("\n");

        return result;
    }

    public void addMultipleAppointmentsToAgents(List<DataStructures.inputDataStructure> Appointments, List<String[][]> workingHoursArray, List<Set <EventType>> eventTypesArray) {

        System.out.println("Trying to add " + Appointments.size() + " appointments to the agency " + agencyTag + ":");
        if (Appointments != null) {
            long start = System.currentTimeMillis();

            if (this.eventTypesArray == null) this.eventTypesArray = eventTypesArray;
            if (this.workingHoursArray == null) this.workingHoursArray = workingHoursArray;

            int i = 1;
            eventLog.clear();
            for (DataStructures.inputDataStructure appointment : Appointments) {
                int indexOfAgentInQuestion = Integer.parseInt(appointment.getAgentTag()) - 1;
                Agent agentInQuestion = getOrCreateAgentByTag(appointment.getAgentTag(), workingHoursArray.get(indexOfAgentInQuestion), eventTypesArray.get(indexOfAgentInQuestion));
                Event newEvent = new Event(appointment.getAppointmentDate(), appointment.getStartTime(), appointment.getEventLength(), appointment.getEventType(), appointment.getDayOfSubmit(), appointment.getAgentTag(), appointment.getPatientID(), appointment.getPatientAge(), appointment.getAskedAgent(), appointment.getAttended(), appointment.getDirectWaitingPeriod(), appointment.getIndirectWaitingPeriod());

                agentInQuestion.addEvent(newEvent);
                printProgress(start, Appointments.size(), i++);
            }
            for (Agent agent : agentList) eventLog.addAll(agent.getEventLog());
            System.out.println("\n" + "Successfully added " + eventLog.size() + " appointments to the agency " + agencyTag + "\n");
        } else throw new IllegalArgumentException("Appointments null");
    }

    public Set < Agent > getAgents () {
        return agentList;
    }

    public Agent findAgentByID(UUID ID) {
        Agent result = null;

        for (Agent agent : agentList) {
            if (agent.getUniqueId().equals(ID)) {
                result = agent;
                break;
            }
        }

        return result;
    }

    public void printStaffList() {
        int i = 0;
        for (Agent a: agentList) {
            i++;

            System.out.println(i + ": " + a.getTag() + " (ID: " + a.getUniqueId() + ")");
        }
    }

    public void exportAllSchedulesAsCSV() {
        for (Agent agent: agentList) {
            agent.exportAllSchedulesAsCSV();
        }
    }

    public void exportAllEventMetrics(String exportFile) {
        List<String[]> metrics = computeMultipleMetricsForAgents(eventLog, workingHoursArray, eventTypesArray);
        exportMultipleEventMetrics(exportFile, metrics, eventLog, returnDetailedMetrics);
    }

    public void exportAllCountOfDailyAppointmentMetrics(String exportFile) {
        exportMultipleDailyAppointmentMetrics(exportFile, getCountOfDailyAppointmentMetrics(), false);
    }

    public void exportAllCountOfDailyAppointmentPerAgentMetrics(String exportFile) {
        exportMultipleDailyAppointmentMetrics(exportFile, getCountOfDailyAppointmentPerAgentMetrics(), true);
    }
}