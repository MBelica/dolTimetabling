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
calc_binReg <- function(data) {
  if ((!is.null(data)) && (length(data[,1]) > 0)) {
    Patienten <- data
    
    Patienten$DaysFromDueToNextSubmit <- ceiling( Patienten$msToNextSDateFromPrevDDate / (1000*60*60*24) );
    # msToNextxDate enthaelt den nächsten Termin, dessen Submit-Date (Termineintrag) älter ist, als das aktuelle Submit Datum
    # Die Differenz zum Due-Date (Arzttermin) kann somit aber sowohl positiv als auch negativ sein
    
    PatientenPDTN <- Patienten[Patienten$DaysFromDueToNextSubmit == 0, ]
    if (length(PatientenPDTN[,1]) > 0) {
      PatientenPDTN$PositiveDueToSubmit <- 1
    }
    PatientenNDTN <- Patienten[Patienten$DaysFromDueToNextSubmit > 0,  ]
    if (length(PatientenNDTN[,1]) > 0) {
      PatientenNDTN$PositiveDueToSubmit <- 0
    }
    
    PatientenDTN <- rbind(PatientenPDTN, PatientenNDTN) 
    
    result <- glm(PositiveDueToSubmit ~ CountOfCurrentAppointments, data = PatientenDTN, family = binomial)
    return(result)
  }
}

# Die Print-Funktion die speziell für Regressionen angepasst ist definieren wir hier
daysFromDueToNextSubmitVsAttended <- function(title, data) {
  print_reg(title, data, calc_binReg)
}

# Title-Seite zum späteren Füllen mit Beschreibung/Erklärung
print_explanation("Nur Stammpatienten - Bin. Regression DueToSubmit positiv bzgl. Anzahl bestehender Termine", "");

## Auszuführendes Skript übergeben, sodass es jeweils für alle Praxen ausgeführt wird 
# execFunctionForAllAgencies(fct, patientType, mfrowCount1, mfrowCount2, funtionType) :
# - fct: die oben definierte Funktion, die für jede Praxis einzeln und für alle gemeinsam ausgeführt werden soll
# - patientType: für welche Patienten soll es durchgeführt werden, -1: alle Patienten, 1: Stammpatienten, 0: Nicht-Stammpatienten
# - mfrowCount1: Anzahl der Spalten von Plots pro Seite
# - mfrowCount2: Anzahl der Zeilen von Plots pro Seite
# - funtionType: Beschreibung der Funktion
execFunctionForAllAgencies(daysFromDueToNextSubmitVsAttended, 1, 1, 1, "Bin. Regression DueToSubmit positiv bzgl. Anzahl bestehender Termine")