package com.se.riddaradb.location;

import com.se.riddaradb.character.CharacterEntity;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "location")
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NotBlank
    String name;

    @OneToMany(mappedBy = "locationEntity",  cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LocationCharacterEntity> locationCharacterEntities = new HashSet<>();

    @ManyToMany(mappedBy = "locationEntities")
    Set<SagaVersionEntity> sagaVersionEntities = new HashSet<>();

    protected LocationEntity() {
    }

    public LocationEntity(Integer id, String name) {
        this.id = id;
        this.name = name;
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