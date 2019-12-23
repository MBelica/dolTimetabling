## Setup
###########
source("functions.R")

## Configuration
# Bitte die working directory in functions.R setzen
# boolean: NutzeTagesSpannen
#  - falls FALSE wird Regression ueber alle IndirectWaitingPerdiod erstellt 
#  - falls TRUE wird fuer jeden Vector c(a, b) in der Liste tagesSpannen jeweils eine Regression fuer IndirectWaitingPerdiod in [a, b] erstellt
NutzeTagesSpannen = FALSE; 
tagesSpannen <- list( c(0, 7), c(8,14), c(15, 56), c(57, 250) )

# Die Regression die wir durchfuehren moechten, definieren wir hier
calc_binReg <- function(dataset) {
  result <- glm(Attended ~ freeSlots + shortSlots + bookedSlots, data = dataset, family = binomial)
  #result <- glm(Attended ~ IndirectWaitingPeriod + Metrik1 + Metrik2 + CountOfCurrentAppointments, data = dataset, family = binomial)
  return(result)
}

# Die Print-Funktion die speziell für Regressionen angepasst ist definieren wir hier
dailyAppointmentsVsFuture <- function(title, data) {
  print_reg(title, data, calc_binReg)
}

# Title-Seite zum späteren Füllen mit Beschreibung/Erklärung
print_explanation("Attended Binomal Regressions", "Text");

## Auszuführendes Skript übergeben, sodass es jeweils für alle Praxen ausgeführt wird 
# execFunctionForAllAgencies(fct, patientType, mfrowCount1, mfrowCount2, funtionType) :
# - fct: die oben definierte Funktion, die für jede Praxis einzeln und für alle gemeinsam ausgeführt werden soll
# - patientType: für welche Patienten soll es durchgeführt werden, -1: alle Patienten, 1: Stammpatienten, 0: Nicht-Stammpatienten
# - mfrowCount1: Anzahl der Spalten von Plots pro Seite
# - mfrowCount2: Anzahl der Zeilen von Plots pro Seite
# - funtionType: Beschreibung der Funktion
execFunctionForAllAgencies(dailyAppointmentsVsFuture, -1, 3, 1, "Binomiale Regression")