package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Получен запрос на формирование бронирования от пользвателя с ID: " + userId);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(
            @Positive @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid BookItemRequestDto requestDto
    ) {
        log.info("Получен запрос на создание бронирования от пользвателя с ID: " + userId +
                " на вещь с ID: " + requestDto.getItemId());
        return bookingClient.createBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @Positive @RequestHeader("X-Sharer-User-Id") long userId,
            @Positive @PathVariable Long bookingId
    ) {
        log.info("Получен запрос на формирование бронирования от пользвателя с ID: " + userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestParam String approved,
            @Positive @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на обновление статуса запроса. Параметр обновления: " + approved +
                ". Запрос получен от пользователя с ID: " + userId);
        return bookingClient.updateBookingStatus(approved, bookingId, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsForOwner(
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "state", defaultValue = "all") String stateParam,
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Получен запрос на формирование списка бронирований для вещей владельца с ID " + userId);
        return bookingClient.getAllBookingsForOwner(from, size, state, userId);
    }
}
