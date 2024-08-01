package com.sparta.doom.fantasticninewebandapi.controllers.web;

import com.sparta.doom.fantasticninewebandapi.models.CommentDoc;
import com.sparta.doom.fantasticninewebandapi.models.MovieDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
public class CommentsWebController {

    private WebClient webClient;

    @Autowired
    public CommentsWebController(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostMapping("/comment/new")
    public String createComment(@ModelAttribute CommentDoc comment, Model model) {

        comment.setMovieId("573a139ff29313caabd015b9");

        webClient.post().uri(uriBuilder -> uriBuilder.path("api/movies/{movies}/comments/create").build(comment.getMovieId()))
                .bodyValue(comment)
                .retrieve()
                .bodyToMono(CommentDoc.class)
                .block();
        model.addAttribute("comment", comment);
        return "redirect:/movie/" +comment.getMovieId();
    }
}
