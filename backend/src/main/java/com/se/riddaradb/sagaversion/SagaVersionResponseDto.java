package com.se.riddaradb.sagaversion;

import com.se.riddaradb.character.CharacterNameDto;
import com.se.riddaradb.location.LocationNameDto;

import java.util.HashSet;
import java.util.Set;

public class SagaVersionResponseDto {

    private Integer id;
    private String title;
    private String abbreviation;
    private String description;
    private SagaDate date;
    private Integer sagaId;
    private Set<SagaVersionMotifDto> sagaMotifs;
    private Set<CharacterNameDto> characterNames;
    private Set<LocationNameDto> locationNames;

    public SagaVersionResponseDto(Integer id,
                                  String title,
                                  String abbreviation,
                                  String description,
                                  SagaDate date) {
        this.id = id;
        this.title = title;
        this.abbreviation = abbreviation;
        this.description = description;
        this.date = date;
        sagaMotifs = new HashSet<>();
        characterNames = new HashSet<>();
        locationNames = new HashSet<>();
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

    public Set<SagaVersionMotifDto> getSagaMotifs() {
        return sagaMotifs;
    }

    public void setSagaMotifs(Set<SagaVersionMotifDto> sagaMotifs) {
        this.sagaMotifs = sagaMotifs;
    }

    public Set<CharacterNameDto> getCharacterNames() {
        return characterNames;
    }

    public void setCharacterNames(Set<CharacterNameDto> characterNames) {
        this.characterNames = characterNames;
    }

    public Set<LocationNameDto> getLocationNames() {
        return locationNames;
    }

    public void setLocationNames(Set<LocationNameDto> locationNames) {
        this.locationNames = locationNames;
    }
}