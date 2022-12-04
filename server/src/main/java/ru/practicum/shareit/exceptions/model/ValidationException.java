package ru.practicum.shareit.exceptions.model;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
