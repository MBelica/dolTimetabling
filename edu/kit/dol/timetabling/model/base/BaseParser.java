package edu.kit.dol.timetabling.model.base;

import edu.kit.dol.timetabling.structures.EventType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import java.text.SimpleDateFormat;
import java.util.*;

public class BaseParser {

    public static final String inputTimeStringPattern = "HH:mm";
    public static final String inputDateStringPattern = "MM/dd/yy";
    public static final String inputDateTimeStringPattern = inputDateStringPattern + " " + inputTimeStringPattern;

    public static final DataFormatter dataFormatter = new DataFormatter();
    public static final List<List> eventTypeDescriptionClassesList = new ArrayList<>();


    public static List<Set<EventType>> createEventWorkingHoursArray(String[][][] data) {
        List<Set<EventType>> result = new ArrayList<>();

        if (data != null) {
            for (String[][] aData : data) {
                if (aData != null) {
                    Set<EventType> eventTypes = new HashSet<>();
                    for (String[] anAData : aData) {
                        if ((anAData != null) && (anAData.length == 4)) {
                            eventTypes.add(new EventType(Integer.valueOf(anAData[0]), Integer.valueOf(anAData[1]), new String[]{anAData[2], anAData[3]}));
                        }
                    }
                    result.add(eventTypes);
                }
            }
        }

        return result;
    }

    public static List<String[][]> createWorkingHoursArray(String[][][] data) {

        List<String[][]> result = new ArrayList<>();

        if (data != null) {
            Collections.addAll(result, data);
        }

        return result;
    }

    public static String retrieveXLSCell(Row row, int cellNumber, String inputPattern, String outputPattern) {

        String cellValue = "";
        if ( (inputPattern == null) && (outputPattern == null) ) {
            cellValue = dataFormatter.formatCellValue(row.getCell(cellNumber));
        }
        else {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(Objects.requireNonNull(inputPattern));
                SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                if (row.getCell(cellNumber) != null) {
                    cellValue = dataFormatter.formatCellValue(row.getCell(cellNumber));
                    if ((cellValue != null) && (cellValue != "") ) {
                        cellValue = outputFormat.format(inputFormat.parse(cellValue));
                    } else cellValue = null;
                } else cellValue = null;
            } catch (Exception e) {
                System.out.println("At cell " + cellNumber + ": " + e  + " (" + row.getCell(cellNumber) + ", " + cellValue + ")");
            }
        }

        return cellValue;
    }

    public static int setEventTypeCoding () {

        eventTypeDescriptionClassesList.add(Arrays.asList(new String[][]{{"Normaler Termin"}}));
        return 1;
    }
}

