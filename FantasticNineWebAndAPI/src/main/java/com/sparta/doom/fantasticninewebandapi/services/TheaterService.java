package com.sparta.doom.fantasticninewebandapi.services;

import com.sparta.doom.fantasticninewebandapi.models.theater.TheaterDoc;
import com.sparta.doom.fantasticninewebandapi.repositories.TheaterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TheaterService {

    private final TheaterRepository theaterRepository;

    @Autowired
    public TheaterService(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    public List<TheaterDoc> getAllTheaters() {
        return theaterRepository.findAll();
    }

    public TheaterDoc getTheaterByTheaterId(int theaterId) {
        Optional<TheaterDoc> theater = theaterRepository.findTheaterModelByTheaterId(theaterId);
        if (!theater.isPresent()) {
            throw new ResourceAccessException("Theater with id " + theaterId + " not found");
        }

        return theater.get();
    }

    public List<TheaterDoc> getTheatersByCityName(String cityName) {
        String formattedCityName = capitalizeEachWord(cityName);
        return theaterRepository.getTheaterDocsByLocation_Address_City(formattedCityName);
    }

    private String capitalizeEachWord(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            return cityName;
        }

        return java.util.Arrays.stream(cityName.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public void deleteTheaterByTheaterId(int theaterId) {
        theaterRepository.deleteTheaterDocByTheaterId(theaterId);
        if (theaterRepository.findTheaterModelByTheaterId(theaterId).isPresent()) {
            throw new ResourceAccessException("Theater with id " + theaterId + " unable to delete");
        }
    }

    public void createTheater(TheaterDoc theaterDoc) {
        System.out.println("Attempting to save theater with ID: " + theaterDoc.getTheaterId());
        try {
            theaterRepository.save(theaterDoc);
            System.out.println("Theater saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error saving theater: " + e.getMessage());
            throw new RuntimeException("Failed to save theater", e);
        }
    }

    public TheaterDoc updateTheater(TheaterDoc theaterDoc) {
        Optional<TheaterDoc> foundTheater = theaterRepository.findTheaterModelByTheaterId(theaterDoc.getTheaterId());
        if (!foundTheater.isPresent()) {
            throw new ResourceAccessException("Theater with id " + theaterDoc.getId() + " does not exist. Unable to update.");
        }
        TheaterDoc toReturn = theaterRepository.save(theaterDoc);
        return toReturn;
    }



}
