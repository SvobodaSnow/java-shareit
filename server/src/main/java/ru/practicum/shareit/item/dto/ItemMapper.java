package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Component
public class ItemMapper {

    public static ItemDtoResponse toItemDtoWithoutBooking(Item item, List<CommentDto> comments) {
        return new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                null,
                null,
                comments
        );
    }

    public static Item toItem(ItemDtoRequest itemDtoRequest, Long ownerId, ItemRequest request) {
        return new Item(
                null,
                itemDtoRequest.getName(),
                itemDtoRequest.getDescription(),
                itemDtoRequest.getAvailable(),
                ownerId,
                request
        );
    }

    public static Item toItem(ItemDtoRequest itemDtoRequest, Long itemId, Long owner, ItemRequest request) {
        return new Item(
                itemId,
                itemDtoRequest.getName(),
                itemDtoRequest.getDescription(),
                itemDtoRequest.getAvailable(),
                owner,
                request
        );
    }

    public static ItemDtoResponse toItemDto(
            Item item,
            BookingForItemDto lastBooking,
            BookingForItemDto nextBooking,
            List<CommentDto> comments
    ) {
        return new ItemDtoResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking,
                comments
        );
    }
}
