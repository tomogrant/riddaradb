package com.se.riddaradb.location;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LocationCharacterKey implements Serializable {

    private Integer locationId;
    private Integer characterId;

    public LocationCharacterKey(){}

    public LocationCharacterKey(Integer locationId, Integer characterId){
        this.locationId = locationId;
        this.characterId = characterId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Integer characterId) {
        this.characterId = characterId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LocationCharacterKey that = (LocationCharacterKey) o;
        return Objects.equals(getLocationId(), that.getLocationId()) && Objects.equals(getCharacterId(), that.getCharacterId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocationId(), getCharacterId());
    }
}
