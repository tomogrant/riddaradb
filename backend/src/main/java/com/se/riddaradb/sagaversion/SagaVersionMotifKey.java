package com.se.riddaradb.sagaversion;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SagaVersionMotifKey implements Serializable {

    public SagaVersionMotifKey(){}

    public SagaVersionMotifKey(Integer sagaVersionId, Integer motifId) {
        this.sagaVersionId = sagaVersionId;
        this.motifId = motifId;
    }

    @Column(name="sagaversion_id")
    private Integer sagaVersionId;

    @Column(name="motif_id")
    private Integer motifId;

    public Integer getSagaVersionId() {
        return sagaVersionId;
    }

    public void setSagaVersionId(Integer sagaVersionId) {
        this.sagaVersionId = sagaVersionId;
    }

    public Integer getMotifId() {
        return motifId;
    }

    public void setMotifId(Integer motifId) {
        this.motifId = motifId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SagaVersionMotifKey that = (SagaVersionMotifKey) o;
        return getSagaVersionId() == that.getSagaVersionId() && getMotifId() == that.getMotifId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSagaVersionId(), getMotifId());
    }
}