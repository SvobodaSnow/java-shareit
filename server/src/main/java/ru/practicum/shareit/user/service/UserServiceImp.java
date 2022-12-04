package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Primary
@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserStorage userStorage;

    @Override
    public UserDto createUser(UserDto userDto) {
        User newUser = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.save(newUser));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User updateUser = UserMapper.toUser(userDto, userId);
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
        return UserMapper.toUserDto(userStorage.save(updateUser));
    }

    @Override
    public User getUserById(Long userId) {
        return userStorage.getById(userId);
    }

    @Override
    public UserDto getUserDtoById(Long userId) {
        return UserMapper.toUserDto(userStorage.getById(userId));
    }

    @Override
    public void deleteUserById(Long userId) {
        userStorage.delete(getUserById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return UserMapper.toUserDtoList(userStorage.findAll());
    }
}
