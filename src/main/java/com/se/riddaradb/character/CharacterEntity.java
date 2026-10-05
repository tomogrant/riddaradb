package com.se.riddaradb.character;

import com.se.riddaradb.location.LocationEntity;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "character")
public class CharacterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotBlank
    String name;

    @Enumerated(EnumType.STRING)
    CharacterEntity.Species species;

    public enum Species {
        HUMAN, GIANT, TROLL, DWARF, ELF, OBJECT
    }

    String realWorldRef;

    @ManyToMany(mappedBy = "characterEntity")
    Set<LocationEntity> locationEntity = new HashSet<>();

    @ManyToMany(mappedBy = "characterEntity")
    Set<SagaVersionEntity> sagaVersionEntity = new HashSet<>();

    protected CharacterEntity() {
    }

    public CharacterEntity(int id, String name, Species species, String realWorldRef) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.realWorldRef = realWorldRef;
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

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }

    public String getRealWorldRef() {
        return realWorldRef;
    }

    public void setRealWorldRef(String realWorldRef) {
        this.realWorldRef = realWorldRef;
    }

    public Set<LocationEntity> getLocationEntity() {
        return locationEntity;
    }

    public void setLocationEntity(Set<LocationEntity> locationEntity) {
        this.locationEntity = locationEntity;
    }

    public Set<SagaVersionEntity> getSagaVersionEntity() {
        return sagaVersionEntity;
    }

    public void setSagaVersionEntity(Set<SagaVersionEntity> sagaVersionEntity) {
        this.sagaVersionEntity = sagaVersionEntity;
    }


}


