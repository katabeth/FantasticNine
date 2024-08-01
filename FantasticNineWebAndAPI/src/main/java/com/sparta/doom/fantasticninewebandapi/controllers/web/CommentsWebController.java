package com.sparta.doom.fantasticninewebandapi.controllers.web;

import com.sparta.doom.fantasticninewebandapi.models.CommentDoc;
import com.sparta.doom.fantasticninewebandapi.models.MovieDoc;
import com.sparta.doom.fantasticninewebandapi.models.UserDoc;
import com.sparta.doom.fantasticninewebandapi.security.api.JwtUtils;
import com.sparta.doom.fantasticninewebandapi.security.api.TokenRequestFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
public class CommentsWebController {



    @Value("${jwt.auth}")
    private String AUTH_HEADER;

    private WebClient webClient;

    @Autowired
    public CommentsWebController(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostMapping("/comment/new")
    public String createComment(@ModelAttribute CommentDoc comment, Model model) {

        UserDoc user = webClient.get().uri(uriBuilder ->
                uriBuilder.path("/api/users/email/{email}")
                        .build(comment.getEmail()))
                .retrieve().bodyToMono(UserDoc.class).block();
        comment.setName(user.getName());
        CommentDoc newComment = webClient.post().uri(uriBuilder -> uriBuilder.path("api/movies/{movies}/comments/create").build(comment.getMovieId()))
                .bodyValue(comment)
                .retrieve()
                .bodyToMono(CommentDoc.class)
                .block();
        model.addAttribute("comment", newComment);
        return "redirect:/movies/" +comment.getMovieId();
    }
    @PostMapping("/comment/delete")
    public String deleteComment(@RequestParam String commentId, @RequestParam String movieId) {

        webClient.delete().uri(uriBuilder -> uriBuilder
                        .path("/api/comments/id/{commentId}")
                        .build(commentId))
                .retrieve()
                .bodyToMono(Void.class).block();
        return "redirect:/movies/"+movieId;
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (AUTH_HEADER.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
