package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@TestPropertySource(properties = {"db.name=test"})
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final Item item = new Item(
            20L,
            "ItemTestName",
            "ItemTestDescription",
            true,
            10L,
            null
    );

    private final User user = new User(
            10L,
            "UserTestName",
            "UserTestName@UserTestName.ru"
    );

    private final BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
            20L,
            LocalDateTime.now().plusMinutes(1),
            LocalDateTime.now().plusMinutes(1).plusDays(1)
    );

    private final BookingDtoResponse bookingDtoResponse = new BookingDtoResponse(
            20L,
            bookingDtoRequest.getStart(),
            bookingDtoRequest.getEnd(),
            item,
            user,
            Status.WAITING
    );

    @Test
    void saveNewBookingTest() throws Exception {
        Mockito.when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingDtoResponse);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item.id").value(bookingDtoResponse.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDtoResponse.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingDtoResponse.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingDtoResponse.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.owner").value(bookingDtoResponse.getItem().getOwner()))
                .andExpect(jsonPath("$.item.request").value(bookingDtoResponse.getItem().getRequest()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoResponse.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingDtoResponse.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingDtoResponse.getBooker().getEmail()))
                .andExpect(jsonPath("$.status").value(bookingDtoResponse.getStatus().toString()));
    }

    @Test
    void getBookingByIdTest() throws Exception {
        Mockito.when(bookingService.getBookingDtoById(anyLong(), anyLong())).thenReturn(bookingDtoResponse);

        mvc.perform(get("/bookings/20")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item.id").value(bookingDtoResponse.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDtoResponse.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingDtoResponse.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingDtoResponse.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.owner").value(bookingDtoResponse.getItem().getOwner()))
                .andExpect(jsonPath("$.item.request").value(bookingDtoResponse.getItem().getRequest()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoResponse.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingDtoResponse.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingDtoResponse.getBooker().getEmail()))
                .andExpect(jsonPath("$.status").value(bookingDtoResponse.getStatus().toString()));
    }

    @Test
    void updateBookingStatusTest() throws Exception {
        Mockito.when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyString()))
                .thenReturn(bookingDtoResponse);

        mvc.perform(patch("/bookings/20?approved=true")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item.id").value(bookingDtoResponse.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDtoResponse.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingDtoResponse.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingDtoResponse.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.owner").value(bookingDtoResponse.getItem().getOwner()))
                .andExpect(jsonPath("$.item.request").value(bookingDtoResponse.getItem().getRequest()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoResponse.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingDtoResponse.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingDtoResponse.getBooker().getEmail()))
                .andExpect(jsonPath("$.status").value(bookingDtoResponse.getStatus().toString()));
    }

    @Test
    void getAllBookingsForUserTest() throws Exception {
        Mockito.when(bookingService.getAllBookingsForUser(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoResponse));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]['id']").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$[0]['start']").isNotEmpty())
                .andExpect(jsonPath("$[0]['end']").isNotEmpty())
                .andExpect(jsonPath("$[0]['item']['id']")
                        .value(bookingDtoResponse.getItem().getId()))
                .andExpect(jsonPath("$[0]['item']['name']")
                        .value(bookingDtoResponse.getItem().getName()))
                .andExpect(jsonPath("$[0]['item']['description']")
                        .value(bookingDtoResponse.getItem().getDescription()))
                .andExpect(jsonPath("$[0]['item']['available']")
                        .value(bookingDtoResponse.getItem().getAvailable()))
                .andExpect(jsonPath("$[0]['item']['owner']")
                        .value(bookingDtoResponse.getItem().getOwner()))
                .andExpect(jsonPath("$[0]['item']['request']")
                        .value(bookingDtoResponse.getItem().getRequest()))
                .andExpect(jsonPath("$[0]['booker']['id']")
                        .value(bookingDtoResponse.getBooker().getId()))
                .andExpect(jsonPath("$[0]['booker']['name']")
                        .value(bookingDtoResponse.getBooker().getName()))
                .andExpect(jsonPath("$[0]['booker']['email']")
                        .value(bookingDtoResponse.getBooker().getEmail()))
                .andExpect(jsonPath("$[0]['status']")
                        .value(bookingDtoResponse.getStatus().toString()));
    }

    @Test
    void getAllBookingsForOwnerTest() throws Exception {
        Mockito.when(bookingService.getAllBookingsForOwner(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoResponse));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 20)
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]['id']").value(bookingDtoResponse.getId()))
                .andExpect(jsonPath("$[0]['start']").isNotEmpty())
                .andExpect(jsonPath("$[0]['end']").isNotEmpty())
                .andExpect(jsonPath("$[0]['item']['id']")
                        .value(bookingDtoResponse.getItem().getId()))
                .andExpect(jsonPath("$[0]['item']['name']")
                        .value(bookingDtoResponse.getItem().getName()))
                .andExpect(jsonPath("$[0]['item']['description']")
                        .value(bookingDtoResponse.getItem().getDescription()))
                .andExpect(jsonPath("$[0]['item']['available']")
                        .value(bookingDtoResponse.getItem().getAvailable()))
                .andExpect(jsonPath("$[0]['item']['owner']")
                        .value(bookingDtoResponse.getItem().getOwner()))
                .andExpect(jsonPath("$[0]['item']['request']")
                        .value(bookingDtoResponse.getItem().getRequest()))
                .andExpect(jsonPath("$[0]['booker']['id']")
                        .value(bookingDtoResponse.getBooker().getId()))
                .andExpect(jsonPath("$[0]['booker']['name']")
                        .value(bookingDtoResponse.getBooker().getName()))
                .andExpect(jsonPath("$[0]['booker']['email']")
                        .value(bookingDtoResponse.getBooker().getEmail()))
                .andExpect(jsonPath("$[0]['status']")
                        .value(bookingDtoResponse.getStatus().toString()));
    }
}