package ru.practicum.moviehub.http.handlers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.http.HttpConstants;
import ru.practicum.moviehub.http.HttpStatusCode;
import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.utils.JsonUtility;
import ru.practicum.moviehub.utils.TestUtility;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostMoviesHandlerTest {
    private static final MoviesServer SERVER = new MoviesServer(TestUtility.STORE, TestUtility.PORT);
    private static final Movie MOVIE_CORRECT = new Movie("Film", 1997);
    private static final Movie MOVIE_INCORRECT = new Movie("", 1655);

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
    public void postMovies_CorrectMovie_StatusCode() {
        HttpRequest request = createRequest(MOVIE_CORRECT);
        TestUtility.assertStatusCode(request, HttpStatusCode.CREATED);
    }

    private HttpRequest createRequest(Movie movie) {
        String movieSerialized = JsonUtility.serialize(movie);
        return HttpRequest.newBuilder()
                .uri(TestUtility.createURI())
                .POST(HttpRequest.BodyPublishers.ofString(movieSerialized))
                .header(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON)
                .build();
    }

    @Test
    public void postMovies_CorrectMovie_ContentType() {
        HttpRequest request = createRequest(MOVIE_CORRECT);
        TestUtility.assertContentType(request);
    }

    @Test
    public void postMovies_CorrectMovie_ID() {
        String description = "Ожидается фильм с присвоенным ID";
        HttpRequest request = createRequest(MOVIE_CORRECT);
        HttpResponse<String> response = TestUtility.sendRequest(request);
        Movie movie = JsonUtility.deserialize(response.body(), Movie.class);
        assertTrue(movie.id() > 0, description);
    }

    @Test
    public void postMovies_IncorrectMovie_StatusCode() {
        HttpRequest request = createRequest(MOVIE_INCORRECT);
        TestUtility.assertStatusCode(request, HttpStatusCode.UNPROCESSABLE_ENTITY);
    }

    @Test
    public void postMovies_IncorrectMovie_ContentType() {
        HttpRequest request = createRequest(MOVIE_INCORRECT);
        TestUtility.assertContentType(request);
    }

    @Test
    public void postMovies_IncorrectMovie_Error_Message() {
        HttpRequest request = createRequest(MOVIE_INCORRECT);
        TestUtility.assertErrorMessage(request, PostMoviesHandler.VALIDATION_ERROR_MESSAGE);
    }

    @Test
    public void postMovies_IncorrectMovie_Error_Details() {
        String description = "Ожидается массив с деталями проблемы";
        HttpRequest request = createRequest(MOVIE_INCORRECT);
        HttpResponse<String> response = TestUtility.sendRequest(request);
        ErrorResponse error = JsonUtility.deserialize(response.body(), ErrorResponse.class);
        assertFalse(error.details().isEmpty(), description);
    }

    @Test
    public void postMovies_IncorrectContentType_StatusCode() {
        HttpRequest request = createTypelessRequest();
        TestUtility.assertStatusCode(request, HttpStatusCode.UNSUPPORTED_MEDIA_TYPE);
    }

    private HttpRequest createTypelessRequest() {
        String movieSerialized = JsonUtility.serialize(MOVIE_CORRECT);
        return HttpRequest.newBuilder()
                .uri(TestUtility.createURI())
                .POST(HttpRequest.BodyPublishers.ofString(movieSerialized))
                .build();
    }

    @Test
    public void postMovies_IncorrectContentType_ContentType() {
        HttpRequest request = createTypelessRequest();
        TestUtility.assertContentType(request);
    }

    @Test
    public void postMovies_EmptyRequestBody_StatusCode() {
        HttpRequest request = createEmptyBodyRequest();
        TestUtility.assertStatusCode(request, HttpStatusCode.UNSUPPORTED_MEDIA_TYPE);
    }

    private HttpRequest createEmptyBodyRequest() {
        return HttpRequest.newBuilder()
                .uri(TestUtility.createURI())
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .header(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON)
                .build();
    }

    @Test
    public void postMovies_EmptyRequestBody_ContentType() {
        HttpRequest request = createEmptyBodyRequest();
        TestUtility.assertContentType(request);
    }
}