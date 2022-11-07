package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@Primary
@Service
public class BookingServiceImp implements BookingService {
    @Autowired
    private BookingStorage bookingStorage;
    @Autowired
    private UserStorage userStorage;

    @Override
    public Booking createBooking(Booking booking) {
        checkAvailableItem(booking);
        checkBooker(booking);
        checkStartInPast(booking);
        checkEndInPast(booking);
        checkEndBeforeStart(booking);
        checkItemIdAndBookerId(booking);
        return bookingStorage.save(booking);
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingStorage.getById(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().equals(userId)) {
            throw new NotFoundException(
                    "Попытка получить бронирование пользователем с ID " + userId +
                    ", не являющегося ни владельцем, ни создателем запроса. " +
                    "ID владельца " + booking.getItem().getOwner() + ". " +
                    "ID создателя запроса " + booking.getBooker().getId()
            );
        }
        return booking;
    }

    @Override
    public Booking updateBookingStatus(Long bookingId, Long userId, String approved) {
        Booking booking = getBookingById(bookingId, userId);
        if (!booking.getItem().getOwner().equals(userId)) {
            throw new NotFoundException(
                    "Попытка изменить статус запроса пользователем с ID " + userId +
                    ", не являющегося владельцем вещи. Владелец вещи - пользователь с ID " +
                    booking.getItem().getOwner()
            );
        }
        if (Boolean.parseBoolean(approved) && booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (Boolean.parseBoolean(approved)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingStorage.save(booking);
    }

    @Override
    public List<Booking> getAllBookingsForUser(String stateString, Long userId) {
        State state;
        checkUser(userStorage.getById(userId));
        try {
            state = State.valueOf(stateString);
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: " + stateString);
        }
        switch (state) {
            case ALL:
                return bookingStorage.findByBookerIdOrderByIdDesc(userId);
            case WAITING:
                return bookingStorage.findByBookerIdAndStatusOrderByIdDesc(userId, Status.WAITING);
            case REJECTED:
                return bookingStorage.findByBookerIdAndStatusOrderByIdDesc(userId, Status.REJECTED);
            case PAST:
                return bookingStorage.findByBookerIdAndEndIsBeforeOrderByIdDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingStorage.findByBookerIdAndStartIsAfterOrderByIdDesc(userId, LocalDateTime.now());
            case CURRENT:
                return bookingStorage.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );
            default:
                throw new NotFoundException("Что-то пошло не так");
        }
    }

    @Override
    public List<Booking> getAllBookingsForOwner(String stateString, Long userId) {
        State state;
        checkUser(userStorage.getById(userId));
        try {
            state = State.valueOf(stateString);
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: " + stateString);
        }
        switch (state) {
            case ALL:
                return bookingStorage.findByItemOwnerOrderByIdDesc(userId);
            case WAITING:
                return bookingStorage.findByItemOwnerAndStatusOrderByIdDesc(userId, Status.WAITING);
            case REJECTED:
                return bookingStorage.findByItemOwnerAndStatusOrderByIdDesc(userId, Status.REJECTED);
            case PAST:
                return bookingStorage.findByItemOwnerAndEndIsBeforeOrderByIdDesc(userId, LocalDateTime.now());
            case FUTURE:
                return bookingStorage.findByItemOwnerAndStartIsAfterOrderByIdDesc(userId, LocalDateTime.now());
            case CURRENT:
                return bookingStorage.findByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );
            default:
                throw new NotFoundException("Что-то пошло не так");
        }
    }

    @Override
    public Booking getLastBooking(Long itemId) {
        return bookingStorage.findTopByItemIdAndStartIsBeforeOrderByStartDesc(itemId, LocalDateTime.now());
    }

    @Override
    public Booking getNextBooking(Long itemId) {
        return bookingStorage.findTopByItemIdAndStartIsAfterOrderByStart(itemId, LocalDateTime.now());
    }

    @Override
    public List<Booking> getBookingByItemIdAndBookerId(Long bookerId, Long itemId) {
        return bookingStorage.findByBookerIdAndItemIdAndStartBeforeAndStatus(
                bookerId,
                itemId,
                LocalDateTime.now(),
                Status.APPROVED
        );
    }

    private void checkAvailableItem(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Вещь с ID " + booking.getItem().getId() + " не доступна для бронирования");
        }
    }

    private void checkEndInPast(Booking booking) {
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Окончание бронирования в прошлом");
        }
    }

    private void checkBooker(Booking booking) {
        booking.getBooker().getName();
    }

    private void checkStartInPast(Booking booking) {
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало бронирования в прошлом");
        }
    }

    private void checkEndBeforeStart(Booking booking) {
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Начало бронирования позже оконччания");
        }
    }

    private void checkUser(User user) {
        user.getName();
    }

    private void checkItemIdAndBookerId(Booking booking) {
        if (booking.getBooker().getId().equals(booking.getItem().getOwner())) {
            throw new NotFoundException("Создатель бронирования владелец вещи");
        }
    }
}
