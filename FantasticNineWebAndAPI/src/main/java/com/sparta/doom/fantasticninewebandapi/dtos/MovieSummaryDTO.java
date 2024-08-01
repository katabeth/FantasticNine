package com.sparta.doom.fantasticninewebandapi.dtos;

public class MovieSummaryDTO {

    private String id;
    private String title;
    private String poster;
    private String genres;
    private String[] languages;
    private String type;

    public MovieSummaryDTO(String id, String title, String poster,
                           String genres, String[] languages, String type) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.genres = genres;
        this.languages = languages;
        this.type = type;
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

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}