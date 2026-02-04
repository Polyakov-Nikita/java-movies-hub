package ru.practicum.moviehub.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.practicum.moviehub.model.Movie;

import java.util.List;

public class JsonUtility {
    private static final Gson GSON = new Gson();

    public static <S> String serialize(S element) {
        return GSON.toJson(element);
    }

    public static <D> D deserialize(String json, Class<D> dClass) {
        return GSON.fromJson(json, dClass);
    }

    public static List<Movie> parseMovies(String body) {
        return GSON.fromJson(body, new TypeToken<>() {
        });
    }
}
