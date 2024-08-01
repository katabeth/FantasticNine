package com.sparta.doom.fantasticninewebandapi.repositories;

import com.sparta.doom.fantasticninewebandapi.models.theater.TheaterDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TheaterRepository extends MongoRepository<TheaterDoc, String> {

    Optional<TheaterDoc> findTheaterModelByTheaterId(Integer theaterId);

    void deleteTheaterDocByTheaterId(Integer theaterId);

    List<TheaterDoc> getTheaterDocsByLocation_Address_City(String cityName);
}