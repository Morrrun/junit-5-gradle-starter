package org.example.service;

import org.example.dto.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final List<User> users = new ArrayList<>();

    public List<User> getAll() {
        return users;
    }

    public boolean add(User user) {
        return  users.add(user);
    }

    public Optional<User> login(String username, String password) {
        if(username == null || password == null) {
            throw new IllegalArgumentException("username or password is null");
        }
        return users.stream()
                .filter(user -> user.username().equals(username))
                .filter(user -> user.password().equals(password))
                .findFirst();
    }
}
