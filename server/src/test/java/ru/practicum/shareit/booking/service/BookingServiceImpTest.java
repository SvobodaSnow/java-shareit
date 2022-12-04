package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImpTest {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final EntityManager em;

    private final UserDto ownerUserDto = new UserDto(
            15L,
            "OwnerNameTest",
            "OwnerNameTest@OwnerNameTest.ru"
    );

    private final UserDto bookerUserDto = new UserDto(
            -15L,
            "BookerNameDto",
            "BookerNameDto@BookerNameDto.ru"
    );

    private final ItemDtoRequest itemDtoRequest = new ItemDtoRequest(
            "ItemNameTest",
            "ItemDescriptionTest",
            true,
            null
    );

    @Test
    void createBookingTest() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        BookingDtoResponse bookingDtoResponse = createBooking(
                newOwnerUserDto,
                newBookerUserDto,
                newItemDtoResponse
        );

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDtoResponse.getId()).getSingleResult();

        assertNotNull(booking.getId());
        assertNotNull(booking.getStart());
        assertNotNull(booking.getEnd());
        assertThat(booking.getItem().getId(), equalTo(newItemDtoResponse.getId()));
        assertThat(booking.getItem().getName(), equalTo(newItemDtoResponse.getName()));
        assertThat(booking.getItem().getDescription(), equalTo(newItemDtoResponse.getDescription()));
        assertThat(booking.getItem().getAvailable(), equalTo(newItemDtoResponse.getAvailable()));
        assertThat(booking.getItem().getRequest(), equalTo(newItemDtoResponse.getRequestId()));
        assertThat(booking.getItem().getOwner(), equalTo(newOwnerUserDto.getId()));
        assertThat(booking.getBooker().getId(), equalTo(newBookerUserDto.getId()));
        assertThat(booking.getBooker().getName(), equalTo(newBookerUserDto.getName()));
        assertThat(booking.getBooker().getEmail(), equalTo(newBookerUserDto.getEmail()));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void createBookingTestWithNotAvailableItem() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        ItemDtoRequest itemDtoRequestNotAvailable = new ItemDtoRequest();
        itemDtoRequestNotAvailable.setAvailable(false);
        newItemDtoResponse = itemService.updateItem(
                itemDtoRequestNotAvailable,
                newItemDtoResponse.getId(),
                newOwnerUserDto.getId()
        );

        ItemDtoResponse finalNewItemDtoResponse = newItemDtoResponse;

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> createBooking(newOwnerUserDto, newBookerUserDto, finalNewItemDtoResponse)
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Вещь с ID " + newItemDtoResponse.getId() + " не доступна для бронирования")
        );
    }

    @Test
    void createBookingTestIllegalBookerId() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        Throwable throwable = assertThrows(
                EntityNotFoundException.class,
                () -> createBooking(newOwnerUserDto, bookerUserDto, newItemDtoResponse)
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Unable to find ru.practicum.shareit.user.model.User with id -15")
        );
    }

//    @Test
//    void createBookingTestWithStartInPass() {
//        UserDto newOwnerUserDto = createOwnerDto();
//        UserDto newBookerUserDto = createBookerDto();
//        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());
//
//        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
//                newItemDtoResponse.getId(),
//                LocalDateTime.now().minusDays(1),
//                LocalDateTime.now().plusSeconds(2)
//        );
//
//        Throwable throwable = assertThrows(
//                ValidationException.class,
//                () -> bookingService.createBooking(bookingDtoRequest, newBookerUserDto.getId())
//        );
//
//        assertThat(
//                throwable.getMessage(),
//                equalTo("Начало бронирования в прошлом")
//        );
//    }

//    @Test
//    void createBookingTestWithEndInPast() {
//        UserDto newOwnerUserDto = createOwnerDto();
//        UserDto newBookerUserDto = createBookerDto();
//        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());
//
//        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
//                newItemDtoResponse.getId(),
//                LocalDateTime.now().plusDays(1),
//                LocalDateTime.now().minusMinutes(2)
//        );
//
//        Throwable throwable = assertThrows(
//                ValidationException.class,
//                () -> bookingService.createBooking(bookingDtoRequest, newBookerUserDto.getId())
//        );
//
//        assertThat(
//                throwable.getMessage(),
//                equalTo("Окончание бронирования в прошлом")
//        );
//    }
//
//    @Test
//    void createBookingTestWithEndBeforeStart() {
//        UserDto newOwnerUserDto = createOwnerDto();
//        UserDto newBookerUserDto = createBookerDto();
//        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());
//
//        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
//                newItemDtoResponse.getId(),
//                LocalDateTime.now().plusDays(3),
//                LocalDateTime.now().plusDays(2)
//        );
//
//        Throwable throwable = assertThrows(
//                ValidationException.class,
//                () -> bookingService.createBooking(bookingDtoRequest, newBookerUserDto.getId())
//        );
//
//        assertThat(
//                throwable.getMessage(),
//                equalTo("Начало бронирования позже оконччания")
//        );
//    }

    @Test
    void createBookingTestWithBookerIsOwner() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
                newItemDtoResponse.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        Throwable throwable = assertThrows(
                NotFoundException.class,
                () -> bookingService.createBooking(bookingDtoRequest, newOwnerUserDto.getId())
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Создатель бронирования владелец вещи")
        );
    }

    @Test
    void getBookingByIdTest() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        BookingDtoResponse bookingDtoResponse = createBooking(
                newOwnerUserDto,
                newBookerUserDto,
                newItemDtoResponse
        );

        BookingDtoResponse retrievedBookingDtoResponse = bookingService.getBookingDtoById(
                bookingDtoResponse.getId(),
                newOwnerUserDto.getId()
        );

        assertNotNull(retrievedBookingDtoResponse.getId());
        assertNotNull(retrievedBookingDtoResponse.getStart());
        assertNotNull(retrievedBookingDtoResponse.getEnd());
        assertThat(retrievedBookingDtoResponse.getItem().getId(), equalTo(newItemDtoResponse.getId()));
        assertThat(retrievedBookingDtoResponse.getItem().getName(), equalTo(newItemDtoResponse.getName()));
        assertThat(retrievedBookingDtoResponse.getItem().getDescription(), equalTo(newItemDtoResponse.getDescription()));
        assertThat(retrievedBookingDtoResponse.getItem().getAvailable(), equalTo(newItemDtoResponse.getAvailable()));
        assertThat(retrievedBookingDtoResponse.getItem().getRequest(), equalTo(newItemDtoResponse.getRequestId()));
        assertThat(retrievedBookingDtoResponse.getItem().getOwner(), equalTo(newOwnerUserDto.getId()));
        assertThat(retrievedBookingDtoResponse.getBooker().getId(), equalTo(newBookerUserDto.getId()));
        assertThat(retrievedBookingDtoResponse.getBooker().getName(), equalTo(newBookerUserDto.getName()));
        assertThat(retrievedBookingDtoResponse.getBooker().getEmail(), equalTo(newBookerUserDto.getEmail()));
        assertThat(retrievedBookingDtoResponse.getStatus(), equalTo(Status.WAITING));
    }

    @Test
    void getBookingByIdTestWithIllegalUserId() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        BookingDtoResponse bookingDtoResponse = createBooking(
                newOwnerUserDto,
                newBookerUserDto,
                newItemDtoResponse
        );

        Throwable throwable = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingDtoById(bookingDtoResponse.getId(), -1L)
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Попытка получить бронирование пользователем с ID -1" +
                        ", не являющегося ни владельцем, ни создателем запроса. " +
                        "ID владельца " + newOwnerUserDto.getId() + ". " +
                        "ID создателя запроса " + newBookerUserDto.getId())
        );
    }

    @Test
    void updateBookingStatusApprovedTest() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        BookingDtoResponse bookingDtoResponse = createBooking(
                newOwnerUserDto,
                newBookerUserDto,
                newItemDtoResponse
        );

        bookingDtoResponse = bookingService.updateBookingStatus(
                bookingDtoResponse.getId(),
                newOwnerUserDto.getId(),
                "true"
        );

        assertNotNull(bookingDtoResponse.getId());
        assertNotNull(bookingDtoResponse.getStart());
        assertNotNull(bookingDtoResponse.getEnd());
        assertThat(bookingDtoResponse.getItem().getId(), equalTo(newItemDtoResponse.getId()));
        assertThat(bookingDtoResponse.getItem().getName(), equalTo(newItemDtoResponse.getName()));
        assertThat(bookingDtoResponse.getItem().getDescription(), equalTo(newItemDtoResponse.getDescription()));
        assertThat(bookingDtoResponse.getItem().getAvailable(), equalTo(newItemDtoResponse.getAvailable()));
        assertThat(bookingDtoResponse.getItem().getRequest(), equalTo(newItemDtoResponse.getRequestId()));
        assertThat(bookingDtoResponse.getItem().getOwner(), equalTo(newOwnerUserDto.getId()));
        assertThat(bookingDtoResponse.getBooker().getId(), equalTo(newBookerUserDto.getId()));
        assertThat(bookingDtoResponse.getBooker().getName(), equalTo(newBookerUserDto.getName()));
        assertThat(bookingDtoResponse.getBooker().getEmail(), equalTo(newBookerUserDto.getEmail()));
        assertThat(bookingDtoResponse.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void updateBookingStatusRejectedTest() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        BookingDtoResponse bookingDtoResponse = createBooking(
                newOwnerUserDto,
                newBookerUserDto,
                newItemDtoResponse
        );

        bookingDtoResponse = bookingService.updateBookingStatus(
                bookingDtoResponse.getId(),
                newOwnerUserDto.getId(),
                "false"
        );

        assertNotNull(bookingDtoResponse.getId());
        assertNotNull(bookingDtoResponse.getStart());
        assertNotNull(bookingDtoResponse.getEnd());
        assertThat(bookingDtoResponse.getItem().getId(), equalTo(newItemDtoResponse.getId()));
        assertThat(bookingDtoResponse.getItem().getName(), equalTo(newItemDtoResponse.getName()));
        assertThat(bookingDtoResponse.getItem().getDescription(), equalTo(newItemDtoResponse.getDescription()));
        assertThat(bookingDtoResponse.getItem().getAvailable(), equalTo(newItemDtoResponse.getAvailable()));
        assertThat(bookingDtoResponse.getItem().getRequest(), equalTo(newItemDtoResponse.getRequestId()));
        assertThat(bookingDtoResponse.getItem().getOwner(), equalTo(newOwnerUserDto.getId()));
        assertThat(bookingDtoResponse.getBooker().getId(), equalTo(newBookerUserDto.getId()));
        assertThat(bookingDtoResponse.getBooker().getName(), equalTo(newBookerUserDto.getName()));
        assertThat(bookingDtoResponse.getBooker().getEmail(), equalTo(newBookerUserDto.getEmail()));
        assertThat(bookingDtoResponse.getStatus(), equalTo(Status.REJECTED));
    }

    @Test
    void updateBookingStatusApprovedTestIllegalUserId() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        BookingDtoResponse bookingDtoResponse = createBooking(
                newOwnerUserDto,
                newBookerUserDto,
                newItemDtoResponse
        );

        Throwable throwable = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBookingStatus(
                        bookingDtoResponse.getId(),
                        newBookerUserDto.getId(),
                        "true"
                )
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Попытка изменить статус запроса пользователем с ID " + newBookerUserDto.getId() +
                        ", не являющегося владельцем вещи. Владелец вещи - пользователь с ID " +
                        newOwnerUserDto.getId())
        );
    }

    @Test
    void updateBookingStatusApprovedTestRepeatApproved() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        BookingDtoResponse bookingDtoResponse = createBooking(
                newOwnerUserDto,
                newBookerUserDto,
                newItemDtoResponse
        );

        bookingDtoResponse = bookingService.updateBookingStatus(
                bookingDtoResponse.getId(),
                newOwnerUserDto.getId(),
                "true"
        );

        assertNotNull(bookingDtoResponse.getId());
        assertNotNull(bookingDtoResponse.getStart());
        assertNotNull(bookingDtoResponse.getEnd());
        assertThat(bookingDtoResponse.getItem().getId(), equalTo(newItemDtoResponse.getId()));
        assertThat(bookingDtoResponse.getItem().getName(), equalTo(newItemDtoResponse.getName()));
        assertThat(bookingDtoResponse.getItem().getDescription(), equalTo(newItemDtoResponse.getDescription()));
        assertThat(bookingDtoResponse.getItem().getAvailable(), equalTo(newItemDtoResponse.getAvailable()));
        assertThat(bookingDtoResponse.getItem().getRequest(), equalTo(newItemDtoResponse.getRequestId()));
        assertThat(bookingDtoResponse.getItem().getOwner(), equalTo(newOwnerUserDto.getId()));
        assertThat(bookingDtoResponse.getBooker().getId(), equalTo(newBookerUserDto.getId()));
        assertThat(bookingDtoResponse.getBooker().getName(), equalTo(newBookerUserDto.getName()));
        assertThat(bookingDtoResponse.getBooker().getEmail(), equalTo(newBookerUserDto.getEmail()));
        assertThat(bookingDtoResponse.getStatus(), equalTo(Status.APPROVED));

        BookingDtoResponse finalBookingDtoResponse = bookingDtoResponse;

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> bookingService.updateBookingStatus(
                        finalBookingDtoResponse.getId(),
                        newOwnerUserDto.getId(),
                        "true"
                )
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Бронирование уже подтверждено")
        );
    }

    @Test
    void getAllBookingsForUserTest() throws InterruptedException {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        BookingDtoRequest bookingDtoApprovedRequest = new BookingDtoRequest(
                newItemDtoResponse.getId(),
                LocalDateTime.now().plusSeconds(5),
                LocalDateTime.now().plusDays(2)
        );

        BookingDtoRequest bookingDtoWaitingRequest = new BookingDtoRequest(
                newItemDtoResponse.getId(),
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4)
        );

        BookingDtoRequest bookingDtoRejectedRequest = new BookingDtoRequest(
                newItemDtoResponse.getId(),
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6)
        );

        BookingDtoRequest bookingDtoPastRequest = new BookingDtoRequest(
                newItemDtoResponse.getId(),
                LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusSeconds(4)
        );

        BookingDtoResponse bookingDtoApprovedResponse = bookingService.createBooking(
                bookingDtoApprovedRequest,
                newBookerUserDto.getId()
        );

        BookingDtoResponse bookingDtoWaitingResponse = bookingService.createBooking(
                bookingDtoWaitingRequest,
                newBookerUserDto.getId()
        );

        BookingDtoResponse bookingDtoRejectedResponse = bookingService.createBooking(
                bookingDtoRejectedRequest,
                newBookerUserDto.getId()
        );

        BookingDtoResponse bookingDtoPastResponse = bookingService.createBooking(
                bookingDtoPastRequest,
                newBookerUserDto.getId()
        );

        bookingDtoApprovedResponse = bookingService.updateBookingStatus(
                bookingDtoApprovedResponse.getId(),
                newOwnerUserDto.getId(),
                "true"
        );

        bookingDtoRejectedResponse = bookingService.updateBookingStatus(
                bookingDtoRejectedResponse.getId(),
                newOwnerUserDto.getId(),
                "false"
        );

        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getAllBookingsForUser(
                "ALL",
                newBookerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(4));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoPastResponse.getId()));
        assertThat(bookingDtoResponseList.get(1).getId(), equalTo(bookingDtoRejectedResponse.getId()));
        assertThat(bookingDtoResponseList.get(2).getId(), equalTo(bookingDtoWaitingResponse.getId()));
        assertThat(bookingDtoResponseList.get(3).getId(), equalTo(bookingDtoApprovedResponse.getId()));

        bookingDtoResponseList = bookingService.getAllBookingsForUser(
                "WAITING",
                newBookerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(2));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoPastResponse.getId()));
        assertThat(bookingDtoResponseList.get(1).getId(), equalTo(bookingDtoWaitingResponse.getId()));

        bookingDtoResponseList = bookingService.getAllBookingsForUser(
                "REJECTED",
                newBookerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(1));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoRejectedResponse.getId()));

        TimeUnit.SECONDS.sleep(6);

        bookingDtoResponseList = bookingService.getAllBookingsForUser(
                "PAST",
                newBookerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(1));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoPastResponse.getId()));

        bookingDtoResponseList = bookingService.getAllBookingsForUser(
                "FUTURE",
                newBookerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(2));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoRejectedResponse.getId()));
        assertThat(bookingDtoResponseList.get(1).getId(), equalTo(bookingDtoWaitingResponse.getId()));

        bookingDtoResponseList = bookingService.getAllBookingsForUser(
                "CURRENT",
                newBookerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(1));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoApprovedResponse.getId()));
    }

    @Test
    void getAllBookingsForOwnerTest() throws InterruptedException {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        BookingDtoRequest bookingDtoApprovedRequest = new BookingDtoRequest(
                newItemDtoResponse.getId(),
                LocalDateTime.now().plusSeconds(5),
                LocalDateTime.now().plusDays(2)
        );

        BookingDtoRequest bookingDtoWaitingRequest = new BookingDtoRequest(
                newItemDtoResponse.getId(),
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4)
        );

        BookingDtoRequest bookingDtoRejectedRequest = new BookingDtoRequest(
                newItemDtoResponse.getId(),
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(6)
        );

        BookingDtoRequest bookingDtoPastRequest = new BookingDtoRequest(
                newItemDtoResponse.getId(),
                LocalDateTime.now().plusSeconds(3),
                LocalDateTime.now().plusSeconds(4)
        );

        BookingDtoResponse bookingDtoApprovedResponse = bookingService.createBooking(
                bookingDtoApprovedRequest,
                newBookerUserDto.getId()
        );

        BookingDtoResponse bookingDtoWaitingResponse = bookingService.createBooking(
                bookingDtoWaitingRequest,
                newBookerUserDto.getId()
        );

        BookingDtoResponse bookingDtoRejectedResponse = bookingService.createBooking(
                bookingDtoRejectedRequest,
                newBookerUserDto.getId()
        );

        BookingDtoResponse bookingDtoPastResponse = bookingService.createBooking(
                bookingDtoPastRequest,
                newBookerUserDto.getId()
        );

        bookingDtoApprovedResponse = bookingService.updateBookingStatus(
                bookingDtoApprovedResponse.getId(),
                newOwnerUserDto.getId(),
                "true"
        );

        bookingDtoRejectedResponse = bookingService.updateBookingStatus(
                bookingDtoRejectedResponse.getId(),
                newOwnerUserDto.getId(),
                "false"
        );

        List<BookingDtoResponse> bookingDtoResponseList = bookingService.getAllBookingsForOwner(
                "ALL",
                newOwnerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(4));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoPastResponse.getId()));
        assertThat(bookingDtoResponseList.get(1).getId(), equalTo(bookingDtoRejectedResponse.getId()));
        assertThat(bookingDtoResponseList.get(2).getId(), equalTo(bookingDtoWaitingResponse.getId()));
        assertThat(bookingDtoResponseList.get(3).getId(), equalTo(bookingDtoApprovedResponse.getId()));

        bookingDtoResponseList = bookingService.getAllBookingsForOwner(
                "WAITING",
                newOwnerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(2));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoPastResponse.getId()));
        assertThat(bookingDtoResponseList.get(1).getId(), equalTo(bookingDtoWaitingResponse.getId()));

        bookingDtoResponseList = bookingService.getAllBookingsForOwner(
                "REJECTED",
                newOwnerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(1));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoRejectedResponse.getId()));

        TimeUnit.SECONDS.sleep(6);

        bookingDtoResponseList = bookingService.getAllBookingsForOwner(
                "PAST",
                newOwnerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(1));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoPastResponse.getId()));

        bookingDtoResponseList = bookingService.getAllBookingsForOwner(
                "FUTURE",
                newOwnerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(2));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoRejectedResponse.getId()));
        assertThat(bookingDtoResponseList.get(1).getId(), equalTo(bookingDtoWaitingResponse.getId()));

        bookingDtoResponseList = bookingService.getAllBookingsForOwner(
                "CURRENT",
                newOwnerUserDto.getId(),
                0,
                10
        );

        assertThat(bookingDtoResponseList.size(), equalTo(1));
        assertThat(bookingDtoResponseList.get(0).getId(), equalTo(bookingDtoApprovedResponse.getId()));
    }

    @Test
    void getAllBookingsForOwnerTestUnknownState() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingsForOwner(
                        "UNKNOWN_STATE",
                        newOwnerUserDto.getId(),
                        0,
                        20
                )
        );

        assertThat(throwable.getMessage(), equalTo("Unknown state: UNKNOWN_STATE"));
    }

    @Test
    void getAllBookingsForUserTestUnknownState() {
        UserDto newOwnerUserDto = createOwnerDto();
        UserDto newBookerUserDto = createBookerDto();
        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> bookingService.getAllBookingsForUser(
                        "UNKNOWN_STATE",
                        newBookerUserDto.getId(),
                        0,
                        20
                )
        );

        assertThat(throwable.getMessage(), equalTo("Unknown state: UNKNOWN_STATE"));
    }

//    @Test
//    void getAllBookingsForUserTestIllegalFromParameter() {
//        UserDto newOwnerUserDto = createOwnerDto();
//        UserDto newBookerUserDto = createBookerDto();
//        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());
//
//        Throwable throwable = assertThrows(
//                ValidationException.class,
//                () -> bookingService.getAllBookingsForUser(
//                        "UNKNOWN_STATE",
//                        newBookerUserDto.getId(),
//                        -1,
//                        20
//                )
//        );
//
//        assertThat(throwable.getMessage(), equalTo("Не верно указано значение первого элемента страницы. " +
//                "Переданное значение: -1"));
//    }
//
//    @Test
//    void getAllBookingsForUserTestIllegalSizeParameter() {
//        UserDto newOwnerUserDto = createOwnerDto();
//        UserDto newBookerUserDto = createBookerDto();
//        ItemDtoResponse newItemDtoResponse = createItem(newOwnerUserDto.getId());
//
//        Throwable throwable = assertThrows(
//                ValidationException.class,
//                () -> bookingService.getAllBookingsForUser(
//                        "UNKNOWN_STATE",
//                        newBookerUserDto.getId(),
//                        0,
//                        -20
//                )
//        );
//
//        assertThat(
//                throwable.getMessage(),
//                equalTo("Не верно указано значение размера страницы. Переданное значение: -20")
//        );
//    }

    private UserDto createOwnerDto() {
        UserDto newUserDto = userService.createUser(ownerUserDto);

        assertThat(newUserDto.getName(), equalTo(ownerUserDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(ownerUserDto.getEmail()));

        return newUserDto;
    }

    private UserDto createBookerDto() {
        UserDto newUserDto = userService.createUser(bookerUserDto);

        assertThat(newUserDto.getName(), equalTo(bookerUserDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(bookerUserDto.getEmail()));

        return newUserDto;
    }

    private ItemDtoResponse createItem(Long ownerId) {
        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, ownerId);

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        return itemDtoResponse;
    }

    private BookingDtoResponse createBooking(
            UserDto newOwnerUserDto,
            UserDto newBookerUserDto,
            ItemDtoResponse newItemDtoResponse
    ) {
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
                newItemDtoResponse.getId(),
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)
        );

        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(
                bookingDtoRequest,
                newBookerUserDto.getId()
        );

        assertNotNull(bookingDtoResponse.getId());
        assertNotNull(bookingDtoResponse.getStart());
        assertNotNull(bookingDtoResponse.getEnd());
        assertThat(bookingDtoResponse.getItem().getId(), equalTo(newItemDtoResponse.getId()));
        assertThat(bookingDtoResponse.getItem().getName(), equalTo(newItemDtoResponse.getName()));
        assertThat(bookingDtoResponse.getItem().getDescription(), equalTo(newItemDtoResponse.getDescription()));
        assertThat(bookingDtoResponse.getItem().getAvailable(), equalTo(newItemDtoResponse.getAvailable()));
        assertThat(bookingDtoResponse.getItem().getRequest(), equalTo(newItemDtoResponse.getRequestId()));
        assertThat(bookingDtoResponse.getItem().getOwner(), equalTo(newOwnerUserDto.getId()));
        assertThat(bookingDtoResponse.getBooker().getId(), equalTo(newBookerUserDto.getId()));
        assertThat(bookingDtoResponse.getBooker().getName(), equalTo(newBookerUserDto.getName()));
        assertThat(bookingDtoResponse.getBooker().getEmail(), equalTo(newBookerUserDto.getEmail()));
        assertThat(bookingDtoResponse.getStatus(), equalTo(Status.WAITING));

        return bookingDtoResponse;
    }
}