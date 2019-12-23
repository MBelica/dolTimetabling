## Setup
###########
# Falls modData auf TRUE gesetzt wird, wird eine Funktion modDataFunction erwartet
# die die Input-Daten im Argument erhält und diese modifiziert wieder zurückgibt
modData = TRUE;
# Falls forEveryDoc auf TRUE gesetzt ist, so wird für jede Praxis mit mehr als einem Arzt
# zusätzlich zu der Untersuchung bzgl. der Gesamtpraxis auch die Untersuchung für jeden Arzt durchgeführt
forEveryDoc = TRUE;

source("functions.R")

# Wir erstellen ein Histogramm für jede von unseren Untersuchungen
disitrbutionBetweenTwoAppointments <- function(title, data) {
  dataSet <- data[(data$distributionBetweenClass %in% c("1")), ];

  if ((!is_empty(dataSet)) && (length(dataSet[, 1]) > 0)) {

      hist(as.numeric(dataSet$relCountOfAppointments), breaks = 25, prob=TRUE, main=paste0("Histogramm fuer relativeCountOfAppointments", title))
      hist(as.numeric(dataSet$differenceOfSubsequentSubmits), breaks = 25, prob=TRUE, main=paste0("Histogramm fuer differenceOfSubsequentSubmits" ,title))
      hist(as.numeric(dataSet$differenceOfSubsequentAppointments), breaks = 25, prob=TRUE, main=paste0("Histogramm fuer differenceOfSubsequentAppointments", title))
      hist(as.numeric(dataSet$differenceOfDueToSubmit), breaks = 25, prob=TRUE, main=paste0("Histogramm fuer differenceOfDueToSubmit", title))
  } else {
    print_explanation("Error", "No data available: patients probably only attend once");
    par(mfrow = c(4, 1), oma = c(2,1,4,1) + 0.1,  mar = c(0,1,4,1) + 0.1)
    
  }
}

modDataFunction <- function(data) {
  relCountOfAppointments = c(); distributionBetweenClass = c(); differenceOfSubsequentSubmits = c(); differenceOfSubsequentAppointments = c(); differenceOfDueToSubmit = c();
  if (length(data[,1]) > 0) {
    for (i in 1:length(data[, 1])) {
      Patient <- data[i, ];
      
      if ( (Patient$Attended %in% c("1")) & (Patient$msToNextSDateFromPrevDDate >= 0) ) {
        PatientAllAppointments <- data[(data$PatientID == Patient$PatientID), ];
        PatientFutureAppointments <- PatientAllAppointments[(as.Date(PatientAllAppointments$EventSubmitDay) > as.Date(Patient$EventSubmitDay)), ];
        if (length(PatientFutureAppointments[, 1]) > 0) {
          PatientFutureAppointmentOrdered <- PatientFutureAppointments[order(PatientFutureAppointments$EventSubmitDay), ];
          PatientNextAppointment <- PatientFutureAppointmentOrdered[1,];
          PatientLastAppointment <- PatientFutureAppointmentOrdered[length(PatientFutureAppointmentOrdered[,1]),];
          daysAtAgency <- as.numeric(as.Date(PatientLastAppointment$EventSubmitDay) - as.Date(PatientNextAppointment$EventSubmitDay));
          relCountOfAppointments             <- append(relCountOfAppointments, length(PatientAllAppointments[, 1]/daysAtAgency));
          differenceOfSubsequentSubmits      <- append(differenceOfSubsequentSubmits, as.numeric(as.Date(PatientNextAppointment$EventSubmitDay) - as.Date(Patient$EventSubmitDay)));
          differenceOfSubsequentAppointments <- append(differenceOfSubsequentAppointments, as.numeric(as.Date(PatientNextAppointment$EventDay) - as.Date(Patient$EventDay)));
          differenceOfDueToSubmit            <- append(differenceOfDueToSubmit, as.numeric(as.Date(PatientNextAppointment$EventSubmitDay) - as.Date(Patient$EventDay)));
          distributionBetweenClass           <- append(distributionBetweenClass, 1);
        } else {
          relCountOfAppointments             <- append(relCountOfAppointments, -1);
          differenceOfSubsequentSubmits      <- append(differenceOfSubsequentSubmits, -1);
          differenceOfSubsequentAppointments <- append(differenceOfSubsequentAppointments, -1);
          differenceOfDueToSubmit            <- append(differenceOfDueToSubmit, -1);
          distributionBetweenClass           <- append(distributionBetweenClass, 0);
        }
      } else {
        relCountOfAppointments               <- append(relCountOfAppointments, -1);
        differenceOfSubsequentSubmits        <- append(differenceOfSubsequentSubmits, -1);
        differenceOfSubsequentAppointments   <- append(differenceOfSubsequentAppointments, -1);
        differenceOfDueToSubmit              <- append(differenceOfDueToSubmit, -1);
        distributionBetweenClass             <- append(distributionBetweenClass, 0);        
      }
      
      if (((i %% 100) == 0) || (i == length(data[, 1]))) {
        print(paste0(i, " of ", length(data[, 1])));
      }
    }
    result <- data.frame(data, relCountOfAppointments, differenceOfSubsequentSubmits, differenceOfSubsequentAppointments, differenceOfDueToSubmit, distributionBetweenClass)
  } else { result <- NULL; }
  
  return(result);
}

# Title-Seite zum späteren Füllen mit Beschreibung/Erklärung
print_explanation("Histograms for different values of subsequent appointments", "Text");

## Auszuführendes Skript übergeben, sodass es jeweils für alle Praxen ausgeführt wird 
# execFunctionForAllAgencies(fct, patientType, mfrowCount1, mfrowCount2, funtionType) :
# - fct: die oben definierte Funktion, die für jede Praxis einzeln und für alle gemeinsam ausgeführt werden soll
# - patientType: für welche Patienten soll es durchgeführt werden, -1: alle Patienten, 1: Stammpatienten, 0: Nicht-Stammpatienten
# - mfrowCount1: Anzahl der Spalten von Plots pro Seite
# - mfrowCount2: Anzahl der Zeilen von Plots pro Seite
# - funtionType: Beschreibung der Funktion
execFunctionForAllAgencies(disitrbutionBetweenTwoAppointments, 1, 4, 1, "")