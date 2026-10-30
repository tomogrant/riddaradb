package com.se.riddaradb.ms;

import com.se.riddaradb.saga.SagaMsEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ms")
public class MsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @NotBlank
    private String shelfmark;

    @NotBlank
    private String date;

    private String entryLink;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "msEntity", orphanRemoval = true)
    private Set<SagaMsEntity> sagaMsEntities = new HashSet<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="msRepositoryId", nullable = false)
    private MsRepositoryEntity msRepositoryEntity;

    public MsEntity() {
    }

    public MsEntity(Integer id, String name, String shelfmark, String date, String entryLink, String description) {
        this.id = id;
        this.name = name;
        this.shelfmark = shelfmark;
        this.date = date;
        this.entryLink = entryLink;
        this.description = description;
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

    public String getShelfmark() {
        return shelfmark;
    }

    public void setShelfmark(String shelfmark) {
        this.shelfmark = shelfmark;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEntryLink() {
        return entryLink;
    }

    public void setEntryLink(String entryLink) {
        this.entryLink = entryLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<SagaMsEntity> getSagaMsEntities() {
        return sagaMsEntities;
    }

    public void setSagaMsEntities(Set<SagaMsEntity> sagaMsEntities) {
        this.sagaMsEntities = sagaMsEntities;
    }

    public MsRepositoryEntity getMsRepositoryEntity() {
        return msRepositoryEntity;
    }

    public void setMsRepositoryEntity(MsRepositoryEntity msRepositoryEntity) {
        this.msRepositoryEntity = msRepositoryEntity;
    }
}
