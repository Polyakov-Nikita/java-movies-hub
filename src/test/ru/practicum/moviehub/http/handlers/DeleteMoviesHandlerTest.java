package ru.practicum.moviehub.http.handlers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.http.HttpStatusCode;
import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.utils.TestUtility;

import java.net.http.HttpRequest;

public class DeleteMoviesHandlerTest {
    private static final MoviesServer SERVER = new MoviesServer(TestUtility.STORE, TestUtility.PORT);

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
    public void deleteMovie_Exists_StatusCode() {
        int id = TestUtility.STORE.add(TestUtility.createMovie());
        HttpRequest request = createRequest(id);
        TestUtility.assertStatusCode(request, HttpStatusCode.NO_CONTENT);
    }

    private HttpRequest createRequest(int id) {
        return HttpRequest.newBuilder()
                .uri(TestUtility.createURI(id))
                .DELETE()
                .build();
    }

    @Test
    public void deleteMovie_Exists_ContentType() {
        int id = TestUtility.STORE.add(TestUtility.createMovie());
        HttpRequest request = createRequest(id);
        TestUtility.assertContentType(request);
    }

    @Test
    public void deleteMovie_NotExists_StatusCode() {
        HttpRequest request = createRequest(TestUtility.ABSENT_ID);
        TestUtility.assertStatusCode(request, HttpStatusCode.NOT_FOUND);
    }

    @Test
    public void deleteMovie_NotExists_ContentType() {
        HttpRequest request = createRequest(TestUtility.ABSENT_ID);
        TestUtility.assertContentType(request);
    }

    @Test
    public void deleteMovie_NotExists_Error() {
        HttpRequest request = createRequest(TestUtility.ABSENT_ID);
        TestUtility.assertErrorMessage(request, GetMoviesHandler.MOVIE_NOT_FOUND_MESSAGE);
    }

    @Test
    public void deleteMovie_IncorrectID_StatusCode() {
        HttpRequest request = createIncorrectIDRequest();
        TestUtility.assertStatusCode(request, HttpStatusCode.BAD_REQUEST);
    }

    private HttpRequest createIncorrectIDRequest() {
        return HttpRequest.newBuilder()
                .uri(TestUtility.createURI("id"))
                .DELETE()
                .build();
    }

    @Test
    public void deleteMovie_IncorrectID_ContentType() {
        HttpRequest request = createIncorrectIDRequest();
        TestUtility.assertContentType(request);
    }

    @Test
    public void deleteMovie_IncorrectID_Error() {
        HttpRequest request = createIncorrectIDRequest();
        TestUtility.assertErrorMessage(request, GetMoviesHandler.BAD_ID_MESSAGE);
    }

    @Test
    public void deleteMovie_NoID_StatusCode() {
        HttpRequest request = createNoIDRequest();
        TestUtility.assertStatusCode(request, HttpStatusCode.BAD_REQUEST);
    }

    private HttpRequest createNoIDRequest() {
        return HttpRequest.newBuilder()
                .uri(TestUtility.createURI())
                .DELETE()
                .build();
    }

    @Test
    public void deleteMovie_NoID_ContentType() {
        HttpRequest request = createNoIDRequest();
        TestUtility.assertContentType(request);
    }

    @Test
    public void deleteMovie_NoID_Error() {
        HttpRequest request = createNoIDRequest();
        TestUtility.assertErrorMessage(request, GetMoviesHandler.BAD_ID_MESSAGE);
    }
}
