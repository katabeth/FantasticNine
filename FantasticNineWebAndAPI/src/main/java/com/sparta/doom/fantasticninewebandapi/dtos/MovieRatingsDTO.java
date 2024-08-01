package com.sparta.doom.fantasticninewebandapi.dtos;

public class MovieRatingsDTO {
    private String id;
    private String title;
    private Integer imdbId;
    private Double imdbRating;
    private Integer imdbVotes;
    private String criticInfo;
    private String viewerInfo;
    private String tomatoesLastUpdated;
    private String tomatoesWebsite;

    public MovieRatingsDTO(String id, String title, Integer imdbId, Double imdbRating, Integer imdbVotes,
                           String criticInfo, String viewerInfo, String tomatoesLastUpdated, String tomatoesWebsite) {
        this.id = id;
        this.title = title;
        this.imdbId = imdbId;
        this.imdbRating = imdbRating;
        this.imdbVotes = imdbVotes;
        this.criticInfo = criticInfo;
        this.viewerInfo = viewerInfo;
        this.tomatoesLastUpdated = tomatoesLastUpdated;
        this.tomatoesWebsite = tomatoesWebsite;
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

    public Integer getImdbId() {
        return imdbId;
    }

    public void setImdbId(Integer imdbId) {
        this.imdbId = imdbId;
    }

    public Double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(Double imdbRating) {
        this.imdbRating = imdbRating;
    }

    public Integer getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(Integer imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public String getCriticInfo() {
        return criticInfo;
    }

    public void setCriticInfo(String criticInfo) {
        this.criticInfo = criticInfo;
    }

    public String getViewerInfo() {
        return viewerInfo;
    }

    public void setViewerInfo(String viewerInfo) {
        this.viewerInfo = viewerInfo;
    }

    public String getTomatoesLastUpdated() {
        return tomatoesLastUpdated;
    }

    public void setTomatoesLastUpdated(String tomatoesLastUpdated) {
        this.tomatoesLastUpdated = tomatoesLastUpdated;
    }

    public String getTomatoesWebsite() {
        return tomatoesWebsite;
    }

    public void setTomatoesWebsite(String tomatoesWebsite) {
        this.tomatoesWebsite = tomatoesWebsite;
    }
}