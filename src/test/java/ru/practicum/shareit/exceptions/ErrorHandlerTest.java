package ru.practicum.shareit.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.model.AlreadyExistsException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@TestPropertySource(properties = {"db.name=test"})
class ErrorHandlerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = new UserDto(
            15L,
            "NameTest1",
            "NameTest1@NameTest1.ru"
    );

    @Test
    void handlerValidationExceptionTest() throws Exception {
        Mockito.when(userService.createUser(any())).thenThrow(new ValidationException("Не указано имя пользователя"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error").value("Не указано имя пользователя"));
    }

    @Test
    void handlerNotFoundExceptionTest() throws Exception {
        Mockito.when(userService.getUserDtoById(any())).thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(get("/users/20")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));
    }

    @Test
    void handlerEntityNotFoundExceptionTest() throws Exception {
        Mockito.when(userService.getUserDtoById(any()))
                .thenThrow(new EntityNotFoundException("Пользователь не найден"));

        mvc.perform(get("/users/20")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));
    }

    @Test
    void handlerAlreadyExistsExceptionTest() throws Exception {
        Mockito.when(userService.getUserDtoById(any()))
                .thenThrow(new AlreadyExistsException("Пользователь уже добавлен"));

        mvc.perform(get("/users/20")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409))
                .andExpect(jsonPath("$.error").value("Пользователь уже добавлен"));
    }
}