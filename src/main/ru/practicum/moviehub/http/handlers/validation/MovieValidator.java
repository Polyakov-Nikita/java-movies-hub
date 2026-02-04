package ru.practicum.moviehub.http.handlers.validation;

import ru.practicum.moviehub.model.Movie;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovieValidator {
    public static final String EMPTY_TITLE_MESSAGE = "название не должно быть пустым";
    public static final int MAX_TITLE_LENGTH = 100;
    public static final String LONG_TITLE_MESSAGE =
            String.format("длина названия не должна превышать %d символов", MAX_TITLE_LENGTH);
    public static final int MIN_YEAR = 1988;
    public static final int MAX_YEAR = LocalDateTime.now().getYear() + 1;
    public static final String INCORRECT_YEAR_MESSAGE =
            String.format("год должен быть между %d и %d", MIN_YEAR, MAX_YEAR);

    private final List<String> messages = new ArrayList<>();

    public List<String> validate(Movie movie) {
        String movieTitle = movie.title();
        int movieYear = movie.year();
        checkCorrectness(movieTitle, movieYear);
        List<String> result = new ArrayList<>(messages);
        messages.clear();
        return result;
    }

    private void checkCorrectness(String movieTitle, int movieYear) {
        checkTitleEmptiness(movieTitle);
        checkTitleLength(movieTitle);
        checkYear(movieYear);
    }

    private void checkTitleEmptiness(String title) {
        if (title.isEmpty()) {
            messages.add(EMPTY_TITLE_MESSAGE);
        }
    }

    private void checkTitleLength(String title) {
        if (title.length() > MAX_TITLE_LENGTH) {
            messages.add(LONG_TITLE_MESSAGE);
        }
    }

    private void checkYear(int year) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            messages.add(INCORRECT_YEAR_MESSAGE);
        }
    }
}
