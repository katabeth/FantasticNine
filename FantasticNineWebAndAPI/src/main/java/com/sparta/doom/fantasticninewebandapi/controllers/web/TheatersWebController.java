package com.sparta.doom.fantasticninewebandapi.controllers.web;

import com.sparta.doom.fantasticninewebandapi.models.theater.Address;
import com.sparta.doom.fantasticninewebandapi.models.theater.Geo;
import com.sparta.doom.fantasticninewebandapi.models.theater.Location;
import com.sparta.doom.fantasticninewebandapi.models.theater.TheaterDoc;
import com.sparta.doom.fantasticninewebandapi.repositories.TheaterRepository;
import com.sparta.doom.fantasticninewebandapi.services.TheaterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Controller
@RequestMapping("/theaters")
public class TheatersWebController {

    private final WebClient webClient;

    @Value("${jwt.auth}")
    private String AUTH_HEADER;

    private static final int PAGE_SIZE = 10;

    public TheatersWebController(WebClient webClient, TheaterService theaterService, TheaterRepository theaterRepository) {
        this.webClient = webClient;

    }

    @GetMapping
    public String getTheatres(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        List<TheaterDoc> theaters = webClient
                .get()
                .uri("/api/theaters")
                .header(AUTH_HEADER, )
                .retrieve()
                .bodyToFlux(TheaterDoc.class)
                .collectList()
                .block();

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, theaters.size());

        List<TheaterDoc> paginatedTheaters = theaters.subList(start, end);

        model.addAttribute("theaters", paginatedTheaters);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) theaters.size() / PAGE_SIZE));
        return "theaters/theaters";
    }

    @GetMapping("/{id}")
    public String getTheaterDetails(@PathVariable Integer id, Model model) {
        TheaterDoc theater = webClient
                .get()
                .uri("/api/theaters/" + id)
                .header(AUTH_HEADER, )
                .retrieve()
                .bodyToMono(TheaterDoc.class)
                .block();
        model.addAttribute("theater", theater);
        return "theaters/theater_details";
    }

    @GetMapping("/cities")
    public String searchTheatersByCityName(@RequestParam String cityName, Model model) {
        try {
            List<TheaterDoc> theaters = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/theaters/cities")
                            .queryParam("cityName", cityName)
                            .build())
                    .header(AUTH_HEADER, )
                    .retrieve()
                    .bodyToFlux(TheaterDoc.class)
                    .collectList()
                    .block();

            if (theaters == null || theaters.isEmpty()) {
                model.addAttribute("message", "No theaters found in " + cityName);
            } else {
                model.addAttribute("theaters", theaters);
                model.addAttribute("cityName", cityName);
            }
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while searching for theaters.");
            e.printStackTrace();
        }

        return "theaters/theaters_by_city";
    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("theater", new TheaterDoc());
        return "theaters/theater_create";
    }

    @PostMapping("/create")
    public String createTheater(
            @RequestParam Integer theaterId,
            @RequestParam String street1,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String zipcode,
            @RequestParam String coordinates) {

        TheaterDoc theater = new TheaterDoc();
        theater.setTheaterId(theaterId);

        Location location = new Location();
        Address address = new Address();
        address.setStreet1(street1);
        address.setCity(city);
        address.setState(state);
        address.setZipcode(zipcode);
        location.setAddress(address);

        Geo geo = new Geo();
        geo.setType("Point");
        if (coordinates != null && !coordinates.isEmpty()) {
            String[] coords = coordinates.split(",");
            if (coords.length == 2) {
                try {
                    double lat = Double.parseDouble(coords[0].trim());
                    double lon = Double.parseDouble(coords[1].trim());
                    geo.setCoordinates(new double[]{lat, lon});
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        location.setGeo(geo);
        theater.setLocation(location);

        try {
            webClient.post()
                    .uri("/api/theaters")
                    .header(AUTH_HEADER, )
                    .bodyValue(theater)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            e.printStackTrace();
        }

        return "redirect:/theaters";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model) {
        TheaterDoc theater = webClient
                .get()
                .uri("/api/theaters/" + id)
                .header(AUTH_HEADER,)
                .retrieve()
                .bodyToMono(TheaterDoc.class)
                .block();
        model.addAttribute("theater", theater);
        return "theaters/theater_update";
    }

    @PostMapping("/update")
    public String updateTheater(@ModelAttribute TheaterDoc theater) {
        webClient
                .put()
                .uri("/api/theaters")
                .header(AUTH_HEADER, )
                .bodyValue(theater)
                .retrieve()
                .bodyToMono(TheaterDoc.class)
                .block();
        return "redirect:/theaters";
    }

    @GetMapping("/delete/{id}")
    public String deleteTheater(@PathVariable Integer id) {
        webClient
                .delete()
                .uri("/api/theaters/" + id)
                .header(AUTH_HEADER, );
        return "redirect:/theaters";
    }
}
