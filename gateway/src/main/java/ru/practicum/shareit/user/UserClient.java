package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

    public ResponseEntity<Object> addNewUser(UserDto userDto) {
        checkNameUser(userDto);
        checkEmailUser(userDto);
        return post("", userDto);
    }

    public ResponseEntity<Object> updateUser(UserDto userDto, Long userId) {
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> deleteUserById(Long userId) {
        return delete("/" + userId);
    }

    private void checkNameUser(UserDto user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Не указано имя пользователя");
        }
    }

    private void checkEmailUser(UserDto user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Не указан адрес почты пользователя");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Некоректный адрес почты");
        }
    }
}
