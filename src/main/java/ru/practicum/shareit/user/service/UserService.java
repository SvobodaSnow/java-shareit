package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    User getUserById(int userId);

    void deleteUserById(int userId);

    Map<Integer, User> getAllUsers();
}
