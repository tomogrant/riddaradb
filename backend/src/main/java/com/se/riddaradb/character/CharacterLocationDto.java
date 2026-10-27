package com.se.riddaradb.character;

public class CharacterLocationDto {

    Integer locationId;
    Boolean significantAssociation;

    public CharacterLocationDto(){}

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Boolean getSignificantAssociation() {
        return significantAssociation;
    }

    public void setSignificantAssociation(Boolean significantAssociation) {
        this.significantAssociation = significantAssociation;
    }

    public CharacterLocationDto(Integer locationId, Boolean significantAssociation) {
        this.locationId = locationId;
        this.significantAssociation = significantAssociation;


    }
}
