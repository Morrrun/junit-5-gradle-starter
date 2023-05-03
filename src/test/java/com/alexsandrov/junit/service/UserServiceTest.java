package com.alexsandrov.junit.service;


import com.alexsandrov.junit.TestBase;
import com.alexsandrov.junit.extension.paramresolver.UserServiceParamResolver;
import org.example.dao.UserDao;
import org.example.service.UserService;
import org.example.dto.User;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;


import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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
@ExtendWith({
        UserServiceParamResolver.class,
//        GlobalExtension.class
})
public class UserServiceTest extends TestBase {

    private static final User IVAN = new User(1, "Ivan", "123");
    private static final User PETR = new User(2, "Petr", "111");
//    @ForPresentation
    private UserService userService;
    private UserDao userDao;


    @BeforeAll
    void init() {
        System.out.println("BeforeAll each: " + this);
        System.out.println();
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each: " + this);
        System.out.println();
        this.userDao = Mockito.mock(UserDao.class);
        this.userService = new UserService(userDao);
    }

    @Test
    void shouldDeleteExistedUser() {
        userService.add(IVAN);
        /**
         * Это объект Stub, который используется mocks
         * и spies для ответа
         * на вызовы методов во время тестов
         */
//        Mockito.doReturn(true).when(userDao).delete(IVAN.id());
        /**
         * Это объект Dummy, который не используется,
         * нужен только для заполнения
         * параметров метода
         */
//        Mockito.doReturn(true).when(userDao).delete(Mockito.anyInt());


        Mockito.when(userDao.delete(IVAN.id()))
                .thenReturn(true)
                .thenReturn(false);

        var deleteResult = userService.delete(IVAN.id());
        System.out.println(deleteResult);
        System.out.println(userDao.delete(IVAN.id()));
        System.out.println(userDao.delete(IVAN.id()));

        assertThat(deleteResult).isTrue();
    }


    @Test
    void usersConvertedToMapById() {
        userService.add(IVAN, PETR);

        Map<Integer, User> users = userService.getAllConvertedById();
        assertAll(
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasKey(IVAN.id())),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasKey(PETR.id())),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasValue(PETR)),
                () -> MatcherAssert.assertThat(users, IsMapContaining.hasValue(IVAN))
        );
    }

    @Test
    void usersEmptyIfNoUserAdded() {
        System.out.println("Test 1: " + this);

        var users = userService.getAll();

        assertTrue(users.isEmpty(), "User list should be empty");
    }

    @Test
    void UsersSizeIfUserAdded() throws IOException {
        System.out.println("Test 2: " + this);
        userService.add(IVAN);
        userService.add(PETR);

        var users = userService.getAll();

        assertThat(users).hasSize(2);
    }

    @Nested
    @DisplayName("test user login functionality")
    @Tag("login")
    class LoginTest {

        @Test
        void checkLoginFunctionalityPerformance() {
            System.out.println(Thread.currentThread().getName());
            Optional<User> result = assertTimeoutPreemptively(Duration.ofMillis(200L), () -> {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(300L);
                return userService.login("dummy", IVAN.password());
            });
        }

        @Test
        @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
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
        @Disabled("flaky, need to see")
        void loginFailIfPasswordNonCorrect() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.username(), "dummy");

            assertTrue(maybeUser.isEmpty());
        }

        @RepeatedTest(5)
        void loginSuccessIfUserExists() {
            userService.add(IVAN);
            Optional<User> maybeUser = userService.login(IVAN.username(), IVAN.password());

            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));

        }

        @ParameterizedTest(name = "{arguments} test")
        @MethodSource("com.alexsandrov.junit.service.UserServiceTest#getArgumentsForLoginTest")
        @DisplayName("login parametrize test")
        void loginParametrizedTest(String username, String password, Optional<User> user) {
            userService.add(IVAN, PETR);

            Optional<User> maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);

        }

    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Ivan", "123", Optional.of(IVAN)),
                Arguments.of("Petr", "111", Optional.of(PETR)),
                Arguments.of("Petr", "dummy", Optional.empty()),
                Arguments.of("dummy", "111", Optional.empty())
        );
    }

    @AfterEach
    void deleteDataFromDatabase() {
        System.out.println("AfterEach: " + this);
        System.out.println();
        userService.clear();
    }

    @AfterAll
    void closeConnectionPool() {
        System.out.println("AfterAll each: " + this);
        System.out.println();
    }

}
