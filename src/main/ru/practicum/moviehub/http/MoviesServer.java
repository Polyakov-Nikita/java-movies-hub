package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpServer;
import ru.practicum.moviehub.http.handlers.MoviesHandler;
import ru.practicum.moviehub.store.MoviesStore;
import ru.practicum.moviehub.http.handlers.validation.MovieValidator;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MoviesServer {
    public static final String URL_MOVIES = "/movies";

    private final HttpServer server;

    public MoviesServer(MoviesStore store, int port) {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(URL_MOVIES, new MoviesHandler(store, new MovieValidator()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}