package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @PostMapping
    public ItemDtoResponse createItem(
            @RequestBody ItemDtoRequest itemDtoRequest,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на добавление вещи от пользователя с ID: " + userId);
        return itemService.createItem(itemDtoRequest, userId);
    }

    @GetMapping
    public List<ItemDtoResponse> getAllItems(
            @PositiveOrZero  @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на формирование списка всех вещей от пользователя с ID: " + userId);
        return itemService.getAllItemsByUser(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse getItemById(
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на отправку вещи с ID " + itemId);
        return itemService.getItemByIdWithBooking(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoResponse updateItem(
            @RequestBody ItemDtoRequest itemDtoRequest,
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на редактирование вещи с ID " + itemId + " пользователем с ID " + userId);
        return itemService.updateItem(itemDtoRequest, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> searchItem(
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size,
            @RequestParam String text
    ) {
        log.info("Получен запрос на поиск вещи. Текст поискового запроса: " + text);
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestBody @Valid CommentDto commentDto,
            @Positive @PathVariable Long itemId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на добавление коментария к вещи вещи с ID: " + itemId +
                " от пользователя с ID: " + userId);
        return itemService.addComment(commentDto, itemId, userId);
    }
}
