## Setup
###########
# Falls modData auf TRUE gesetzt wird, wird eine Funktion modDataFunction erwartet
# die die input daten als Argument erhält und diese modifiziert wieder zurückgibt
modData = FALSE;
# Falls forEveryDoc auf TRUE gesetzt wird, so wird für jede Praxis mit mehr als einem Arzt
# zusätzlich zu der Untersuchung auf der Gesamtpraxis auch die Untersuchung für jeden Arzt durchgeführt
forEveryDoc = TRUE;

source("functions.R")
i <<- 1
# Die Print-Funktion die speziell für Regressionen angepasst ist definieren wir hier
disitrbutionOfDailyNewAppointments <- function(title, data) {
  appointmentsNT <<- data[(data$minsToNextPossibleEventDate <= 0), ]
  appointmentsKT <<- data[((data$minsToNextPossibleEventDate > 0) & (data$minsToNextPossibleEventDate < 720)), ]
  appointmentsGT <<- data[data$minsToNextPossibleEventDate >= 720, ]
    
  if (length(appointmentsKT[,1]) > 0) {
    hist(appointmentsKT$minsToNextPossibleEventDate/60, prob=TRUE, breaks=25, main=paste0(title, " in Stunden"))
    mtext(paste0(length(appointmentsKT[,1]), " Appointments with less than 12 Hours to Next Possible Appointment"))
  } else { print_output("", "Keine Stammpatienten und/oder Nicht-Stammpatienten vorhanden") }
  if (length(appointmentsGT[,1]) > 0) {
    hist(appointmentsGT$minsToNextPossibleEventDate/(60 * 24), prob=TRUE, breaks=25, main=paste0(title, " in Tagen"))
    mtext(paste0(length(appointmentsGT[,1]), " Appointments with more than 12 Hours to Next Possible Appointment"))
  } else { print_output("", "Keine Stammpatienten und/oder Nicht-Stammpatienten vorhanden") }
}

# Title-Seite zum späteren Füllen mit Beschreibung/Erklärung
print_explanation("Distribution of Next Possible Appointment", "text");

## Auszuführendes Skript übergeben, sodass es jeweils für alle Praxen ausgeführt wird 
# execFunctionForAllAgencies(fct, patientType, mfrowCount1, mfrowCount2, funtionType) :
# - fct: die oben definierte Funktion, die für jede Praxis einzeln und für alle gemeinsam ausgeführt werden soll
# - patientType: für welche Patienten soll es durchgeführt werden, -1: alle Patienten, 1: Stammpatienten, 0: Nicht-Stammpatienten
# - mfrowCount1: Anzahl der Spalten von Plots pro Seite
# - mfrowCount2: Anzahl der Zeilen von Plots pro Seite
# - funtionType: Beschreibung der Funktion
execFunctionForAllAgencies(disitrbutionOfDailyNewAppointments, 1, 2, 1, "Distribution of Next Possible Appointment")