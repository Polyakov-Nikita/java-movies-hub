package ru.practicum.moviehub.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.http.HttpStatusCode;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.http.handlers.validation.MovieValidator;

public class MoviesHandler extends BaseHttpHandler {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";

    private final BaseHttpHandler getHandler;
    private final BaseHttpHandler postHandler;
    private final BaseHttpHandler deleteHandler;

    public MoviesHandler(MoviesStore store, MovieValidator validator) {
        super(store);
        getHandler = new GetMoviesHandler(store);
        postHandler = new PostMoviesHandler(store, validator);
        deleteHandler = new DeleteMoviesHandler(store);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        switch (method) {
            case GET -> getHandler.handle(exchange);
            case POST -> postHandler.handle(exchange);
            case DELETE -> deleteHandler.handle(exchange);
            default -> sendNoContent(exchange, HttpStatusCode.METHOD_NOT_ALLOWED);
        }
    }
}
