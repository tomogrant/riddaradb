package com.se.riddaradb.sagaversion;

import java.util.HashSet;
import java.util.Set;

public class SagaVersionRequestDto {

    private Integer id;
    private String title;
    private String abbreviation;
    private String description;
    private SagaDate date;
    private Integer sagaId;
    private Set<Integer> characterIds;
    private Set<Integer> locationIds;

    public SagaVersionRequestDto(Integer id, String title, String abbreviation, String description, SagaDate date) {
        this.id = id;
        this.title = title;
        this.abbreviation = abbreviation;
        this.description = description;
        this.date = date;
        characterIds = new HashSet<>();
        locationIds = new HashSet<>();
    }

    public SagaVersionRequestDto() {

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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SagaDate getDate() {
        return date;
    }

    public void setDate(SagaDate date) {
        this.date = date;
    }

    public Integer getSagaId() {
        return sagaId;
    }

    public void setSagaId(Integer sagaId) {
        this.sagaId = sagaId;
    }

    public Set<Integer> getCharacterIds() {
        return characterIds;
    }

    public void setCharacterIds(Set<Integer> characterIds) {
        this.characterIds = characterIds;
    }

    public Set<Integer> getLocationIds() {
        return locationIds;
    }

    public void setLocationIds(Set<Integer> locationIds) {
        this.locationIds = locationIds;
    }
}
