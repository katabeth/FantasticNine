package com.sparta.doom.fantasticninewebandapi.models.theater;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "geo")
public class Geo {
    private String type = "Point";
    private double[] coordinates;

    // Getters and Setters
    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public double[] getCoordinates() { return coordinates; }

    public void setCoordinates(double[] coordinates) { this.coordinates = coordinates; }
}
