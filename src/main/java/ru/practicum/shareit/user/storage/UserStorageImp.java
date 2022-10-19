package ru.practicum.shareit.user.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.GeneratorIdUsers;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Primary
@Component
public class UserStorageImp implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Autowired
    private GeneratorIdUsers generatorId;

    @Override
    public User addUser(User user) {
        user.setId(generatorId.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUserName(User user) {
        users.get(user.getId()).setName(user.getName());
        return users.get(user.getId());
    }

    @Override
    public User updateUserEmail(User user) {
        users.get(user.getId()).setEmail(user.getEmail());
        return users.get(user.getId());
    }

    @Override
    public User getUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public void deleteUserById(int userId) {
        users.remove(userId);
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        return users;
    }
}
