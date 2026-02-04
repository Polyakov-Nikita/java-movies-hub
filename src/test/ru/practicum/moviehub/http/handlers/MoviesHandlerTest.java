package ru.practicum.moviehub.http.handlers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.http.HttpStatusCode;
import ru.practicum.moviehub.http.MoviesServer;
import ru.practicum.moviehub.utils.TestUtility;

import java.net.http.HttpRequest;

public class MoviesHandlerTest {
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
    public void handleOtherMethod_StatusCode() {
        HttpRequest request = createRequest();
        TestUtility.assertStatusCode(request, HttpStatusCode.METHOD_NOT_ALLOWED);
    }

    private HttpRequest createRequest() {
        return HttpRequest.newBuilder()
                .uri(TestUtility.createURI())
                .HEAD()
                .build();
    }

    @Test
    public void handleOtherMethod_ContentType() {
        HttpRequest request = createRequest();
        TestUtility.assertContentType(request);
    }
}
