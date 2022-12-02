package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getItemRequestForUser(Long userId);

    List<ItemRequestDto> getAllItemRequest(Long userId, int from, int size);

    ItemRequestDto getItemRequestById(Long itemRequestId, Long userId);
}
