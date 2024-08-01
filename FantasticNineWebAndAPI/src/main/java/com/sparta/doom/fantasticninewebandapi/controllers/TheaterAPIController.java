package com.sparta.doom.fantasticninewebandapi.controllers;

import com.sparta.doom.fantasticninewebandapi.models.theater.TheaterDoc;
import com.sparta.doom.fantasticninewebandapi.services.TheaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TheaterAPIController {

    private final TheaterService theaterService;

    @Autowired
    public TheaterAPIController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @GetMapping("/theaters")
    public ResponseEntity<List<TheaterDoc>> getTheaters() {
        List<TheaterDoc> theaters = theaterService.getAllTheaters();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(theaters);
    }

    @GetMapping("/theaters/cities")
    public ResponseEntity<List<TheaterDoc>> getTheatersByCityName(@RequestParam String cityName) {
        List<TheaterDoc> theaters = theaterService.getTheatersByCityName(cityName);
        return new ResponseEntity<>(theaters, HttpStatus.OK);
    }

    @GetMapping("/theaters/{theaterId}")
    public ResponseEntity<TheaterDoc> getTheaterByTheaterId(@PathVariable Integer theaterId) {
        TheaterDoc theater = theaterService.getTheaterByTheaterId(theaterId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(theater);
    }

    @DeleteMapping("/theaters/{theaterId}")
    public ResponseEntity<HttpStatus> deleteTheaterByTheaterId(@PathVariable Integer theaterId) {
        System.out.println("Received request to delete theater with ID: " + theaterId);
        theaterService.deleteTheaterByTheaterId(theaterId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/theaters")
    public ResponseEntity<HttpStatus> createTheater(@RequestBody TheaterDoc theater) {
        theaterService.createTheater(theater);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/theaters")
    public ResponseEntity<TheaterDoc> updateTheater(@RequestBody TheaterDoc theater) {
        TheaterDoc updatedTheater = theaterService.updateTheater(theater);
        return new ResponseEntity<>(updatedTheater, HttpStatus.OK);
    }
}