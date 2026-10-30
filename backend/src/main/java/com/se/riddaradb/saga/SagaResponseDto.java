package com.se.riddaradb.saga;

import com.se.riddaradb.bib.BibDto;
import com.se.riddaradb.sagaversion.SagaVersionResponseDto;

import java.util.HashSet;
import java.util.Set;

public class SagaResponseDto {

    private Integer id;
    private String title;
    private String translatedTitle;
    private String pageAuthor;
    private String description;
    private Boolean translated;
    private Boolean published;
    private Set<SagaVersionResponseDto> sagaVersions;
    private Set<BibDto> bibDtos;
    private Set<SagaMsDto> sagaMsDtos = new HashSet<>();

    public SagaResponseDto(
            Integer id,
            String title,
            String translatedTitle,
            String pageAuthor,
            String description,
            Boolean translated,
            Boolean published
    ) {
        this.id = id;
        this.title = title;
        this.translatedTitle = translatedTitle;
        this.pageAuthor = pageAuthor;
        this.description = description;
        this.translated = translated;
        this.published = published;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = title;
    }

    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    public String getPageAuthor() {
        return pageAuthor;
    }

    public void setPageAuthor(String pageAuthor) {
        this.pageAuthor = pageAuthor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getTranslated() {
        return translated;
    }

    public void setTranslated(Boolean translated) {
        this.translated = translated;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Set<SagaVersionResponseDto> getSagaVersions() {
        return this.sagaVersions;
    }

    public void setSagaVersions(Set<SagaVersionResponseDto> sagaVersions) {
        this.sagaVersions = sagaVersions;
    }

    public Set<BibDto> getBibDtos() {
        return bibDtos;
    }

    public void setBibDtos(Set<BibDto> bibDtos) {
        this.bibDtos = bibDtos;
    }

    public Set<SagaMsDto> getSagaMsDtos() {
        return sagaMsDtos;
    }

    public void setSagaMsDtos(Set<SagaMsDto> sagaMsDtos) {
        this.sagaMsDtos = sagaMsDtos;
    }
}
