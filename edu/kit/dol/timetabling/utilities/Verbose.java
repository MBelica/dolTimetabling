package edu.kit.dol.timetabling.utilities;

import java.util.Arrays;
import java.util.Collections;

import static edu.kit.dol.timetabling.utilities.Configuration.silentMod;
import static edu.kit.dol.timetabling.utilities.Dates.longMsToTime;

@SuppressWarnings("unused")
public class Verbose {
    public static void print(String message) {
        if ( (!silentMod) && (message != null) ) {
            System.out.println(message);
        }
    }

    public static void printProgress(long startTime, long total, long current) {
        long now = System.currentTimeMillis();
        long pta = current == 0 ? 0 : (now - startTime);
        long eta = current == 0 ? 0 : (total - current) * (now - startTime) / current;

        String etaHms   = current == 0 ? "0" : longMsToTime(eta);
        String pastTime = current == 0 ? "0" : longMsToTime(pta);

        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string
                .append('\r')
                .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% [", percent))
                .append(String.join("", Collections.nCopies(percent, "=")))
                .append('>')
                .append(String.join("", Collections.nCopies(100 - percent, " ")))
                .append(']')
                .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                .append(String.format(" %d/%d, ETA: %s, Time elapsed: %s", current, total, etaHms, pastTime));

        System.out.print(string);
    }

    public static void printMetrics( double[] result ) {
        print(" ");
        if ( result != null ) {
            print( " - Metrics: " + Arrays.toString(result) );
        }
    }
}
