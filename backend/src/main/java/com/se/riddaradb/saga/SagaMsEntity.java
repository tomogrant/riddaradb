package com.se.riddaradb.saga;

import com.se.riddaradb.ms.MsEntity;
import jakarta.persistence.*;

@Entity
@Table(name="sagams")
public class SagaMsEntity {

    @EmbeddedId
    private SagaMsKey id = new SagaMsKey();

    @ManyToOne
    @MapsId("sagaId")
    @JoinColumn(name="saga_id")
    private SagaEntity sagaEntity;

    @ManyToOne
    @MapsId("msId")
    @JoinColumn(name="ms_id")
    private MsEntity msEntity;

    private String folioNumber;

    private String note;

    public SagaMsEntity(){}

    public SagaMsEntity(SagaEntity sagaEntity, MsEntity msEntity, String folioNumber, String note) {
        this.sagaEntity = sagaEntity;
        this.msEntity = msEntity;
        this.folioNumber = folioNumber;
        this.note = note;
    }

    public SagaMsKey getId() {
        return id;
    }

    public void setId(SagaMsKey id) {
        this.id = id;
    }

    public SagaEntity getSagaEntity() {
        return sagaEntity;
    }

    public void setSagaEntity(SagaEntity sagaEntity) {
        this.sagaEntity = sagaEntity;
    }

    public MsEntity getMsEntity() {
        return msEntity;
    }

    public void setMsEntity(MsEntity msEntity) {
        this.msEntity = msEntity;
    }

    public String getFolioNumber() {
        return folioNumber;
    }

    public void setFolioNumber(String folioNumber) {
        this.folioNumber = folioNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
