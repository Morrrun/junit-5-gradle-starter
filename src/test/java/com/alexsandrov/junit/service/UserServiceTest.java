package com.alexsandrov.junit.service;


import org.example.service.UserService;
import org.example.dto.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;


import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Показывает ЖЦ Unit-теста
 *
 * @BeforeAll -> @BeforeEach -> @Test -> @AfterEach -> @AfterAll
 * @TestInstance - определяет стратегию для создания объекта класса
 * TestInstance.Lifecycle.PER_METHOD - создает новый экземпляр для каждого тестового метода
 * TestInstance.Lifecycle.PER_CLASS - создает один экземпляр для всех тестовых методов
 */
@Tag("fast")
@Tag("user")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
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
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedById();
//      Library Hamcrest
        assertAll(
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.id())),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasKey(PETR.id())),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasValue(PETR)),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasValue(IVAN))
        );

//        Library AsserJ
//        assertAll(
//                () -> assertThat(users).containsKeys(IVAN.id(), PETR.id()),
//                () -> assertThat(users).containsValues(IVAN, PETR)
//        );

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

        assertThat(users).hasSize(2);
//        DEFAULT
//        assertEquals(2, users.size());
    }

    @Nested
    @DisplayName("test user login functionality")
    @Tag("login")
    class LoginTest {
        @Test
        void throwExceptionIfUsernameOrPasswordIsNull() {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> userService.login(null, "dummy")),

                    () -> assertThrows(IllegalArgumentException.class,
                            () -> userService.login("dummy", null))
            );

        }
        @Test
        void loginFailIfUserDontNotExist() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login("dummy", IVAN.password());

            assertTrue(maybeUser.isEmpty());
        }

        @Test
        void loginFailIfPasswordNonCorrect() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.username(), "dummy");

            assertTrue(maybeUser.isEmpty());
        }

        @Test
        void loginSuccessIfUserExists() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.username(), IVAN.password());

            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
//        DEFAULT
//        assertTrue(maybeUser.isPresent());
//        User user = maybeUser.get();
//        assertEquals(IVAN, user);

        }
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
