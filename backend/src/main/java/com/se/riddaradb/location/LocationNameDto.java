package com.se.riddaradb.location;

public class LocationNameDto {

    private Integer id;
    private String name;
    private String translation;

    public LocationNameDto(){}

    public LocationNameDto(Integer id, String name, String translation) {
        this.id = id;
        this.name = name;
        this.translation = translation;
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
}
