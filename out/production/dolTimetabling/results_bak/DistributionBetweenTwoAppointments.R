## Setup
###########
source("functions.R")

createDataFrame <- function(data) {
  betweenTwoAppointmentsData <- data.frame()
  Patienten <- data[(data$Attended %in% c("1")) & (data$msToNextSDateFromPrevDDate >= 0), ]
  if (length(Patienten[, 1]) > 0) {
    PatientNextAppointment <- NULL;
    for (i in 1:length(Patienten[, 1])) {
      
      Patient <- Patienten[(Patienten$PatientID == Patienten[i, 5]), ];
      PatientFutureAppointment <- Patient[(Patient$EventSubmitDay > Patienten[i, 2]), ];
      if (length(PatientFutureAppointment[, 1]) > 0) {
        PatientFutureAppointmentOrdered <- PatientFutureAppointment[order(PatientFutureAppointment$EventSubmitDay), ];
        PatientNextAppointment <- PatientFutureAppointmentOrdered[1,];
        
        countOfTotalAppointments           = length (Patient[, 1]);
        differenceOfSubsequentSubmits      = NULL; #= as.Date(PatientNextAppointment$EventSubmitDay) - as.Date(Patienten[i, 2]);
        differenceOfSubsequentAppointments = as.Date(PatientNextAppointment$EventDay) - as.Date(Patienten[i, 3]);
        averageIndirectWaitingPeriod       = (PatientNextAppointment$IndirectWaitingPeriod + Patienten[i, 8]) / 2;
        differenceOfDueToSubmit            = as.Date(PatientNextAppointment$EventSubmitDay) - as.Date(Patienten[i, 3]);
        row = c(countOfTotalAppointments, differenceOfSubsequentSubmits, differenceOfSubsequentAppointments, averageIndirectWaitingPeriod, differenceOfDueToSubmit);
        betweenTwoAppointmentsData <- rbind(betweenTwoAppointmentsData, row)
      }
    }
    colnames(betweenTwoAppointmentsData) <- c("countOfTotalAppointments", "differenceOfSubsequentSubmits", "differenceOfSubsequentAppointments", "averageIndirectWaitingPeriod", "differenceOfDueToSubmit");
  }
  
  
  return(betweenTwoAppointmentsData)
}

# Wir erstellen ein Histogramm mit den Anzahl der Termine pro Tag für jeden Tag der Woche einzeln
disitrbutionBetweenTwoAppointments <- function(title, data) {
  
  dataFrame <<- createDataFrame(data);
  
  #hist(dataFrame$countOfTotalAppointments, prob=TRUE)
  #hist(dataFrame$differenceOfSubsequentSubmits, prob=TRUE)
  
  #hist(dataFrame$differenceOfSubsequentAppointments, prob=TRUE)
  #hist(dataFrame$averageIndirectWaitingPeriod, prob=TRUE)
  #hist(dataFrame$differenceOfDueToSubmit, prob=TRUE)
}

# Title-Seite zum späteren Füllen mit Beschreibung/Erklärung
print_explanation("Distribution of Daily New Appointments", "Text");

## Auszuführendes Skript übergeben, sodass es jeweils für alle Praxen ausgeführt wird 
# execFunctionForAllAgencies(fct, patientType, mfrowCount1, mfrowCount2, funtionType) :
# - fct: die oben definierte Funktion, die für jede Praxis einzeln und für alle gemeinsam ausgeführt werden soll
# - patientType: für welche Patienten soll es durchgeführt werden, -1: alle Patienten, 1: Stammpatienten, 0: Nicht-Stammpatienten
# - mfrowCount1: Anzahl der Spalten von Plots pro Seite
# - mfrowCount2: Anzahl der Zeilen von Plots pro Seite
# - funtionType: Beschreibung der Funktion
execFunctionForAllAgencies(disitrbutionBetweenTwoAppointments, 1, 1, 1, "Distribution of Daily New Appointments")