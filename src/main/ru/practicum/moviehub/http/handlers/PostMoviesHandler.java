package ru.practicum.moviehub.http.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.http.HttpConstants;
import ru.practicum.moviehub.http.HttpStatusCode;
import ru.practicum.moviehub.http.handlers.validation.MovieValidator;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.utils.JsonUtility;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

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
            processRequest(exchange);
        } else {
            sendNoContent(exchange, HttpStatusCode.UNSUPPORTED_MEDIA_TYPE);
        }
    }

    private boolean isJSONContentType(Headers headers) {
        String contentTypeHeaderValue = headers.getFirst(HttpConstants.CONTENT_TYPE);
        return contentTypeHeaderValue != null && contentTypeHeaderValue.equals(HttpConstants.CONTENT_TYPE_JSON);
    }

    private void processRequest(HttpExchange exchange) {
        Optional<Movie> movieOptional = getMovie(exchange.getRequestBody());
        movieOptional.ifPresentOrElse(
                movie -> processMovie(exchange, movie),
                () -> sendNoContent(exchange, HttpStatusCode.UNSUPPORTED_MEDIA_TYPE)
        );
    }

    private Optional<Movie> getMovie(InputStream bodyStream) {
        String body = new String(getBodyBytes(bodyStream), StandardCharsets.UTF_8);
        if (body.isEmpty()) {
            return Optional.empty();
        }
        return parseMovie(body);
    }

    private static Optional<Movie> parseMovie(String body) {
        try {
            return Optional.of(JsonUtility.deserialize(body, Movie.class));
        } catch (JsonSyntaxException e) {
            return Optional.empty();
        }
    }

    private void processMovie(HttpExchange exchange, Movie movie) {
        List<String> validatorMessages = validator.validate(movie);
        if (validatorMessages.isEmpty()) {
            addToStore(exchange, movie);
        } else {
            sendError(exchange, HttpStatusCode.UNPROCESSABLE_ENTITY, VALIDATION_ERROR_MESSAGE, validatorMessages);
        }
    }

    private void addToStore(HttpExchange exchange, Movie movie) {
        int id = store.add(movie);
        sendStoredMovie(exchange, new Movie(movie, id));
    }

    private void sendStoredMovie(HttpExchange exchange, Movie movie) {
        String movieJson = JsonUtility.serialize(movie);
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
