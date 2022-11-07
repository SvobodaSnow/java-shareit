package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static Booking toBooking(BookingDtoRequest bookingDtoRequest, Item item, User booker) {
        return new Booking(
                null,
                bookingDtoRequest.getStart(),
                bookingDtoRequest.getEnd(),
                item,
                booker,
                Status.WAITING
        );
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return new BookingDtoResponse(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static List<BookingDtoResponse> toBookingDtoResponseList(List<Booking> bookingList) {
        List<BookingDtoResponse> bookingDtoResponseList = new ArrayList<>();
        for (Booking booking : bookingList) {
            bookingDtoResponseList.add(toBookingDtoResponse(booking));
        }
        return bookingDtoResponseList;
    }
}
