package com.se.riddaradb.character;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CharacterDto {

    private Integer id;

    @NotBlank
    private String name;
    @NotNull
    private Species species;
    @NotNull
    private Sex sex;
    private String notes;
    private Set<CharacterLocationDto> characterLocationDtos;
    private Set<Integer> sagaVersionIds;

    public CharacterDto(Integer id, String name, Species species, Sex sex, String notes) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.sex = sex;
        this.notes = notes;
        characterLocationDtos = new HashSet<>();
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

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Set<CharacterLocationDto> getCharacterLocationDtos() {
        return characterLocationDtos;
    }

    public void setCharacterLocationDtos(Set<CharacterLocationDto> characterLocationDtos) {
        this.characterLocationDtos = characterLocationDtos;
    }

    public Set<Integer> getSagaVersionIds() {
        return sagaVersionIds;
    }

    public void setSagaVersionIds(Set<Integer> sagaVersionIds) {
        this.sagaVersionIds = sagaVersionIds;
    }
}
