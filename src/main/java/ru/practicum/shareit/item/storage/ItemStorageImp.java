package ru.practicum.shareit.item.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.GeneratorIdItems;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;

@Primary
@Service
public class ItemStorageImp implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();

    @Autowired
    private GeneratorIdItems generatorId;

    @Override
    public Item addItem(Item item) {
        item.setId(generatorId.getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(int id) {
        return items.get(id);
    }

    @Override
    public Map<Integer, Item> getAllItems() {
        return items;
    }
}
