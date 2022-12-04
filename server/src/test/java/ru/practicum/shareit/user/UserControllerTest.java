package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@TestPropertySource(properties = {"db.name=test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDto1 = new UserDto(
            15L,
            "NameTest1",
            "NameTest1@NameTest1.ru"
    );

    private final UserDto userDto2 = new UserDto(
            16L,
            "NameTest2",
            "NameTest2@NameTest2.ru"
    );

    @Test
    void saveNewUserTest() throws Exception {
        Mockito.when(userService.createUser(any())).thenReturn(userDto1);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect(jsonPath("$.email").value(userDto1.getEmail()));
    }

    @Test
    void getAllUsersTest() throws Exception {
        Mockito.when(userService.getAllUsers()).thenReturn(new ArrayList<>(List.of(userDto1, userDto2)));

        mvc.perform(get("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]['id']").value(userDto1.getId()))
                .andExpect(jsonPath("$[0]['name']").value(userDto1.getName()))
                .andExpect(jsonPath("$[0]['email']").value(userDto1.getEmail()))
                .andExpect(jsonPath("$[1]['id']").value(userDto2.getId()))
                .andExpect(jsonPath("$[1]['name']").value(userDto2.getName()))
                .andExpect(jsonPath("$[1]['email']").value(userDto2.getEmail()));
    }

    @Test
    void updateUserTest() throws Exception {
        Mockito.when(userService.updateUser(any(), any())).thenReturn(userDto2);

        Mockito.when(userService.updateUser(any(), eq(15L))).thenReturn(userDto1);

        mvc.perform(patch("/users/15")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect(jsonPath("$.email").value(userDto1.getEmail()));

        mvc.perform(patch("/users/16")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto2.getId()))
                .andExpect(jsonPath("$.name").value(userDto2.getName()))
                .andExpect(jsonPath("$.email").value(userDto2.getEmail()));
    }

    @Test
    void getUserByIdTest() throws Exception {
        Mockito.when(userService.getUserDtoById(eq(16L))).thenReturn(userDto2);

        Mockito.when(userService.getUserDtoById(eq(15L))).thenReturn(userDto1);

        mvc.perform(get("/users/15")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto1.getId()))
                .andExpect(jsonPath("$.name").value(userDto1.getName()))
                .andExpect(jsonPath("$.email").value(userDto1.getEmail()));

        mvc.perform(get("/users/16")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto2.getId()))
                .andExpect(jsonPath("$.name").value(userDto2.getName()))
                .andExpect(jsonPath("$.email").value(userDto2.getEmail()));
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        doNothing().when(userService).deleteUserById(anyLong());

        mvc.perform(delete("/users/15")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(15L);
    }
}