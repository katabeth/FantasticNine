package com.sparta.doom.fantasticninewebandapi.controllers.web;

import com.sparta.doom.fantasticninewebandapi.models.CommentDoc;
import com.sparta.doom.fantasticninewebandapi.models.MovieDoc;
import com.sparta.doom.fantasticninewebandapi.models.UserDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserPageWebController {

    private WebClient webClient;

    @Autowired
    public UserPageWebController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/my_account")
    public String userPage(Model model)
    {
        return "users/my_account";
    }

    @PostMapping("/my_account")
    public String userUpdatePage(Model model, UserDoc userDoc) {
        return "users/my_account";
    }

    @GetMapping("/user/{userId}")
    public String userPage(@PathVariable("userId") String userId, Model model) {
        UserDoc user = webClient.get().uri("/api/users/{userId}", userId).retrieve().bodyToMono(UserDoc.class).block();
        int pageToGet = 0;
        List<CommentDoc> commentDocList = fetchComments(user.getEmail(),pageToGet);
        List<CommentDoc> withMovieTitle = new ArrayList<>();

        for(CommentDoc commentDoc : commentDocList) {
            MovieDoc movie = webClient.get().uri("/api/movies/" + commentDoc.getMovieId().toString()).retrieve().bodyToMono(MovieDoc.class).block();
            CommentDoc commentWithTitle = commentDoc;
            commentWithTitle.setName(movie.getTitle());
            withMovieTitle.add(commentWithTitle);
        }

        model.addAttribute("userDocName", user.getName());
        model.addAttribute("userDocEmail", user.getEmail());
        model.addAttribute("commentDocList", withMovieTitle);

//        model.addAttribute("comments", comments);

        //todo favourite films?
        // friends?
        return "users/user_page";
    }

    private List<CommentDoc> fetchComments(String email, int page) {
        Mono<PagedModel<CommentDoc>> commentsMono = webClient.get().uri(uriBuilder -> uriBuilder
                .path("/api/users/email/{email}/comments")
                .queryParam("page", page)
                .queryParam("size",10)
                .build(email))
        .retrieve().bodyToMono(new ParameterizedTypeReference<PagedModel<CommentDoc>>() {});
        PagedModel<CommentDoc> comments = commentsMono.block();

        if (comments != null) {
            return new ArrayList<>(comments.getContent());
        }
        return new ArrayList<>();
    }
}
