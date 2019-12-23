package edu.kit.dol.timetabling.utilities;

import static edu.kit.dol.timetabling.utilities.Configuration.UUIDTruncationLength;

@SuppressWarnings("unused")
public class Miscellaneous {

    public static String truncateUUID (String value) {
        if (value != null && value.length() > UUIDTruncationLength)
            value = value.substring(0, UUIDTruncationLength);
        return value;
    }

    public static int crossSum(int num) {

        int sum = 0;
        while (num > 0) {
            sum += num % 10;
            num = num / 10;
        }

        return sum;
    }
}
