package com.se.riddaradb.location;

import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

public class LocationDto {

    private Integer id;
    @NotBlank
    private String name;
    private String translation;
    private Continent continent;
    private String notes;
    private Set<LocationCharacterDto> locationCharacterDtos;
    private Set<Integer> sagaVersionIds;

    public LocationDto(Integer id, String name, String translation, Continent continent, String notes) {
        this.id = id;
        this.name = name;
        this.translation = translation;
        this.continent = continent;
        this.notes = notes;
        locationCharacterDtos = new HashSet<>();
        sagaVersionIds = new HashSet<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Continent getContinent() {
        return continent;
    }

    public void setContinent(Continent continent) {
        this.continent = continent;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<LocationCharacterDto> getLocationCharacterDtos() {
        return locationCharacterDtos;
    }

    public void setLocationCharacterDtos(Set<LocationCharacterDto> locationCharacterDtos) {
        this.locationCharacterDtos = locationCharacterDtos;
    }

    public Set<Integer> getSagaVersionIds() {
        return sagaVersionIds;
    }

    public void setSagaVersionIds(Set<Integer> sagaVersionIds) {
        this.sagaVersionIds = sagaVersionIds;
    }
}
