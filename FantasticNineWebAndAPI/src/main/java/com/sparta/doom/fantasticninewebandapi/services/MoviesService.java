package com.sparta.doom.fantasticninewebandapi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.doom.fantasticninewebandapi.dtos.*;
import com.sparta.doom.fantasticninewebandapi.models.MovieDoc;
import com.sparta.doom.fantasticninewebandapi.models.movie.*;
import com.sparta.doom.fantasticninewebandapi.repositories.MoviesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MoviesService {

    private final MoviesRepository moviesRepository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper jacksonObjectMapper;

    @Autowired
    public MoviesService(MoviesRepository moviesRepository, MongoTemplate mongoTemplate, ObjectMapper jacksonObjectMapper) {
        this.moviesRepository = moviesRepository;
        this.mongoTemplate = mongoTemplate;
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    public List<MovieDoc> getAllMovies() {
        Query query = new Query();
        return mongoTemplate.find(query, MovieDoc.class);
    }

    public List<MovieDoc> getAllMovies(int page, int size) {
        Query query = new Query();
        int skip = page * size;
        query.skip(skip).limit(size);
        return mongoTemplate.find(query, MovieDoc.class);
    }

    public Optional<MovieDoc> getMovieById(String id) {
        Query query = new Query(Criteria.where("id").is(id));
        MovieDoc movieDoc = mongoTemplate.findOne(query, MovieDoc.class);
        return Optional.of(movieDoc);
    }

    public Optional<MovieDoc> getMovieByTitle(String title) {
        if (title == null || title.isEmpty()) {
            return Optional.empty();
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("title").regex(".*" + title + ".*", "i"));
        MovieDoc movieDoc = mongoTemplate.findOne(query, MovieDoc.class);
        return Optional.ofNullable(movieDoc);
    }

    public List<MovieDoc> getMoviesOnly() {
        return moviesRepository.findAll().stream()
                .filter(movieDoc -> "movie".equalsIgnoreCase(movieDoc.getType()))
                .collect(Collectors.toList());
    }

    public List<MovieDoc> getAllSeries() {
        return moviesRepository.findAll().stream()
                .filter(movieDoc -> "series".equalsIgnoreCase(movieDoc.getType()))
                .collect(Collectors.toList());
    }

    public List<MovieDoc> getMoviesByPartialTitle(String title) {
        if (title == null || title.isEmpty()) {
            return List.of();
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("title").regex(".*" + title + ".*", "i"));
        return mongoTemplate.find(query, MovieDoc.class);
    }

    public MovieDoc createMovie(MovieDoc movieDoc) {
        return moviesRepository.save(movieDoc);
    }

    public Optional<MovieDoc> updateMovie(String id, MovieDoc movieDoc) {
        if (moviesRepository.existsById(id)) {
            movieDoc.setId(id);
            return Optional.of(moviesRepository.save(movieDoc));
        } else {
            return Optional.empty();
        }
    }

    public List<String> getAllGenres() {
        return moviesRepository.findAll().stream()
                .filter(movieDoc -> movieDoc.getGenres() != null)
                .flatMap(movieDoc -> Arrays.stream(movieDoc.getGenres().split(",")))
                .map(String::trim)
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean deleteMovie(String id) {
        if (moviesRepository.existsById(id)) {
            moviesRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public List<MovieDoc> getMoviesByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return List.of();
        }
        String normalizedGenre = genre.trim().toLowerCase();
        Query query = new Query(Criteria.where("genres")
                .regex(".*\\b" + Pattern.quote(normalizedGenre) + "\\b.*", "i"));
        return mongoTemplate.find(query, MovieDoc.class);
    }

    private List<MovieDoc> getTop10ByImdbRatingAndType(String type) {
        Query query = new Query();
        query.addCriteria(Criteria.where("imdb.rating").ne(""));

        if (type != null) {
            query.addCriteria(Criteria.where("type").is(type));
        }
        query.with(Sort.by(Sort.Order.desc("imdb.rating")));
        List<MovieDoc> allItems = mongoTemplate.find(query.limit(20), MovieDoc.class);
        return allItems.stream()
                .filter(distinctByKey(MovieDoc::getTitle))
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<MovieDoc> getTop10ByImdbRating() {
        return getTop10ByImdbRatingAndType(null);
    }

    public List<MovieDoc> getTop10MoviesByImdbRating() {
        return getTop10ByImdbRatingAndType("movie");
    }

    public List<MovieDoc> getTop10SeriesByImdbRating() {
        return getTop10ByImdbRatingAndType("series");
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public Optional<MovieSummaryDTO> getMovieSummary(String id) {
        return moviesRepository.findById(id).map(movie ->
                new MovieSummaryDTO(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getPoster(),
                        movie.getGenres(),
                        movie.getLanguages(),
                        movie.getType()
                )
        );
    }

    public Optional<MovieDetailsDTO> getMovieDetails(String id) {
        return Optional.of(moviesRepository.findById(id).map(movie ->
                new MovieDetailsDTO(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getCast(),
                        movie.getCountries(),
                        movie.getDirectors(),
                        movie.getFullplot(),
                        movie.getPlot(),
                        movie.getRated(),
                        movie.getReleased(),
                        movie.getRuntime(),
                        movie.getWriters(),
                        movie.getYear(),
                        movie.getLastupdated()
                )
        ).orElseThrow());
    }

    public Optional<MovieAwardsDTO> getMovieAwards(String movieId) {
        return moviesRepository.findById(movieId).map(movie -> {
            String awardsJson = movie.getAwards();
            if (awardsJson != null && !awardsJson.isEmpty()) {
                try {
                    Awards awards = jacksonObjectMapper.readValue(awardsJson, Awards.class);
                    return new MovieAwardsDTO(movie.getId(), movie.getTitle(), awards.getNominations(), awards.getWins(), awards.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    public Optional<MoviesImdbRatingsDTO> getImdbRatings(String movieId) {
        return moviesRepository.findById(movieId).map(movie -> {
            String imdbJson = movie.getImdb();
            if (imdbJson != null && !imdbJson.isEmpty()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    MoviesImdbRatingsDTO moviesImdbRatingsDTO = objectMapper.readValue(imdbJson, MoviesImdbRatingsDTO.class);
                    return new MoviesImdbRatingsDTO(
                            movie.getId(),
                            movie.getTitle(),
                            moviesImdbRatingsDTO.getId(),
                            moviesImdbRatingsDTO.getRating(),
                            moviesImdbRatingsDTO.getVotes()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    public long getNumberOfMovies() {
        return moviesRepository.count();
    }

}
