package com.se.riddaradb.location;

public class LocationCharacterDto {

    private Integer characterId;
    private String characterName;
    private Boolean significantAssociation;

    public LocationCharacterDto(){}

    public LocationCharacterDto(Integer characterId, String characterName, Boolean significantAssociation) {
        this.characterId = characterId;
        this.characterName = characterName;
        this.significantAssociation = significantAssociation;
    }

    public Integer getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Integer characterId) {
        this.characterId = characterId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }

    public Boolean getSignificantAssociation() {
        return significantAssociation;
    }

    public void setSignificantAssociation(Boolean significantAssociation) {
        this.significantAssociation = significantAssociation;
    }
}
