package ru.practicum.moviehub.store;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.utils.TestUtility;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.moviehub.utils.TestUtility.createMovie;

public class MoviesStoreTest {
    @AfterEach
    public void afterEach() {
        TestUtility.STORE.clear();
    }

    @Test
    public void add() {
        String description = "Ожидается уникальный ID";
        int firstId = TestUtility.STORE.add(createMovie());
        int secondId = TestUtility.STORE.add(createMovie());
        assertNotEquals(firstId, secondId, description);
    }

    @Test
    public void count() {
        String description = "Ожидается правильное количество записей";
        int moviesCount = 3;
        TestUtility.fillStore(moviesCount);
        assertEquals(moviesCount, TestUtility.STORE.count(), description);
    }

    @Test
    public void clear() {
        String description = "Ожидается количество записей, равное 0";
        TestUtility.fillStore();
        TestUtility.STORE.clear();
        assertEquals(0, TestUtility.STORE.count(), description);
    }

    @Test
    public void getAll() {
        String description = "Ожидается список фильмов";
        int moviesCount = 4;
        TestUtility.fillStore(moviesCount);
        assertEquals(moviesCount, TestUtility.STORE.getAll().size(), description);
    }

    @Test
    public void get_Exists() {
        String description = "Ожидается фильм";
        Movie created = TestUtility.createMovie();
        int id = TestUtility.STORE.add(created);
        assertEquals(created, TestUtility.STORE.get(id), description);
    }

    @Test
    public void get_NotExists() {
        String description = "Ожидается null";
        assertNull(TestUtility.STORE.get(TestUtility.ABSENT_ID), description);
    }

    @Test
    public void containsID_Exists() {
        int id = TestUtility.STORE.add(TestUtility.createMovie());
        String description = "Должен возвращать true, если содержит индекс " + id;
        assertTrue(TestUtility.STORE.containsID(id), description);
    }

    @Test
    public void containsID_NotExists() {
        String description = "Должен возвращать false, если не содержит индекс " + TestUtility.ABSENT_ID;
        assertFalse(TestUtility.STORE.containsID(TestUtility.ABSENT_ID), description);
    }

    @Test
    public void remove() {
        String description = "Должен возвращать размер на 1 меньше прежнего";
        int id = TestUtility.STORE.add(TestUtility.createMovie());
        int moviesCountBefore = TestUtility.STORE.count();
        TestUtility.STORE.remove(id);
        assertEquals(moviesCountBefore - 1, TestUtility.STORE.count(), description);
    }

    @Test
    public void getByYear_Exists() {
        String description = "Ожидается список фильмов";
        TestUtility.STORE.add(TestUtility.createMovie(TestUtility.CORRECT_YEAR - 1));
        TestUtility.STORE.add(TestUtility.createMovie(TestUtility.CORRECT_YEAR)); // 1
        TestUtility.STORE.add(TestUtility.createMovie(TestUtility.CORRECT_YEAR)); // 2
        TestUtility.STORE.add(TestUtility.createMovie(TestUtility.CORRECT_YEAR + 1));
        assertEquals(2, TestUtility.STORE.getByYear(TestUtility.CORRECT_YEAR).size(), description);
    }

    @Test
    public void getByYear_NotExists() {
        String description = "Ожидается пустой список";
        TestUtility.STORE.add(TestUtility.createMovie(TestUtility.CORRECT_YEAR - 1));
        TestUtility.STORE.add(TestUtility.createMovie(TestUtility.CORRECT_YEAR + 1));
        assertEquals(0, TestUtility.STORE.getByYear(TestUtility.CORRECT_YEAR).size(), description);
    }
}
