package com.sparta.doom.fantasticninewebandapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.doom.fantasticninewebandapi.models.CommentDoc;
import com.sparta.doom.fantasticninewebandapi.services.CommentsService;
import org.bson.types.ObjectId;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;


@SpringBootTest
@AutoConfigureMockMvc
public class CommentsApiControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentsService commentsService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }


    @Test
    void testGetComments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movies/573a1390f29313caabcd4323/comments"))
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    void testGetCommentByMovie() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movies/573a1390f29313caabcd4323/comments/id/5a9427648b0beebeb69579e7"))
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    void testGetCommentByMovieNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movies/573a1390f29313caabcd4323/comments/id/5a9427648b0beebeb69579e8"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    void testGetCommentByMovieWrongMovie() throws Exception {

        CommentDoc commentDoc = new CommentDoc();
        ObjectId movieId = new ObjectId("573a1390f29313caabcd4323");
        ObjectId commentId = new ObjectId("5a9427648b0beebeb69579e7");
        commentDoc.setId(commentId.toString());
        commentDoc.setMovieId(movieId.toString());
        commentDoc.setEmail("test@example.com");
        commentDoc.setDate(new Date());
        commentDoc.setText("This is a test comment.");
        commentDoc.setName("Mercedes Tyler");
        commentsService.createComment(commentDoc);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/movies/573a1390f29313caabcd446f/comments/id/5a9427648b0beebeb69579e7"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        commentsService.deleteComment(commentId.toString());
    }
    @Test
    void testGetCommentsByDateRange() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movies/573a1390f29313caabcd4323/comments/dates/2015-01-01/2020-01-01"))
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    void testGetCommentsByDateRangeBeforeDateAfterEndDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movies/573a1390f29313caabcd4323/comments/dates/2022-01-01/2020-01-01"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    @Test
    void testGetCommentsByMovieAndUsername() throws Exception {

        CommentDoc commentDoc = new CommentDoc();
        ObjectId movieId = new ObjectId("573a1390f29313caabcd4323");
        ObjectId commentId = new ObjectId("5a9427648b0beebeb69579e7");
        commentDoc.setId(commentId.toString());
        commentDoc.setMovieId(movieId.toString());
        commentDoc.setEmail("test@example.com");
        commentDoc.setDate(new Date());
        commentDoc.setText("This is a test comment.");
        commentDoc.setName("Mercedes Tyler");
        commentsService.createComment(commentDoc);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/movies/573a1390f29313caabcd4323/comments/name/Mercedes-Tyler"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Mercedes Tyler")));

        commentsService.deleteComment(commentId.toString());
    }
    @Test
    void testGetCommentsByMovieAndUsernameNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/movies/573a1390f29313caabcd4323/comments/name/MercedesTyler"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    void testGetCommentsByUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/name/Mercedes-Tyler/comments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Mercedes Tyler")))
                .andDo(MockMvcResultHandlers.print());

    }
    @Test
    void testGetCommentsByUserNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/name/MercedesTyler/comments"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"totalElements\":0")));
    }
    @Test
    void testGetCommentsByUserEmailNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/email/wrong.email.address@something/comments"))
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"totalElements\":0")));
    }
    @Test
    void testGetCommentsByUserEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/email/mercedes_tyler@fakegmail.com/comments"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("Mercedes Tyler")))
                .andDo(MockMvcResultHandlers.print());

    }
    @Test
    void testGetCommentsByDateAndUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/Mercedes-Tyler/comments/dates/2000-01-01/2001-01-01"))
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    void testGetCommentsByUserAndDatesWrongUserNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/MercedesTyler/comments/dates/2000-01-01/2001-01-01"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    void testCommentsByUserDatesNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/Mercedes-Tyler/comments/dates/1901-01-01/1915-01-01"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    //todo when creating a new comment need to - pass in the name and email address of the current logged in user
    // the text will be whatever they type in can store in the body
    // the date should be NOW saved in the way that the database likes it
    // the movie id should be taken from the page on, as should only be able to leave comments about that movie on its page

    @Test
    void testCreateNewCommentReturnsBadRequestIfNotSameMovie() throws Exception {

        CommentDoc commentDoc = new CommentDoc();
        ObjectId movieId = new ObjectId("573a1390f29313caabcd4eaf");
        commentDoc.setMovieId(movieId.toString());
        commentDoc.setEmail("test@example.com");
        commentDoc.setDate(new Date());
        commentDoc.setText("This is a test comment.");
        commentDoc.setName("Test User");

        String commentJson = objectMapper.writeValueAsString(commentDoc);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/movies/573a1390f29313caabcd4323/comments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("DOOM-API-KEY", "unique-api-key-123")
                        .content(commentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testCreateNewCommentReturnsIsCreatedIfCorrect() throws Exception {
        CommentDoc commentDoc = new CommentDoc();
        ObjectId movieId = new ObjectId("573a1390f29313caabcd4323");
        commentDoc.setMovieId(movieId.toString());
        commentDoc.setEmail("test@example.com");
        commentDoc.setDate(new Date());
        commentDoc.setText("This is a test comment.");
        commentDoc.setName("Test User");

        String commentJson = objectMapper.writeValueAsString(commentDoc);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/movies/573a1390f29313caabcd4323/comments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("DOOM-API-KEY", "unique-api-key-123")
                        .content(commentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    //todo when updating comments need validation to ensure only admin or user can update comment

    @Test
    void testUpdateCommentReturnsNotFoundIfNoOldComment() throws Exception {
        CommentDoc commentDoc = new CommentDoc();
        ObjectId commentId = new ObjectId("5a9427648b0beebeb69579e8");
        ObjectId movieId = new ObjectId("573a1390f29313caabcd4323");
        commentDoc.setMovieId(movieId.toString());
        commentDoc.setId(commentId.toString());
        commentDoc.setEmail("test@example.com");
        commentDoc.setDate(new Date());
        commentDoc.setText("This is a test comment.");
        commentDoc.setName("Test User");

        String commentJson = objectMapper.writeValueAsString(commentDoc);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/movies/573a1390f29313caabcd4323/comments/id/5a9427648b0beebeb69579e8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("DOOM-API-KEY", "unique-api-key-123")
                        .content(commentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void testUpdateCommentReturnsBadRequestIfWrongMovie() throws Exception {

        CommentDoc commentDocSetUp = new CommentDoc();
        ObjectId movieIdSetUp = new ObjectId("573a1390f29313caabcd4323");
        ObjectId commentIdSetUp = new ObjectId("5a9427648b0beebeb69579e7");
        commentDocSetUp.setId(commentIdSetUp.toString());
        commentDocSetUp.setMovieId(movieIdSetUp.toString());
        commentDocSetUp.setEmail("test@example.com");
        commentDocSetUp.setDate(new Date());
        commentDocSetUp.setText("This is a test comment.");
        commentDocSetUp.setName("Mercedes Tyler");
        commentsService.createComment(commentDocSetUp);

        CommentDoc commentDoc = new CommentDoc();
        ObjectId commentId = new ObjectId("5a9427648b0beebeb69579e7");
        ObjectId movieId = new ObjectId("573a1390f29313caabcd4323");
        commentDoc.setMovieId(movieId.toString());
        commentDoc.setId(commentId.toString());
        commentDoc.setEmail("test@example.com");
        commentDoc.setDate(new Date());
        commentDoc.setText("This is a test comment.");
        commentDoc.setName("Test User");

        String commentJson = objectMapper.writeValueAsString(commentDoc);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/movies/573a1390f29313caabcd446f/comments/id/5a9427648b0beebeb69579e7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("DOOM-API-KEY", "unique-api-key-123")
                        .content(commentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        commentsService.deleteComment(commentIdSetUp.toString());
    }

    @Test
    void testUpdateCommentReturnsNoContentIfSuccessful() throws Exception {

        CommentDoc commentDocSetUp = new CommentDoc();
        ObjectId movieIdSetUp = new ObjectId("573a1390f29313caabcd4323");
        ObjectId commentIdSetUp = new ObjectId("5a9427648b0beebeb69579e7");
        commentDocSetUp.setId(commentIdSetUp.toString());
        commentDocSetUp.setMovieId(movieIdSetUp.toString());
        commentDocSetUp.setEmail("test@example.com");
        commentDocSetUp.setDate(new Date());
        commentDocSetUp.setText("This is a test comment.");
        commentDocSetUp.setName("Mercedes Tyler");
        commentsService.createComment(commentDocSetUp);

        CommentDoc commentDoc = new CommentDoc();
        ObjectId commentId = new ObjectId("5a9427648b0beebeb69579e7");
        ObjectId movieId = new ObjectId("573a1390f29313caabcd4323");
        commentDoc.setMovieId(movieId.toString());
        commentDoc.setId(commentId.toString());
        commentDoc.setEmail("test@example.com");
        commentDoc.setDate(new Date());
        commentDoc.setText("This is a test comment.");
        commentDoc.setName("Test User");

        String commentJson = objectMapper.writeValueAsString(commentDoc);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/movies/573a1390f29313caabcd4323/comments/id/5a9427648b0beebeb69579e7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("DOOM-API-KEY", "unique-api-key-123")
                        .content(commentJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
        commentsService.deleteComment(commentIdSetUp.toString());
    }

    @Test
    void testDeleteCommentReturnsCommentNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/movies/573a1390f29313caabcd4323/comments/id/5a9427648b0beebeb69579e8"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    @Test
    void testDeleteCommentReturnsNoContentIfSuccessful() throws Exception {

        CommentDoc commentDocSetUp = new CommentDoc();
        ObjectId movieIdSetUp = new ObjectId("573a1390f29313caabcd4323");
        ObjectId commentIdSetUp = new ObjectId("5a9427648b0beebeb69579e7");
        commentDocSetUp.setId(commentIdSetUp.toString());
        commentDocSetUp.setMovieId(movieIdSetUp.toString());
        commentDocSetUp.setEmail("test@example.com");
        commentDocSetUp.setDate(new Date());
        commentDocSetUp.setText("This is a test comment.");
        commentDocSetUp.setName("Mercedes Tyler");
        commentsService.createComment(commentDocSetUp);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/movies/573a1390f29313caabcd4323/comments/id/5a9427648b0beebeb69579e7")
                        .header("DOOM-API-KEY", "unique-api-key-123"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
