## Setup
###########
source("functions.R")

# Wir erstellen ein Histogramm mit den Anzahl der eingehenden Termine pro Tag
disitrbutionOfDailyNewAppointments <- function(title, data) {
  submitsPerDay <- data %>% group_by(EventSubmitDay) %>% count() 
  if (length(submitsPerDay[,1]) > 0) {
    hist(submitsPerDay$n, breaks=10, prob=TRUE, main=paste0("Distribution of Daily New Appointments (Submits) fuer Stammpatienten"))
  } else { print_output("", "Keine Stammpatienten vorhanden") }
}

# Wir erstellen ein Histogramm mit den Anzahl der Termine pro Tag für jeden Tag der Woche einzeln
disitrbutionOfDailyNewAppointmentsPerDay <- function(title, data) {
  submitsPerDay <- data
  submitsPerDay$weekDay <- weekdays(as.Date(submitsPerDay$EventSubmitDay))
  submitsPerDay$weekDayNumber <- wday(as.Date(submitsPerDay$EventSubmitDay))
  
  for (i in 2:6) {
    if (length(submitsPerDay[,1]) > 0) {
      oneDaySubmitsPerDay <- submitsPerDay[submitsPerDay$weekDayNumber == i, ] %>% group_by(EventSubmitDay) %>% count() 
      if (length(oneDaySubmitsPerDay[,1]) > 0) {
        hist(oneDaySubmitsPerDay$n, breaks=10, prob=TRUE, main=paste0("Count Event Submit Day fuer Stammpatienten am Tag ", i - 1))
      } else { print_output("", paste0("Keine Stammpatienten vorhanden am Tag ", i - 1)) }
    } else { print_output("", "Keine Stammpatienten vorhanden") }
  }
  print_output("", "")
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
execFunctionForAllAgencies(disitrbutionOfDailyNewAppointments, 1, 1, 1, "Distribution of Daily New Appointments")