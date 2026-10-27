package com.se.riddaradb.location;

public class LocationCharacterDto {

    private Integer characterId;
    private Boolean significantAssociation;

    public LocationCharacterDto(){}

    public LocationCharacterDto(Integer characterId, Boolean significantAssociation) {
        this.characterId = characterId;
        this.significantAssociation = significantAssociation;
    }

    public Integer getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Integer characterId) {
        this.characterId = characterId;
    }

    public Boolean getSignificantAssociation() {
        return significantAssociation;
    }

    public void setSignificantAssociation(Boolean significantAssociation) {
        this.significantAssociation = significantAssociation;
    }
}
