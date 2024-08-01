package com.sparta.doom.fantasticninewebandapi.services;

import com.sparta.doom.fantasticninewebandapi.models.MovieDoc;
import com.sparta.doom.fantasticninewebandapi.models.ScheduleDoc;
import com.sparta.doom.fantasticninewebandapi.models.theater.TheaterDoc;
import com.sparta.doom.fantasticninewebandapi.repositories.SchedulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SchedulesService {

    private final SchedulesRepository schedulesRepository;

    @Autowired
    public SchedulesService(SchedulesRepository schedulesRepository) {
        this.schedulesRepository = schedulesRepository;
    }


    public Stream<ScheduleDoc> getAllSchedules() {
        return schedulesRepository.findAllBy()
                .sorted(Comparator.comparing(ScheduleDoc::getStartTime));
    }

    public Stream<ScheduleDoc> getFutureSchedules(){
        LocalDateTime now = LocalDateTime.now();
        return schedulesRepository.findAllBy()
                .filter(schedule -> schedule.getStartTime().isAfter(now))
                .sorted(Comparator.comparing(ScheduleDoc::getStartTime));
    }

    public Stream<ScheduleDoc> getAllSchedules(int page, int size) {
        return paginate(getAllSchedules(),page,size);
    }

    public Stream<ScheduleDoc> getFutureSchedules(int page, int size){
        return paginate(getFutureSchedules(),page,size);
    }

    public Stream<ScheduleDoc> getSchedules() {
        return getFutureSchedules();
    }

    public Stream<ScheduleDoc> getSchedules(int page, int size) {
        return paginate(getSchedules(),page,size);

    }

    private Stream<ScheduleDoc> paginate(Stream<ScheduleDoc> schedules,int page, int size) {
        return schedules
                .skip((long) page * size)
                .limit(size);
    }

    public Optional<ScheduleDoc> getScheduleById(String Id){
        return schedulesRepository.findById(Id);
    }

    public Stream<ScheduleDoc> getSchedulesByTheatre(TheaterDoc theatre) {
        String theatreId = theatre.getId();
        return getSchedulesByTheaterId(theatreId);
    }

    public Stream<ScheduleDoc> getSchedulesByTheaterId(String theatreId) {
        return getSchedules()
                .filter(schedule -> schedule.getTheater().getId().equals(theatreId));
    }

    public Stream<ScheduleDoc> getSchedulesByTheaterId(String theatreId, int page, int size) {
        return paginate(getSchedulesByTheaterId(theatreId),page,size);
    }

    public Stream<ScheduleDoc> getSchedulesByTheatre(TheaterDoc theatre, int page, int size) {
        String theatreId = theatre.getId();
        return paginate(getSchedulesByTheaterId(theatreId),page,size);
    }


    public Stream<ScheduleDoc> getSchedulesByMovie(MovieDoc movie) {
        String movieId = movie.getId();
        return getSchedulesByMovieId(movieId);
    }

    public Stream<ScheduleDoc> getSchedulesByMovieId(String movieId) {
        return getSchedules()
                .filter(schedule -> schedule.getMovie().getId().equals(movieId));
    }

    public Stream<ScheduleDoc> getSchedulesByMovie(MovieDoc movie, int page, int size) {
        String movieId = movie.getId();
        return getSchedulesByMovieId(movieId, page, size);
    }

    public Stream<ScheduleDoc> getSchedulesByMovieId(String movieId, int page, int size) {
        return paginate(getSchedulesByMovieId(movieId),page,size);
    }

    public Stream<ScheduleDoc> getSchedulesByStartTimeAfter(LocalDateTime startTime) {
        return getSchedules()
                .filter(schedule -> schedule.getStartTime().isAfter(startTime));
    }

    public Stream<ScheduleDoc> getSchedulesByStartTimeBefore(LocalDateTime startTime) {
        return getSchedules()
                .filter(schedule -> schedule.getStartTime().isBefore(startTime));
    }


    public Optional<ScheduleDoc> addSchedule(ScheduleDoc scheduleDoc) {
        return Optional.of(schedulesRepository.save(scheduleDoc));
    }

    public void removeSchedule(String id) {
        schedulesRepository.deleteById(id);
    }
    public void removeSchedule(ScheduleDoc scheduleDoc) {
        schedulesRepository.delete(scheduleDoc);
    }

    public Optional<ScheduleDoc> updateSchedule(ScheduleDoc scheduleDoc) {
        return Optional.of(schedulesRepository.save(scheduleDoc));
    }
}
