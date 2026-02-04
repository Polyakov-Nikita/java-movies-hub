package ru.practicum.moviehub.http.handlers.validation;

import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.utils.TestUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovieValidatorTest {
    private static final MovieValidator VALIDATOR = new MovieValidator();
    private static final String CORRECT_TITLE = "Title";

    @Test
    public void validate_Correct() {
        String description = "Список сообщений должен быть пустым";
        Movie movie = new Movie(CORRECT_TITLE, TestUtility.CORRECT_YEAR);
        assertEquals(0, VALIDATOR.validate(movie).size(), description);
    }

    @Test
    public void validate_IncorrectTitle_EmptyTitle() {
        String description = "В списке сообщений должна быть одна запись о пустом названии";
        Movie movie = new Movie("", TestUtility.CORRECT_YEAR);
        assertEquals(1, VALIDATOR.validate(movie).size(), description);
        assertEquals(MovieValidator.EMPTY_TITLE_MESSAGE, VALIDATOR.validate(movie).getFirst(), description);
    }

    @Test
    public void validate_IncorrectTitle_TooLongTitle() {
        String description = "В списке сообщений должна быть одна запись о длинном названии";
        Movie movie = new Movie("t".repeat(MovieValidator.MAX_TITLE_LENGTH + 1), TestUtility.CORRECT_YEAR);
        assertEquals(1, VALIDATOR.validate(movie).size(), description);
        assertEquals(MovieValidator.LONG_TITLE_MESSAGE, VALIDATOR.validate(movie).getFirst(), description);
    }

    @Test
    public void validate_IncorrectYear_LessThanMin() {
        String description = "В списке сообщений должна быть одна запись о неправильном году";
        Movie movie = new Movie(CORRECT_TITLE, MovieValidator.MIN_YEAR - 1);
        assertEquals(1, VALIDATOR.validate(movie).size(), description);
        assertEquals(MovieValidator.INCORRECT_YEAR_MESSAGE, VALIDATOR.validate(movie).getFirst(), description);
    }

    @Test
    public void validate_IncorrectYear_GreaterThanMax() {
        String description = "В списке сообщений должна быть одна запись о неправильном году";
        Movie movie = new Movie(CORRECT_TITLE, MovieValidator.MAX_YEAR + 1);
        assertEquals(1, VALIDATOR.validate(movie).size(), description);
        assertEquals(MovieValidator.INCORRECT_YEAR_MESSAGE, VALIDATOR.validate(movie).getFirst(), description);
    }

    @Test
    public void validate_IncorrectAll() {
        String description = "В списке сообщений должно быть две записи: о пустой строке и неправильном году";
        Movie movie = new Movie("", MovieValidator.MAX_YEAR + 1);
        assertEquals(2, VALIDATOR.validate(movie).size(), description);
        assertEquals(MovieValidator.EMPTY_TITLE_MESSAGE, VALIDATOR.validate(movie).get(0), description);
        assertEquals(MovieValidator.INCORRECT_YEAR_MESSAGE, VALIDATOR.validate(movie).get(1), description);
    }
}
