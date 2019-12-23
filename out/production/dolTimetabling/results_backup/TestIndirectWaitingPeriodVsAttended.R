## Setup
###########
# Falls modData auf TRUE gesetzt wird, wird eine Funktion modDataFunction erwartet
# die die input daten als Argument erhält und diese modifiziert wieder zurückgibt
modData = FALSE;
# Falls forEveryDoc auf TRUE gesetzt wird, so wird für jede Praxis mit mehr als einem Arzt
# zusätzlich zu der Untersuchung auf der Gesamtpraxis auch die Untersuchung für jeden Arzt durchgeführt
forEveryDoc = TRUE;

source("functions.R")

## Script
indirectWaitingPeriodVsAttendedHist <- function(title, data) {
  if ((!is.null(data)) && (length(data[,1]) > 0)) {
    Patienten <- data
    
    Patienten$IndirectWaitingPeriod <- ceiling( (Patienten$msToNextDDateFromPrevDDate - Patienten$msToNextSDateFromPrevDDate) / (1000*60*60*24) );
    # msToNextxDate enthaelt den nächsten Termin, dessen Submit-Date (Termineintrag) älter ist, als das aktuelle Submit Datum
    # Die Differenz zum Due-Date (Arzttermin) kann somit aber sowohl positiv als auch negativ sein
    
    # Wir gehen davon aus, dass diejenigen, die einfach nicht erschienen sind, eine "Strafe" erhalten.
    # Wir wollen also, dass sie nicht erschienen sind (attended = 0), aber auch, dass sie nicht vorab angerufen haben und ggf.
    # einen neuen Termin ausgemacht haben, das heißt sDateNeu > dDateAlt
    if (length(Patienten[(Patienten$msToNextSDateFromPrevDDate > 0) & (Patienten$Attended %in% c("0")), 1]) > 0) {
      PatientenNE <- Patienten[(Patienten$msToNextSDateFromPrevDDate > 0) & (Patienten$Attended %in% c("0")), c("IndirectWaitingPeriod", "Attended") ]
      PatientenNE$group <- "NE";  PatientenNE$group <- as.factor(PatientenNE$group)
      
      # Wir möchten das nun testen gegen alle anderen "braven" Patienten. Allerdings ist das Problem, dass falls der Termin besucht wurde,
      # die Dringlichkeit für einen Termin verschwindet. Wir wollen also nur Termine/Patienten betrachten, die "unabhängig" vom aktuellen Termin
      # einen neuen ausmachen. Dafür bieten sich meiner Meinung nach zwei Ansätze:
      #  - entweder fordern wir, dass die Patienten, obwohl sie den aktuellen Termin verfallen liesen (d.h. nicht erschienen sind), noch vor dem aktuellen Due-Date einen neuen Termin ausmachen;
      PatientenRS1 <- Patienten[(Patienten$msToNextSDateFromPrevDDate < 0) & (Patienten$Attended %in% c("0")), c("IndirectWaitingPeriod", "Attended") ]
      PatientenRS1$group <- "RS1"; PatientenRS1$group <- as.factor(PatientenRS1$group)
      # - oder wir testen gegen alle Patieten die einfach zu Ihrem Termin erschienen sind:
      PatientenRS2 <- Patienten[Patienten$Attended %in% c("1"), c("IndirectWaitingPeriod", "Attended") ]
      PatientenRS2$group <- "RS2"; PatientenRS2$group <- as.factor(PatientenRS2$group)

      if (length(PatientenNE[,1]) > 0) {
        hist(PatientenNE$IndirectWaitingPeriod, prob=TRUE, ylab="Relative Frequency", xlab="Indirect Waiting Period", main=paste0("Nicht-Erschienene Patienten (mean = ", round(mean(PatientenNE$IndirectWaitingPeriod), 4), ")"))
      } else { print_output("", "Keine nicht-erschienene Patienten mit DaysFromDueToNextSubmit > 0 & Attended = 0:") }
      
      ## Zu V1
      if (length(PatientenRS1[,1]) > 0) {
        hist(PatientenRS1$IndirectWaitingPeriod, prob=TRUE, ylab="Relative Frequency", xlab="Indirect Waiting Period", main=paste0("Erschienene Patienten v1 (mean = ", round(mean(PatientenRS1$IndirectWaitingPeriod), 4), ")"))
      } else { print_output("", "Keine nicht-erschienene Patienten mit ToNextSDateFromPrevDDate < 0 & Attended = 0:") }
      
      ## Zu V2
      if (length(PatientenRS2[,1]) > 0) {
        hist(PatientenRS2$IndirectWaitingPeriod, prob=TRUE, ylab="Relative Frequency", xlab="Indirect Waiting Period", main=paste0("Erschienene Patienten v2 (mean = ", round(mean(PatientenRS2$IndirectWaitingPeriod), 4), ")"))
      } else { print_output("", "Keine nicht-erschienene Patienten mit Attended = 1:") }
      
    } else {
      for (i in 1:3) {
        if (i == 1) {
          print_output("", "Keine Stammpatienten mit ToNextSDateFromPrevDDate > 0 & Patienten$Attended = 0")
        } else { print_output("", "") }
      }
    }
  }
}

indirectWaitingPeriodVsAttendedTest <- function(title, data) {
  if ((!is.null(data)) && (length(data[,1]) > 0)) {
    Patienten <- data
    
    Patienten$IndirectWaitingPeriod <- ceiling( (Patienten$msToNextDDateFromPrevDDate - Patienten$msToNextSDateFromPrevDDate) / (1000*60*60*24) );
    # msToNextxDate enthaelt den nächsten Termin, dessen Submit-Date (Termineintrag) älter ist, als das aktuelle Submit Datum
    # Die Differenz zum Due-Date (Arzttermin) kann somit aber sowohl positiv als auch negativ sein
    
    # Wir gehen davon aus, dass diejenigen, die einfach nicht erschienen sind, eine "Strafe" erhalten.
    # Wir wollen also, dass sie nicht erschienen sind (attended = 0), aber auch, dass sie nicht vorab angerufen haben und ggf.
    # einen neuen Termin ausgemacht haben, das heißt sDateNeu > dDateAlt
    if (length(Patienten[(Patienten$msToNextSDateFromPrevDDate > 0) & (Patienten$Attended %in% c("0")), 1]) > 0) {
      PatientenNE <- Patienten[(Patienten$msToNextSDateFromPrevDDate > 0) & (Patienten$Attended %in% c("0")), c("IndirectWaitingPeriod", "Attended") ]
      PatientenNE$group <- "NE";  PatientenNE$group <- as.factor(PatientenNE$group)
      
      # Wir möchten das nun testen gegen alle anderen "braven" Patienten. Allerdings ist das Problem, dass falls der Termin besucht wurde,
      # die Dringlichkeit für einen Termin verschwindet. Wir wollen also nur Termine/Patienten betrachten, die "unabhängig" vom aktuellen Termin
      # einen neuen ausmachen. Dafür bieten sich meiner Meinung nach zwei Ansätze:
      #  - entweder fordern wir, dass die Patienten, obwohl sie den aktuellen Termin verfallen liesen (d.h. nicht erschienen sind), noch vor dem aktuellen Due-Date einen neuen Termin ausmachen;
      PatientenRS1 <- Patienten[(Patienten$msToNextSDateFromPrevDDate < 0) & (Patienten$Attended %in% c("0")), c("IndirectWaitingPeriod", "Attended") ]
      PatientenRS1$group <- "RS1"; PatientenRS1$group <- as.factor(PatientenRS1$group)
      # - oder wir testen gegen alle Patieten die einfach zu Ihrem Termin erschienen sind:
      PatientenRS2 <- Patienten[Patienten$Attended %in% c("1"), c("IndirectWaitingPeriod", "Attended") ]
      PatientenRS2$group <- "RS2"; PatientenRS2$group <- as.factor(PatientenRS2$group)
      
      y1 <- rbind(PatientenNE, PatientenRS1)
      y1$IndirectWaitingPeriod <- as.numeric(y1$IndirectWaitingPeriod)
      y1$Attended <- as.factor(y1$Attended)
      # Wir betrachten nun alle Datensaetze und suchen nach einem Einfluss von Attended/Good Behaviour auf IndirectWaitingPeriod
      # Dafür untersuchen wir, ob sich die Mittelwerte statistisch signifikant unterscheiden. Zuerst teste auf Varainzhomogenitaet
      print_output("Levene-Test (Varianz-Homogenitaet) zu v1", leveneTest(IndirectWaitingPeriod ~ group, data = y1))
      # Da die Varianzen anscheinend stat. signifikant unterschiedlich sind, fuehren wir einen Welch-Test durch:
      # Teste auf Mittelwertgleichheit (Achtung: Gruppengroessen unterschiedlich (1653 vs 270))
      print_output("Welch Two Sample Test (Mittelwert-Gleichheit) zu v1", t.test(PatientenNE$IndirectWaitingPeriod, PatientenRS1$IndirectWaitingPeriod, var.equal=FALSE))
      #
      
      #### mtext("Histogramm fuer Indirekte Wartezeit bis zum naechsten Termin - Alle Praxen", side = 3, line = 1, outer = TRUE, cex = 1.5)
      y2 <- rbind(PatientenNE, PatientenRS2)
      y2$IndirectWaitingPeriod <- as.numeric(y2$IndirectWaitingPeriod)
      y2$Attended <- as.factor(y2$Attended)
      # Wir betrachten nun alle Datensaetze und suchen nach einem Einfluss von Attended/Good Behaviour auf IndirectWaitingPeriod
      # Dafür untersuchen wir, ob sich die Mittelwerte statistisch signifikant unterscheiden. Zuerst teste auf Varainzhomogenitaet
      print_output("Levene-Test (Varianz-Homogenitaet) zu v2", leveneTest(IndirectWaitingPeriod ~ group, data = y2))
      # Da die Varianzen anscheinend stat. signifikant unterschiedlich sind, fuehren wir einen Welch-Test durch:
      # Teste auf Mittelwertgleichheit (Achtung: Gruppengroessen unterschiedlich (75 vs 270))
      print_output("Welch Two Sample Test (Mittelwert-Gleichheit) zu v2", t.test(PatientenNE$IndirectWaitingPeriod, PatientenRS2$IndirectWaitingPeriod, var.equal=FALSE))
      #
      #### mtext("Histogramm fuer Indirekte Wartezeit bis zum naechsten Termin - Alle Praxen", side = 3, line = 1, outer = TRUE, cex = 1.5)
    } else {
      for (i in 1:4) {
        if (i == 1) {
          print_output("", "Keine Stammpatienten mit ToNextSDateFromPrevDDate > 0 & Patienten$Attended = 0")
        } else { print_output("", "") }
      }
    }
  }
}

# Title-Seite zum späteren Füllen mit Beschreibung/Erklärung
print_explanation("Indirect Waiting Period Vs Attended", "Text");

## Auszuführendes Skript übergeben, sodass es jeweils für alle Praxen ausgeführt wird 
# execFunctionForAllAgencies(fct, patientType, mfrowCount1, mfrowCount2, funtionType) :
# - fct: die oben definierte Funktion, die für jede Praxis einzeln und für alle gemeinsam ausgeführt werden soll
# - patientType: für welche Patienten soll es durchgeführt werden, -1: alle Patienten, 1: Stammpatienten, 0: Nicht-Stammpatienten
# - mfrowCount1: Anzahl der Spalten von Plots pro Seite
# - mfrowCount2: Anzahl der Zeilen von Plots pro Seite
# - funtionType: Beschreibung der Funktion
execFunctionForAllAgencies(indirectWaitingPeriodVsAttendedHist, 1, 3, 1, "Nur Stammpatienten - Indirekte Wartezeit fuer nachfolgenden Termin")
execFunctionForAllAgencies(indirectWaitingPeriodVsAttendedTest, 1, 2, 2, "Nur Stammpatienten - Indirekte Wartezeit fuer nachfolgenden Termin")