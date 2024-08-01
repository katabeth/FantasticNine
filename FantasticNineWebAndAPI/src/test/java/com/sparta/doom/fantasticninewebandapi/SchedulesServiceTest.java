package com.sparta.doom.fantasticninewebandapi;

import com.sparta.doom.fantasticninewebandapi.models.MovieDoc;
import com.sparta.doom.fantasticninewebandapi.models.ScheduleDoc;
import com.sparta.doom.fantasticninewebandapi.models.theater.TheaterDoc;
import com.sparta.doom.fantasticninewebandapi.repositories.SchedulesRepository;
import com.sparta.doom.fantasticninewebandapi.services.SchedulesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SchedulesServiceTest {

    private static SchedulesRepository scheduleRepository;

    private static SchedulesService scheduleService;

    ScheduleDoc schedule1 = new ScheduleDoc("1", new TheaterDoc(), new MovieDoc(), LocalDateTime.now().plusDays(1));
    ScheduleDoc schedule2 = new ScheduleDoc("2", new TheaterDoc(), new MovieDoc(), LocalDateTime.now().plusDays(2));


    @BeforeAll
    public static void setUp() {
        scheduleRepository = Mockito.mock(SchedulesRepository.class);
        scheduleService = new SchedulesService(scheduleRepository);
    }

    @Test
    public void testGetSchedules() {
        when(scheduleRepository.findAllBy()).thenReturn(Stream.of(schedule1, schedule2));

        List<ScheduleDoc> schedules = scheduleService.getSchedules().toList();

        assertNotNull(schedules);
        Assertions.assertEquals(2, schedules.size());
    }

    @Test
    public void testGetScheduleById() {
        when(scheduleRepository.findById("1")).thenReturn(Optional.of(schedule1));

        Optional<ScheduleDoc> retrievedSchedule = scheduleService.getScheduleById("1");

        assertTrue(retrievedSchedule.isPresent());
        assertEquals("1", retrievedSchedule.get().getId());
        verify(scheduleRepository, times(1)).findById("1");
    }

    @Test
    public void testAddSchedule() {
        when(scheduleRepository.save(any(ScheduleDoc.class))).thenReturn(schedule1);

        ScheduleDoc savedSchedule = scheduleService.addSchedule(schedule1).orElseThrow();

        assertNotNull(savedSchedule);
        assertEquals("1", savedSchedule.getId());
        verify(scheduleRepository, times(1)).save(schedule1);
    }

    @Test
    public void testRemoveSchedule() {
        doNothing().when(scheduleRepository).delete(schedule1);

        scheduleService.removeSchedule(schedule1);

        verify(scheduleRepository, times(1)).delete(schedule1);
    }

    @Test
    public void testUpdateSchedule() {
        when(scheduleRepository.save(any(ScheduleDoc.class))).thenReturn(schedule1);

        ScheduleDoc updatedSchedule = scheduleService.updateSchedule(schedule1).orElseThrow();

        assertNotNull(updatedSchedule);
        assertEquals("1", updatedSchedule.getId());
        verify(scheduleRepository, times(1)).save(schedule1);
    }

    @Test
    public void testGetSchedulesByTheatre() {
        TheaterDoc theatre = mock(TheaterDoc.class);
        when(theatre.getId()).thenReturn("1");
        schedule1.setTheater(theatre);
        when(scheduleRepository.findAllBy()).thenReturn(Stream.of(schedule1));

        List<ScheduleDoc> schedules = scheduleService.getSchedulesByTheatre(theatre).toList();

        assertNotNull(schedules);
        assertEquals(1, schedules.size());
        assertEquals("1", schedules.getFirst().getTheater().getId());
    }

    @Test
    public void testGetSchedulesByMovie() {
        MovieDoc movie = mock(MovieDoc.class);
        when(movie.getId()).thenReturn("1");
        schedule1.setMovie(movie);
        when(scheduleRepository.findAllBy()).thenReturn(Stream.of(schedule1));

        List<ScheduleDoc> schedules = scheduleService.getSchedulesByMovie(movie).toList();
    }

}
