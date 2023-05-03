package org.example.service;

import org.example.dao.UserDao;
import org.example.dto.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

public class UserService {

    private final List<User> users = new ArrayList<>();
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAll() {
        return users;
    }

    public void add(User... users) {
        this.users.addAll(Arrays.asList(users));
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("username or password is null");
        }
        return users.stream()
                .filter(user -> user.username().equals(username))
                .filter(user -> user.password().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream().collect(Collectors.toMap(User::id, identity()));
    }

    public void clear() {
        users.clear();
    }

    public boolean delete(Integer userId) {
        return userDao.delete(userId);
    }
}
