package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDtoResponse createItem(ItemDtoRequest itemDtoRequest, Long owner);

    List<ItemDtoResponse> getAllItemsByUser(Long userId, int from, int size);

    Item getItemById(Long itemId);

    ItemDtoResponse getItemByIdWithBooking(Long itemId, Long userId);

    ItemDtoResponse updateItem(ItemDtoRequest itemDtoRequest, Long itemId, Long ownerId);

    List<ItemDtoResponse> searchItem(String text, int from, int size);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long userId);

    List<Item> getItemsByRequestId(Long requestId);

    List<Item> getItemsByRequestIdList(List<Long> itemRequestIdList);
}
