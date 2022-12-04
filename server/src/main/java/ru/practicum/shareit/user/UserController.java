package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на формирование списка всех пользователей");
        return userService.getAllUsers();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Получен запрос на обновление пользователя с ID " + userId);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Получен запрос на отправку пользователя с ID " + userId);
        return userService.getUserDtoById(userId);
    }

    @DeleteMapping("/{userId}")
    public String deleteUserById(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя с ID " + userId);
        userService.deleteUserById(userId);
        return "true";
    }
}
