package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoviesStore {
    private final HashMap<Integer, Movie> movies = new HashMap<>();

    private int currentId = 0;

    public int add(Movie movie) {
        currentId++;
        movies.put(currentId, movie);
        return currentId;
    }

    public int count() {
        return movies.size();
    }

    public void clear() {
        movies.clear();
    }

    public List<Movie> getAll() {
        return new ArrayList<>(movies.values());
    }

    public Movie get(int id) {
        return movies.get(id);
    }

    public boolean containsID(int id) {
        return movies.containsKey(id);
    }

    public void remove(int id) {
        movies.remove(id);
    }

    public List<Movie> getByYear(int year) {
        return movies.values().stream().filter(movie -> movie.year() == year).toList();
    }
}