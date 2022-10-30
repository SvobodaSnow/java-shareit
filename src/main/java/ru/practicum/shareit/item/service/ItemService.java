package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Map;

public interface ItemService {
    Item createItem(Item item);

    Map<Integer, Item> getAllItemsByUser(int userId);

    Item getItemById(int itemId);

    Item updateItem(Item item);

    Map<Integer, Item> searchItem(String text);
}
