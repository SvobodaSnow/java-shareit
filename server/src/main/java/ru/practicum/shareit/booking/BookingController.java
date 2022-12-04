package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(
            @RequestBody BookingDtoRequest bookingDtoRequest,
            @RequestHeader("X-Sharer-User-Id") Long bookerId
    ) {
        log.info("Получен запрос на создание бронирования от пользвателя с ID: " + bookerId +
                " на вещь с ID: " + bookingDtoRequest.getItemId());
        return bookingService.createBooking(bookingDtoRequest, bookerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на формирование бронирования от пользвателя с ID: " + userId);
        return bookingService.getBookingDtoById(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBookingStatus(
            @RequestParam String approved,
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на обновление статуса запроса. Параметр обновления: " + approved +
                ". Запрос получен от пользователя с ID: " + userId);
        return bookingService.updateBookingStatus(bookingId, userId, approved);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsForUser(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос для формирования списка бронирований пользователя с ID: " + userId);
        return bookingService.getAllBookingsForUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingsForOwner(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на формирование списка бронирований для вещей владельца с ID " + userId);
        return bookingService.getAllBookingsForOwner(state, userId, from, size);
    }
}
