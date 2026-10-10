package com.se.riddaradb.saga;

import com.se.riddaradb.bib.BibRepository;
import com.se.riddaradb.exceptions.ResourceNotFoundException;
import com.se.riddaradb.ms.MsRepository;
import com.se.riddaradb.sagaversion.*;
import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class SagaService {

    final SagaRepository sagaRepository;
    final SagaMapper sagaMapper;
    final SagaVersionRepository sagaVersionRepository;
    final SagaVersionService sagaVersionService;
    final SagaVersionMapper sagaVersionMapper;
    final SagaMsRepository sagaMsRepository;
    final BibRepository bibRepository;
    final MsRepository msRepository;

    public SagaService(SagaRepository sagaRepository,
                       SagaMapper sagaMapper,
                       SagaVersionRepository sagaVersionRepository,
                       SagaVersionService sagaVersionService,
                       SagaVersionMapper sagaVersionMapper,
                       SagaMsRepository sagaMsRepository,
                       BibRepository bibRepository,
                       MsRepository msRepository) {

        this.sagaRepository = sagaRepository;
        this.sagaMapper = sagaMapper;
        this.sagaVersionRepository = sagaVersionRepository;
        this.sagaVersionService = sagaVersionService;
        this.sagaVersionMapper = sagaVersionMapper;
        this.sagaMsRepository = sagaMsRepository;
        this.bibRepository = bibRepository;
        this.msRepository = msRepository;
    }

    public Set<SagaResponseDto> getSagas(){
        return sagaRepository.findAll()
                .stream()
                .map(sagaMapper::mapToResponseDto)
                .collect(Collectors.toSet());
    }

    public SagaResponseDto getSagaById(int id){
            return sagaMapper.mapToResponseDto(sagaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Saga with ID " + id + " not found in DB")));
    }

    public Set<SagaTitleDto> getSagaTitles(){
        return sagaRepository.findAll()
                .stream()
                .map(sagaMapper::mapToSagaTitle)
                .collect(Collectors.toSet());
    }

    public SagaResponseDto updateSaga(SagaRequestDto sagaRequestDto){

        SagaEntity sagaEntity = sagaRepository.findById(sagaRequestDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Saga not found in database"));

        sagaEntity.setTitle(sagaRequestDto.getTitle());
        sagaEntity.setTranslatedTitle(sagaRequestDto.getTranslatedTitle());
        sagaEntity.setDescription(sagaRequestDto.getDescription());
        sagaEntity.setTranslated(sagaRequestDto.getTranslated());

        updateSagaVersions(sagaEntity, sagaRequestDto);

        sagaEntity.setBibEntities(new HashSet<>(bibRepository.findAllById(sagaRequestDto.getBibIds())));

        updateMs(sagaEntity, sagaRequestDto);

        return sagaMapper.mapToResponseDto(sagaRepository.save(sagaEntity));
    }

    public SagaResponseDto saveSaga(SagaRequestDto sagaRequestDto){

        SagaEntity sagaEntity = new SagaEntity(null, sagaRequestDto.getTitle(), sagaRequestDto.getTranslatedTitle(), sagaRequestDto.getDescription(), sagaRequestDto.getTranslated());

        // This is actually optimal for objects which are not created in the saga menu,
        // as you're just linking the saga to objects which already exist via ID. The only
        // exceptions are composite tables (for e.g. SagaMS) and objects which ARE created in
        // the saga menu, like saga versions.

        //Bib entries
        sagaEntity.setBibEntities(new HashSet<>(bibRepository.findAllById(sagaRequestDto.getBibIds())));

        //Saga versions
        for (SagaVersionRequestDto sagaVersionRequestDto : sagaRequestDto.getSagaVersions()){
            sagaEntity.addSagaVersion(new SagaVersionEntity(null, sagaVersionRequestDto.getTitle(), sagaVersionRequestDto.getDescription(), sagaVersionRequestDto.getDate()));
            System.out.println("Saga ID: " + sagaEntity.getSagaVersionEntities().stream().findFirst().get().getSagaEntity().getId());
        }

        //Manuscript entries
        for (SagaMsDto sagaMsDto : sagaRequestDto.getSagaMsDtos()){
            msRepository.findById(sagaMsDto.getMsId()).ifPresent(ms -> sagaEntity.addMs(ms, sagaMsDto.getFolioNumber(), sagaMsDto.getNote()));
        }

        return sagaMapper.mapToResponseDto(this.sagaRepository.save(sagaEntity));
    }

    void updateSagaVersions(SagaEntity sagaEntity, SagaRequestDto sagaRequestDto){
        Set<Integer> currentSagaVersions = sagaEntity.getSagaVersionEntities()
                .stream()
                .map(SagaVersionEntity::getId)
                .collect(Collectors.toSet());

        Set<Integer> newSagaVersions = sagaRequestDto.getSagaVersions()
                .stream()
                .map(SagaVersionRequestDto::getId)
                .collect(Collectors.toSet());

        for (SagaVersionRequestDto sagaVersionDto : sagaRequestDto.getSagaVersions()){
            //Incoming saga version is new and lacks ID; add to saga version
            if (sagaVersionDto.getId() == null){
                sagaEntity.addSagaVersion(new SagaVersionEntity(null, sagaVersionDto.getTitle(), sagaVersionDto.getDescription(), sagaVersionDto.getDate()));
            }
            else{
                //If saga version already present, update it
                SagaVersionEntity sagaVersionEntity =  sagaEntity.getSagaVersionEntities()
                        .stream()
                        .filter(entity -> Objects.equals(entity.getId(), sagaVersionDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Saga version not found"));

                sagaVersionEntity.setTitle(sagaVersionDto.getTitle());
                sagaVersionEntity.setDescription(sagaVersionDto.getDescription());
                sagaVersionEntity.setDate(sagaVersionDto.getDate());
            }
        }

        for (int currentSagaVersionId : currentSagaVersions) {
            if (!newSagaVersions.contains(currentSagaVersionId)) {
                sagaEntity.removeSagaVersion(currentSagaVersionId);
            }
        }
    }

    void updateMs(SagaEntity sagaEntity, SagaRequestDto sagaRequestDto){

        //Map of saga-MS join entities already associated with this saga. Indexed by MS ID.
        Map<Integer, SagaMsEntity> currentSagaMss = sagaEntity.getSagaMsEntities()
                .stream()
                .collect(Collectors.toMap(sagaMsEntity -> sagaMsEntity.getMsEntity().getId(), Function.identity()));

        //IDs of manuscripts to be joined to this motif
        Set<Integer> newSagaMsIds = sagaRequestDto.getSagaMsDtos()
                .stream()
                .map(SagaMsDto::getMsId)
                .collect(Collectors.toSet());

        //For each saga-MS entity attached to saga...
        for (SagaMsDto sagaMsDto : sagaRequestDto.getSagaMsDtos()){
            SagaMsEntity sagaMsCurrent = currentSagaMss.get(sagaMsDto.getMsId());

            //Saga-MS exists already. Update.
            if (sagaMsCurrent != null){
                sagaMsCurrent.setFolioNumber(sagaMsDto.getFolioNumber());
                sagaMsCurrent.setNote(sagaMsDto.getNote());
            }

            //Saga-MS entity does not exist in DB. Fetch MS and attach to saga.
            else{
                msRepository.findById(sagaMsDto.getMsId()).ifPresent(ms -> {
                    sagaEntity.addMs(ms, sagaMsDto.getFolioNumber(), sagaMsDto.getNote());
                });
            }
        }

        //If set of IDs does not include manuscripts previously associated with
        // this saga, remove them.
        sagaMsRepository.findBySagaEntityId(sagaRequestDto.getId()).forEach(sagaMs -> {
            if (!newSagaMsIds.contains(sagaMs.getMsEntity().getId())){
                sagaEntity.removeMs(msRepository.findById(sagaMs.getMsEntity().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("MS entity not found in database")));
            }
        });
    }

    public void deleteSagaById(int id) {

        if (sagaRepository.findById(id).isPresent()){
            SagaEntity sagaEntity = sagaRepository.findById(id).get();
            for (SagaVersionEntity sagaVersionEntity : sagaEntity.getSagaVersionEntities()){
                sagaVersionService.deleteSagaVersionById(sagaVersionEntity.getId());
            }

            removeSagaFromBibEntries(id);

            sagaRepository.deleteById(id);
        }
        else {
            System.out.println("Record not found in database.");
        }
    }

    public void deleteAll(){
        sagaRepository.deleteAll();
    }

    private void removeSagaFromBibEntries(int id){
        for (SagaEntity saga : sagaRepository.findAll()) {
            saga.getBibEntities().removeIf(bib -> bib.getId() == id);
        }
    }
}
