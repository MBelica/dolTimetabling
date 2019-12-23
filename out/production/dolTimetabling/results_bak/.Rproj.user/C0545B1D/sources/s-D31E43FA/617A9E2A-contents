## Variables
# Wir haben die Datensaetze 'IML', 'WD' und 'B' und dieses sind im Ordner 'rawData' gespeichert
dataFolder     = 'rawData/'
praxen         = c('B', 'IML',  'WD')
eventIndicator = c('AgentTag', 'EventSubmitDay', 'EventDay', 'EventTimestamp', 'PatientID')

## Scripts
library(lattice)
library(corrplot)
library(dplyr)
library(ltm)
library(tidyverse)
library(lubridate)
library(polycor)
library(ggplot2)
library(car)

print_output <- function(header, output, cex = 0.75) {
  tmp <- capture.output(output) 
  plot.new()
  title(header, cex.main = 1.25 * cex, line = 0.5)
  text(0, 1, paste0(tmp, collapse='\n'), adj = c(0,1), family = 'mono', cex = cex)
  box()
}

print_reg <- function(header, dataset, reg_fct) {
  if (length(dataset[,1]) > 0) {
    if (NutzeTagesSpannen) {
        if (!is.null(tagesSpannen) & (length(tagesSpannen) > 0) & (max(lengths(tagesSpannen)) == 2) & (min(lengths(tagesSpannen)) == 2)) {
          par(mfrow = c(ceiling(sqrt(length(tagesSpannen))), ceiling(sqrt(length(tagesSpannen)))), oma = c(2,1,4,1) + 0.1,  mar = c(0,1,4,1) + 0.1)
          for (j in 1:length(tagesSpannen)) {
              timespanData = dataset[(dataset$IndirectWaitingPeriod %in% seq(max(min(dataset$IndirectWaitingPeriod), tagesSpannen[[j]][1]), min(max(dataset$IndirectWaitingPeriod), tagesSpannen[[j]][2]))), ]
              blm.fit <- reg_fct(timespanData)
              print_output(paste0("Tagesspanne: von ", tagesSpannen[[j]][1], " bis ", tagesSpannen[[j]][2]) , summary(blm.fit))
          }
        } else print_output(header, "Keine Tagesspannen verfuegbar oder mindestens eine der Tagesspannen hat das falsche Format")
        mtext(header, side = 3, line = -1.1, outer = TRUE, cex = 0.75)
    } else {
      blm.fit <- reg_fct(dataset)
      print_output(header, summary(blm.fit))
    }
  } else {
    if (NutzeTagesSpannen) par(mfrow = c(1, 1), oma = c(2,1,4,1) + 0.1,  mar = c(0,1,4,1) + 0.1)
    print_output(header, "Keine Daten verfuegbar")
  }
}

cleanRawData <- function(data) {
  data[,c('X.x', 'X.y', 'X')] <- NULL;
  sapply(data, class)
  data$Metrik1 <- as.numeric(data$Metrik1)
  data$Metrik2 <- as.numeric(data$Metrik2)
  data$EventTimestamp <- as.numeric(data$EventTimestamp)
  data$EventDay <- as.character(data$EventDay)
  data$PatientID <- as.character(data$PatientID)
  data$EventSubmitDay <- as.factor(data$EventSubmitDay)  
  
  return(data)
}

execFunctionForAllAgencies <- function(fct, patientType, mfrowCount1, mfrowCount2, funtionType) {
  allRawData <- NULL
  
  par(mfrow = c(mfrowCount1, mfrowCount2), oma = c(2,1,4,1) + 0.1,  mar = c(0,1,4,1) + 0.1)
  for (i in 1:1){#length(praxen)) {
    dataIndicator = praxen[i]
    
    RawData  <-  read.csv(paste0(dataFolder, 'eventMetrics', dataIndicator, '.csv'), sep = ";", quote = "\"'", dec = ".") %>%
      left_join(read.csv(paste0(dataFolder, 'dailyAppointmentMetrics', dataIndicator, '.csv'), sep = ";", quote = "\"'", dec = "."), by = eventIndicator) %>%
      left_join(read.csv(paste0(dataFolder, 'dailyPerAgentAppointmentMetrics', dataIndicator, '.csv'), sep = ";", quote = "\"'", dec = "."), by = eventIndicator)
    RawData  <<-  cleanRawData(RawData)                    
    
    if ((patientType == 1) || (patientType == -1)) {
      # Datensatz fuer Stammpatienten
      Stammpatienten <- RawData[RawData$Stammpatient %in% c("1"), ]
      fct(paste0(funtionType, " bezueglich Stammpatienten"), Stammpatienten)
      mtext(paste0("Praxis ", praxen[i], " - ", funtionType), side = 3, line = -0.1, outer = TRUE)
      

    }
    
    if  ((patientType == 0) || (patientType == -1)) {
      # Datensatz fuer Nicht-Stammpatienten
      NichtStammpatienten <- RawData[RawData$Stammpatient %in% c("0"), ]
      fct(paste0(funtionType, " bezueglich Nicht-Stammpatienten"), NichtStammpatienten)
      mtext(paste0("Praxis ", praxen[i], " - ", funtionType), side = 3, line = -0.1, outer = TRUE)
    }
    
    if (patientType == -1) {
      # Datensatz fuer alle Patienten
      fct(paste0(funtionType, " bezueglich allen Patienten"), RawData)
      mtext(paste0("Praxis ", praxen[i], " - ", funtionType), side = 3, line = -0.1, outer = TRUE)
    }
    
    allRawData <- rbind(allRawData, RawData) 
  }
  
  if ((patientType == 1) || (patientType == -1)) {
    # Datensatz fuer Stammpatienten
    Stammpatienten <- allRawData[allRawData$Stammpatient %in% c("1"), ]
    fct(paste0(funtionType, " bezueglich Stammpatienten"), Stammpatienten)
    mtext(paste0("Alle Praxen zusammengelegt - ", funtionType), side = 3, line = -0.1, outer = TRUE)
  }
  
  if  ((patientType == 0) || (patientType == -1)) {
    # Datensatz fuer Nicht-Stammpatienten
    NichtStammpatienten <- allRawData[allRawData$Stammpatient %in% c("0"), ]
    fct(paste0(funtionType, " bezueglich Nicht-Stammpatienten"), NichtStammpatienten)
    mtext(paste0("Alle Praxen zusammengelegt - ", funtionType), side = 3, line = -0.1, outer = TRUE)
  }
  
  if (patientType == -1) {
    # Datensatz fuer alle Patienten
    fct(paste0(funtionType, " bezueglich allen Patienten"), allRawData)
    mtext(paste0("Alle Praxen zusammengelegt - ", funtionType), side = 3, line = -0.1, outer = TRUE)
  }
  
  #return(allRawData)
}

print_explanation <- function (header, text) {
  par(mfrow = c(1, 1), oma = c(2,1,4,1) + 0.1,  mar = c(0,1,4,1) + 0.1)
  print_output(header, text);
}

percent <- function(x, digits = 2, format = "f") {
  paste0(format(100 * x, format = format, digits = (digits + 2)), "%")
}