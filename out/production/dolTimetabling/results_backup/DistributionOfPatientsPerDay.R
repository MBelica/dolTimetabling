## Setup
###########
# Falls modData auf TRUE gesetzt wird, wird eine Funktion modDataFunction erwartet
# die die Input-Daten im Argument erhält und diese modifiziert wieder zurückgibt
modData = FALSE;
# Falls forEveryDoc auf TRUE gesetzt ist, so wird für jede Praxis mit mehr als einem Arzt
# zusätzlich zu der Untersuchung bzgl. der Gesamtpraxis auch die Untersuchung für jeden Arzt durchgeführt
forEveryDoc = TRUE;

source("functions.R")

# Wir erstellen ein Histogramm mit den Anzahl der Termine pro Tag (unabhängig vom Tag)
disitrbutionOfPatientsPerDay <- function(title, data) {
  patientsPerDay <- data %>% group_by(as.Date(EventDay)) %>% count() 
  if ((!is_empty(patientsPerDay)) && (length(patientsPerDay[[1]]) > 0)) {
    layout(matrix(c(1, 1, 2), nrow = 3, ncol = 1, byrow = TRUE))
    hist(patientsPerDay$n, breaks=25, prob=TRUE, main=paste0(title))
    lines(density(patientsPerDay$n, bw=0.8), col=rgb(1, 0, 0, 0.6))
    print_output("Maximum-likelihood fitting of poisson distributions", fitdistr(patientsPerDay$n, densfun="Poisson"))
  } else { print_output("", "Keine Stammpatienten vorhanden") }
  
  par(mfrow = c(3, 2), oma = c(2,1,4,1) + 0.1,  mar = c(0,1,8,1) + 0.1)
  patientsPerDay <- data
  patientsPerDay$weekDay <- weekdays(as.Date(patientsPerDay$EventDay))
  patientsPerDay$weekDayNumber <- wday(as.Date(patientsPerDay$EventDay))
  
  for (i in 2:6) {
    if ((!is_empty(patientsPerDay)) && (length(patientsPerDay[[1]]) > 0)) {
      oneDaySubmitsPerDay <- patientsPerDay[patientsPerDay$weekDayNumber == i, ] %>% group_by(EventDay) %>% count() 
      if ((!is_empty(oneDaySubmitsPerDay)) && (length(oneDaySubmitsPerDay[[1]]) > 0)) {
        hist(oneDaySubmitsPerDay$n, breaks=25, prob=TRUE, main=paste0(title, " am Tag ", i - 1))
        lines(density(oneDaySubmitsPerDay$n, bw=0.8), col=rgb(1, 0, 0, 0.6))
        print_output("Maximum-likelihood fitting of poisson distributions", fitdistr(oneDaySubmitsPerDay$n, densfun="Poisson"))
      } else { print_output("", paste0("Keine Stammpatienten vorhanden am Tag ", i - 1)) }
    } else { print_output("", "Keine Stammpatienten vorhanden") }
  }
  print_output("", "")  
  print_output("", "")  
  par(mfrow = c(3, 1), oma = c(2,1,4,1) + 0.1,  mar = c(0,1,8,1) + 0.1)
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
execFunctionForAllAgencies(disitrbutionOfPatientsPerDay, 1, 3, 1, "Distribution of Patients per Day")