package ru.practicum.moviehub.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.http.HttpStatusCode;
import ru.practicum.moviehub.store.MoviesStore;

public class DeleteMoviesHandler extends BaseHttpHandler {
    public DeleteMoviesHandler(MoviesStore store) {
        super(store);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String[] pathParts = getPathParts(exchange);
        if (pathParts.length == ID_PATH_LENGTH) {
            processID(exchange, pathParts[2], this::deleteMovie);
        } else {
            sendError(exchange, HttpStatusCode.BAD_REQUEST, BAD_ID_MESSAGE);
        }
    }

    private void deleteMovie(HttpExchange exchange, int id) {
        store.remove(id);
        sendNoContent(exchange, HttpStatusCode.NO_CONTENT);
    }
}
