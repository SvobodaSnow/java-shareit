package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByIdDesc(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByIdDesc(Long bookerId, Status status, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByIdDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfterOrderByIdDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    List<Booking> findByItemOwnerOrderByIdDesc(Long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerAndStatusOrderByIdDesc(Long ownerId, Status status, Pageable pageable);

    List<Booking> findByItemOwnerAndEndIsBeforeOrderByIdDesc(Long ownerId, LocalDateTime end, Pageable pageable);

    List<Booking> findByItemOwnerAndStartIsAfterOrderByIdDesc(Long ownerId, LocalDateTime start, Pageable pageable);

    List<Booking> findByItemOwnerAndStartIsBeforeAndEndIsAfterOrderByIdDesc(
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
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
