package com.se.riddaradb.text;

public class TextDto {

    public TextDto() {
    }

    public TextDto(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
