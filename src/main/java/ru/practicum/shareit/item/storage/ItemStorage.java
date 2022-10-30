package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Map;

public interface ItemStorage {
    Item addItem(Item item);

    Item getItemById(int id);

    Map<Integer, Item> getAllItems();
}
