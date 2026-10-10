package com.se.riddaradb.saga;

import com.se.riddaradb.bib.BibEntity;
import com.se.riddaradb.ms.MsEntity;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "saga")
public class SagaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String title;

    @NotBlank
    private String translatedTitle;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean translated;

    @OneToMany(mappedBy = "sagaEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SagaVersionEntity> sagaVersionEntities = new HashSet<>();

    @ManyToMany()
    @JoinTable(name = "saga-bib",
            joinColumns = @JoinColumn(name = "saga_id"),
            inverseJoinColumns = @JoinColumn(name = "bib_id"))
    private Set<BibEntity> bibEntities = new HashSet<>();

    @OneToMany(mappedBy = "sagaEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SagaMsEntity> sagaMsEntities = new HashSet<>();

    public SagaEntity(){
    }

    public SagaEntity(Integer id, String title, String translatedTitle, String description, Boolean translated) {
        this.id = id;
        this.title = title;
        this.translatedTitle = translatedTitle;
        this.description = description;
        this.translated = translated;
    }


    public void addSagaVersion(SagaVersionEntity sagaVersion){
        sagaVersionEntities.add(sagaVersion);
        sagaVersion.setSagaEntity(this);
    }

    public void removeSagaVersion(int id){
        getSagaVersionEntities().removeIf(existingSagaVersionEntity -> existingSagaVersionEntity.getId() == id);
    }

    public void addBib(BibEntity bib){
        bibEntities.add(bib);
        bib.getSagaEntity().add(this);
    }

    public void addMs(MsEntity msEntity, String folioNumber, String note){
        SagaMsEntity sagaMsEntity = new SagaMsEntity(this, msEntity, folioNumber, note);

        getSagaMsEntities().add(sagaMsEntity);
        msEntity.getSagaMsEntities().add(sagaMsEntity);
    }

    public void removeMs(MsEntity msEntity){
        getSagaMsEntities().removeIf(sagaMs -> Objects.equals(sagaMs.getMsEntity().getId(), msEntity.getId()));
        msEntity.getSagaMsEntities().removeIf(sagaMs -> Objects.equals(sagaMs.getSagaEntity().getId(), getId()));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotBlank String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank String title) {
        this.title = title;
    }

    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getTranslated() {
        return translated;
    }

    public void setTranslated(Boolean translated) {
        this.translated = translated;
    }

    public Set<SagaVersionEntity> getSagaVersionEntities() {
        return sagaVersionEntities;
    }

    public void setSagaVersionEntities(Set<SagaVersionEntity> sagaVersionEntities) {
        this.sagaVersionEntities = sagaVersionEntities;
    }

    public Set<BibEntity> getBibEntities() {
        return bibEntities;
    }

    public void setBibEntities(Set<BibEntity> bibEntities) {
        this.bibEntities = bibEntities;
    }

    public Set<SagaMsEntity> getSagaMsEntities() {
        return sagaMsEntities;
    }

    public void setSagaMsEntities(Set<SagaMsEntity> sagaMsEntities) {
        this.sagaMsEntities = sagaMsEntities;
    }
}
