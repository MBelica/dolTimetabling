package edu.kit.dol.timetabling.structures;

import java.text.SimpleDateFormat;
import java.util.Date;

import static edu.kit.dol.timetabling.utilities.Dates.stringToDate;

@SuppressWarnings("unused")
public interface DataStructures {

    class inputDataStructure {
        private final int eventLength;
        private final int directWaitingPeriod;
        private final int indirectWaitingPeriod;
        private final int eventTypeCoding;
        private final int patientAge;
        private final String patientID;
        private final String agentTag;
        private final String agentName;
        private final String askedAgent;
        private final String startTime;
        private final String eventType;
        private final String dayOfSubmit;
        private final String appointmentDate;

        private final boolean attended;

        private long dDateInMS = -1;
        private String formattedEventDay = null;
        private String formattedDayOfSubmit = null;

        public inputDataStructure(String appointmentDate, String startTime, int length, String patientID, int patientAge, int directWaitingPeriod, int indirectWaitingPeriod, String dayOfSubmit, String eventType, int eventTypeCoding, String agentName, String agentTag, String askedAgent, boolean attended) {
            this.appointmentDate = appointmentDate;
            this.directWaitingPeriod = directWaitingPeriod;
            this.indirectWaitingPeriod = indirectWaitingPeriod;
            this.dayOfSubmit = dayOfSubmit;
            this.startTime = startTime;
            this.eventLength = length;
            this.patientID = patientID;
            this.patientAge = patientAge;
            this.agentName = agentName;
            this.agentTag = agentTag;
            this.askedAgent = askedAgent;
            this.attended = attended;

            this.eventType = eventType;
            this.eventTypeCoding = eventTypeCoding;
        }

        public String getPatientID() { return this.patientID; }
        public int getPatientAge() { return this.patientAge; }
        public String getAppointmentDate() { return this.appointmentDate; }
        public String getStartTime() { return this.startTime; }
        public int getEventLength() { return this.eventLength; }
        public int getDirectWaitingPeriod() { return this.directWaitingPeriod; }
        public int getIndirectWaitingPeriod() { return this.indirectWaitingPeriod; }
        public String getDayOfSubmit() { return this.dayOfSubmit; }
        public String getEventDescription() { return this.eventType; }
        public int getEventType () { return this.eventTypeCoding; }
        public boolean getAttended () { return this.attended; }
        public String getAgentName() { return this.agentName; }
        public String getAgentTag() { return this.agentTag; }
        public String getAskedAgent() { return this.askedAgent; }

        public String getFormattedDayOfSubmit() {

            if (formattedDayOfSubmit == null) {
                Date date = stringToDate(dayOfSubmit);
                formattedDayOfSubmit = new SimpleDateFormat("yyyy-MM-dd").format(date);
            }

            return formattedDayOfSubmit;
        }

        public String getFormattedEventDay() {

            if (formattedEventDay == null) {
                Date date = stringToDate(appointmentDate);
                formattedEventDay = new SimpleDateFormat("yyyy-MM-dd").format(date);
            }

            return formattedEventDay;
        }

        public long getDDateInMS() {

            if (dDateInMS == -1) {
                Date dDate = stringToDate(appointmentDate + " " + startTime);
                dDateInMS = dDate.getTime();
            }

            return dDateInMS;
        }
    }
}
