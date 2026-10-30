package com.se.riddaradb.ms;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="ms_repository")
public class MsRepositoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    @OneToMany(mappedBy = "msRepositoryEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MsEntity> msEntities = new HashSet<>();

    public void addMs(MsEntity msEntity){
        this.msEntities.add(msEntity);
        msEntity.setMsRepositoryEntity(this);
    }

    public void removeMs(MsEntity msEntity){
        this.msEntities.remove(msEntity);
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

    public Set<MsEntity> getMsEntities() {
        return msEntities;
    }

    public void setMsEntities(Set<MsEntity> msEntities) {
        this.msEntities = msEntities;
    }
}
