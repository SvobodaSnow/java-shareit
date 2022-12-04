package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@TestPropertySource(properties = {"db.name=test"})
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            20L,
            "ItemRequestDescription",
            LocalDateTime.now(),
            null
    );

    @Test
    void saveNewItemRequestTest() throws Exception {
        Mockito.when(itemRequestService.createItemRequest(any(), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void getItemRequestForUserTest() throws Exception {
        Mockito.when(itemRequestService.getItemRequestForUser(anyLong())).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]['id']").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0]['description']").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0]['created']").isNotEmpty())
                .andExpect(jsonPath("$[0]['items']").isEmpty());
    }

    @Test
    void getAllItemRequestTest() throws Exception {
        Mockito.when(itemRequestService.getAllItemRequest(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]['id']").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0]['description']").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0]['created']").isNotEmpty())
                .andExpect(jsonPath("$[0]['items']").isEmpty());
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        Mockito.when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto);

        mvc.perform(get("/requests/20")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());
    }
}