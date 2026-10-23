package com.se.riddaradb.saga;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SagaMsKey implements Serializable {

    public SagaMsKey(){}

    public SagaMsKey(Integer sagaId, Integer msId) {
        this.sagaId = sagaId;
        this.msId = msId;
    }

    @Column(name="saga_id")
    private Integer sagaId;

    @Column(name="ms_id")
    private Integer msId;

    public Integer getSagaId() {
        return sagaId;
    }

    public void setSagaId(Integer sagaId) {
        this.sagaId = sagaId;
    }

    public Integer getmsId() {
        return msId;
    }

    public void setmsId(Integer msId) {
        this.msId = msId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SagaMsKey that = (SagaMsKey) o;
        return getSagaId() == that.getSagaId() && getmsId() == that.getmsId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSagaId(), getmsId());
    }
}