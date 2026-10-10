package com.se.riddaradb.character;

import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

public class CharacterDto {

    int id;

    @NotBlank
    String name;
    CharacterEntity.Species species;
    String realWorldRef;
    Set<Integer> locationIds;
    Set<Integer> SagaVersionIds;

    public CharacterDto(int id, String name, CharacterEntity.Species species, String realWorldRef) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.realWorldRef = realWorldRef;
        locationIds = new HashSet<>();
        SagaVersionIds = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CharacterEntity.Species getSpecies() {
        return species;
    }

    public void setSpecies(CharacterEntity.Species species) {
        this.species = species;
    }

    public String getRealWorldRef() {
        return realWorldRef;
    }

    public void setRealWorldRef(String realWorldRef) {
        this.realWorldRef = realWorldRef;
    }

    public Set<Integer> getLocationIds() {
        return locationIds;
    }

    public void setLocationIds(Set<Integer> locationIds) {
        this.locationIds = locationIds;
    }

    public Set<Integer> getSagaVersionIds() {
        return SagaVersionIds;
    }

    public void setSagaVersionIds(Set<Integer> SagaVersionIds) {
        this.SagaVersionIds = SagaVersionIds;
    }
}
