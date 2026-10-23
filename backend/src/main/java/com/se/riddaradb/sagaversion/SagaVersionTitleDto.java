package com.se.riddaradb.sagaversion;

public class SagaVersionTitleDto {
    private Integer id;
    private Integer sagaId;
    private String title;

    public SagaVersionTitleDto(int id, int sagaId, String title) {
        this.id = id;
        this.sagaId = sagaId;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSagaId() {
        return sagaId;
    }

    public void setSagaId(Integer sagaId) {
        this.sagaId = sagaId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
