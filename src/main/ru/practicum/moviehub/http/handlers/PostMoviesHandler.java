package ru.practicum.moviehub.http.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.http.HttpConstants;
import ru.practicum.moviehub.http.HttpStatusCode;
import ru.practicum.moviehub.http.handlers.validation.MovieValidator;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.model.StoredMovie;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.utils.JsonUtility;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PostMoviesHandler extends BaseHttpHandler {
    public static final String VALIDATION_ERROR_MESSAGE = "Ошибка валидации";

    private final MovieValidator validator;

    public PostMoviesHandler(MoviesStore store, MovieValidator validator) {
        super(store);
        this.validator = validator;
    }

    @Override
    public void handle(HttpExchange exchange) {
        if (isJSONContentType(exchange.getRequestHeaders())) {
            processMovie(exchange);
        } else {
            sendNoContent(exchange, HttpStatusCode.UNSUPPORTED_MEDIA_TYPE);
        }
    }

    private boolean isJSONContentType(Headers headers) {
        String contentTypeHeaderValue = headers.getFirst(HttpConstants.CONTENT_TYPE);
        return contentTypeHeaderValue != null && contentTypeHeaderValue.equals(HttpConstants.CONTENT_TYPE_JSON);
    }

    private void processMovie(HttpExchange exchange) {
        Movie movie = getMovie(exchange.getRequestBody());
        List<String> validatorMessages = validator.validate(movie);
        if (validatorMessages.isEmpty()) {
            addToStore(exchange, movie);
        } else {
            sendError(exchange, HttpStatusCode.UNPROCESSABLE_ENTITY, VALIDATION_ERROR_MESSAGE, validatorMessages);
        }
    }

    private Movie getMovie(InputStream bodyStream) {
        String body = new String(getBodyBytes(bodyStream), StandardCharsets.UTF_8);
        return JsonUtility.deserialize(body, Movie.class);
    }

    private void addToStore(HttpExchange exchange, Movie movie) {
        int id = store.add(movie);
        StoredMovie storedMovie = createStored(movie, id);
        sendStoredMovie(storedMovie, exchange);
    }

    private StoredMovie createStored(Movie movie, int id) {
        return new StoredMovie(movie.title(), movie.year(), id);
    }

    private void sendStoredMovie(StoredMovie storedMovie, HttpExchange exchange) {
        String movieJson = JsonUtility.serialize(storedMovie);
        sendJson(exchange, HttpStatusCode.CREATED, movieJson);
    }

    private byte[] getBodyBytes(InputStream bodyStream) {
        try {
            return bodyStream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
