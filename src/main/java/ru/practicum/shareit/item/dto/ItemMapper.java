package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    public static Item toItem(ItemDto itemDto, int owner) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
    }

    public static Item toItem(ItemDto itemDto, int itemId, int owner) {
        return new Item(
                itemId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
    }

    public static List<ItemDto> toItemDtoList(Map<Integer, Item> itemMap) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemMap.values()) {
            itemDtoList.add(toItemDto(item));
        }
        return itemDtoList;
    }
}
