## Setup
###########
source("functions.R")

# Wir erstellen ein Histogramm mit den Anzahl der Termine pro Tag (unabhängig vom Tag)
disitrbutionOfPatientsPerDay <- function(title, data) {
  patientsPerDay <- data %>% group_by(EventDay) %>% count() 
  if (length(patientsPerDay[,1]) > 0) {
    hist(patientsPerDay$n, breaks=10, prob=TRUE, main=paste0("Count Event Submit Day fuer Stamm-Patienten bezueglich allen Tagen"))
  } else { print_output("", "Keine Stamm-Patienten vorhanden") }
}

# Wir erstellen ein Histogramm mit den Anzahl der Termine pro Tag für jeden Tag der Woche einzeln
disitrbutionOfPatientsPerDayPerWeekDay <- function(title, data) {
  patientsPerDay <- data
  patientsPerDay$weekDay <- weekdays(as.Date(patientsPerDay$EventDay))
  patientsPerDay$weekDayNumber <- wday(as.Date(patientsPerDay$EventDay))
  
  for (i in 2:6) {
    if (length(patientsPerDay[,1]) > 0) {
      oneDaySubmitsPerDay <- patientsPerDay[patientsPerDay$weekDayNumber == i, ] %>% group_by(EventDay) %>% count() 
      if (length(oneDaySubmitsPerDay[,1]) > 0) {
        hist(oneDaySubmitsPerDay$n, breaks=10, prob=TRUE, main=paste0("Count Event Submit Day fuer Stamm-Patienten am Tag ", i - 1))
      } else { print_output("", paste0("Keine Stamm-Patienten vorhanden am Tag ", i - 1)) }
    } else { print_output("", "Keine Stamm-Patienten vorhanden") }
  }
  print_output("", "")
}

# Title-Seite zum späteren Füllen mit Beschreibung/Erklärung
print_explanation("Distribution of Daily Appointments", "Text");

## Auszuführendes Skript übergeben, sodass es jeweils für alle Praxen ausgeführt wird 
# execFunctionForAllAgencies(fct, patientType, mfrowCount1, mfrowCount2, funtionType) :
# - fct: die oben definierte Funktion, die für jede Praxis einzeln und für alle gemeinsam ausgeführt werden soll
# - patientType: für welche Patienten soll es durchgeführt werden, -1: alle Patienten, 1: Stammpatienten, 0: Nicht-Stammpatienten
# - mfrowCount1: Anzahl der Spalten von Plots pro Seite
# - mfrowCount2: Anzahl der Zeilen von Plots pro Seite
# - funtionType: Beschreibung der Funktion
execFunctionForAllAgencies(disitrbutionOfPatientsPerDay, 1, 1, 1, "Distribution of Patients per Day")
execFunctionForAllAgencies(disitrbutionOfPatientsPerDayPerWeekDay, 1, 3, 2, "Distribution of Patients per Day")