package com.sparta.doom.fantasticninewebandapi.models.theater;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "address")
public class Address {
    private String street1;
    private String city;
    private String state;
    private String zipcode;

    public String getStreet1() { return street1; }

    public void setStreet1(String street1) { this.street1 = street1; }

    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public String getZipcode() { return zipcode; }

    public void setZipcode(String zipcode) { this.zipcode = zipcode; }
}
