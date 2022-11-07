package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @PostMapping
    public BookingDtoResponse createBooking(@RequestBody BookingDtoRequest bookingDtoRequest,
                                            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("Получен запрос на создание бронирования");
        return BookingMapper.toBookingDtoResponse(
                bookingService.createBooking(
                        BookingMapper.toBooking(bookingDtoRequest,
                                itemService.getItemById(bookingDtoRequest.getItemId()),
                                userService.getUserById(bookerId))
                )
        );
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на формирование бронирования");
        return BookingMapper.toBookingDtoResponse(bookingService.getBookingById(bookingId, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBookingStatus(
            @RequestParam String approved,
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Получен запрос на обновление статуса запроса");
        return BookingMapper.toBookingDtoResponse(bookingService.updateBookingStatus(bookingId, userId, approved));
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsForUser(@RequestParam(defaultValue = "ALL") String state,
                                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос для формирования списка бронирований пользователя");
        return BookingMapper.toBookingDtoResponseList(bookingService.getAllBookingsForUser(state, userId));
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingsForOwner(@RequestParam(defaultValue = "ALL") String state,
                                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на формирование списка бронирований для вещей владельца с ID " + userId);
        return BookingMapper.toBookingDtoResponseList(bookingService.getAllBookingsForOwner(state, userId));
    }
}
