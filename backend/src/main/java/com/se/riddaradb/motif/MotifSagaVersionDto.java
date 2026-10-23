package com.se.riddaradb.motif;

public class MotifSagaVersionDto {
    private Integer sagaVersionId;
    private String pageChapterNumber;
    private Boolean inBoberg;

    public MotifSagaVersionDto(Integer sagaVersionId, String pageChapterNumber, Boolean inBoberg) {
        this.sagaVersionId = sagaVersionId;
        this.pageChapterNumber = pageChapterNumber;
        this.inBoberg = inBoberg;
    }

    public Integer getSagaVersionId() {
        return sagaVersionId;
    }

    public void setSagaVersionId(Integer sagaVersionId) {
        this.sagaVersionId = sagaVersionId;
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
