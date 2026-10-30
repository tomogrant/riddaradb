package com.se.riddaradb.saga;

public class SagaTitleDto {
    private Integer id;
    private String title;
    private Boolean published;

    public SagaTitleDto(Integer id, String title, Boolean published) {
        this.id = id;
        this.title = title;
        this.published = published;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }
}
