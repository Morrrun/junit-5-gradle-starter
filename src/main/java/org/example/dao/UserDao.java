package org.example.dao;

import java.sql.DriverManager;
import java.sql.SQLException;

public class UserDao {


    public boolean delete(Integer userId) {
        try (var connection = DriverManager.getConnection("url", "username", "password")) {
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
