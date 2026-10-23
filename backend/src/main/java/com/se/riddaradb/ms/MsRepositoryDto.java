package com.se.riddaradb.ms;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public class MsRepositoryDto {

    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String city;
    @NotBlank
    private String country;
    private Set<Integer> msIds;

    public MsRepositoryDto(){

    }
    public MsRepositoryDto(Integer id, String name, String city, String country){
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Set<Integer> getMsIds() {
        return msIds;
    }

    public void setMsIds(Set<Integer> msIds) {
        this.msIds = msIds;
    }
}
