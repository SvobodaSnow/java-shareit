package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestBody @Valid ItemRequestDto itemRequestDto,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на добавление нового запроса на вещь от пользователя с ID: " + userId);
        return itemRequestClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestForUser(@Positive @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на формирование списка запросов для пользователя с ID " + userId);
        return itemRequestClient.getItemRequestForUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на формирование списка всех запросов на вещи для пользователя с ID: " + userId);
        return itemRequestClient.getAllItemRequest(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @Positive @PathVariable Long requestId,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на отправку запроса на вещь с ID " + requestId);
        return itemRequestClient.getItemRequestById(requestId, userId);
    }
}
