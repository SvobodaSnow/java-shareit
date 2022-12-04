package ru.practicum.shareit.request.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Primary
@Service
public class ItemRequestServiceImp implements ItemRequestService {
    @Autowired
    private ItemRequestStorage itemRequestStorage;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        checkUser(userId);
        checkDescription(itemRequestDto);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, userService.getUserById(userId));
        return ItemRequestMapper.toItemRequestDtoWithoutItems(itemRequestStorage.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getItemRequestForUser(Long userId) {
        checkUser(userId);
        List<ItemRequest> itemRequestList = itemRequestStorage.findByRequesterId(userId);
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestList) {
            List<Item> items = itemService.getItemsByRequestId(itemRequest.getId());
            itemRequestDtoList.add(ItemRequestMapper.toItemRequestDto(itemRequest, items));
        }
        return itemRequestDtoList;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequest(Long userId, int from, int size) {
        checkUser(userId);
        int page = from / size;
        Page<ItemRequest> itemRequestList = itemRequestStorage.findAllWithoutRequesterId(
                userId,
                PageRequest.of(page, size)
        );

        List<Long> itemRequestIdList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestList) {
            itemRequestIdList.add(itemRequest.getId());
        }

        List<Item> allItemList = itemService.getItemsByRequestIdList(itemRequestIdList);

        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestList) {
            List<Item> items = new ArrayList<>();
            for (Item item : allItemList) {
                if (item.getRequest().getId().equals(itemRequest.getId())) {
                    items.add(item);
                }
            }
            itemRequestDtoList.add(ItemRequestMapper.toItemRequestDto(itemRequest, items));
        }
        return itemRequestDtoList;
    }

    @Override
    public ItemRequestDto getItemRequestById(Long itemRequestId, Long userId) {
        checkUser(userId);
        ItemRequest itemRequest = itemRequestStorage.getById(itemRequestId);
        List<Item> items = itemService.getItemsByRequestId(itemRequestId);
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    private void checkUser(Long userId) {
        userService.getUserById(userId).getName();
    }

    private void checkDescription(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isEmpty()) {
            throw new ValidationException("Не указано описание запрашиваемой вещи");
        }
    }
}
