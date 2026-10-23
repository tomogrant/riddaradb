package com.se.riddaradb.motif.boberg;

import com.se.riddaradb.motif.MotifEntity;
import com.se.riddaradb.motif.MotifRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Service
@Transactional
public class MotifImporter implements ApplicationRunner {

    @Value("${app.motifs.import-motifs-on-startup:false}")
    private boolean importMotifsOnStartup;

    final MotifRepository motifRepository;

    public MotifImporter(MotifRepository motifRepository){
        this.motifRepository = motifRepository;
    }

    public void loadMotifs() throws IOException {

        if (motifRepository.count() > 0){
            return;
        }

        CsvReader csvReader = new CsvReader();
        Set<MotifEntity> motifs = new HashSet<>();
        Map<String, MotifEntity> motifMap = new HashMap<>();

        List<BobergMotif> rows = csvReader.readCsv(Path.of("backend/data", "boberg_motifs_filtered.csv"));

        for (BobergMotif row : rows){
            MotifEntity motif = new MotifEntity(null, row.getMotifCode(), row.getMotifName(), row.getDescription());
            motifs.add(motif);
            motifMap.put(motif.getMotifCode(), motif);
        }

        motifRepository.saveAll(motifs);

        for (BobergMotif row : rows){
            if (!row.getParentCode().isBlank()){
                MotifEntity motif = motifMap.get(row.getMotifCode());
                MotifEntity parent = motifMap.get(row.getParentCode());

                motif.setParent(parent);
            }
        }
    }

    @Override
    public void run(@NonNull ApplicationArguments args) throws Exception {
        if (importMotifsOnStartup){
            loadMotifs();
        }
    }
}