package com.sparta.doom.fantasticninewebandapi.dtos;
import java.util.Arrays;

public class MovieDetailsDTO {
    private String id;
    private String title;
    private Integer runtime;
    private String released;
    private String year;
    private String rated;
    private String plot;
    private String fullplot;
    private String[] countries;
    private String[] cast;
    private String[] writers;
    private String[] directors;
    private String lastupdated;

    public MovieDetailsDTO(String id, String title, String[] cast, String[] countries,
                           String[] directors, String fullplot, String plot,
                           String rated, String released, Integer runtime,
                           String[] writers, String year, String lastupdated) {
        this.id = id;

        this.title = title;
        this.runtime = runtime;
        this.released = released;
        this.year = year;
        this.rated = rated;

        this.plot = plot;
        this.fullplot = fullplot;
        this.countries = countries;

        this.cast = cast;
        this.writers = writers;
        this.directors = directors;

        this.lastupdated = lastupdated;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getRated() {
        return rated;
    }

    public void setRated(String rated) {
        this.rated = rated;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getFullplot() {
        return fullplot;
    }

    public void setFullplot(String fullplot) {
        this.fullplot = fullplot;
    }

    public String[] getCountries() {
        return countries;
    }

    public void setCountries(String[] countries) {
        this.countries = countries;
    }

    public String[] getCast() {
        return cast;
    }

    public void setCast(String[] cast) {
        this.cast = cast;
    }

    public String[] getWriters() {
        return writers;
    }

    public void setWriters(String[] writers) {
        this.writers = writers;
    }

    public String[] getDirectors() {
        return directors;
    }

    public void setDirectors(String[] directors) {
        this.directors = directors;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

}
