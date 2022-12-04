package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requester) {
        return new ItemRequest(
                null,
                itemRequestDto.getDescription(),
                requester,
                LocalDateTime.now()
        );
    }

    public static ItemRequestDto toItemRequestDtoWithoutItems(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                null
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> itemList) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                toItemForItemRequestDtoList(itemList)
        );
    }

    public static List<ItemForItemRequestDto> toItemForItemRequestDtoList(List<Item> itemList) {
        List<ItemForItemRequestDto> itemForItemRequestDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemForItemRequestDtoList.add(toItemForItemRequestDto(item));
        }
        return itemForItemRequestDtoList;
    }

    public static ItemForItemRequestDto toItemForItemRequestDto(Item item) {
        return new ItemForItemRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId()
        );
    }
}
