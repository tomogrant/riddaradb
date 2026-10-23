package com.se.riddaradb.ms;

public class MsSagaDto {
    private Integer sagaId;
    private String folioNumber;
    private String note;

    public MsSagaDto(Integer sagaId, String folioNumber, String note) {
        this.sagaId = sagaId;
        this.folioNumber = folioNumber;
        this.note = note;
    }

    public Integer getSagaId() {
        return sagaId;
    }

    public void setSagaId(Integer sagaId) {
        this.sagaId = sagaId;
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
