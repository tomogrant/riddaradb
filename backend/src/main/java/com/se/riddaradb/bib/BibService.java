package com.se.riddaradb.bib;

import com.se.riddaradb.exceptions.ResourceNotFoundException;
import com.se.riddaradb.saga.SagaEntity;
import com.se.riddaradb.saga.SagaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Service
@Transactional
public class BibService {

    final BibRepository bibRepository;
    final SagaRepository sagaRepository;
    final BibMapper bibMapper;

    public BibService(BibRepository bibRepository, SagaRepository sagaRepository, BibMapper bibMapper) {
        this.bibRepository = bibRepository;
        this.sagaRepository = sagaRepository;
        this.bibMapper = bibMapper;
    }

    public Collection<BibDto> getBibEntries(){
        return bibRepository.findAll()
                .stream()
                .map(bibMapper::mapToDto)
                .toList();
    }

    public BibDto getBibEntryById(int id){
        return bibRepository.findById(id)
                .map(bibMapper::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Bibliography entry with ID " + id + " not found"));
    }

    public BibDto saveBibEntry(BibDto bibDto){

        BibEntity bibEntity = bibMapper.mapFromDto(bibDto);

        for (Integer sagaId : bibDto.getSagaIds()){
            sagaRepository.findById(sagaId).ifPresent(saga -> saga.addBib(bibEntity));
        }

        return bibMapper.mapToDto(bibRepository.save(bibEntity));
    }
    public BibDto updateBibEntry(BibDto bibDto){

        BibEntity bibEntity = bibRepository.findById(bibDto.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Bibliography entry not found in DB"));

        updateBibEntity(bibEntity, bibDto);
        updateSagas(bibEntity, bibDto);

        return bibMapper.mapToDto(bibRepository.save(bibEntity));
    }

    public void deleteBibEntryById(int id){

        bibRepository.findById(id).ifPresent(bibEntity -> {
            for (SagaEntity sagaEntity : bibEntity.getSagaEntities()){
                sagaEntity.removeBib(bibEntity);
            }
        });

        bibRepository.deleteById(id);
    }

    public void deleteAll(){
        bibRepository.deleteAll();
    }

    private void updateBibEntity(BibEntity bibEntity, BibDto bibDto){
        bibEntity.setPublicationType(bibDto.getPublicationType());
        bibEntity.setAuthors(bibDto.getAuthors());
        bibEntity.setEditors(bibDto.getEditors());
        bibEntity.setTranslators(bibDto.getTranslators());
        bibEntity.setTitle(bibDto.getTitle());
        bibEntity.setUrl(bibDto.getUrl());
        bibEntity.setBookEditors(bibDto.getBookEditors());
        bibEntity.setBook(bibDto.getBook());
        bibEntity.setBookSeries(bibDto.getBookSeries());
        bibEntity.setVolume(bibDto.getVolume());
        bibEntity.setNumOfVolumes(bibDto.getNumOfVolumes());
        bibEntity.setPublisher(bibDto.getPublisher());
        bibEntity.setPublicationYear(bibDto.getPublicationYear());
        bibEntity.setPageNumbers(bibDto.getPageNumbers());
        bibEntity.setRecommended(bibDto.getRecommended());
        bibEntity.setDescription(bibDto.getDescription());
    }

    private void updateSagas(BibEntity bibEntity, BibDto bibDto){

        Set<Integer> newIds = bibDto.getSagaIds();

        for (SagaEntity sagaEntity : bibEntity.getSagaEntities()){
            if (!newIds.contains(sagaEntity.getId())){
                sagaEntity.removeBib(bibEntity);
            }
        }

        for (Integer sagaId : newIds){
            sagaRepository.findById(sagaId).ifPresent(saga ->
                    saga.addBib(bibEntity));
        }
    }
}
