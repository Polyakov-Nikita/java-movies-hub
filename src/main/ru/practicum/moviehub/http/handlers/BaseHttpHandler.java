package ru.practicum.moviehub.http.handlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.moviehub.api.ErrorResponse;
import ru.practicum.moviehub.http.HttpConstants;
import ru.practicum.moviehub.http.HttpStatusCode;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.utils.JsonUtility;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class BaseHttpHandler implements HttpHandler {
    public static final String MOVIE_NOT_FOUND_MESSAGE = "Фильм не найден";
    public static final String BAD_ID_MESSAGE = "Некорректный ID";

    protected static final int ID_PATH_LENGTH = 3;

    protected final MoviesStore store;

    public BaseHttpHandler(MoviesStore store) {
        this.store = store;
    }

    @Override
    public abstract void handle(HttpExchange exchange);

    protected void sendJson(HttpExchange exchange, int statusCode, String json) {
        setHeaders(exchange);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        sendHeaders(exchange, statusCode, bytes.length);
        writeBytes(exchange, bytes);
    }

    private static void setHeaders(HttpExchange exchange) {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set(HttpConstants.CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
    }

    private void sendHeaders(HttpExchange exchange, int statusCode, int length) {
        try {
            exchange.sendResponseHeaders(statusCode, length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeBytes(HttpExchange exchange, byte[] bytes) {
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String[] getPathParts(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        return path.split("/");
    }

    protected void sendNoContent(HttpExchange exchange, int statusCode) {
        setHeaders(exchange);
        sendHeaders(exchange, statusCode, -1);
    }

    protected void sendError(HttpExchange exchange, int statusCode, String errorMessage) {
        ErrorResponse error = new ErrorResponse(errorMessage);
        sendError(exchange, statusCode, error);
    }

    private void sendError(HttpExchange exchange, int statusCode, ErrorResponse error) {
        String errorJson = JsonUtility.serialize(error);
        sendJson(exchange, statusCode, errorJson);
    }

    protected void sendError(HttpExchange exchange, int statusCode, String errorMessage, List<String> details) {
        ErrorResponse error = new ErrorResponse(errorMessage, details);
        sendError(exchange, statusCode, error);
    }

    protected void processID(HttpExchange exchange, String idString, BiConsumer<HttpExchange, Integer> ifExists) {
        Optional<Integer> idOptional = parseInt(idString);
        idOptional.ifPresentOrElse(
                (id) -> {
                    if (store.containsID(id)) {
                        ifExists.accept(exchange, id);
                    } else {
                        sendError(exchange, HttpStatusCode.NOT_FOUND, MOVIE_NOT_FOUND_MESSAGE);
                    }
                },
                () -> sendError(exchange, HttpStatusCode.BAD_REQUEST, BAD_ID_MESSAGE)
        );
    }

    protected Optional<Integer> parseInt(String idString) {
        try {
            return Optional.of(Integer.parseInt(idString));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}