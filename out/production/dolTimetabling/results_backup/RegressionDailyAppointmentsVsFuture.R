## Setup
###########
# Falls modData auf TRUE gesetzt wird, wird eine Funktion modDataFunction erwartet
# die die input daten als Argument erhält und diese modifiziert wieder zurückgibt
modData = FALSE;
# Falls forEveryDoc auf TRUE gesetzt wird, so wird für jede Praxis mit mehr als einem Arzt
# zusätzlich zu der Untersuchung auf der Gesamtpraxis auch die Untersuchung für jeden Arzt durchgeführt
forEveryDoc = TRUE;

source("functions.R")

## Configuration
# Bitte die working directory in functions.R setzen
# boolean: NutzeTagesSpannen
#  - falls FALSE wird Regression ueber alle IndirectWaitingPerdiod erstellt 
#  - falls TRUE wird fuer jeden Vector c(a, b) in der Liste tagesSpannen jeweils eine Regression fuer IndirectWaitingPerdiod in [a, b] erstellt
NutzeTagesSpannen = FALSE; 
tagesSpannen <- list( c(0, 7), c(8,14), c(15, 56), c(57, 250) )

# Die Regression die wir durchfuehren moechten, definieren wir hier
calc_reg <- function(dataset) {
  result <- glm(CountOfDailyAppointmentsPerAgency ~ SumOfFutureAppointmentsPerAgency, data = dataset)
  #result <- glm(CountOfDailyAppointmentsPerAgent ~ SumOfFutureAppointmentsPerAgent, data = dataset)
  return(result)
}

# Die Print-Funktion die speziell für Regressionen angepasst ist definieren wir hier
dailyAppointmentsVsFuture <- function(title, data) {
  print_reg(title, data, calc_reg)
}

# Title-Seite zum späteren Füllen mit Beschreibung/Erklärung
print_explanation("Daily Appointments Vs Future Appointments", "Text");

## Auszuführendes Skript übergeben, sodass es jeweils für alle Praxen ausgeführt wird 
# execFunctionForAllAgencies(fct, patientType, mfrowCount1, mfrowCount2, funtionType) :
# - fct: die oben definierte Funktion, die für jede Praxis einzeln und für alle gemeinsam ausgeführt werden soll
# - patientType: für welche Patienten soll es durchgeführt werden, -1: alle Patienten, 1: Stammpatienten, 0: Nicht-Stammpatienten
# - mfrowCount1: Anzahl der Spalten von Plots pro Seite
# - mfrowCount2: Anzahl der Zeilen von Plots pro Seite
# - funtionType: Beschreibung der Funktion
res <- execFunctionForAllAgencies(dailyAppointmentsVsFuture, -1, 3, 1, "Lineare Regression")