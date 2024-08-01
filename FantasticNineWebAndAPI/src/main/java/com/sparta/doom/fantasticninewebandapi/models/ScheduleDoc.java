package com.sparta.doom.fantasticninewebandapi.models;

import com.sparta.doom.fantasticninewebandapi.models.theater.TheaterDoc;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
@Document(collection = "schedules")
public class ScheduleDoc {
    @Id
    private String id;

    @NotNull
    @DBRef
    private TheaterDoc theater;

    @NotNull
    @DBRef
    private MovieDoc movie;

    @NotNull
    @Future
    private LocalDateTime startTime;

}
