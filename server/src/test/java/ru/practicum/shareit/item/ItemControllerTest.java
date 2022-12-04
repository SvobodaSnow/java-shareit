package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@TestPropertySource(properties = {"db.name=test"})
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final ItemDtoResponse itemDtoResponse = new ItemDtoResponse(
            20L,
            "ItemName",
            "ItemDescription",
            true,
            12L,
            null,
            null,
            null
    );

    private final ItemDtoRequest itemDtoRequest = new ItemDtoRequest(
            "ItemName",
            "ItemDescription",
            true,
            null
    );

    private final CommentDto commentDto = new CommentDto(
            20L,
            "CommentText",
            "CommentAuthorName",
            LocalDateTime.now()
    );

    @Test
    void saveNewItemTest() throws Exception {
        Mockito.when(itemService.createItem(any(), anyLong())).thenReturn(itemDtoResponse);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoResponse.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoResponse.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoResponse.getAvailable()));
    }

    @Test
    void getAllItemsTest() throws Exception {
        Mockito.when(itemService.getAllItemsByUser(any(), anyInt(), anyInt())).thenReturn(List.of(itemDtoResponse));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]['id']").value(itemDtoResponse.getId()))
                .andExpect(jsonPath("$[0]['name']").value(itemDtoResponse.getName()))
                .andExpect(jsonPath("$[0]['description']").value(itemDtoResponse.getDescription()))
                .andExpect(jsonPath("$[0]['available']").value(itemDtoResponse.getAvailable()));
    }

    @Test
    void getItemByIdTest() throws Exception {
        Mockito.when(itemService.getItemByIdWithBooking(anyLong(), anyLong())).thenReturn(itemDtoResponse);

        mvc.perform(get("/items/20")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoResponse.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoResponse.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoResponse.getAvailable()));
    }

    @Test
    void updateItemTest() throws Exception {
        Mockito.when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(itemDtoResponse);

        mvc.perform(patch("/items/20")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoResponse.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoResponse.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoResponse.getAvailable()));
    }

    @Test
    void searchItemTest() throws Exception {
        String search = "search";

        Mockito.when(itemService.searchItem(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDtoResponse));

        mvc.perform(get("/items/search?text=" + search)
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]['id']").value(itemDtoResponse.getId()))
                .andExpect(jsonPath("$[0]['name']").value(itemDtoResponse.getName()))
                .andExpect(jsonPath("$[0]['description']").value(itemDtoResponse.getDescription()))
                .andExpect(jsonPath("$[0]['available']").value(itemDtoResponse.getAvailable()));
    }

    @Test
    void addCommentTest() throws Exception {
        Mockito.when(itemService.addComment(any(), anyLong(), anyLong())).thenReturn(commentDto);

        mvc.perform(post("/items/20/comment")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @Test
    void addItemWithoutUserHandlerTest() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500))
                .andExpect(
                        jsonPath("$.error").value("Required request header 'X-Sharer-User-Id' " +
                                "for method parameter type Long is not present")
                );
    }
}