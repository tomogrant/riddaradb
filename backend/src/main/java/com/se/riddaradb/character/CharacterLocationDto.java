package com.se.riddaradb.character;

public class CharacterLocationDto {

    Integer locationId;
    String locationName;
    Boolean significantAssociation;

    public CharacterLocationDto(){}

    public CharacterLocationDto(Integer locationId, String locationName, Boolean significantAssociation) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.significantAssociation = significantAssociation;
    }


        public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Boolean getSignificantAssociation() {
        return significantAssociation;
    }

    public void setSignificantAssociation(Boolean significantAssociation) {
        this.significantAssociation = significantAssociation;
    }
}
