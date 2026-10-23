package com.se.riddaradb.saga;

public class SagaMsDto {
    private Integer msId;
    private String shelfmark;
    private String date;
    private String folioNumber;
    private String note;

    public SagaMsDto(){}

    public SagaMsDto(Integer msId, String shelfmark, String date, String folioNumber, String note) {
        this.msId = msId;
        this.shelfmark = shelfmark;
        this.date = date;
        this.folioNumber = folioNumber;
        this.note = note;
    }

    public SagaMsDto(Integer msId) {
        this.msId = msId;
    }

    public Integer getMsId() {
        return msId;
    }

    public void setMsId(Integer msId) {
        this.msId = msId;
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
