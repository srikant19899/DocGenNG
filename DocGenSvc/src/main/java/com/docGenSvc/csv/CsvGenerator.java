package com.docGenSvc.csv;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class CsvGenerator {

    public void generateCsvFiles(String outputDirectory, Map<String, List<List<String>>> sheetDataMap) throws IOException {
        for (Map.Entry<String, List<List<String>>> entry : sheetDataMap.entrySet()) {
            String sheetName = entry.getKey();
            List<List<String>> data = entry.getValue();
            String outputFileName = outputDirectory + "/" + sheetName + ".csv";
            File outputFile = new File(outputFileName);

            // Ensure the parent directory exists
            File parentDir = outputFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (CSVWriter writer = new CSVWriter(new FileWriter(outputFile))) {
                for (List<String> row : data) {
                    writer.writeNext(row.toArray(new String[0]));
                }
            }
        }
    }
}
