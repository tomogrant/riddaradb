package com.se.riddaradb.motif;

import com.se.riddaradb.sagaversion.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class MotifService {

    final MotifRepository motifRepository;
    final SagaVersionRepository sagaVersionRepository;
    final SagaVersionMotifRepository sagaVersionMotifRepository;
    final MotifMapper motifMapper;

    public MotifService(MotifRepository motifRepository, SagaVersionRepository sagaVersionRepository, SagaVersionMotifRepository sagaVersionMotifRepository, MotifMapper motifMapper) {
        this.motifRepository = motifRepository;
        this.sagaVersionRepository = sagaVersionRepository;
        this.sagaVersionMotifRepository = sagaVersionMotifRepository;
        this.motifMapper = motifMapper;
    }

    public Collection<MotifDto> getMotifEntries(){
        return motifRepository.findAll()
                .stream()
                .map(motifMapper::mapToDto)
                .toList();
    }

    public MotifDto getMotifEntryById(int id){
        if (motifRepository.findById(id).isPresent()){
            return motifMapper.mapToDto(motifRepository.findById(id).get());
        }
        else {
            return null;
        }
    }

    public Collection<MotifDto> getRootMotifs(){
        return motifRepository.findByParentIsNull()
                .stream()
                .map(motifMapper::mapToDto)
                .collect(Collectors.toSet());
    }

    public Collection<MotifDto> getChildMotifs(int parentId){
        Set<MotifDto> childMotifs = new HashSet<>();

        if (motifRepository.findById(parentId).isPresent()){
            childMotifs = motifRepository.findById(parentId).get().getChildren()
                    .stream()
                    .map(motifMapper::mapToDto)
                    .collect(Collectors.toSet());
        }

        return childMotifs;
    }

    public Collection<MotifDto> getMotifsByIds(Set<Integer> ids){
        return motifRepository.findAllById(ids)
                .stream()
                .map(motifMapper::mapToDto)
                .collect(Collectors.toSet());
    }

    public MotifDto updateMotifEntry(MotifDto motifDto){
        MotifEntity motifEntity = motifRepository.findById(motifDto.getId()).orElseThrow();

        motifEntity.setMotifCode(motifDto.getMotifCode());
        motifEntity.setMotifName(motifDto.getMotifName());
        motifEntity.setDescription(motifDto.getDescription());

        updateSagaMotif(motifDto, motifEntity);

        //If this motif is a child motif, add it to its parent.
        if (motifDto.getParentId() != null){
            motifRepository.findById(motifDto.getParentId()).ifPresent(parentMotif ->
                    parentMotif.addChildMotif(motifEntity));
        }


        return motifMapper.mapToDto(motifRepository.save(motifEntity));
    }

    public MotifDto saveMotifEntry(MotifDto motifDto){

        MotifEntity motifEntity = new MotifEntity(null, motifDto.getMotifCode(), motifDto.getMotifName(), motifDto.getDescription());

        for (MotifSagaVersionDto motifSagaDto : motifDto.getSagaMotifs()){
            System.out.println("There is a MotifSagaDto associated with this DTO, but is there a saga version in the DB?");
                sagaVersionRepository.findById(motifSagaDto.getSagaVersionId()).ifPresent(sagaVersion -> {
                    System.out.println("Saga version found");
                    sagaVersion.addMotif(motifEntity, motifSagaDto.getPageChapterNumber(), motifSagaDto.getInBoberg());
                });
        }

        if (motifDto.getParentId() != null){
            motifRepository.findById(motifDto.getParentId()).ifPresent(parentMotif ->
                    parentMotif.addChildMotif(motifEntity));
        }

        return motifMapper.mapToDto(motifRepository.save(motifEntity));
    }

    void updateSagaMotif(MotifDto motifDto, MotifEntity motifEntity){
        //Map of saga-motif join entities already associated with this motif
        Map<Integer, SagaVersionMotifEntity> currentSagaMotifs = motifEntity.getSagaVersionMotifEntities()
                .stream()
                .collect(Collectors.toMap(sagaVersionMotifEntity -> sagaVersionMotifEntity.getSagaVersionEntity().getId(), Function.identity()));

        //IDs of sagas to be joined to this motif
        Set<Integer> newSagaMotifIds = motifDto.getSagaMotifs()
                .stream()
                .map(MotifSagaVersionDto::getSagaVersionId)
                .collect(Collectors.toSet());

        for (MotifSagaVersionDto motifSagaDto : motifDto.getSagaMotifs()){
            SagaVersionMotifEntity sagaMotifCurrent = currentSagaMotifs.get(motifSagaDto.getSagaVersionId());

            //Saga-motif exists already. Update.
            if (sagaMotifCurrent != null){
                sagaMotifCurrent.setPageChapterNumber(motifSagaDto.getPageChapterNumber());
                sagaMotifCurrent.setInBoberg(motifSagaDto.getInBoberg());
            }

            //Saga-motif does not exist. Create.
            else{
                sagaVersionRepository.findById(motifSagaDto.getSagaVersionId()).ifPresent(sagaVersion -> {
                    sagaVersion.addMotif(motifEntity, motifSagaDto.getPageChapterNumber(), motifSagaDto.getInBoberg());
                });
            }
        }

        //If set of IDs does not include sagas previously associated with this motif, remove them
        sagaVersionMotifRepository.findByMotifEntityId(motifDto.getId()).forEach(sagaMotif -> {
            if (!newSagaMotifIds.contains(sagaMotif.getSagaVersionEntity().getId())){
                sagaVersionRepository.findById(sagaMotif.getSagaVersionEntity().getId()).ifPresent(sagaVersion -> sagaVersion.removeMotif(motifEntity));
            }
        });
    }

    public void deleteMotifEntryById(int id){

        MotifEntity motifEntity = motifRepository.findById(id).orElseThrow();

        if (motifEntity.getParent() != null){
            MotifEntity motifParent = motifRepository.findById(motifEntity.getParent().getId()).orElseThrow();
            motifParent.getChildren().removeIf(child -> child.getId() == id);
        }

        removeChildren(id);

        motifRepository.deleteById(id);
    }

    public void deleteAll(){
        motifRepository.deleteAll();
    }

    private void removeChildren(int id){
        motifRepository.findById(id).ifPresent(motif -> {
            motif.getChildren().forEach((child -> removeChildren(child.getId())));
        });

        motifRepository.deleteById(id);
    }

    Set<MotifSearchResult> search(String searchTerm){
        Set<MotifSearchResult> searchResults = new HashSet<>();

        Set<Integer> motifIds = new HashSet<>();
        motifRepository.findByMotifCodeContainsIgnoreCase(searchTerm).forEach(motif
                -> motifIds.add(motif.getId()));

        motifRepository.findByMotifNameContainsIgnoreCase(searchTerm).forEach(motif
                -> motifIds.add(motif.getId()));

        motifRepository.findByDescriptionContainsIgnoreCase(searchTerm).forEach(motif
                -> motifIds.add(motif.getId()));

        sagaVersionRepository.findByTitleContainsIgnoreCase(searchTerm).forEach(sagaVersion ->{
            sagaVersion.getSagaVersionMotifEntities().forEach(sagaMotif -> {
                motifIds.add(sagaMotif.getMotifEntity().getId());
            });
        });

        for (int motifId : motifIds){
            searchResults.add(new MotifSearchResult(motifId, buildPath(motifId)));
        }

        return searchResults;
    }

    Set<MotifSearchResult> searchExact(String searchTerm){
        Set<MotifSearchResult> searchResults = new HashSet<>();

        Set<Integer> motifIds = new HashSet<>();
        motifRepository.findByMotifCodeIgnoreCase(searchTerm).forEach(motif
                -> motifIds.add(motif.getId()));

        for (int motifId : motifIds){
            searchResults.add(new MotifSearchResult(motifId, buildPath(motifId)));
        }

        return searchResults;
    }

    Set<Integer> buildPath(int motifId){
        int localMotifId = motifId;
        Set<Integer> path = new HashSet<>();
        boolean hasParent = true;

        while (hasParent){
            MotifEntity motif = motifRepository.findById(localMotifId).orElseThrow();

            if (motif.getParent() == null){
                hasParent = false;
            }
            else{
                int parentId = motif.getParent().getId();
                path.add(parentId);
                localMotifId = parentId;
            }
        }

        return path;
    }
}
