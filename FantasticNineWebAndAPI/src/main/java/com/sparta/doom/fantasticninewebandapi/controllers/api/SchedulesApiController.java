package com.sparta.doom.fantasticninewebandapi.controllers.api;

import com.sparta.doom.fantasticninewebandapi.models.ScheduleDoc;
import com.sparta.doom.fantasticninewebandapi.services.SchedulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class SchedulesApiController {

    private final SchedulesService schedulesService;
    private final Logger logger = LoggerFactory.getLogger(SchedulesApiController.class);

    public SchedulesApiController(SchedulesService schedulesService) {
        this.schedulesService = schedulesService;
    }

    @GetMapping("/schedules")
    public CollectionModel<EntityModel<ScheduleDoc>> getSchedules(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        logger.info("getting Schedule Stream from service layer in controller");
        CollectionModel<EntityModel<ScheduleDoc>> scheduleModel = getCollectionModelOf(schedulesService.getSchedules(page, size));

        return scheduleModel.add(linkTo(methodOn(SchedulesApiController.class).getSchedules(page, size)).withSelfRel());
    }

    @GetMapping("/schedules/all")
    public CollectionModel<EntityModel<ScheduleDoc>> getAllSchedules(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        CollectionModel<EntityModel<ScheduleDoc>> scheduleModel = getCollectionModelOf(schedulesService.getAllSchedules(page, size));

        return scheduleModel.add(linkTo(methodOn(SchedulesApiController.class).getAllSchedules(page, size)).withSelfRel());
    }

    @GetMapping("/schedules/{id}")
    public ResponseEntity<EntityModel<ScheduleDoc>> getScheduleById(@PathVariable String id) {
        logger.info("Getting schedule by id in controller");
        return ResponseEntity.ok(schedulesService.getScheduleById(id).map(this::mapScheduleHateoas).orElseThrow(NoSuchElementException::new));
    }

    @PostMapping("/schedules")
    public ResponseEntity<EntityModel<ScheduleDoc>> addSchedule(@RequestBody ScheduleDoc newSchedule) {
        checkApiKey();
        logger.info("Adding new schedules in controller");
        if (newSchedule == null || newSchedule.getMovie() == null || newSchedule.getTheater() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        EntityModel<ScheduleDoc> newScheduleModel = mapScheduleHateoas(schedulesService.addSchedule(newSchedule).orElseThrow(NoSuchElementException::new));
        return ResponseEntity.ok().body(newScheduleModel);
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable String id) {
        checkApiKey();
        logger.info("deleting schedule by Id in controller");
        schedulesService.removeSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<EntityModel<ScheduleDoc>> updateSchedule(@PathVariable String id, @RequestBody ScheduleDoc schedule) {
        checkApiKey();
        logger.info("Updating schedule in controller");

        if (schedule == null || schedule.getMovie() == null || schedule.getTheater() == null || !id.equals(schedule.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        EntityModel<ScheduleDoc> updatedSchedule = mapScheduleHateoas(schedulesService.updateSchedule(schedule).orElseThrow(NoSuchElementException::new));
        return ResponseEntity.ok().body(updatedSchedule);
    }

    @GetMapping("theaters/{Id}/schedules")
    public ResponseEntity<CollectionModel<EntityModel<ScheduleDoc>>> getTheaterSchedules(@PathVariable String Id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        logger.info("Getting schedules by Theater Id in controller");
        CollectionModel<EntityModel<ScheduleDoc>> schedulesModel = getCollectionModelOf(schedulesService.getSchedulesByTheaterId(Id, page, size));

        return ResponseEntity.ok(schedulesModel.add(linkTo(methodOn(SchedulesApiController.class).getTheaterSchedules(Id, page, size)).withSelfRel()));
    }

    @GetMapping("movies/{Id}/schedules")
    public ResponseEntity<CollectionModel<EntityModel<ScheduleDoc>>> getMovieSchedules(@PathVariable String Id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        logger.info("Getting schedules by Movie Id in controller");
        CollectionModel<EntityModel<ScheduleDoc>> schedulesModel = getCollectionModelOf(schedulesService.getSchedulesByMovieId(Id, page, size));
        return ResponseEntity.ok(schedulesModel.add(linkTo(methodOn(SchedulesApiController.class).getMovieSchedules(Id, page, size)).withSelfRel()));
    }

    private CollectionModel<EntityModel<ScheduleDoc>> getCollectionModelOf(Stream<ScheduleDoc> schedules) {
        logger.info("Mapping schedule stream to collection model");
        CollectionModel<EntityModel<ScheduleDoc>> scheduleModel = schedules.map(this::mapScheduleHateoas).collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        if (scheduleModel.getContent().isEmpty()) {
            throw new NoSuchElementException("No schedules found");
        }
        return scheduleModel;
    }

    private EntityModel<ScheduleDoc> mapScheduleHateoas(ScheduleDoc doc) {
        logger.info("Mapping schedule object to an appropriate hateoas response");
        Link selfLink = linkTo(methodOn(SchedulesApiController.class).getScheduleById(doc.getId())).withSelfRel();
        Link movieLink = doc.getMovie() == null ? Link.of("Not Found") : linkTo(methodOn(MoviesApiController.class).getMovieById(doc.getMovie().getId())).withRel("movie");
        Link theaterLink = doc.getTheater() == null ? Link.of("Not Found") : linkTo(methodOn(TheaterApiController.class).getTheaters()).slash(doc.getTheater().getId()).withRel("theater");
        return EntityModel.of(doc, selfLink, movieLink, theaterLink);
    }

    private void checkApiKey() {
        logger.info("Checking user is allowed to use method in controller");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        if (userDetails.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "only Admins are allowed to perform this action");
        }
    }
}
