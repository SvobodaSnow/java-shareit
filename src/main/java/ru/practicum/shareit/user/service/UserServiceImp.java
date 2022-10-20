package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.model.AlreadyExistsException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

@Primary
@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserStorage userStorage;

    @Override
    public User createUser(User newUser) {
        checkNameUser(newUser);
        checkEmailUser(newUser);
        checkDuplicate(newUser);
        return userStorage.addUser(newUser);
    }

    @Override
    public User updateUser(User updateUser) {
        getUserById(updateUser.getId());
        checkDuplicate(updateUser);
        if (updateUser.getEmail() == null && updateUser.getName() != null) {
            return userStorage.updateUserName(updateUser);
        } else if (updateUser.getEmail() != null && updateUser.getName() == null) {
            return userStorage.updateUserEmail(updateUser);
        } else if (updateUser.getEmail() != null && updateUser.getName() != null) {
            return userStorage.updateUser(updateUser);
        } else {
            throw new ValidationException("Не указаны новые имя или email");
        }
    }

    @Override
    public User getUserById(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователя с ID " + userId + " нет в списке");
        }
        return user;
    }

    @Override
    public void deleteUserById(int userId) {
        userStorage.deleteUserById(userId);
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    private void checkNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Не указано имя пользователя");
        }
    }

    private void checkEmailUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Не указано имя пользователя");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Некоректный адрес почты");
        }
    }

    private void checkDuplicate(User user) {
        for (User userCheck : userStorage.getAllUsers().values()) {
            if (userCheck.getEmail().equals(user.getEmail())) {
                throw new AlreadyExistsException("Пользователь с email: " + user.getEmail() + " уже зарегистрирован");
            }
        }
    }
}
