package com.se.riddaradb.sagaversion;

import com.se.riddaradb.motif.MotifEntity;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="sagaversionmotif")
public class SagaVersionMotifEntity {

    @EmbeddedId
    private SagaVersionMotifKey id = new SagaVersionMotifKey();

    @ManyToOne
    @MapsId("sagaVersionId")
    @JoinColumn(name="sagaversion_id")
    private SagaVersionEntity sagaVersionEntity;

    @ManyToOne
    @MapsId("motifId")
    @JoinColumn(name="motif_id")
    private MotifEntity motifEntity;

    private String pageChapterNumber;

    private Boolean inBoberg;

    public SagaVersionMotifEntity(){}

    public SagaVersionMotifEntity(SagaVersionEntity sagaVersionEntity, MotifEntity motifEntity, String pageChapterNumber, Boolean inBoberg) {
        this.sagaVersionEntity = sagaVersionEntity;
        this.motifEntity = motifEntity;
        this.pageChapterNumber = pageChapterNumber;
        this.inBoberg = inBoberg;
    }

    public SagaVersionMotifKey getId() {
        return id;
    }

    public void setId(SagaVersionMotifKey id) {
        this.id = id;
    }

    public SagaVersionEntity getSagaVersionEntity() {
        return sagaVersionEntity;
    }

    public void setSagaVersionEntity(SagaVersionEntity sagaVersionEntity) {
        this.sagaVersionEntity = sagaVersionEntity;
    }

    public MotifEntity getMotifEntity() {
        return motifEntity;
    }

    public void setMotifEntity(MotifEntity motifEntity) {
        this.motifEntity = motifEntity;
    }

    public String getPageChapterNumber() {
        return pageChapterNumber;
    }

    public void setPageChapterNumber(String pageChapterNumber) {
        this.pageChapterNumber = pageChapterNumber;
    }

    public Boolean getInBoberg() {
        return inBoberg;
    }

    public void setInBoberg(Boolean inBoberg) {
        this.inBoberg = inBoberg;
    }
}
