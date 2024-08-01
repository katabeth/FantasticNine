package com.sparta.doom.fantasticninewebandapi.dtos;

public class ViewerDTO {
    private Integer meter;
    private Integer numReviews;
    private Double rating;

    public ViewerDTO() {}

    public ViewerDTO(Integer meter, Integer numReviews, Double rating) {
        this.meter = meter;
        this.numReviews = numReviews;
        this.rating = rating;
    }

    public Integer getMeter() {
        return meter;
    }

    public void setMeter(Integer meter) {
        this.meter = meter;
    }

    public Integer getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(Integer numReviews) {
        this.numReviews = numReviews;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}