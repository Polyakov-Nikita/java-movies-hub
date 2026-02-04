package ru.practicum.moviehub.utils;

import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.http.HttpConstants;
import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.http.handlers.GetMoviesHandler;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtility {
    public static final MoviesStore STORE = new MoviesStore();
    public static final int PORT = 8080;
    public static final int ABSENT_ID = 404;
    public static final int CORRECT_YEAR = 1997;

    private static final String URL_BASE = "http://localhost:" + PORT;
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();
    private static final int MOVIES_TO_ADD_COUNT = 5;

    public static Movie createMovie() {
        return createMovie(CORRECT_YEAR);
    }

    public static Movie createMovie(int year) {
        return new Movie("Film", year);
    }

    public static void fillStore() {
        fillStore(MOVIES_TO_ADD_COUNT);
    }

    public static void fillStore(int moviesCount) {
        for (int i = 0; i < moviesCount; i++) {
            STORE.add(createMovie());
        }
    }

    public static URI createURI() {
        return URI.create(URL_BASE + MoviesServer.URL_MOVIES);
    }

    public static URI createURI(int id) {
        return URI.create(URL_BASE + MoviesServer.URL_MOVIES + "/" + id);
    }

    public static URI createURI(String id) {
        return URI.create(URL_BASE + MoviesServer.URL_MOVIES + "/" + id);
    }

    public static URI createYearURI(int year) {
        return URI.create(URL_BASE + MoviesServer.URL_MOVIES + "?" + GetMoviesHandler.YEAR_PARAMETER + "=" + year);
    }

    public static URI createYearURI(String parameter, int year) {
        return URI.create(URL_BASE + MoviesServer.URL_MOVIES + "?" + parameter + "=" + year);
    }

    public static URI createYearURI(String year) {
        return URI.create(URL_BASE + MoviesServer.URL_MOVIES + "?" + GetMoviesHandler.YEAR_PARAMETER + "=" + year);
    }

    public static HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void assertStatusCode(HttpRequest request, int expected) {
        String description = "Ответ должен вернуть " + expected;
        HttpResponse<String> response = TestUtility.sendRequest(request);
        assertEquals(expected, response.statusCode(), description);
    }

    public static void assertContentType(HttpRequest request) {
        String description = "Content-Type должен содержать формат данных и кодировку";
        HttpResponse<String> response = TestUtility.sendRequest(request);
        String contentTypeHeaderValue = response.headers().firstValue(HttpConstants.CONTENT_TYPE).orElse("");
        assertEquals(HttpConstants.CONTENT_TYPE_JSON, contentTypeHeaderValue, description);
    }

    public static void assertErrorMessage(HttpRequest request, String expected) {
        String description = "Ожидается описание ошибки '" + expected + "'";
        HttpResponse<String> response = TestUtility.sendRequest(request);
        ErrorResponse error = JsonUtility.deserialize(response.body(), ErrorResponse.class);
        assertEquals(expected, error.description(), description);
    }
}
