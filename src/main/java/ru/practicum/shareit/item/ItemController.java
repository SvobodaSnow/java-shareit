package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
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
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получен запрос на добавление вещи");
        return ItemMapper.toItemDto(itemService.createItem(ItemMapper.toItem(itemDto, userId)));
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получен запрос на формирование списка всех вещей");
        return ItemMapper.toItemDtoList(itemService.getAllItemsByUser(userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        log.info("Получен запрос на отправку вещи с ID " + itemId);
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestBody ItemDto itemDto,
            @PathVariable int itemId,
            @RequestHeader("X-Sharer-User-Id") int userId
    ) {
        log.info("Получен запрос на редактирование вещи с ID " + itemId + " пользователем с ID " + userId);
        return ItemMapper.toItemDto(itemService.updateItem(ItemMapper.toItem(itemDto, itemId, userId)));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        log.info("Получен запрос на поиск вещи");
        return ItemMapper.toItemDtoList(itemService.searchItem(text));
    }
}
