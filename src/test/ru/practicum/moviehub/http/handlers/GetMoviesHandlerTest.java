package ru.practicum.moviehub.http.handlers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.http.HttpStatusCode;
import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.utils.JsonUtility;
import ru.practicum.moviehub.utils.TestUtility;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetMoviesHandlerTest {
    private static final MoviesServer SERVER = new MoviesServer(TestUtility.STORE, TestUtility.PORT);
    private static final int NOT_EXISTING_YEAR = 0;

    @BeforeAll
    public static void beforeAll() {
        SERVER.start();
    }

    @AfterEach
    public void afterEach() {
        TestUtility.STORE.clear();
    }

    @AfterAll
    public static void afterAll() {
        SERVER.stop();
    }

    @Test
    public void getMovies_Empty_StatusCode() {
        HttpRequest request = createRequest();
        TestUtility.assertStatusCode(request, HttpStatusCode.OK);
    }

    private HttpRequest createRequest() {
        return HttpRequest.newBuilder()
                .uri(TestUtility.createURI())
                .GET()
                .build();
    }

    @Test
    public void getMovies_Empty_ContentType() {
        HttpRequest request = createRequest();
        TestUtility.assertContentType(request);
    }

    @Test
    public void getMovies_Empty_JSONArray() {
        HttpRequest request = createRequest();
        assertJsonArray(request, 0);
    }

    private void assertJsonArray(HttpRequest request, int expected) {
        String description = "Ожидается JSON-массив из " + expected + " элементов";
        HttpResponse<String> response = TestUtility.sendRequest(request);
        List<Movie> movies = JsonUtility.parseMovies(response.body());
        assertEquals(expected, movies.size(), description);
    }

    @Test
    public void getMovies_Filled_StatusCode() {
        TestUtility.fillStore();
        HttpRequest request = createRequest();
        TestUtility.assertStatusCode(request, HttpStatusCode.OK);
    }

    @Test
    public void getMovies_Filled_ContentType() {
        TestUtility.fillStore();
        HttpRequest request = createRequest();
        TestUtility.assertContentType(request);
    }

    @Test
    public void getMovies_Filled_JSONArray() {
        TestUtility.fillStore();
        HttpRequest request = createRequest();
        assertJsonArray(request, TestUtility.STORE.count());
    }

    @Test
    public void getMovie_Exists_StatusCode() {
        int id = TestUtility.STORE.add(TestUtility.createMovie());
        HttpRequest request = createRequest(id);
        TestUtility.assertStatusCode(request, HttpStatusCode.OK);
    }

    private HttpRequest createRequest(int id) {
        return HttpRequest.newBuilder()
                .uri(TestUtility.createURI(id))
                .GET()
                .build();
    }

    @Test
    public void getMovie_Exists_ContentType() {
        int id = TestUtility.STORE.add(TestUtility.createMovie());
        HttpRequest request = createRequest(id);
        TestUtility.assertContentType(request);
    }

    @Test
    public void getMovie_Exists_JSONMovie() {
        String description = "Ожидается JSON-объект фильма";
        Movie created = TestUtility.createMovie();
        int id = TestUtility.STORE.add(created);
        HttpRequest request = createRequest(id);
        HttpResponse<String> response = TestUtility.sendRequest(request);
        Movie resieved = JsonUtility.deserialize(response.body(), Movie.class);
        assertEquals(created, resieved, description);
    }

    @Test
    public void getMovie_NotExists_StatusCode() {
        HttpRequest request = createRequest(TestUtility.ABSENT_ID);
        TestUtility.assertStatusCode(request, HttpStatusCode.NOT_FOUND);
    }

    @Test
    public void getMovie_NotExists_ContentType() {
        HttpRequest request = createRequest(TestUtility.ABSENT_ID);
        TestUtility.assertContentType(request);
    }

    @Test
    public void getMovie_NotExists_Error() {
        HttpRequest request = createRequest(TestUtility.ABSENT_ID);
        TestUtility.assertErrorMessage(request, GetMoviesHandler.MOVIE_NOT_FOUND_MESSAGE);
    }

    @Test
    public void getMovie_IncorrectID_StatusCode() {
        HttpRequest request = createIncorrectIDRequest();
        TestUtility.assertStatusCode(request, HttpStatusCode.BAD_REQUEST);
    }

    private HttpRequest createIncorrectIDRequest() {
        return HttpRequest.newBuilder()
                .uri(TestUtility.createURI("id"))
                .GET()
                .build();
    }

    @Test
    public void getMovie_IncorrectID_ContentType() {
        HttpRequest request = createIncorrectIDRequest();
        TestUtility.assertContentType(request);
    }

    @Test
    public void getMovie_IncorrectID_Error() {
        HttpRequest request = createIncorrectIDRequest();
        TestUtility.assertErrorMessage(request, GetMoviesHandler.BAD_ID_MESSAGE);
    }

    @Test
    public void getMoviesByYear_Filled_CorrectYear_StatusCode() {
        fillStoreByYear(3);
        HttpRequest request = createYearRequest(TestUtility.CORRECT_YEAR);
        TestUtility.assertStatusCode(request, HttpStatusCode.OK);
    }

    private void fillStoreByYear(int count) {
        TestUtility.STORE.add(TestUtility.createMovie(TestUtility.CORRECT_YEAR - 5));
        for (int i = 0; i < count; i++) {
            TestUtility.STORE.add(TestUtility.createMovie(TestUtility.CORRECT_YEAR));
        }
        TestUtility.STORE.add(TestUtility.createMovie(TestUtility.CORRECT_YEAR + 1));
        TestUtility.STORE.add(TestUtility.createMovie(TestUtility.CORRECT_YEAR + 1));
    }

    private HttpRequest createYearRequest(int year) {
        return HttpRequest.newBuilder()
                .uri(TestUtility.createYearURI(year))
                .GET()
                .build();
    }

    @Test
    public void getMoviesByYear_CorrectYear_Filled_ContentType() {
        fillStoreByYear(5);
        HttpRequest request = createYearRequest(TestUtility.CORRECT_YEAR);
        TestUtility.assertContentType(request);
    }

    @Test
    public void getMoviesByYear_CorrectYear_Filled_JSONArray() {
        int yearMoviesCount = 3;
        fillStoreByYear(yearMoviesCount);
        HttpRequest request = createYearRequest(TestUtility.CORRECT_YEAR);
        assertJsonArray(request, yearMoviesCount);
    }

    @Test
    public void getMoviesByYear_CorrectYear_Empty_StatusCode() {
        fillStoreByYear(3);
        HttpRequest request = createYearRequest(NOT_EXISTING_YEAR);
        TestUtility.assertStatusCode(request, HttpStatusCode.OK);
    }

    @Test
    public void getMoviesByYear_CorrectYear_Empty_ContentType() {
        fillStoreByYear(3);
        HttpRequest request = createYearRequest(NOT_EXISTING_YEAR);
        TestUtility.assertContentType(request);
    }

    @Test
    public void getMoviesByYear_CorrectYear_Empty_JSONArray() {
        fillStoreByYear(5);
        HttpRequest request = createYearRequest(NOT_EXISTING_YEAR);
        assertJsonArray(request, 0);
    }

    @Test
    public void getMoviesByYear_IncorrectParameter_StatusCode() {
        HttpRequest request = createIncorrectParameterRequest();
        TestUtility.assertStatusCode(request, HttpStatusCode.BAD_REQUEST);
    }

    private HttpRequest createIncorrectParameterRequest() {
        return HttpRequest.newBuilder()
                .uri(TestUtility.createYearURI("notYear", TestUtility.CORRECT_YEAR))
                .GET()
                .build();
    }

    @Test
    public void getMoviesByYear_IncorrectParameter_ContentType() {
        HttpRequest request = createIncorrectParameterRequest();
        TestUtility.assertContentType(request);
    }

    @Test
    public void getMoviesByYear_IncorrectParameter_Error() {
        HttpRequest request = createIncorrectParameterRequest();
        TestUtility.assertErrorMessage(request, GetMoviesHandler.INCORRECT_YEAR_PARAMETER_MESSAGE);
    }

    @Test
    public void getMoviesByYear_IncorrectYear_StatusCode() {
        HttpRequest request = createIncorrectYearRequest();
        TestUtility.assertStatusCode(request, HttpStatusCode.BAD_REQUEST);
    }

    private HttpRequest createIncorrectYearRequest() {
        return HttpRequest.newBuilder()
                .uri(TestUtility.createYearURI("notYear"))
                .GET()
                .build();
    }

    @Test
    public void getMoviesByYear_IncorrectYear_ContentType() {
        HttpRequest request = createIncorrectYearRequest();
        TestUtility.assertContentType(request);
    }

    @Test
    public void getMoviesByYear_IncorrectYear_Error() {
        HttpRequest request = createIncorrectYearRequest();
        TestUtility.assertErrorMessage(request, GetMoviesHandler.INCORRECT_YEAR_MESSAGE);
    }
}
