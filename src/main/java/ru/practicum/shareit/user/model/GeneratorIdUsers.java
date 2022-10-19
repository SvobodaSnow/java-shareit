package ru.practicum.shareit.user.model;

import org.springframework.stereotype.Service;

@Service
public class GeneratorIdUsers {
    private int id;

    public GeneratorIdUsers() {
        id = 0;
    }

    public int getId() {
        id++;
        return id;
    }
}
