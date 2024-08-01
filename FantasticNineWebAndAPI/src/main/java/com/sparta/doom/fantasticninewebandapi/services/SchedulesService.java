package com.sparta.doom.fantasticninewebandapi.services;

import com.sparta.doom.fantasticninewebandapi.models.MovieDoc;
import com.sparta.doom.fantasticninewebandapi.models.ScheduleDoc;
import com.sparta.doom.fantasticninewebandapi.models.theater.TheaterDoc;
import com.sparta.doom.fantasticninewebandapi.repositories.SchedulesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class SchedulesService {

    private final SchedulesRepository schedulesRepository;
    private final Logger logger = LoggerFactory.getLogger(SchedulesService.class);

    @Autowired
    public SchedulesService(SchedulesRepository schedulesRepository) {
        this.schedulesRepository = schedulesRepository;
    }

    public Stream<ScheduleDoc> getAllSchedules() {
        logger.info("Fetching all schedules sorted by start time");
        return schedulesRepository.findAllBy()
                .sorted(Comparator.comparing(ScheduleDoc::getStartTime));
    }

    public Stream<ScheduleDoc> getFutureSchedules() {
        LocalDateTime now = LocalDateTime.now();
        logger.info("Fetching future schedules starting after: {}", now);
        return schedulesRepository.findAllBy()
                .filter(schedule -> schedule.getStartTime().isAfter(now))
                .sorted(Comparator.comparing(ScheduleDoc::getStartTime));
    }

    public Stream<ScheduleDoc> getAllSchedules(int page, int size) {
        logger.info("Fetching all schedules for page {} with size {}", page, size);
        return paginate(getAllSchedules(), page, size);
    }

    public Stream<ScheduleDoc> getFutureSchedules(int page, int size) {
        logger.info("Fetching future schedules for page {} with size {}", page, size);
        return paginate(getFutureSchedules(), page, size);
    }

    public Stream<ScheduleDoc> getSchedules() {
        logger.info("Fetching future schedules");
        return getFutureSchedules();
    }

    public Stream<ScheduleDoc> getSchedules(int page, int size) {
        logger.info("Fetching future schedules for page {} with size {}", page, size);
        return paginate(getSchedules(), page, size);
    }

    private Stream<ScheduleDoc> paginate(Stream<ScheduleDoc> schedules, int page, int size) {
        logger.info("Paginating schedules, page: {}, size: {}", page, size);
        return schedules
                .skip((long) page * size)
                .limit(size);
    }

    public Optional<ScheduleDoc> getScheduleById(String id) {
        logger.info("Fetching schedule by ID: {}", id);
        return schedulesRepository.findById(id);
    }

    public Stream<ScheduleDoc> getSchedulesByTheatre(TheaterDoc theatre) {
        String theatreId = theatre.getId();
        logger.info("Fetching schedules by theatre ID: {}", theatreId);
        return getSchedulesByTheaterId(theatreId);
    }

    public Stream<ScheduleDoc> getSchedulesByTheaterId(String theatreId) {
        logger.info("Fetching schedules by theatre ID: {}", theatreId);
        return getSchedules()
                .filter(schedule -> schedule.getTheater().getId().equals(theatreId));
    }

    public Stream<ScheduleDoc> getSchedulesByTheaterId(String theatreId, int page, int size) {
        logger.info("Fetching schedules by theatre ID: {} for page {} with size {}", theatreId, page, size);
        return paginate(getSchedulesByTheaterId(theatreId), page, size);
    }

    public Stream<ScheduleDoc> getSchedulesByTheatre(TheaterDoc theatre, int page, int size) {
        String theatreId = theatre.getId();
        logger.info("Fetching schedules by theatre ID: {} for page {} with size {}", theatreId, page, size);
        return paginate(getSchedulesByTheaterId(theatreId), page, size);
    }

    public Stream<ScheduleDoc> getSchedulesByMovie(MovieDoc movie) {
        String movieId = movie.getId();
        logger.info("Fetching schedules by movie ID: {}", movieId);
        return getSchedulesByMovieId(movieId);
    }

    public Stream<ScheduleDoc> getSchedulesByMovieId(String movieId) {
        logger.info("Fetching schedules by movie ID: {}", movieId);
        return getSchedules()
                .filter(schedule -> schedule.getMovie().getId().equals(movieId));
    }

    public Stream<ScheduleDoc> getSchedulesByMovie(MovieDoc movie, int page, int size) {
        String movieId = movie.getId();
        logger.info("Fetching schedules by movie ID: {} for page {} with size {}", movieId, page, size);
        return getSchedulesByMovieId(movieId, page, size);
    }

    public Stream<ScheduleDoc> getSchedulesByMovieId(String movieId, int page, int size) {
        logger.info("Fetching schedules by movie ID: {} for page {} with size {}", movieId, page, size);
        return paginate(getSchedulesByMovieId(movieId), page, size);
    }

    public Stream<ScheduleDoc> getSchedulesByStartTimeAfter(LocalDateTime startTime) {
        logger.info("Fetching schedules starting after: {}", startTime);
        return getSchedules()
                .filter(schedule -> schedule.getStartTime().isAfter(startTime));
    }

    public Stream<ScheduleDoc> getSchedulesByStartTimeBefore(LocalDateTime startTime) {
        logger.info("Fetching schedules starting before: {}", startTime);
        return getSchedules()
                .filter(schedule -> schedule.getStartTime().isBefore(startTime));
    }

    public Optional<ScheduleDoc> addSchedule(ScheduleDoc scheduleDoc) {
        logger.info("Adding new schedule: {}", scheduleDoc);
        return Optional.of(schedulesRepository.save(scheduleDoc));
    }

    public void removeSchedule(String id) {
        logger.info("Removing schedule by ID: {}", id);
        schedulesRepository.deleteById(id);
    }

    public void removeSchedule(ScheduleDoc scheduleDoc) {
        logger.info("Removing schedule: {}", scheduleDoc);
        schedulesRepository.delete(scheduleDoc);
    }

    public Optional<ScheduleDoc> updateSchedule(ScheduleDoc scheduleDoc) {
        logger.info("Updating schedule with Id: {}", scheduleDoc.getId());
        return Optional.of(schedulesRepository.save(scheduleDoc));
    }
}
