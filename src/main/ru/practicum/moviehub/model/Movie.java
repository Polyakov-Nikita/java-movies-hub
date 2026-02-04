package ru.practicum.moviehub.model;

public record Movie(String title, int year, int id) {
    public Movie(String title, int year) {
        this(title, year, -1);
    }

    public Movie(Movie instance, int id) {
        this(instance.title, instance.year, id);
    }
}