package edu.kit.dol.timetabling.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static edu.kit.dol.timetabling.utilities.Configuration.exportFilePath;

class Commons {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    static void saveExport(String filename, StringBuilder content) {

        if (content != null) {
            String fullFilePath = exportFilePath + filename;
            try {
                File file = new File(fullFilePath);

                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullFilePath))) {
                    writer.write(content.toString());
                }
            } catch (IOException e) { throw new IllegalStateException(e); }
        }
    }
}
