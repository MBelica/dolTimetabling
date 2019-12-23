## Setup
###########
# Falls modData auf TRUE gesetzt wird, wird eine Funktion modDataFunction erwartet
# die die Input-Daten im Argument erhält und diese modifiziert wieder zurückgibt
modData = FALSE;
# Falls forEveryDoc auf TRUE gesetzt ist, so wird für jede Praxis mit mehr als einem Arzt
# zusätzlich zu der Untersuchung bzgl. der Gesamtpraxis auch die Untersuchung für jeden Arzt durchgeführt
forEveryDoc = TRUE;
# boolean: NutzeTagesSpannen
#  - falls FALSE wird Regression ueber alle IndirectWaitingPerdiod erstellt 
#  - falls TRUE wird fuer jeden Vector c(a, b) in der Liste tagesSpannen jeweils eine Regression fuer IndirectWaitingPerdiod in [a, b] erstellt
NutzeTagesSpannen = FALSE; 
tagesSpannen <- list( c(0, 7), c(8,14), c(15, 56), c(57, 250) )

source("functions.R")

# Die Regression die wir durchfuehren moechten, definieren wir hier
calc_binReg <- function(dataset) {
  #result <- glm(Attended ~ freeSlots + shortSlots + bookedSlots, data = dataset, family = binomial)
  result <- 0
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
pdf(paste0(substr(basename(rstudioapi::getSourceEditorContext()$path), 1, nchar(basename(rstudioapi::getSourceEditorContext()$path))-1),'pdf'))
execFunctionForAllAgencies(dailyAppointmentsVsFuture, -1, 1, 1, "Binomiale Regression")
dev.off()
execFunctionForAllAgencies(dailyAppointmentsVsFuture, -1, 1, 1, "Binomiale Regression")