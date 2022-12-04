package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Primary
@Service
public class BookingServiceImp implements BookingService {
    @Autowired
    private BookingStorage bookingStorage;
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;

    @Override
    public BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, Long bookerId) {
        Booking booking = BookingMapper.toBooking(
                bookingDtoRequest,
                itemStorage.getById(bookingDtoRequest.getItemId()),
                userStorage.getById(bookerId)
        );
        checkAvailableItem(booking);
        checkBooker(booking);
        checkItemIdAndBookerId(booking);
        return BookingMapper.toBookingDtoResponse(bookingStorage.save(booking));
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
    public BookingDtoResponse getBookingDtoById(Long bookingId, Long userId) {
        return BookingMapper.toBookingDtoResponse(getBookingById(bookingId, userId));
    }

    @Override
    public BookingDtoResponse updateBookingStatus(Long bookingId, Long userId, String approved) {
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
        return BookingMapper.toBookingDtoResponse(bookingStorage.save(booking));
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsForUser(String stateString, Long userId, int from, int size) {
        State state;
        checkUser(userStorage.getById(userId));
        int page = from / size;
        try {
            state = State.valueOf(stateString);
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: " + stateString);
        }
        List<Booking> bookingList = new ArrayList<>();
        switch (state) {
            case ALL:
                bookingList = bookingStorage.findByBookerIdOrderByIdDesc(userId, PageRequest.of(page, size));
                break;
            case WAITING:
                bookingList = bookingStorage.findByBookerIdAndStatusOrderByIdDesc(
                        userId,
                        Status.WAITING,
                        PageRequest.of(page, size)
                );
                break;
            case REJECTED:
                bookingList = bookingStorage.findByBookerIdAndStatusOrderByIdDesc(
                        userId,
                        Status.REJECTED,
                        PageRequest.of(page, size)
                );
                break;
            case PAST:
                bookingList = bookingStorage.findByBookerIdAndEndIsBeforeOrderByIdDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(page, size)
                );
                break;
            case FUTURE:
                bookingList = bookingStorage.findByBookerIdAndStartIsAfterOrderByIdDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(page, size)
                );
                break;
            case CURRENT:
                bookingList = bookingStorage.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        PageRequest.of(page, size)
                );
                break;
        }
        return BookingMapper.toBookingDtoResponseList(bookingList);
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsForOwner(String stateString, Long userId, int from, int size) {
        State state;
        checkUser(userStorage.getById(userId));
        int page = from / size;
        try {
            state = State.valueOf(stateString);
        } catch (RuntimeException e) {
            throw new ValidationException("Unknown state: " + stateString);
        }
        List<Booking> bookingList = new ArrayList<>();
        switch (state) {
            case ALL:
                bookingList = bookingStorage.findByItemOwnerOrderByIdDesc(userId, PageRequest.of(page, size));
                break;
            case WAITING:
                bookingList = bookingStorage.findByItemOwnerAndStatusOrderByIdDesc(
                        userId,
                        Status.WAITING,
                        PageRequest.of(page, size)
                );
                break;
            case REJECTED:
                bookingList = bookingStorage.findByItemOwnerAndStatusOrderByIdDesc(
                        userId,
                        Status.REJECTED,
                        PageRequest.of(page, size)
                );
                break;
            case PAST:
                bookingList = bookingStorage.findByItemOwnerAndEndIsBeforeOrderByIdDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(page, size)
                );
                break;
            case FUTURE:
                bookingList = bookingStorage.findByItemOwnerAndStartIsAfterOrderByIdDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(page, size)
                );
                break;
            case CURRENT:
                bookingList = bookingStorage.findByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        PageRequest.of(page, size)
                );
                break;
        }
        return BookingMapper.toBookingDtoResponseList(bookingList);
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

    private void checkBooker(Booking booking) {
        booking.getBooker().getName();
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
