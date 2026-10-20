package com.se.riddaradb.location;

import com.se.riddaradb.character.CharacterEntity;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "location")
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @NotBlank
    String name;

    @Enumerated(EnumType.STRING)
    Type type;

    public enum Type {
        COUNTRY, SETTLEMENT, SITE, OTHER
    }

    @ManyToMany()
    @JoinTable(name = "location-character",
            joinColumns = @JoinColumn(name = "location_id"),
            inverseJoinColumns = @JoinColumn(name = "character_id"))
    Set<CharacterEntity> characterEntities = new HashSet<>();

    @ManyToMany(mappedBy = "locationEntities")
    Set<SagaVersionEntity> sagaVersionEntities = new HashSet<>();

    protected LocationEntity() {
    }

    public LocationEntity(int id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Set<CharacterEntity> getCharacterEntities() {
        return characterEntities;
    }

    public void setCharacterEntities(Set<CharacterEntity> characterEntities) {
        this.characterEntities = characterEntities;
    }

    public Set<SagaVersionEntity> getSagaVersionEntities() {
        return sagaVersionEntities;
    }

    public void setSagaVersionEntities(Set<SagaVersionEntity> sagaVersionEntities) {
        this.sagaVersionEntities = sagaVersionEntities;
    }
}