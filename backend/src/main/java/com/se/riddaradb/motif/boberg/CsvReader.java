package com.se.riddaradb.motif.boberg;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public List<BobergMotif> readCsv(Path csvPath) throws IOException {
        List<BobergMotif> rows = new ArrayList<>();

        try (
                Reader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .get()
                        .parse(reader);
        )
        {
            for (CSVRecord record : parser){
                BobergMotif row = new BobergMotif(
                        record.get("motifCode"),
                        record.get("motifName"),
                        record.get("description"),
                        record.get("parentCode")
                );

                rows.add(row);
            }
        }

        return rows;
    }
}
