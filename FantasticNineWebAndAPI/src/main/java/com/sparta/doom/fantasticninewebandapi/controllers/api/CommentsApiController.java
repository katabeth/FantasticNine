package com.sparta.doom.fantasticninewebandapi.controllers.api;

import com.sparta.doom.fantasticninewebandapi.models.CommentDoc;
import com.sparta.doom.fantasticninewebandapi.services.CommentsService;
import com.sparta.doom.fantasticninewebandapi.services.SecurityService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/")
public class CommentsApiController {

    private final CommentsService commentsService;
    private final SecurityService securityService;

    @Autowired
    public CommentsApiController(CommentsService commentsService, SecurityService securityService) {
        this.commentsService = commentsService;
        this.securityService = securityService;
    }

//    @GetMapping("/movies/{movie}/comments")
//    public ResponseEntity<CollectionModel<EntityModel<CommentDoc>>> getComments(@PathVariable("movie") ObjectId movie) {
//        List<EntityModel<CommentDoc>> comments = commentsService.getCommentsByMovieId(movie).stream().map(this::commentEntityModel).toList();
//        return new ResponseEntity<>(CollectionModel.of(comments), HttpStatus.OK);
//    }

    @GetMapping("/movies/{movie}/comments")
    public ResponseEntity<PagedModel<CommentDoc>> getCommentsPaged(@PathVariable("movie") String movieId
            ,@RequestParam(value = "page", defaultValue = "0") int page
            ,@RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<CommentDoc> commentsPage = commentsService.getCommentsByMovie(movieId, pageable);
        PagedModel<CommentDoc> pagedModel = PagedModel.of(commentsPage.getContent(), new PagedModel.PageMetadata(commentsPage.getSize(), commentsPage.getNumber(), commentsPage.getTotalElements()));
        return ResponseEntity.ok(pagedModel);
    }


    @GetMapping("/movies/{movie}/comments/id/{commentId}")
    public ResponseEntity<EntityModel<CommentDoc>> getComment(@PathVariable("movie") String movie, @PathVariable("commentId") String commentId) {
        CommentDoc comment = commentsService.getCommentById(commentId);
        if (comment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(!comment.getMovieId().equals(movie)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        EntityModel<CommentDoc> commentDocEntityModel = EntityModel.of(comment
                ,linkTo(methodOn(MoviesApiController.class).getMovieById(comment.getMovieId())).withRel("movie")
                ,linkTo(methodOn(CommentsApiController.class).getComment(comment.getMovieId(),comment.getId())).withSelfRel());
        return new ResponseEntity<>(commentDocEntityModel, HttpStatus.OK);
    }

    @GetMapping("/movies/{movie}/comments/dates/{date1}/{date2}")
    public ResponseEntity<CollectionModel<EntityModel<CommentDoc>>> getCommentsByMovieAndDate(@PathVariable("movie") String movie
            , @PathVariable String date1, @PathVariable String date2) {
        if(date1 == null || date2 == null || LocalDate.parse(date1).isAfter(LocalDate.parse(date2)) || date1.length()!=10 || date2.length()!=10) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<CommentDoc> comments = commentsService.getCommentsByMovieId(movie);
        List<EntityModel<CommentDoc>> commentsOutput = commentsService.getCommentsByDateRange(date1,date2,comments).stream().map(this::commentEntityModel).toList();
        if(commentsOutput.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(CollectionModel.of(commentsOutput), HttpStatus.OK);
        }
    }

    @GetMapping("/movies/{movie}/comments/name/{username}")
    public ResponseEntity<CollectionModel<EntityModel<CommentDoc>>> getCommentsByMovieAndUsername(@PathVariable("movie") String movie, @PathVariable("username") String username) {
        List<CommentDoc> comments = commentsService.getCommentsByMovieId(movie);
        List<EntityModel<CommentDoc>> commentDocList = commentsService.getCommentsByUsernameAndMovie(username, comments).stream().map(this::commentEntityModel).toList();

        if(commentDocList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(CollectionModel.of(commentDocList), HttpStatus.OK);
    }

    @GetMapping("/users/name/{username}/comments")
    public ResponseEntity<PagedModel<CommentDoc>> getCommentsByUsername(@PathVariable("username") String username
            ,@RequestParam(value = "page", defaultValue = "0") int page
            ,@RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<CommentDoc> commentsPage = commentsService.getCommentsByName(username, pageable);
        PagedModel<CommentDoc> pagedModel = PagedModel.of(commentsPage.getContent(), new PagedModel.PageMetadata(commentsPage.getSize(), commentsPage.getNumber(), commentsPage.getTotalElements()));
        return ResponseEntity.ok(pagedModel);
    }
    @GetMapping("/users/email/{email}/comments")
    public ResponseEntity<PagedModel<CommentDoc>> getCommentsByEmail(@PathVariable("email") String email, Pageable pageable) {
        Page<CommentDoc> commentsPage = commentsService.getCommentsByEmailAddress(email, pageable);
        PagedModel<CommentDoc> pagedModel = PagedModel.of(commentsPage.getContent(), new PagedModel.PageMetadata(commentsPage.getSize(), commentsPage.getNumber(), commentsPage.getTotalElements()));
        return ResponseEntity.ok(pagedModel);
    }

//    @GetMapping("/users/{username}/comments")
//    public ResponseEntity<CollectionModel<CommentDoc>> getCommentsByUsername(@PathVariable("username") String username) {
//        //regex matches to catch bad usernames?
//        List<CommentDoc> comments = commentsService.getCommentsByName(username);
//        if(comments.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//        return new ResponseEntity<>(CollectionModel.of(comments), HttpStatus.OK);
//    }

    @GetMapping("/users/{username}/comments/dates/{date1}/{date2}")
    public ResponseEntity<CollectionModel<EntityModel<CommentDoc>>> getCommentsByUsernameAndDateRange(@PathVariable("username") String username
            , @PathVariable("date1") String date1, @PathVariable("date2") String date2) {
        if(date1 == null || date2 == null || LocalDate.parse(date1).isAfter(LocalDate.parse(date2)) || date1.length()!=10 || date2.length()!=10) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<CommentDoc> comments = commentsService.getCommentsByName(username);
        List<EntityModel<CommentDoc>> commentsOutput = commentsService.getCommentsByDateRange(date1,date2,comments)
                .stream().map(this::commentEntityModel).toList();
        if(commentsOutput.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(CollectionModel.of(commentsOutput), HttpStatus.OK);
        }
    }

    @PostMapping("/movies/{movie}/comments/create")
    public ResponseEntity<EntityModel<CommentDoc>> createComment(@PathVariable("movie") String movieId, @RequestBody CommentDoc newComment) {
        if(!newComment.getMovieId().equals(movieId)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = formatter.format(new Date());
        Date date = formatter.parse(formattedDate, new ParsePosition(0));
        newComment.setDate(date);

        CommentDoc returnComment = commentsService.createComment(newComment);
        if(returnComment == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        URI location = URI.create("/api/movies/" + movieId + "/comments/id/" + newComment.getId());
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CommentsApiController.class).getComment(movieId,newComment.getId())).withSelfRel();
        return ResponseEntity.created(location).body(EntityModel.of(newComment).add(selfLink));
    }
    @PutMapping("/movies/{movie}/comments/id/{commentId}")
    public ResponseEntity<CommentDoc> updateComment(@RequestHeader(name = "DOOM-API-KEY") String key,@PathVariable("movie") String movieId, @PathVariable("commentId") String commentId, @RequestBody CommentDoc newComment) {

        CommentDoc oldComment = commentsService.getCommentById(commentId);
        if(oldComment == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(!oldComment.getMovieId().equals(movieId)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        commentsService.updateComment(newComment);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/movies/{movie}/comments/id/{commentId}")
    public ResponseEntity<CommentDoc> deleteComment(@RequestHeader(name = "DOOM-API-KEY") String key, @PathVariable("commentId") String commentId) {
        CommentDoc comment = commentsService.getCommentById(commentId);
        if(comment == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        commentsService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    private EntityModel<CommentDoc> commentEntityModel(CommentDoc comment) {
        Link movieLink = WebMvcLinkBuilder.linkTo(methodOn(MoviesApiController.class).getMovieById(comment.getMovieId().toString())).withRel("movie");
//        Link userLink = WebMvcLinkBuilder.linkTo(methodOn(UsersController.class).getUserByEmail(comment.getEmail())).withRel("user");
        Link selfLink = WebMvcLinkBuilder.linkTo(methodOn(CommentsApiController.class).getComment(comment.getMovieId(),comment.getId())).withSelfRel();
        return EntityModel.of(comment, /*userLink,*/ movieLink, selfLink);
    }
}
