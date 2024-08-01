package com.sparta.doom.fantasticninewebandapi.controllers.web;

import com.sparta.doom.fantasticninewebandapi.models.CommentDoc;
import com.sparta.doom.fantasticninewebandapi.models.MovieDoc;
import com.sparta.doom.fantasticninewebandapi.services.MoviesService;
import com.sparta.doom.fantasticninewebandapi.models.UserDoc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import com.sparta.doom.fantasticninewebandapi.models.theater.TheaterDoc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/movies")
public class MoviesWebController {

    public final WebClient webClient;
    private final MoviesService moviesService;

//    @Value("${key}")
    private String key;

    public MoviesWebController(WebClient webClient, MoviesService moviesService) {
        this.webClient = webClient;
        this.moviesService = moviesService;
    }

    @GetMapping
    public String getMovies(Model model) {
        return "redirect:/movies/pages";
    }

    @GetMapping("/pages")
    public String getMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size,
            Model model) {
        ResponseEntity<List<MovieDoc>> moviesResponse = webClient
                .get()
                .uri("/api/movies/pages?page=" + page + "&size=" + size)
                .header("DOOM-API-KEY", key)
                .retrieve()
                .toEntityList(MovieDoc.class)
                .block();

        long totalPages = moviesService.getNumberOfMovies() / size;

        model.addAttribute("movies", moviesResponse.getBody());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("search", false);
        return "movies/movies";
    }

    @GetMapping("/search")
    public String searchMovies(@RequestParam String query, Model model) {
        ResponseEntity<List<MovieDoc>> moviesResponse = webClient
                .get()
                .uri("/api/movies/search/" + query)
                .header("DOOM-API-KEY", key)
                .retrieve()
                .toEntityList(MovieDoc.class)
                .block();

        List<MovieDoc> moviesList = new ArrayList<>();
        if (moviesResponse.hasBody()) {
            moviesList = moviesResponse.getBody().stream().limit(10).collect(Collectors.toList());
        }

        model.addAttribute("movies", moviesList);
        model.addAttribute("search", true);
        return "movies/movies";
    }

    @GetMapping("/{id}")
    public String getMovieDetails(@PathVariable String id, Model model) {
        MovieDoc movie = webClient
                .get()
                .uri("/api/movies/" + id)
                .header("DOOM-API-KEY", key)
                .retrieve()
                .bodyToMono(MovieDoc.class)
                .block();

        List<CommentDoc> comments = fetchComments(movie.getId(),0);
        List<CommentDoc> returnComments = new ArrayList<>();

        for(CommentDoc comment : comments) {
            String emailAddress = comment.getEmail();
            UserDoc user = webClient.get().uri("/api/users/email/"+emailAddress)
                    .retrieve().bodyToMono(UserDoc.class).block();
            comment.setEmail(user.getId());
            returnComments.add(comment);
        }

        model.addAttribute("comments", returnComments);
        model.addAttribute("movie", movie);
        model.addAttribute("comment", new CommentDoc());
        return "movies/movies_details";
    }

    @GetMapping("/create")
    public String createMovie() {
        return "movies/movies_create";
    }

    @PostMapping("/create")
    public String createMoviePost(@ModelAttribute MovieDoc movieDoc, Model model) {
        webClient.post()
                .uri("/api/movies/create")
                .header("DOOM-API-KEY", key)
                .bodyValue(movieDoc);
        model.addAttribute("movie", movieDoc);
        return "redirect:/movies/" + movieDoc.getId();
    }

    @PostMapping("/update/{id}")
    public String updateMovie(@PathVariable String id, @RequestBody MovieDoc moviesModel, Model model) {
        webClient.patch()
                .uri("/api/movies/update" + id)
                .header("DOOM-API-KEY", key)
                .bodyValue(moviesModel);
        model.addAttribute("movie", moviesModel);
        return "redirect:/movies/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable String id) {
        webClient.delete()
                .uri("/api/movies/delete" + id)
                .header("DOOM-API-KEY", key);
        return "redirect:/movies";
    }

    private List<CommentDoc> fetchComments(String movieId, int page) {

        Mono<PagedModel<CommentDoc>> commentsMono = webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/api/movies/{movieId}/comments")
                        .queryParam("page", page)
                        .queryParam("size",10)
                        .build(movieId))
                .retrieve().bodyToMono(new ParameterizedTypeReference<PagedModel<CommentDoc>>() {});
        PagedModel<CommentDoc> comments = commentsMono.block();
        if (comments != null) {
            return new ArrayList<>(comments.getContent());
        }
        return new ArrayList<>();
    }
}