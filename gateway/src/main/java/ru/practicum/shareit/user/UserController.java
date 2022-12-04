package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Positive;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получен запрос на добавление пользователя");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя");
        return userClient.addNewUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @Positive @PathVariable Long userId) {
        log.info("Получен запрос на обновление пользователя с ID " + userId);
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@Positive @PathVariable Long userId) {
        log.info("Получен запрос на отправку пользователя с ID " + userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@Positive @PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя с ID " + userId);
        return userClient.deleteUserById(userId);
    }
}
