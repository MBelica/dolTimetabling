## Setup
###########
source("functions.R")

## Script
daysFromDueToNextSubmitVsAttended <- function(title, data) {
  if ((!is.null(data)) && (length(data[,1]) > 0)) {
    Patienten <- data
    layout(matrix(c(1, 1, 1, 2, 3, 4, 5, 5, 5, 6, 7, 8, 9, 10, 11), nrow = 5, ncol = 3, byrow = TRUE))
 
    Patienten$DaysFromDueToNextSubmit <- ceiling( Patienten$msToNextSDateFromPrevDDate / (1000*60*60*24) );
    # msToNextxDate enthaelt den nächsten Termin, dessen Submit-Date (Termineintrag) älter ist, als das aktuelle Submit Datum
    # Die Differenz zum Due-Date (Arzttermin) kann somit aber sowohl positiv als auch negativ sein
    
    # Wir gehen davon aus, dass diejenigen, die einfach nicht erschienen sind, eine "Strafe" erhalten.
    # Wir wollen also, dass sie nicht erschienen sind (attended = 0), aber auch, dass sie nicht vorab angerufen haben und ggf.
    # einen neuen Termin ausgemacht haben, das heißt sDateNeu > dDateAlt
    PatientenNE <- Patienten[Patienten$Attended %in% c("0"), c("DaysFromDueToNextSubmit", "Attended") ]
    PatientenNE$group <- "NE";  PatientenNE$group <- as.factor(PatientenNE$group)
    
    PatientenIE <- Patienten[Patienten$Attended %in% c("1"), c("DaysFromDueToNextSubmit", "Attended") ]
    PatientenIE$group <- "IE"; PatientenIE$group <- as.factor(PatientenIE$group)
    
    ## Zu NE
    if (length(PatientenNE[PatientenNE$DaysFromDueToNextSubmit > 0, 1]) > 0) {
      hist(PatientenNE[PatientenNE$DaysFromDueToNextSubmit > 0, ]$DaysFromDueToNextSubmit, prob=TRUE, ylab="Relative Frequency", xlab="Indirect Waiting Period", main=paste0("Nicht-erschienene mit DueToNextSubmit > 0 (mean = ", round(mean(PatientenNE$DaysFromDueToNextSubmit), 4), ")"))
    } else { print_output("", "Keine nicht-erschienene Patienten mit DaysFromDueToNextSubmit > 0:") }
    
    print_output("",  paste0("DueToNextSubmit < 0: ", percent(count(PatientenNE[PatientenNE$DaysFromDueToNextSubmit < 0,  ]) / count(PatientenNE)), " of ", count(PatientenNE), " Appointments") );
    print_output("",  paste0("DueToNextSubmit = 0: ", percent(count(PatientenNE[PatientenNE$DaysFromDueToNextSubmit == 0, ]) / count(PatientenNE)), " of ", count(PatientenNE), " Appointments") );
    print_output("",  paste0("DueToNextSubmit > 0: ", percent(count(PatientenNE[PatientenNE$DaysFromDueToNextSubmit > 0,  ]) / count(PatientenNE)), " of ", count(PatientenNE), " Appointments") );
    
    ## Zu IE
    if (length(PatientenIE[PatientenIE$DaysFromDueToNextSubmit > 0, 1]) > 0) {
      hist(PatientenIE[PatientenIE$DaysFromDueToNextSubmit > 0, ]$DaysFromDueToNextSubmit, prob=TRUE, ylab="Relative Frequency", xlab="Indirect Waiting Period", main=paste0("Erschienene mit DueToNextSubmit > 0 (mean = ", round(mean(PatientenIE$DaysFromDueToNextSubmit), 4), ")"))
    } else { print_output("", "Keine erschienene Patienten mit DaysFromDueToNextSubmit > 0:") }
    
    print_output("",  paste0("DueToNextSubmit < 0: ", percent(count(PatientenIE[PatientenIE$DaysFromDueToNextSubmit < 0,  ]) / count(PatientenIE)), " of ", count(PatientenIE), " Appointments") );
    print_output("",  paste0("DueToNextSubmit = 0: ", percent(count(PatientenIE[PatientenIE$DaysFromDueToNextSubmit == 0, ]) / count(PatientenIE)), " of ", count(PatientenIE), " Appointments") );
    print_output("",  paste0("DueToNextSubmit > 0: ", percent(count(PatientenIE[PatientenIE$DaysFromDueToNextSubmit > 0,  ]) / count(PatientenIE)), " of ", count(PatientenIE), " Appointments") );
    
    y1 <- rbind(PatientenNE, PatientenIE)
    y1$DaysFromDueToNextSubmit <- as.numeric(y1$DaysFromDueToNextSubmit)
    y1$Attended <- as.factor(y1$Attended)
    # Wir betrachten nun alle Datensaetze und suchen nach einem Einfluss von Attended/Good Behaviour auf DaysFromDueToNextSubmit
    # Dafür untersuchen wir, ob sich die Mittelwerte statistisch signifikant unterscheiden. Zuerst teste auf Varainzhomogenitaet
    print_output("Levene-Test (Varianz-Homogenität)", leveneTest(DaysFromDueToNextSubmit ~ group, data = y1))
    # Da die Varianzen anscheinend stat. signifikant unterschiedlich sind, fuehren wir einen Welch-Test durch:
    # Teste auf Mittelwertgleichheit (Achtung: Gruppengroessen unterschiedlich (1653 vs 270))
    print_output("Welch Two Sample Test (Mittelwert-Gleichheit)", t.test(PatientenNE$DaysFromDueToNextSubmit, PatientenIE$DaysFromDueToNextSubmit, var.equal=FALSE))
    #
    print_output("", "");
  }
}

# Title-Seite zum späteren Füllen mit Beschreibung/Erklärung
print_explanation("Days From Due Date To Next Submit Date Vs Attended", "Text");

## Auszuführendes Skript übergeben, sodass es jeweils für alle Praxen ausgeführt wird 
# execFunctionForAllAgencies(fct, patientType, mfrowCount1, mfrowCount2, funtionType) :
# - fct: die oben definierte Funktion, die für jede Praxis einzeln und für alle gemeinsam ausgeführt werden soll
# - patientType: für welche Patienten soll es durchgeführt werden, -1: alle Patienten, 1: Stammpatienten, 0: Nicht-Stammpatienten
# - mfrowCount1: Anzahl der Spalten von Plots pro Seite
# - mfrowCount2: Anzahl der Zeilen von Plots pro Seite
# - funtionType: Beschreibung der Funktion
execFunctionForAllAgencies(daysFromDueToNextSubmitVsAttended, 1, 3, 4, "Nur Stammpatienten - Indirekte Wartezeit fuer nachfolgenden Termin")