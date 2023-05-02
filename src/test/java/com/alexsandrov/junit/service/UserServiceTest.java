package com.alexsandrov.junit.service;

import org.example.service.UserService;
import org.example.dto.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Показывает ЖЦ Unit-теста
 * @BeforeAll -> @BeforeEach -> @Test -> @AfterEach -> @AfterAll
 *
 * @TestInstance - определяет стратегию для создания объекта класса
 * TestInstance.Lifecycle.PER_METHOD - создает новый экземпляр для каждого тестового метода
 * TestInstance.Lifecycle.PER_CLASS - создает один экземпляр для всех тестовых методов
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
    private static final User IVAN = new User(1, "Ivan", "123");
    private static final User PETR = new User(2, "Petr", "111");
    private UserService userService;

    @BeforeAll
    void init() {
        System.out.println("BeforeAll each: " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
        userService = new UserService();
    }

    @Test
    void logicFailIfUserDontNotExist() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login("dummy", IVAN.password());

        assertTrue(maybeUser.isEmpty());
    }
    @Test
    void logicFailIfPasswordNonCorrect() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.username(), "dummy");

        assertTrue(maybeUser.isEmpty());
    }
    @Test
    void logicSuccessIfUserExists() {
        userService.add(IVAN);
        Optional<User> maybeUser = userService.login(IVAN.username(), IVAN.password());

        assertTrue(maybeUser.isPresent());

        User user = maybeUser.get();
        assertEquals(IVAN, user);

    }

    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this);

        var users = userService.getAll();

        assertTrue(users.isEmpty(), "User list should be empty");
    }
    @Test
    void UsersSizeIfUserAdded() {
        System.out.println("Test 2: " + this);

        userService.add(IVAN);
        userService.add(PETR);

        var users = userService.getAll();

        assertEquals(2, users.size());
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("AfterEach: " + this);
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("AfterAll each: " + this);
    }
}
