package com.se.riddaradb.sagaversion;

import com.se.riddaradb.character.CharacterEntity;
import com.se.riddaradb.motif.MotifEntity;
import com.se.riddaradb.location.LocationEntity;
import com.se.riddaradb.saga.SagaEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "sagaversion")
public class SagaVersionEntity {

    public enum SagaDate {
        UNDEFINED,
        UNKNOWN,
        _1200_1250,
        _1250_1300,
        _1300_1350,
        _1350_1400,
        _1400_1450,
        _1450_1500,
        _1500_1550
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private SagaDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sagaId")
    private SagaEntity sagaEntity;

    @OneToMany(mappedBy = "sagaVersionEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SagaVersionMotifEntity> sagaVersionMotifEntities = new HashSet<>();

    @ManyToMany()
    @JoinTable(name = "sagaversion-character",
            joinColumns = @JoinColumn(name = "sagaversion_id"),
            inverseJoinColumns = @JoinColumn(name = "character_id"))
    private Set<CharacterEntity> characterEntities = new HashSet<>();

    @ManyToMany()
    @JoinTable(name = "sagaversion-location",
            joinColumns = @JoinColumn(name = "sagaversion_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id"))
    private Set<LocationEntity> locationEntities = new HashSet<>();
    

    public SagaVersionEntity() {
    }

    public SagaVersionEntity(Integer id, String title, String description, SagaDate date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public void addMotif(MotifEntity motifEntity, String pageChapterNumber, Boolean inBoberg){
        SagaVersionMotifEntity sagaVersionMotifEntity = new SagaVersionMotifEntity(this, motifEntity, pageChapterNumber, inBoberg);

        getSagaVersionMotifEntities().add(sagaVersionMotifEntity);
        motifEntity.getSagaVersionMotifEntities().add(sagaVersionMotifEntity);
    }

    public void removeMotif(MotifEntity motifEntity){
        getSagaVersionMotifEntities().removeIf(sagaMotif -> Objects.equals(sagaMotif.getMotifEntity().getId(), motifEntity.getId()));
        motifEntity.getSagaVersionMotifEntities().removeIf(sagaMotif -> Objects.equals(sagaMotif.getSagaVersionEntity().getId(), getId()));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SagaDate getDate() {
        return date;
    }

    public void setDate(SagaDate sagaDate) {
        this.date = sagaDate;
    }

    public SagaEntity getSagaEntity() {
        return sagaEntity;
    }

    public void setSagaEntity(SagaEntity sagaEntity) {
        this.sagaEntity = sagaEntity;
    }

    public Set<SagaVersionMotifEntity> getSagaVersionMotifEntities() {
        return sagaVersionMotifEntities;
    }

    public void setSagaVersionMotifEntities(Set<SagaVersionMotifEntity> sagaVersionMotifEntities) {
        this.sagaVersionMotifEntities = sagaVersionMotifEntities;
    }

    public Set<CharacterEntity> getCharacterEntities() {
        return characterEntities;
    }

    public void setCharacterEntities(Set<CharacterEntity> characterEntities) {
        this.characterEntities = characterEntities;
    }

    public Set<LocationEntity> getLocationEntities() {
        return locationEntities;
    }

    public void setLocationEntities(Set<LocationEntity> locationEntities) {
        this.locationEntities = locationEntities;
    }
}
