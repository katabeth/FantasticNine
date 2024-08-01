package com.sparta.doom.fantasticninewebandapi.dtos;

public class MovieAwardsDTO {
    private String id;
    private String title;
    private Integer nominations;
    private Integer wins;
    private String text;

    public MovieAwardsDTO(String id, String title, Integer nominations, Integer wins, String text) {
        this.id = id;
        this.title = title;
        this.wins = wins;
        this.nominations = nominations;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getNominations() {
        return nominations;
    }

    public void setNominations(Integer nominations) {
        this.nominations = nominations;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}