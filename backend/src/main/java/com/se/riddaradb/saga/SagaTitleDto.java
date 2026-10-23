package com.se.riddaradb.saga;

public class SagaTitleDto {
    private Integer id;
    private String title;

    public SagaTitleDto(Integer id, String title) {
        this.id = id;
        this.title = title;
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
}
