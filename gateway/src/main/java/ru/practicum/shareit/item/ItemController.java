package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestBody ItemDtoRequest itemDtoRequest,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на добавление вещи от пользователя с ID: " + userId);
        return itemClient.createItem(itemDtoRequest, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItem(
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на формирование списка всех вещей от пользователя с ID: " + userId);
        return itemClient.getAllItems(from, size, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на отправку вещи с ID " + itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestBody ItemDtoRequest itemDtoRequest,
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на редактирование вещи с ID " + itemId + " пользователем с ID " + userId);
        return itemClient.updateItem(itemDtoRequest, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size,
            @RequestParam String text
    ) {
        log.info("Получен запрос на поиск вещи. Текст поискового запроса: " + text);
        return itemClient.searchItem(userId, from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestBody CommentDto commentDto,
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на добавление коментария к вещи вещи с ID: " + itemId +
                " от пользователя с ID: " + userId);
        return itemClient.addComment(commentDto, itemId, userId);
    }
}
