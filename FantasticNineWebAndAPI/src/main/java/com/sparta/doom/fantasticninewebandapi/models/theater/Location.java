package com.sparta.doom.fantasticninewebandapi.models.theater;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "location")
public class Location {
    private Address address;
    private Geo geo;

    public Address getAddress() { return address; }

    public void setAddress(Address address) { this.address = address; }

    public Geo getGeo() { return geo; }

    public void setGeo(Geo geo) { this.geo = geo; }
}
