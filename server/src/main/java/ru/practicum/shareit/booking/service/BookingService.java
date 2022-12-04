package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, Long bookerId);

    Booking getBookingById(Long bookingId, Long userId);

    BookingDtoResponse getBookingDtoById(Long bookingId, Long userId);

    BookingDtoResponse updateBookingStatus(Long bookingId, Long userId, String status);

    List<BookingDtoResponse> getAllBookingsForUser(String state, Long userId, int from, int size);

    List<BookingDtoResponse> getAllBookingsForOwner(String stateString, Long userId, int from, int size);

    Booking getLastBooking(Long ownerId);

    Booking getNextBooking(Long ownerId);

    List<Booking> getBookingByItemIdAndBookerId(Long bookerId, Long itemId);
}
