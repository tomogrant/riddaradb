package com.se.riddaradb.location;

import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

public class LocationDto {

    int id;
    @NotBlank
    String name;
    LocationEntity.Type type;
    Set<Integer> characterIds;
    Set<Integer> SagaVersionIds;

    public LocationDto(int id, String name, LocationEntity.Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
        SagaVersionIds = new HashSet<>();
        characterIds = new HashSet<>();
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

    public LocationEntity.Type getType() {
        return type;
    }

    public void setType(LocationEntity.Type type) {
        this.type = type;
    }

    public Set<Integer> getCharacterIds() {
        return characterIds;
    }

    public void setCharacterIds(Set<Integer> characterIds) {
        this.characterIds = characterIds;
    }

    public Set<Integer> getSagaVersionIds() {
        return SagaVersionIds;
    }

    public void setSagaVersionIds(Set<Integer> SagaVersionIds) {
        this.SagaVersionIds = SagaVersionIds;
    }
}
