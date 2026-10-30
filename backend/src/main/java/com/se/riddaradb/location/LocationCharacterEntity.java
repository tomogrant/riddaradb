package com.se.riddaradb.location;

import com.se.riddaradb.character.CharacterEntity;
import jakarta.persistence.*;

@Entity
@Table(name="location_character")
public class LocationCharacterEntity {

    @EmbeddedId
    private LocationCharacterKey id = new LocationCharacterKey();

    @ManyToOne
    @MapsId("locationId")
    @JoinColumn(name="location_id")
    private LocationEntity locationEntity;

    @ManyToOne
    @MapsId("characterId")
    @JoinColumn(name="character_id")
    private CharacterEntity characterEntity;

    private Boolean significantAssociation;

    public LocationCharacterEntity() {
    }

    public LocationCharacterEntity(LocationEntity locationEntity,  CharacterEntity characterEntity, Boolean significantAssociation) {
        this.locationEntity = locationEntity;
        this.characterEntity = characterEntity;
        this.significantAssociation = significantAssociation;
    }

    public LocationCharacterKey getId() {
        return id;
    }

    public void setId(LocationCharacterKey id) {
        this.id = id;
    }

    public LocationEntity getLocationEntity() {
        return locationEntity;
    }

    public void setLocationEntity(LocationEntity locationEntity) {
        this.locationEntity = locationEntity;
    }

    public CharacterEntity getCharacterEntity() {
        return characterEntity;
    }

    public void setCharacterEntity(CharacterEntity characterEntity) {
        this.characterEntity = characterEntity;
    }

    public Boolean getSignificantAssociation() {
        return significantAssociation;
    }

    public void setSignificantAssociation(Boolean significantAssociation) {
        this.significantAssociation = significantAssociation;
    }
}
