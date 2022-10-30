package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    User updateUserName(User user);

    User updateUserEmail(User user);

    User getUserById(int userId);

    void deleteUserById(int userId);

    Map<Integer, User> getAllUsers();
}
