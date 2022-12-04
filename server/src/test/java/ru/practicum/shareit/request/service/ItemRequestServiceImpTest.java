package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImpTest {

    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final EntityManager em;

    private final UserDto requesterUserDto = new UserDto(
            15L,
            "UserNameTest",
            "UserNameTest@UserNameTest.ru"
    );

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            20L,
            "ItemRequestDescription",
            LocalDateTime.now(),
            null
    );

    @Test
    void saveNewItemRequestTest() {
        UserDto newRequesterDto = createBookerDto();
        ItemRequestDto newItemRequestDto = createItemRequestDto(newRequesterDto.getId());

        TypedQuery<ItemRequest> query = em.createQuery(
                "Select i from ItemRequest i where i.id = :id",
                ItemRequest.class
        );
        ItemRequest itemRequest = query.setParameter("id", newItemRequestDto.getId()).getSingleResult();

        assertThat(itemRequest.getId(), equalTo(newItemRequestDto.getId()));
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getRequester().getId(), equalTo(newRequesterDto.getId()));
        assertThat(itemRequest.getRequester().getName(), equalTo(newRequesterDto.getName()));
        assertThat(itemRequest.getRequester().getEmail(), equalTo(newRequesterDto.getEmail()));
        assertNotNull(itemRequest.getCreated());
    }

    @Test
    void saveNewItemRequestTestIllegalRequesterId() {
        UserDto newRequesterDto = createBookerDto();

        Throwable throwable = assertThrows(
                EntityNotFoundException.class,
                () -> createItemRequestDto(-1L)
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Unable to find ru.practicum.shareit.user.model.User with id -1")
        );
    }

//    @Test
//    void saveNewItemRequestTestEmptyDescription() {
//        UserDto newRequesterDto = createBookerDto();
//        ItemRequestDto itemRequestDtoEmptyDescription = new ItemRequestDto(
//                20L,
//                "",
//                LocalDateTime.now(),
//                null
//        );
//
//        Throwable throwable = assertThrows(
//                ValidationException.class,
//                () -> itemRequestService.createItemRequest(itemRequestDtoEmptyDescription, newRequesterDto.getId())
//        );
//
//        assertThat(
//                throwable.getMessage(),
//                equalTo("Не указано описание запрашиваемой вещи")
//        );
//    }

    @Test
    void getItemRequestForUserTest() {
        UserDto newRequesterDto = createBookerDto();
        ItemRequestDto newItemRequestDto = createItemRequestDto(newRequesterDto.getId());

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getItemRequestForUser(newRequesterDto.getId());

        assertThat(itemRequestDtoList.size(), equalTo(1));
        assertThat(itemRequestDtoList.get(0).getId(), equalTo(newItemRequestDto.getId()));
        assertThat(itemRequestDtoList.get(0).getDescription(), equalTo(newItemRequestDto.getDescription()));
        assertNotNull(itemRequestDtoList.get(0).getCreated());
    }

    @Test
    void getAllItemRequestTest() {
        UserDto newRequesterDto = createBookerDto();
        ItemRequestDto newItemRequestDto = createItemRequestDto(newRequesterDto.getId());

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAllItemRequest(
                newRequesterDto.getId(),
                0,
                10
        );

        assertThat(itemRequestDtoList.size(), equalTo(0));

        UserDto userDto = new UserDto(
                15L,
                "NameTest",
                "NameTest@NameTest.ru"
        );

        UserDto newUserDto = userService.createUser(userDto);

        itemRequestDtoList = itemRequestService.getAllItemRequest(
                newUserDto.getId(),
                0,
                10
        );

        assertThat(itemRequestDtoList.size(), equalTo(1));
        assertThat(itemRequestDtoList.get(0).getId(), equalTo(newItemRequestDto.getId()));
        assertThat(itemRequestDtoList.get(0).getDescription(), equalTo(newItemRequestDto.getDescription()));
        assertNotNull(itemRequestDtoList.get(0).getCreated());
    }

//    @Test
//    void getAllItemRequestTestIllegalFromParameter() {
//        UserDto newRequesterDto = createBookerDto();
//        ItemRequestDto newItemRequestDto = createItemRequestDto(newRequesterDto.getId());
//
//        Throwable throwable = assertThrows(
//                ValidationException.class,
//                () -> itemRequestService.getAllItemRequest(
//                        newRequesterDto.getId(),
//                        -1,
//                        10
//                )
//        );
//
//        assertThat(throwable.getMessage(), equalTo("Не верно указано значение первого элемента страницы. " +
//                "Переданное значение: -1"));
//    }
//
//    @Test
//    void getAllItemRequestTestIllegalSizeParameter() {
//        UserDto newRequesterDto = createBookerDto();
//        ItemRequestDto newItemRequestDto = createItemRequestDto(newRequesterDto.getId());
//
//        Throwable throwable = assertThrows(
//                ValidationException.class,
//                () -> itemRequestService.getAllItemRequest(
//                        newRequesterDto.getId(),
//                        0,
//                        -10
//                )
//        );
//
//        assertThat(throwable.getMessage(), equalTo("Не верно указано значение размера страницы. " +
//                "Переданное значение: -10"));
//    }

    @Test
    void getItemRequestByIdTest() {
        UserDto newRequesterDto = createBookerDto();
        ItemRequestDto newItemRequestDto = createItemRequestDto(newRequesterDto.getId());

        ItemRequestDto itemRequestDtoResponse = itemRequestService.getItemRequestById(
                newItemRequestDto.getId(),
                newRequesterDto.getId()
        );

        assertNotNull(itemRequestDtoResponse.getId());
        assertThat(itemRequestDtoResponse.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertNotNull(itemRequestDtoResponse.getCreated());
    }

    @Test
    void getItemRequestByIdTestWithItem() {
        UserDto newRequesterDto = createBookerDto();
        ItemRequestDto newItemRequestDto = createItemRequestDto(newRequesterDto.getId());

        UserDto userDto = new UserDto(
                15L,
                "NameTest",
                "NameTest@NameTest.ru"
        );
        UserDto newUserDto = userService.createUser(userDto);

        ItemDtoRequest itemDtoRequest = new ItemDtoRequest(
                "ItemNameTest",
                "ItemDescriptionTest",
                true,
                newItemRequestDto.getId()
        );

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        ItemRequestDto itemRequestDtoResponse = itemRequestService.getItemRequestById(
                newItemRequestDto.getId(),
                newRequesterDto.getId()
        );

        assertNotNull(itemRequestDtoResponse.getId());
        assertThat(itemRequestDtoResponse.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertNotNull(itemRequestDtoResponse.getCreated());
        assertThat(itemRequestDtoResponse.getItems().size(), equalTo(1));
        assertThat(itemRequestDtoResponse.getItems().get(0).getId(), equalTo(itemDtoResponse.getId()));
        assertThat(itemRequestDtoResponse.getItems().get(0).getAvailable(), equalTo(itemDtoResponse.getAvailable()));
        assertThat(
                itemRequestDtoResponse.getItems().get(0).getDescription(),
                equalTo(itemDtoResponse.getDescription())
        );
        assertThat(itemRequestDtoResponse.getItems().get(0).getName(), equalTo(itemDtoResponse.getName()));
        assertThat(itemRequestDtoResponse.getItems().get(0).getRequestId(), equalTo(newItemRequestDto.getId()));
    }

    private UserDto createBookerDto() {
        UserDto newUserDto = userService.createUser(requesterUserDto);

        assertThat(newUserDto.getName(), equalTo(requesterUserDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(requesterUserDto.getEmail()));

        return newUserDto;
    }

    private ItemRequestDto createItemRequestDto(Long bookerId) {
        ItemRequestDto newItemRequestDto = itemRequestService.createItemRequest(itemRequestDto, bookerId);

        assertNotNull(newItemRequestDto.getId());
        assertThat(newItemRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertNotNull(newItemRequestDto.getCreated());

        return newItemRequestDto;
    }
}