package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Item item);

    List<ItemDto> getAllItemsByUser(Long userId);

    Item getItemById(Long itemId);

    ItemDto getItemByIdWithBooking(Long itemId, Long userId);

    ItemDto updateItem(Item item);

    List<ItemDto> searchItem(String text);

    CommentDto addComment(Comment comment);
}
