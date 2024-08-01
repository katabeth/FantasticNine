package com.sparta.doom.fantasticninewebandapi.services;

import com.sparta.doom.fantasticninewebandapi.exceptions.CommentNotFoundException;
import com.sparta.doom.fantasticninewebandapi.models.CommentDoc;
import com.sparta.doom.fantasticninewebandapi.repositories.CommentsRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommentsService {

    private final CommentsRepository commentsRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    public List<CommentDoc> getAllComments() {
        return commentsRepository.findAll();
    }

    public CommentDoc getCommentById(String id) {
        return commentsRepository.findById(id).orElse(null);
        //orElseThrow(()-> new CommentNotFoundException("comment not found with id: " + id));
    }

    public Page<CommentDoc> getCommentsByName(String name, Pageable pageable) {
        return commentsRepository.findByNameContainingIgnoreCase(name.replaceAll("-", " "), pageable);
    }

    public Page<CommentDoc> getCommentsByEmailAddress(String email, Pageable pageable) {
        return commentsRepository.findByEmailContainingIgnoreCase(email, pageable);
    }

    public List<CommentDoc> getCommentsByName(String name){
        List<CommentDoc> commentDocList = new ArrayList<>();
        for(CommentDoc comment : commentsRepository.findAll()){
            String commentName = comment.getName();
            commentName = commentName.replaceAll(" ", "-");
            if(commentName.equals(name)){
                commentDocList.add(comment);
            }
        }
        return commentDocList;
    }

    public Page<CommentDoc> getCommentsByMovie(String movieId, Pageable pageable) {
        return commentsRepository.findByMovieId(movieId,pageable);
    }

    public List<CommentDoc> getCommentsByUsernameAndMovie(String name, List<CommentDoc> comments) {
        List<CommentDoc> commentDocList = new ArrayList<>();
        for(CommentDoc comment : comments){
            String commentName = comment.getName();
            commentName = commentName.replaceAll(" ", "-");
            if(commentName.equals(name)){
                commentDocList.add(comment);
            }
        }
        return commentDocList;
    }

    public List<CommentDoc> getCommentsByMovieId(String id){
        List<CommentDoc> commentDocList = new ArrayList<>();
        for(CommentDoc comment : commentsRepository.findAll()){
            if (comment.getMovieId().equals(id)){
                commentDocList.add(comment);
            }
        }
        return commentDocList;
    }

    public List<CommentDoc> getCommentsByDateRange(String startDateString, String endDateString, List<CommentDoc> comments){
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<CommentDoc> commentDocList = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(startDateString, inputFormatter);
        LocalDate endDate = LocalDate.parse(endDateString, inputFormatter);

        for(CommentDoc comment : comments){
            Date commentDate = comment.getDate();
            SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDateString = targetFormat.format(commentDate);
            LocalDate formattedDate = LocalDate.parse(formattedDateString, inputFormatter);
            if(formattedDate.isAfter(startDate) && formattedDate.isBefore(endDate)){
                commentDocList.add(comment);
            }
        }
        return commentDocList;
    }

    public CommentDoc createComment(CommentDoc comment) {
        comment = censorBadText(comment);
        commentsRepository.save(comment);
        return comment;

    }

    public void deleteComment(String id) {
        if(commentsRepository.existsById(id)){
            commentsRepository.deleteById(id);
        }
        else throw new CommentNotFoundException("comment not found with id: " + id);
    }

    public CommentDoc updateComment(CommentDoc commentUpdate) {

        if(commentsRepository.existsById(commentUpdate.getId())){
            for(CommentDoc comment : commentsRepository.findAll()){
                if(comment.getId().equals(commentUpdate.getId())){
                    comment = censorBadText(commentUpdate);
                    commentsRepository.save(comment);
                    return comment;
                }
            }
        }
        else {
            throw new CommentNotFoundException("comment not found with id: " + commentUpdate.getId());
        }
        return null;
    }

    public CommentDoc censorBadText(CommentDoc comment) {
        String[] censoredText = {"fuck","shit","cunt","arse","ass","shite","sh1t","5hit","5h1t","ar5e","a55","a5s","as5","wank"};
        String patternString = "(" + String.join("|", censoredText) + ")";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(comment.getText());
        StringBuilder censoredComment = new StringBuilder();

        while (matcher.find()) {
            String replacement = "*".repeat(matcher.group().length());
            matcher.appendReplacement(censoredComment, replacement);
        }

        matcher.appendTail(censoredComment);
        comment.setText(censoredComment.toString());
        return comment;

    }
}
