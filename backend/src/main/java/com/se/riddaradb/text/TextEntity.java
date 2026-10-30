package com.se.riddaradb.text;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "text")
public class TextEntity {

    public TextEntity(){}

    @Id
    private Integer id = 1;
    @Column(columnDefinition = "TEXT")
    private String homeText = "";
    @Column(columnDefinition = "TEXT")
    private String aboutText = "";

    public Integer getId(){
        return id;
    }

    public String getHomeText() {
        return homeText;
    }

    public void setHomeText(String homeText) {
        this.homeText = homeText;
    }

    public String getAboutText() {
        return aboutText;
    }

    public void setAboutText(String aboutText) {
        this.aboutText = aboutText;
    }
}
