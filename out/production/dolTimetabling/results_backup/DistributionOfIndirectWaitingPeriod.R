## Setup
###########
# Falls modData auf TRUE gesetzt wird, wird eine Funktion modDataFunction erwartet
# die die input daten als Argument erhält und diese modifiziert wieder zurückgibt
modData = FALSE;
# Falls forEveryDoc auf TRUE gesetzt wird, so wird für jede Praxis mit mehr als einem Arzt
# zusätzlich zu der Untersuchung auf der Gesamtpraxis auch die Untersuchung für jeden Arzt durchgeführt
forEveryDoc = TRUE;

source("functions.R")

# Die Print-Funktion die speziell für Regressionen angepasst ist definieren wir hier
disitrbutionOfDailyNewAppointments <- function(title, data) {
  appointments <- data
  if (length(appointments[,1]) > 0) {
    hist(appointments$IndirectWaitingPeriod, prob=TRUE, breaks=25, main=title)
  } else { print_output("", "Keine Stammpatienten und/oder Nicht-Stammpatienten vorhanden") }
}

# Title-Seite zum späteren Füllen mit Beschreibung/Erklärung
print_explanation("Distribution of Daily Appointments", "text");

## Auszuführendes Skript übergeben, sodass es jeweils für alle Praxen ausgeführt wird 
# execFunctionForAllAgencies(fct, patientType, mfrowCount1, mfrowCount2, funtionType) :
# - fct: die oben definierte Funktion, die für jede Praxis einzeln und für alle gemeinsam ausgeführt werden soll
# - patientType: für welche Patienten soll es durchgeführt werden, -1: alle Patienten, 1: Stammpatienten, 0: Nicht-Stammpatienten
# - mfrowCount1: Anzahl der Spalten von Plots pro Seite
# - mfrowCount2: Anzahl der Zeilen von Plots pro Seite
# - funtionType: Beschreibung der Funktion
execFunctionForAllAgencies(disitrbutionOfDailyNewAppointments, -1, 3, 1, "Distribution of Indirect Waiting Period")