package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking);

    Booking getBookingById(Long bookingId, Long userId);

    Booking updateBookingStatus(Long bookingId, Long userId, String status);

    List<Booking> getAllBookingsForUser(String state, Long userId);

    List<Booking> getAllBookingsForOwner(String stateString, Long userId);

    Booking getLastBooking(Long ownerId);

    Booking getNextBooking(Long ownerId);

    List<Booking> getBookingByItemIdAndBookerId(Long bookerId, Long itemId);
}
