package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long owner);

    List<ItemDto> getAllItemsByUser(Long userId);

    Item getItemById(Long itemId);

    ItemDto getItemByIdWithBooking(Long itemId, Long userId);

    ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    List<ItemDto> searchItem(String text);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);
}
