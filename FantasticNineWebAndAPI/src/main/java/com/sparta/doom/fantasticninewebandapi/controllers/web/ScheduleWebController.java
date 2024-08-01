package com.sparta.doom.fantasticninewebandapi.controllers.web;

import com.sparta.doom.fantasticninewebandapi.models.ScheduleDoc;
import com.sparta.doom.fantasticninewebandapi.services.SchedulesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping
public class ScheduleWebController {
    // TODO Theatre has one schedule with many showings
    // TODO movies have many schedules with many showings at many theatres
    // TODO searches are based off of what called it
    // TODO Create
    // TODO Update
    // TODO Delete
    // TODO Web Page design
    public final WebClient webClient;
    @Value("${jwt.auth}")
    private String AUTH_HEADER;
    @Autowired
    public ScheduleWebController(SchedulesService schedulesService, WebClient webClient) {
        this.webClient = webClient;
    }
    // Redirect gets to a single URL with two parameters
    // Redirect to /schedules/ or /schedules/{id}/{searchType}
    @GetMapping("/theaters/{id}/schedules/")
    public String getSchedulesForTheatre(@PathVariable String id) {
        return "redirect:/schedules?searchType=theater&id="+id;
    }
    @GetMapping("/movies/{id}/schedules/")
    public String getSchedulesForMovie(@PathVariable String id, Model model) {
        return "redirect:/schedules?searchType=movie&id="+id;
    }

    @GetMapping("/schedules")
    public String getSchedulesSearch(@RequestParam(defaultValue = "all" ,required = false) String searchType,@RequestParam(required = false) String id, Model model) {
        if (Objects.equals(searchType, "theater")) {
            //schedules by theater id
            List<List<ScheduleDoc>> returnSchedules = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                List<ScheduleDoc> schedules = Objects.requireNonNull(webClient
                                .get()
                                .uri("api/theaters/" + id + "/schedules?page=" + i)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<CollectionModel<EntityModel<ScheduleDoc>>>() {
                                })
                                .block())
                        .getContent()
                        .stream()
                        .map(EntityModel::getContent)
                        .toList();
                returnSchedules.add(schedules);
                if (schedules.size() < 10) {
                    break;
                }
            }
            model.addAttribute("schedules", returnSchedules);
        } else if (Objects.equals(searchType, "movie")) {
            //schedules by movie id
            List<List<ScheduleDoc>> returnSchedules = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                List<ScheduleDoc> schedules = Objects.requireNonNull(webClient
                                .get()
                                .uri("api/movies/" + id + "/schedules?page=" + i)
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<CollectionModel<EntityModel<ScheduleDoc>>>() {
                                })
                                .block())
                        .getContent()
                        .stream()
                        .map(EntityModel::getContent)
                        .toList();
                returnSchedules.add(schedules);
                if (schedules.size() < 10) {
                    break;
                }
            }
            model.addAttribute("schedules", returnSchedules);
        } else if (Objects.equals(searchType, "schedule")) {
            //Schedules id
            List<ScheduleDoc> schedules = Objects.requireNonNull(webClient
                            .get()
                            .uri("api/schedules/" + id)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<CollectionModel<EntityModel<ScheduleDoc>>>() {
                            })
                            .block())
                    .getContent()
                    .stream()
                    .map(EntityModel::getContent)
                    .toList();
            model.addAttribute("schedules",schedules);
        } else if (Objects.equals(searchType, "all")) {
            List<ScheduleDoc> schedules = Objects.requireNonNull(webClient
                            .get()
                            .uri("api/schedules")
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<CollectionModel<EntityModel<ScheduleDoc>>>() {
                            })
                            .block())
                    .getContent()
                    .stream()
                    .map(EntityModel::getContent)
                    .toList();
            model.addAttribute("schedules", schedules);
        }
        model.addAttribute("searchType", searchType);
        return "schedules/show";
    }

    @GetMapping("/schedules/create")
    public String createSchedule(Model model) {
        model.addAttribute("schedule", new ScheduleDoc());
        // add any other needed attributes
        return "schedules/create";
    }

    @PostMapping("/schedules/create")
    public String createSchedule(@ModelAttribute ScheduleDoc schedule, Errors errors, @CookieValue(name = "jwt", required = false) String jwtToken) {
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Invalid schedule: " + errors);
        } else {
            ScheduleDoc newSchedule = webClient
                    .post()
                    .uri("api/schedules")
                    .header(AUTH_HEADER, "Bearer " + jwtToken)
                    .bodyValue(schedule)
                    .retrieve()
                    .bodyToMono(ScheduleDoc.class)
                    .block();
            assert newSchedule != null;
            return "redirect:/schedules?searchType=schedule&id=" + newSchedule.getId();
        }
    }

    @PostMapping("/schedules/update/{id}")
    public String updateSchedule(@PathVariable String id, @RequestBody ScheduleDoc schedule, Model model, Errors errors, @CookieValue(name = "jwt", required = false) String jwtToken) {
        if (errors.hasErrors()) {
            throw new IllegalArgumentException("Invalid schedule: " + errors);
        } else {
            webClient.put()
                    .uri("api/schedules/" + id)
                    .header(AUTH_HEADER, "Bearer " + jwtToken)
                    .bodyValue(schedule)
                    .retrieve()
                    .bodyToMono(ScheduleDoc.class)
                    .block();
            return "redirect:/schedules/schedule/" + schedule.getId() + "/";
        }
    }

    @PostMapping("/schedules/delete/{id}")
    public String deleteSchedule(@PathVariable String id, @CookieValue(name = "jwt", required = false) String jwtToken) {
        webClient
                .delete()
                .uri("api/schedules/" + id)
                .header(AUTH_HEADER, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(ScheduleDoc.class)
                .block();
        return "redirect:/schedules/";
    }
}
