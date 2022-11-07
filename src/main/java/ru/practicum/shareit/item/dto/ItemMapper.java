package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public static ItemDto toItemDtoWithoutBooking(Item item, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null,
                null,
                null,
                comments
        );
    }

    public static Item toItem(ItemDto itemDto, Long owner) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
    }

    public static Item toItem(ItemDto itemDto, Long itemId, Long owner) {
        return new Item(
                itemId,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
    }

    public static List<ItemDto> toItemDtoList(List<Item> itemList) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(toItemDtoWithoutBooking(item, null));
        }
        return itemDtoList;
    }

    public static ItemDto toItemDto(
            Item item,
            BookingForItemDto lastBooking,
            BookingForItemDto nextBooking,
            List<CommentDto> comments
    ) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest() : null,
                lastBooking,
                nextBooking,
                comments
        );
    }
}
