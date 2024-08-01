package com.sparta.doom.fantasticninewebandapi;

import com.sparta.doom.fantasticninewebandapi.models.CommentDoc;
import com.sparta.doom.fantasticninewebandapi.repositories.CommentsRepository;
import com.sparta.doom.fantasticninewebandapi.services.CommentsService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CommentDocServiceTests {

    @Mock
    CommentsRepository commentsRepository;

    @InjectMocks
    CommentsService service;

    private CommentDoc commentOne;
    private CommentDoc commentTwo;
    private CommentDoc commentThree;
    private ObjectId id;
    private ObjectId id2;
    private ObjectId movieId1;
    private ObjectId movieId2;
    private Date startDate;
    private Date endDate;

    @Autowired
    private CommentsService commentsService;

    @BeforeEach()
    public void setUp() {

        Calendar calendar = Calendar.getInstance();

        id = new ObjectId();
        movieId1 = new ObjectId();
        commentOne = new CommentDoc();
        commentOne.setId(id.toString());
        calendar.set(2001,Calendar.JANUARY,1,0,0,0);
        commentOne.setDate(calendar.getTime());
        commentOne.setEmail("test@test.com");
        commentOne.setMovieId(movieId1.toString());
        commentOne.setText("This is some sample text");
        commentOne.setName("Testing Account");

        movieId2 = new ObjectId();
        id2 = new ObjectId();
        commentTwo = new CommentDoc();
        commentTwo.setId(id2.toString());
        calendar.set(2002,Calendar.JANUARY,1,0,0,0);
        commentTwo.setDate(calendar.getTime());
        commentTwo.setEmail("test2@test2.com");
        commentTwo.setMovieId(movieId2.toString());
        commentTwo.setText("This is some sample text again");
        commentTwo.setName("Testing Account2");

        commentThree = new CommentDoc();
        commentThree.setId(id.toString());
        calendar.set(2001,Calendar.JANUARY,1,0,0,0);
        commentThree.setDate(calendar.getTime());
        commentThree.setEmail("test@test.com");
        commentThree.setMovieId(movieId1.toString());
        commentThree.setText("This is some sample text that has been changed");
        commentThree.setName("Testing Account");




        calendar.set(2001,Calendar.MAY,1,0,0,0);
        startDate = calendar.getTime();
        calendar.set(2002,Calendar.MAY,1,0,0,0);
        endDate = calendar.getTime();
    }

    @Test
    public void findAllComments(){
        int expected = 2;
        List<CommentDoc> commentList = new ArrayList<>();
        commentList.add(commentOne);
        commentList.add(commentTwo);
        when(commentsRepository.findAll()).thenReturn(commentList);
        List<CommentDoc> actual = service.getAllComments();
        Assertions.assertEquals(expected, actual.size());
    }

    @Test
    public void findCommentById(){
        CommentDoc expected = commentOne;
        when(commentsRepository.findById(id.toString())).thenReturn(Optional.of(commentOne));
        CommentDoc actual = service.getCommentById(id.toString());
        Assertions.assertEquals(expected, actual);
    }
//    @Test
//    public void commentNotFoundExceptionThrown(){
//        when(commentsRepository.findById(id)).thenReturn(Optional.empty());
//        assertThrows(CommentNotFoundException.class, () -> service.getCommentById(id));
//    } changed way of dealing with null comments in controller

    @Test
    public void findCommentsByUsername(){
        List<CommentDoc> expected = Arrays.asList(commentOne);
        List<CommentDoc> commentList = new ArrayList<>();
        commentList.add(commentOne);
        commentList.add(commentTwo);
        when(commentsRepository.findAll()).thenReturn(commentList);
        List<CommentDoc> actual = service.getCommentsByName("Testing-Account");
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void findCommentsByUsernameMovie(){
        List<CommentDoc> expected = Arrays.asList(commentOne);
        List<CommentDoc> commentList = new ArrayList<>();
        commentList.add(commentOne);
        commentList.add(commentTwo);
        when(commentsRepository.findAll()).thenReturn(commentList);
        List<CommentDoc> actual = service.getCommentsByUsernameAndMovie("Testing-Account",commentList);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void findCommentByMovieId(){
        List<CommentDoc> expected = Arrays.asList(commentTwo);
        List<CommentDoc> commentList = new ArrayList<>();
        commentList.add(commentOne);
        commentList.add(commentTwo);
        when(commentsRepository.findAll()).thenReturn(commentList);
        List<CommentDoc> actual = service.getCommentsByMovieId(commentTwo.getMovieId());
        Assertions.assertEquals(expected, actual);

    }

    @Test
    public void findCommentsByDateRange(){
        List<CommentDoc> expected = Arrays.asList(commentTwo);
        List<CommentDoc> commentList = new ArrayList<>();
        commentList.add(commentOne);
        commentList.add(commentTwo);
        when(commentsRepository.findAll()).thenReturn(commentList);
        List<CommentDoc> actual = service.getCommentsByDateRange("2001-05-01", "2002-05-01",commentList);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void updateComment(){
        List<CommentDoc> commentList = new ArrayList<>();
        commentList.add(commentOne);
        commentList.add(commentTwo);
        when(commentsRepository.existsById(id.toString())).thenReturn(true);
        when(commentsRepository.findById(id.toString())).thenReturn(Optional.of(commentThree));
        when(commentsRepository.findAll()).thenReturn(commentList);
        when(commentsRepository.save(commentOne)).thenReturn(commentThree);
        String expected = "This is some sample text that has been changed";
        service.updateComment(commentThree);
        CommentDoc updated = service.getCommentById(id.toString());
        String actual = updated.getText();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void deleteComment(){
        when(commentsRepository.existsById(id.toString())).thenReturn(true);
        service.deleteComment(commentOne.getId());
        verify(commentsRepository, times(1)).deleteById(commentOne.getId());
    }

//    @Test
//    public void deleteCommentThrowsException(){
//        ObjectId notFoundId = new ObjectId();
//        assertThrows(CommentNotFoundException.class, () -> service.deleteComment(notFoundId));
//    } changed way of dealing with null comments in controller

    @Test
    public void createNewComment(){
        CommentDoc comment = new CommentDoc();
        comment.setId(new ObjectId().toString());
        comment.setText("This is some sample text that has been created");
        when(commentsRepository.save(comment)).thenReturn(comment);
        CommentDoc newCreatedComment = service.createComment(comment);
        verify(commentsRepository, times(1)).save(comment);
        Assertions.assertEquals(comment, newCreatedComment);
    }

    @Test
    public void censorBadTextLowerCase(){
        CommentDoc comment = new CommentDoc();
        comment.setText("This is some sample text that has fuck censored");
        String expected = "This is some sample text that has **** censored";
        String actual = commentsService.censorBadText(comment).getText();
        Assertions.assertEquals(expected, actual);
    }
    @Test
    public void censorBadTextUpperCase(){
        CommentDoc comment = new CommentDoc();
        comment.setText("This is some sample text that has Fucking censored");
        String expected = "This is some sample text that has ****ing censored";
        String actual = commentsService.censorBadText(comment).getText();
        Assertions.assertEquals(expected, actual);
    }
}
