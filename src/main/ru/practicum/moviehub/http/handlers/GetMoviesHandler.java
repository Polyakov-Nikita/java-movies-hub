package ru.practicum.moviehub.http.handlers;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.http.HttpStatusCode;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.utils.JsonUtility;

import java.util.List;
import java.util.Optional;

public class GetMoviesHandler extends BaseHttpHandler {
    public static final String YEAR_PARAMETER = "year";
    public static final String INCORRECT_YEAR_PARAMETER_MESSAGE = "Некорректный параметр запроса";
    public static final String INCORRECT_YEAR_MESSAGE = "Значение параметра запроса '"+ YEAR_PARAMETER + "' не число";

    private static final int QUERY_PARTS_LENGTH = 2;

    public GetMoviesHandler(MoviesStore store) {
        super(store);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        String[] pathParts = getPathParts(exchange);
        if (query != null) {
            processQuery(exchange, query);
        } else if (pathParts.length == ID_PATH_LENGTH) {
            processID(exchange, pathParts[2], this::sendMovie);
        } else {
            sendAllMovies(exchange);
        }
    }

    private void processQuery(HttpExchange exchange, String query) {
        String[] queryParts = query.split("=");
        if (queryParts.length == QUERY_PARTS_LENGTH && queryParts[0].equals(YEAR_PARAMETER)) {
            Optional<Integer> yearOptional = parseInt(queryParts[1]);
            yearOptional.ifPresentOrElse(
                    year -> processYear(exchange, year),
                    () -> sendError(exchange, HttpStatusCode.BAD_REQUEST, INCORRECT_YEAR_MESSAGE)
            );
        } else {
            sendError(exchange, HttpStatusCode.BAD_REQUEST, INCORRECT_YEAR_PARAMETER_MESSAGE);
        }
    }

    private void processYear(HttpExchange exchange, int year) {
        List<Movie> movies = store.getByYear(year);
        sendMovies(exchange, movies);
    }

    private void sendMovies(HttpExchange exchange, List<Movie> movies) {
        String moviesJson = JsonUtility.serialize(movies);
        sendJson(exchange, HttpStatusCode.OK, moviesJson);
    }

    private void sendMovie(HttpExchange exchange, int id) {
        Movie movie = store.get(id);
        String movieJson = JsonUtility.serialize(movie);
        sendJson(exchange, HttpStatusCode.OK, movieJson);
    }

    private void sendAllMovies(HttpExchange exchange) {
        List<Movie> movies = store.getAll();
        sendMovies(exchange, movies);
    }
}
