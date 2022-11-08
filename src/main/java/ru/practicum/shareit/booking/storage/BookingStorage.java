package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByIdDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByIdDesc(Long bookerId, Status status);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByIdDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartIsAfterOrderByIdDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Booking> findByItemOwnerOrderByIdDesc(Long ownerId);

    List<Booking> findByItemOwnerAndStatusOrderByIdDesc(Long ownerId, Status status);

    List<Booking> findByItemOwnerAndEndIsBeforeOrderByIdDesc(Long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerAndStartIsAfterOrderByIdDesc(Long ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end
    );

    Booking findTopByItemIdAndStartIsAfterOrderByStart(Long itemId, LocalDateTime start);

    Booking findTopByItemIdAndStartIsBeforeOrderByStartDesc(Long itemId, LocalDateTime start);

    List<Booking> findByBookerIdAndItemIdAndStartBeforeAndStatus(
            Long bookerId,
            Long itemId,
            LocalDateTime start,
            Status status
    );
}
