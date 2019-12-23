## Setup
###########
# Falls modData auf TRUE gesetzt wird, wird eine Funktion modDataFunction erwartet
# die die Input-Daten im Argument erhält und diese modifiziert wieder zurückgibt
modData = FALSE;
# Falls forEveryDoc auf TRUE gesetzt ist, so wird für jede Praxis mit mehr als einem Arzt
# zusätzlich zu der Untersuchung bzgl. der Gesamtpraxis auch die Untersuchung für jeden Arzt durchgeführt
forEveryDoc = TRUE;

source("functions.R")

# Wir erstellen ein Histogramm mit den Anzahl der eingehenden Termine pro Tag
# Außerdem erstellen wir ein Histogramm mit den Anzahl der Termine pro Tag für jeden Tag der Woche einzeln
disitrbutionOfDailyNewAppointments <- function(title, data) {
  submitsPerDay <- data %>% group_by(as.Date(EventSubmitDay)) %>% count() 
  if ((!is_empty(submitsPerDay)) && (length(submitsPerDay[[1]]) > 0)) {
    layout(matrix(c(1, 1, 2), nrow = 3, ncol = 1, byrow = TRUE))
    hist(submitsPerDay$n, breaks=25, prob=TRUE, main=paste0(title))
    lines(density(submitsPerDay$n, bw=0.8), col=rgb(1, 0, 0, 0.6))
    print_output("Maximum-likelihood fitting of poisson distributions", fitdistr(submitsPerDay$n, densfun="Poisson"))
  } else { print_output("", "Keine Stammpatienten vorhanden") }
  
  par(mfrow = c(3, 2), oma = c(2,1,4,1) + 0.1,  mar = c(0,1,8,1) + 0.1)
  submitsPerDay <- data
  submitsPerDay$weekDay <- weekdays(as.Date(submitsPerDay$EventSubmitDay))
  submitsPerDay$weekDayNumber <- wday(as.Date(submitsPerDay$EventSubmitDay))
  
  for (i in 2:6) {
    if ((!is_empty(submitsPerDay)) && (length(submitsPerDay[[1]]) > 0)) {
      oneDaySubmitsPerDay <- submitsPerDay[submitsPerDay$weekDayNumber == i, ] %>% group_by(EventSubmitDay) %>% count() 
      if (length(oneDaySubmitsPerDay[,1]) > 0) {
        hist(oneDaySubmitsPerDay$n, breaks=25, prob=TRUE, main=paste0(title, " am Tag ", i - 1))
        lines(density(oneDaySubmitsPerDay$n, bw=0.5), col=rgb(1, 0, 0, 0.6))
        print_output("Maximum-likelihood fitting of poisson distributions", fitdistr(oneDaySubmitsPerDay$n, densfun="Poisson"))
      } else { print_output("", paste0("Keine Stammpatienten vorhanden am Tag ", i - 1)) }
    } else { print_output("", "Keine Stammpatienten vorhanden") }
  }
  print_output("", "") 
  par(mfrow = c(3, 1), oma = c(2,1,4,1) + 0.1,  mar = c(0,1,8,1) + 0.1)
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
execFunctionForAllAgencies(disitrbutionOfDailyNewAppointments, 1, 3, 1, "Distribution of Daily New Appointments")