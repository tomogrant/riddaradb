package com.se.riddaradb.motif;

import com.se.riddaradb.sagaversion.SagaVersionMotifEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "motif")
public class MotifEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String motifCode;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String motifName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private MotifEntity parent;

    @OneToMany(mappedBy = "parent")
    private Set<MotifEntity> children = new HashSet<>();

    @OneToMany(mappedBy = "motifEntity", orphanRemoval = true)
    private Set<SagaVersionMotifEntity> sagaVersionMotifEntities = new HashSet<>();

    protected MotifEntity() {
    }

    public MotifEntity(Integer id,
                       String motifCode,
                       String motifName,
                       String description) {
        this.id = id;
        this.motifCode = motifCode;
        this.motifName = motifName;
        this.description = description;
    }

    public void addChildMotif(MotifEntity childMotif){
        this.getChildren().add(childMotif);
        childMotif.setParent(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMotifCode() {
        return motifCode;
    }

    public void setMotifCode(String motifCode) {
        this.motifCode = motifCode;
    }

    public String getMotifName() {
        return motifName;
    }

    public void setMotifName(String motifName) {
        this.motifName = motifName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MotifEntity getParent() {
        return parent;
    }

    public void setParent(MotifEntity parent) {
        this.parent = parent;
    }

    public Set<MotifEntity> getChildren() {
        return children;
    }

    public void setChildren(Set<MotifEntity> children) {
        this.children = children;
    }

    public Set<SagaVersionMotifEntity> getSagaVersionMotifEntities() {
        return sagaVersionMotifEntities;
    }

    public void setSagaVersionMotifEntities(Set<SagaVersionMotifEntity> sagaVersionMotifEntities) {
        this.sagaVersionMotifEntities = sagaVersionMotifEntities;
    }
}
