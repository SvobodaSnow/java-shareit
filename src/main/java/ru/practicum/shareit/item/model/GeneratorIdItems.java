package ru.practicum.shareit.item.model;

import org.springframework.stereotype.Service;

@Service
public class GeneratorIdItems {
    private int id;

    public GeneratorIdItems() {
        id = 0;
    }

    public int getId() {
        id++;
        return id;
    }
}
