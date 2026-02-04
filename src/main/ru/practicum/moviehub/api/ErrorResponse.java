package ru.practicum.moviehub.api;

import java.util.ArrayList;
import java.util.List;

public record ErrorResponse(String description, List<String> details) {
    public ErrorResponse(String description) {
        this(description, new ArrayList<>());
    }
}
