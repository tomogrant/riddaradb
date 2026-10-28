package com.se.riddaradb.character;

import com.se.riddaradb.location.LocationCharacterEntity;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "character")
public class CharacterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Species species;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Sex sex;

    private String notes;

    @OneToMany(mappedBy = "characterEntity", orphanRemoval = true)
    private Set<LocationCharacterEntity> locationCharacterEntities = new HashSet<>();

    @ManyToMany(mappedBy = "characterEntities")
    Set<SagaVersionEntity> sagaVersionEntities = new HashSet<>();

    protected CharacterEntity() {
    }

    public CharacterEntity(Integer id, String name, Species species, Sex sex, String notes) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.sex = sex;
        this.notes = notes;
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

    public Set<LocationCharacterEntity> getLocationCharacterEntities() {
        return locationCharacterEntities;
    }

    public void setLocationCharacterEntities(Set<LocationCharacterEntity> locationCharacterEntities) {
        this.locationCharacterEntities = locationCharacterEntities;
    }

    public Set<SagaVersionEntity> getSagaVersionEntities() {
        return sagaVersionEntities;
    }

    public void setSagaVersionEntities(Set<SagaVersionEntity> sagaVersionEntities) {
        this.sagaVersionEntities = sagaVersionEntities;
    }
}


