package com.sparta.doom.fantasticninewebandapi.dtos;

public class MoviesImdbRatingsDTO {
    private String movieId;
    private String title;
    private Integer id;
    private Double rating;
    private Integer votes;

    public MoviesImdbRatingsDTO() {
    }

    public MoviesImdbRatingsDTO(String movieId, String title, Integer id, Double rating, Integer votes) {
        this.movieId = movieId;
        this.title = title;
        this.id = id;
        this.rating = rating;
        this.votes = votes;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }
}