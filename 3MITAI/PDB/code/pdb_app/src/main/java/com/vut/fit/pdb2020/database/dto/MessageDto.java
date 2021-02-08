package com.vut.fit.pdb2020.database.dto;

import java.time.Instant;

public class MessageDto {

    private String content;

    private String author;

    private Instant time;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
