package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ItemDtoResponse createItem(
            @RequestBody ItemDtoRequest itemDtoRequest,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на добавление вещи");
        return itemService.createItem(itemDtoRequest, userId);
    }

    @GetMapping
    public List<ItemDtoResponse> getAllItems(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на формирование списка всех вещей");
        return itemService.getAllItemsByUser(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на отправку вещи с ID " + itemId);
        return itemService.getItemByIdWithBooking(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoResponse updateItem(
            @RequestBody ItemDtoRequest itemDtoRequest,
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на редактирование вещи с ID " + itemId + " пользователем с ID " + userId);
        return itemService.updateItem(itemDtoRequest, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> searchItem(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String text
    ) {
        log.info("Получен запрос на поиск вещи");
        return itemService.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(
            @RequestBody CommentDto commentDto,
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на добавление коментария к вещи вещи с ID ");
        return itemService.addComment(commentDto, itemId, userId);
    }
}
