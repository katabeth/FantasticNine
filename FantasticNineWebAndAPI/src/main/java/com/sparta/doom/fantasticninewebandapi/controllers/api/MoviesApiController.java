package com.sparta.doom.fantasticninewebandapi.controllers.api;

import com.sparta.doom.fantasticninewebandapi.dtos.MovieSummaryDTO;
import com.sparta.doom.fantasticninewebandapi.dtos.*;
import com.sparta.doom.fantasticninewebandapi.models.MovieDoc;
import com.sparta.doom.fantasticninewebandapi.services.MoviesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class MoviesApiController {

    private final MoviesService moviesService;

    public MoviesApiController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    @GetMapping("/movies")
    public ResponseEntity<List<MovieDoc>> getAllMovies() {
        List<MovieDoc> movies = moviesService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/movies/pages") // also add ?page=2&size=50 at the end of your endpoint. e.g. http://localhost:8080/api/movies/pages?page=2&size=50
    public ResponseEntity<List<MovieDoc>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<MovieDoc> movies = moviesService.getAllMovies(page, size);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<MovieDoc> getMovieById(@PathVariable String id) {
        Optional<MovieDoc> movie = moviesService.getMovieById(id);
        return movie.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/movies/title/{title}")
    public ResponseEntity<MovieDoc> getMovieByTitle(@PathVariable String title) {
        Optional<MovieDoc> movie = moviesService.getMovieByTitle(title);
        return movie.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/movies/genres")
    public ResponseEntity<List<String>> getAllGenres() {
        List<String> genres = moviesService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @PostMapping("/movies")
    public ResponseEntity<MovieDoc> createMovie(@RequestBody MovieDoc movieDoc) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            MovieDoc createdMovie = moviesService.createMovie(movieDoc);
            return ResponseEntity.status(201).body(createdMovie);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/movies/{id}")
    public ResponseEntity<MovieDoc> updateMovie(@PathVariable String id, @RequestBody MovieDoc movieDoc) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            Optional<MovieDoc> updatedMovie = moviesService.updateMovie(id, movieDoc);
            return updatedMovie.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/movies/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        if (userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            boolean isDeleted = moviesService.deleteMovie(id);
            return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/movies/genres/{genre}")
    public ResponseEntity<List<MovieDoc>> getMoviesByGenre(@PathVariable String genre) {
        List<MovieDoc> movies = moviesService.getMoviesByGenre(genre);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/movies/search/{title}")
    public ResponseEntity<List<MovieDoc>> getMoviesByPartialTitle(@PathVariable String title) {
        List<MovieDoc> movieDocs = moviesService.getMoviesByPartialTitle(title);
        return ResponseEntity.ok(movieDocs);
    }

    @GetMapping("/movies/top-rated")
    public ResponseEntity<List<MovieDoc>> getTop10ByImdbRating() {
        List<MovieDoc> topMovies = moviesService.getTop10ByImdbRating();
        return ResponseEntity.ok(topMovies);
    }

    @GetMapping("/movies/top-rated-movies")
    public ResponseEntity<List<MovieDoc>> getTop10MoviesByImdbRating() {
        List<MovieDoc> topMovies = moviesService.getTop10MoviesByImdbRating();
        return ResponseEntity.ok(topMovies);
    }

    @GetMapping("/series/top-rated")
    public ResponseEntity<List<MovieDoc>> getTop10SeriesByImdbRating() {
        List<MovieDoc> topSeries = moviesService.getTop10SeriesByImdbRating();
        return ResponseEntity.ok(topSeries);
    }

    @GetMapping("/series")
    public ResponseEntity<List<MovieDoc>> getAllSeries() {
        List<MovieDoc> series = moviesService.getAllSeries();
        return ResponseEntity.ok(series);
    }

    @GetMapping("/movies/summary/{id}")
    public ResponseEntity<MovieSummaryDTO> getMovieSummary(@PathVariable String id) {
        Optional<MovieSummaryDTO> movieSummaryDTO = moviesService.getMovieSummary(id);
        return movieSummaryDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/movies/details/{id}")
    public ResponseEntity<MovieDetailsDTO> getMovieDetails(@PathVariable String id) {
        return moviesService.getMovieDetails(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/movies/awards/{id}")
    public ResponseEntity<MovieAwardsDTO> getMovieAwards(@PathVariable String id) {
        Optional<MovieAwardsDTO> movieAwardsDTO = moviesService.getMovieAwards(id);
        return movieAwardsDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/movies/imdb-ratings/{id}")
    public ResponseEntity<MoviesImdbRatingsDTO> getImdbRatings(@PathVariable("id") String id) {
        Optional<MoviesImdbRatingsDTO> imdbDTO = moviesService.getImdbRatings(id);
        return imdbDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}