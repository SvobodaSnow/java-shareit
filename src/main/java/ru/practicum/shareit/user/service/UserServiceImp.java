package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.model.AlreadyExistsException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
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
        return userStorage.save(newUser);
    }

    @Override
    public User updateUser(User updateUser) {
        User oldUser = getUserById(updateUser.getId());
        if ((updateUser.getEmail() == null || updateUser.getEmail().isEmpty()) &&
                (updateUser.getName() == null || updateUser.getName().isEmpty())) {
            throw new ValidationException("Не указаны новые имя или email");
        }
        if (updateUser.getEmail() == null || updateUser.getEmail().isEmpty()) {
            updateUser.setEmail(oldUser.getEmail());
        }
        if (updateUser.getName() == null || updateUser.getName().isEmpty()) {
            updateUser.setName(oldUser.getName());
        }
        return userStorage.save(updateUser);
    }

    @Override
    public User getUserById(Long userId) {
        return userStorage.getById(userId);
    }

    @Override
    public void deleteUserById(Long userId) {
        userStorage.delete(getUserById(userId));
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.findAll();
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
}
