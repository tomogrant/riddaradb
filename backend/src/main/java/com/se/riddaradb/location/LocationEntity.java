package com.se.riddaradb.location;

import com.se.riddaradb.character.CharacterEntity;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "location")
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String translation;

    @NotNull
    private Continent continent;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "locationEntity",  cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LocationCharacterEntity> locationCharacterEntities = new HashSet<>();

    @ManyToMany(mappedBy = "locationEntities")
    private Set<SagaVersionEntity> sagaVersionEntities = new HashSet<>();

    protected LocationEntity() {
    }

    public LocationEntity(Integer id, String name, String translation, Continent continent, String notes) {
        this.id = id;
        this.name = name;
        this.translation = translation;
        this.continent = continent;
        this.notes = notes;
    }

    public void addCharacter(CharacterEntity characterEntity, Boolean significantAssociation){
        LocationCharacterEntity locationCharacterEntity = new LocationCharacterEntity(this, characterEntity, significantAssociation);

        getLocationCharacterEntities().add(locationCharacterEntity);
        characterEntity.getLocationCharacterEntities().add(locationCharacterEntity);
    }

    public void removeCharacter(CharacterEntity characterEntity) {
        getLocationCharacterEntities().removeIf(locationCharacter -> Objects.equals(locationCharacter.getCharacterEntity().getId(), characterEntity.getId()));
        characterEntity.getLocationCharacterEntities().removeIf(locationCharacter -> Objects.equals(locationCharacter.getLocationEntity().getId(), getId()));
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