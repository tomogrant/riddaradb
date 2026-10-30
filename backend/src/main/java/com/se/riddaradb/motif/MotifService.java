package com.se.riddaradb.motif;

import com.se.riddaradb.exceptions.ResourceNotFoundException;
import com.se.riddaradb.exceptions.TooManyResultsException;
import com.se.riddaradb.sagaversion.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
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

    @Value("${app.motifs.max-search-results:200}")
    private Integer maxSearchResults;

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
        MotifEntity motifEntity = motifRepository.findById(motifDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Motif not found in database"));

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

        for (MotifSagaVersionDto motifSagaDto : motifDto.getMotifSagas()){
                sagaVersionRepository.findById(motifSagaDto.getSagaVersionId()).ifPresent(sagaVersion -> {
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
        Set<Integer> newSagaMotifIds = motifDto.getMotifSagas()
                .stream()
                .map(MotifSagaVersionDto::getSagaVersionId)
                .collect(Collectors.toSet());

        for (MotifSagaVersionDto motifSagaDto : motifDto.getMotifSagas()){
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
                sagaMotif.getSagaVersionEntity().removeMotif(motifEntity);
            }
        });
    }

    public void deleteMotifEntryById(int id) throws AccessDeniedException {

        boolean isAdmin = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getAuthorities()
                .stream()
                .anyMatch(auth ->
                        Objects.equals(auth.getAuthority(), "ROLE_ADMINISTRATOR"));

        MotifEntity motifEntity = motifRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motif entity not found in database"));

        //Admins can delete all motifs. Otherwise, the motif can only be deleted if it lacks children.
        if (isAdmin || motifEntity.getChildren().isEmpty()) {
            if (motifEntity.getParent() != null){
                MotifEntity motifParent = motifRepository.findById(motifEntity.getParent().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Motif parent not found in database"));
                motifParent.getChildren().removeIf(child -> child.getId() == id);
            }

            removeChildren(id);

            motifRepository.deleteById(id);
        }
        else{
            throw(new AccessDeniedException("Unauthorised for motif deletion request"));
        }
    }

    public void deleteAll(){
        motifRepository.deleteAll();
    }

    private void removeChildren(int id){
        motifRepository.findById(id).ifPresent(motif -> {
            motif.getChildren().forEach((child -> removeChildren(child.getId())));

            for (SagaVersionMotifEntity sagaVersionMotifEntity : new HashSet<>(motif.getSagaVersionMotifEntities())){
                sagaVersionMotifEntity.getSagaVersionEntity().removeMotif(motif);
            };

            motifRepository.delete(motif);
        });
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

        sagaVersionRepository.findByAbbreviationContainsIgnoreCase(searchTerm).forEach(sagaVersion ->{
            sagaVersion.getSagaVersionMotifEntities().forEach(sagaMotif -> {
                motifIds.add(sagaMotif.getMotifEntity().getId());
            });
        });

        if (motifIds.size() > maxSearchResults){
            throw new TooManyResultsException("Too many results. Results received: " + motifIds.size());
        }

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

        if (motifIds.size() > maxSearchResults){
            throw new TooManyResultsException("Too many results. Results received: " + motifIds.size());
        }

        for (int motifId : motifIds){
            searchResults.add(new MotifSearchResult(motifId, buildPath(motifId)));
        }

        return searchResults;
    }

    List<Integer> buildPath(int motifId){
        int localMotifId = motifId;
        List<Integer> path = new ArrayList<>();
        boolean hasParent = true;

        while (hasParent){
            MotifEntity motif = motifRepository.findById(localMotifId)
                    .orElseThrow(() -> new ResourceNotFoundException("Motif entity not found in database"));

            if (motif.getParent() == null){
                hasParent = false;
            }
            else{
                int parentId = motif.getParent().getId();
                path.add(parentId);
                localMotifId = parentId;
            }
        }

        return path.reversed();
    }
}
