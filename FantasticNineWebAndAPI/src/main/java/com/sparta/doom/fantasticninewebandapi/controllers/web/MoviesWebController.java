
package com.sparta.doom.fantasticninewebandapi.controllers.web;

import com.sparta.doom.fantasticninewebandapi.models.CommentDoc;
import com.sparta.doom.fantasticninewebandapi.models.MovieDoc;
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

    //    @Value("${key}")
    private String key;

    public MoviesWebController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping
    public String getMovies(Model model) {
        ResponseEntity<List<MovieDoc>> moviesResponse = webClient
                .get()
                .uri("/api/movies")
                .header("DOOM-API-KEY", key)
                .retrieve()
                .toEntityList(MovieDoc.class)
                .block();

        ArrayList<MovieDoc> moviesList = new ArrayList<>();
        if (moviesResponse.hasBody()) {
            for (int i = 0; i<10; i++) {
                moviesList.add(moviesResponse.getBody().get(i));
            }
        }

        model.addAttribute("movies", moviesList);
        return "movies/movies";
    }

//    @GetMapping
//    public String getTheatres(
//            @RequestParam(defaultValue = "0") int page,
//            Model model) {
//        List<TheaterDoc> theaters = webClient
//                .get()
//                .uri("/api/theaters")
//                .header("DOOM-API-KEY", key)
//                .retrieve()
//                .bodyToFlux(TheaterDoc.class)
//                .collectList()
//                .block();
//
//        int start = page * PAGE_SIZE;
//        int end = Math.min(start + PAGE_SIZE, theaters.size());
//
//        List<TheaterDoc> paginatedTheaters = theaters.subList(start, end);
//
//        model.addAttribute("theaters", paginatedTheaters);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", (int) Math.ceil((double) theaters.size() / PAGE_SIZE));
//        return "theaters/theaters";
//    }

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
    public String createMoviePost(@ModelAttribute MovieDoc moviesModel, Model model) {
        webClient.post()
                .uri("/api/movies/create")
                .header("DOOM-API-KEY", key)
                .bodyValue(moviesModel);
        model.addAttribute("movie", moviesModel);
        return "redirect:/movies/" + moviesModel.getId();
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