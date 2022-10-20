package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Primary
@Service
public class ItemServiceImp implements ItemService {
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserService userService;

    @Override
    public Item createItem(Item item) {
        checkNameItem(item);
        checkDescriptionItem(item);
        checkAvailableItem(item);
        userService.getUserById(item.getOwner());
        return itemStorage.addItem(item);
    }

    @Override
    public Map<Integer, Item> getAllItemsByUser(int userId) {
        Map<Integer, Item> items = new HashMap<>();
        for (Item item : itemStorage.getAllItems().values()) {
            if (item.getOwner() == userId) {
                items.put(item.getId(), item);
            }
        }
        return items;
    }

    @Override
    public Item getItemById(int itemId) {
        Item item = itemStorage.getItemById(itemId);
        if (item == null) {
            throw new NotFoundException("Вещи с ID " + itemId + " нет в списке");
        }
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Item oldItem = getItemById(item.getId());
        if (oldItem.getOwner() != item.getOwner()) {
            throw new NotFoundException("ID владельца: " + oldItem.getOwner() +
                    " и пользователя: " + item.getOwner() + ", изменяющего вещь, не совпадают");
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        return getItemById(item.getId());
    }

    @Override
    public Map<Integer, Item> searchItem(String text) {
        Map<Integer, Item> items = new HashMap<>();
        if (!text.isEmpty()) {
            for (Item item : itemStorage.getAllItems().values()) {
                if (item.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                        item.getDescription().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) &&
                                item.getAvailable()) {
                    items.put(item.getId(), item);
                }
            }
        }
        return items;
    }

    private void checkNameItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Не указано имя предмета");
        }
    }

    private void checkDescriptionItem(Item item) {
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Не указано описание предмета");
        }
    }

    private void checkAvailableItem(Item item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("Не указана доступность товара");
        }
    }
}
