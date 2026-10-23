package com.se.riddaradb.bib;

import com.se.riddaradb.saga.SagaEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class BibMapper {

    public BibDto mapToDto(BibEntity bibEntity){
        BibDto bibDto = new BibDto(bibEntity.getId(),
                bibEntity.getPublicationType(),
                bibEntity.getAuthors(),
                bibEntity.getEditors(),
                bibEntity.getTranslators(),
                bibEntity.getTitle(),
                bibEntity.getUrl(),
                bibEntity.getBookEditors(),
                bibEntity.getBook(),
                bibEntity.getBookSeries(),
                bibEntity.getVolume(),
                bibEntity.getNumOfVolumes(),
                bibEntity.getPlaceOfPublication(),
                bibEntity.getPublisher(),
                bibEntity.getPublicationYear(),
                bibEntity.getPageNumbers(),
                bibEntity.getRecommended(),
                bibEntity.getDescription());

        bibDto.setSagaIds(bibEntity.getSagaEntity()
                .stream()
                .map(SagaEntity::getId)
                .collect(Collectors.toSet()));

        return bibDto;
    }

    public BibEntity mapFromDto(BibDto bibDto){
        return new BibEntity(bibDto.getId(),
                bibDto.getPublicationType(),
                bibDto.getAuthors(),
                bibDto.getEditors(),
                bibDto.getTranslators(),
                bibDto.getTitle(),
                bibDto.getUrl(),
                bibDto.getBookEditors(),
                bibDto.getBook(),
                bibDto.getBookSeries(),
                bibDto.getVolume(),
                bibDto.getNumOfVolumes(),
                bibDto.getPlaceOfPublication(),
                bibDto.getPublisher(),
                bibDto.getPublicationYear(),
                bibDto.getPageNumbers(),
                bibDto.getRecommended(),
                bibDto.getDescription());
    }

    public void updateBibEntity(BibEntity bibEntity, BibDto bibDto){
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
}
