package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImpTest {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final EntityManager em;

    private final UserDto userDto = new UserDto(
            15L,
            "UserNameTest",
            "UserNameTest@UserNameTest.ru"
    );

    private final UserDto userDto2 = new UserDto(
            15L,
            "User2NameTest",
            "User2NameTest@User2NameTest.ru"
    );

    private final ItemDtoRequest itemDtoRequest = new ItemDtoRequest(
            "ItemNameTest",
            "ItemDescriptionTest",
            true,
            null
    );

    private final CommentDto commentDto = new CommentDto(
            20L,
            "CommentText",
            "CommentAuthorName",
            LocalDateTime.now()
    );

    @Test
    void saveNewItemTest() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoResponse.getId()).getSingleResult();

        assertThat(item.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDtoRequest.getAvailable()));
    }

    @Test
    void saveItemTestWithEmptyName() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        ItemDtoRequest itemDtoRequestWithEmptyName = new ItemDtoRequest();
        itemDtoRequestWithEmptyName.setDescription("ItemDescription");
        itemDtoRequestWithEmptyName.setAvailable(true);

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(itemDtoRequestWithEmptyName, newUserDto.getId())
        );

        assertThat(throwable.getMessage(), equalTo("Не указано имя предмета"));
    }

    @Test
    void saveItemTestWithEmptyDescription() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        ItemDtoRequest itemDtoRequestWithEmptyDescription = new ItemDtoRequest();
        itemDtoRequestWithEmptyDescription.setName("ItemName");
        itemDtoRequestWithEmptyDescription.setAvailable(true);

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(itemDtoRequestWithEmptyDescription, newUserDto.getId())
        );

        assertThat(throwable.getMessage(), equalTo("Не указано описание предмета"));
    }

    @Test
    void saveItemTestWithEmptyAvailable() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        ItemDtoRequest itemDtoRequestWithEmptyAvailable = new ItemDtoRequest();
        itemDtoRequestWithEmptyAvailable.setDescription("ItemDescription");
        itemDtoRequestWithEmptyAvailable.setName("ItemName");

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> itemService.createItem(itemDtoRequestWithEmptyAvailable, newUserDto.getId())
        );

        assertThat(throwable.getMessage(), equalTo("Не указана доступность товара"));
    }

    @Test
    void saveItemTestWithEmptyOwnerId() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        Throwable throwable = assertThrows(
                InvalidDataAccessApiUsageException.class,
                () -> itemService.createItem(itemDtoRequest, null)
        );

        assertThat(
                throwable.getMessage(),
                equalTo("The given id must not be null!; nested exception is " +
                "java.lang.IllegalArgumentException: The given id must not be null!")
        );
    }

    @Test
    void saveItemTestWithIncorrectOwnerId() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        Throwable throwable = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.createItem(itemDtoRequest, -1L)
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Unable to find ru.practicum.shareit.user.model.User with id -1")
        );
    }

    @Test
    void getAllItemsByUserTest() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        ItemDtoResponse itemDtoResponse1 = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse1.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse1.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse1.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        ItemDtoResponse itemDtoResponse2 = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse2.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse2.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse2.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        List<ItemDtoResponse> retrievedItemDtoResponseList = itemService.getAllItemsByUser(
                newUserDto.getId(),
                0,
                10
        );

        itemDtoResponse1.setComments(new ArrayList<>());
        itemDtoResponse2.setComments(new ArrayList<>());
        List<ItemDtoResponse> itemDtoResponseList = List.of(itemDtoResponse1, itemDtoResponse2);

        assertArrayEquals(retrievedItemDtoResponseList.toArray(), itemDtoResponseList.toArray());
    }

    @Test
    void getAllItemsByUserTestIllegalSize() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> itemService.getAllItemsByUser(
                        newUserDto.getId(),
                        0,
                        -1
                )
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Не верно указано значение размера страницы. Переданное значение: -1")
        );
    }

    @Test
    void getAllItemsByUserTestIllegalFrom() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> itemService.getAllItemsByUser(
                        newUserDto.getId(),
                        -1,
                        10
                )
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Не верно указано значение первого элемента страницы. Переданное значение: -1")
        );
    }

    @Test
    void getItemsByIdTest() throws InterruptedException {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        UserDto newUserDto2 = userService.createUser(userDto2);

        assertThat(newUserDto2.getName(), equalTo(userDto2.getName()));
        assertThat(newUserDto2.getEmail(), equalTo(userDto2.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
                itemDtoResponse.getId(),
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)
        );

        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(bookingDtoRequest, newUserDto2.getId());
        bookingService.updateBookingStatus(bookingDtoResponse.getId(), newUserDto.getId(), "true");

        TimeUnit.SECONDS.sleep(3);

        CommentDto newCommentDto = itemService.addComment(commentDto, itemDtoResponse.getId(), newUserDto2.getId());

        assertThat(newCommentDto.getText(), equalTo(commentDto.getText()));
        assertThat(newCommentDto.getAuthorName(), equalTo(userDto2.getName()));
        assertNotNull(newCommentDto.getCreated());

        ItemDtoResponse retrievedItemDtoResponse = itemService.getItemByIdWithBooking(
                itemDtoResponse.getId(),
                newUserDto.getId()
        );

        assertThat(retrievedItemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(retrievedItemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(retrievedItemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));
        assertNotNull(retrievedItemDtoResponse.getComments());
        assertNotNull(retrievedItemDtoResponse.getLastBooking());
        assertNotNull(retrievedItemDtoResponse.getLastBooking().getId());
        assertThat(retrievedItemDtoResponse.getLastBooking().getBookerId(), equalTo(newUserDto2.getId()));
        assertNotNull(retrievedItemDtoResponse.getLastBooking().getStart());
        assertNotNull(retrievedItemDtoResponse.getLastBooking().getEnd());

        retrievedItemDtoResponse = itemService.getItemByIdWithBooking(
                itemDtoResponse.getId(),
                newUserDto2.getId()
        );

        assertThat(retrievedItemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(retrievedItemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(retrievedItemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));
        assertNotNull(retrievedItemDtoResponse.getComments());
        assertNull(retrievedItemDtoResponse.getLastBooking());
        assertNull(retrievedItemDtoResponse.getNextBooking());
    }

    @Test
    void updateItemTest() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        ItemDtoRequest updateItem = new ItemDtoRequest();
        updateItem.setName("UpdateNameItem");

        ItemDtoResponse updateItemDtoResponse = itemService.updateItem(
                updateItem,
                itemDtoResponse.getId(),
                newUserDto.getId()
        );

        assertThat(updateItemDtoResponse.getName(), equalTo(updateItem.getName()));
        assertThat(updateItemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(updateItemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        updateItem = new ItemDtoRequest();
        updateItem.setDescription("UpdateItemDescription");

        updateItemDtoResponse = itemService.updateItem(
                updateItem,
                itemDtoResponse.getId(),
                newUserDto.getId()
        );

        assertThat(updateItemDtoResponse.getName(), equalTo("UpdateNameItem"));
        assertThat(updateItemDtoResponse.getDescription(), equalTo("UpdateItemDescription"));
        assertThat(updateItemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));
    }

    @Test
    void updateItemTestEmptyParameters() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        ItemDtoRequest updateItem = new ItemDtoRequest();

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> itemService.updateItem(
                        updateItem,
                        itemDtoResponse.getId(),
                        newUserDto.getId()
                )
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Не указаны новые поля для вещи")
        );
    }

    @Test
    void updateItemTestIllegalOwnerId() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        ItemDtoRequest updateItem = new ItemDtoRequest();
        updateItem.setName("UpdateNameItem");

        Throwable throwable = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(
                        updateItem,
                        itemDtoResponse.getId(),
                        1L
                )
        );

        assertThat(
                throwable.getMessage(),
                equalTo("ID владельца: " + newUserDto.getId() +
                                " и пользователя: " + 1 + ", изменяющего вещь, не совпадают")
        );
    }

    @Test
    void searchItemTest() throws InterruptedException {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        UserDto newUserDto2 = userService.createUser(userDto2);

        assertThat(newUserDto2.getName(), equalTo(userDto2.getName()));
        assertThat(newUserDto2.getEmail(), equalTo(userDto2.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
                itemDtoResponse.getId(),
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)
        );

        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(bookingDtoRequest, newUserDto2.getId());
        bookingService.updateBookingStatus(bookingDtoResponse.getId(), newUserDto.getId(), "true");

        TimeUnit.SECONDS.sleep(3);

        CommentDto newCommentDto = itemService.addComment(commentDto, itemDtoResponse.getId(), newUserDto2.getId());

        assertThat(newCommentDto.getText(), equalTo(commentDto.getText()));
        assertThat(newCommentDto.getAuthorName(), equalTo(userDto2.getName()));
        assertNotNull(newCommentDto.getCreated());

        List<ItemDtoResponse> retrievedItemDtoResponseList = itemService.searchItem("test", 0, 10);

        assertThat(retrievedItemDtoResponseList.size(), equalTo(1));
        assertThat(retrievedItemDtoResponseList.get(0).getName(), equalTo(itemDtoResponse.getName()));
        assertThat(retrievedItemDtoResponseList.get(0).getDescription(), equalTo(itemDtoResponse.getDescription()));
        assertNotNull(retrievedItemDtoResponseList.get(0).getComments());
    }

    @Test
    void searchItemTestWrongSearchText() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());
        itemDtoResponse.setComments(new ArrayList<>());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        List<ItemDtoResponse> retrievedItemDtoResponseList = itemService.searchItem("", 0, 10);

        assertThat(retrievedItemDtoResponseList.size(), equalTo(0));
    }

    @Test
    void addCommentTest() throws InterruptedException {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        UserDto newUserDto2 = userService.createUser(userDto2);

        assertThat(newUserDto2.getName(), equalTo(userDto2.getName()));
        assertThat(newUserDto2.getEmail(), equalTo(userDto2.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
                itemDtoResponse.getId(),
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)
        );

        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(bookingDtoRequest, newUserDto2.getId());
        bookingService.updateBookingStatus(bookingDtoResponse.getId(), newUserDto.getId(), "true");

        TimeUnit.SECONDS.sleep(3);

        CommentDto newCommentDto = itemService.addComment(commentDto, itemDtoResponse.getId(), newUserDto2.getId());

        assertThat(newCommentDto.getText(), equalTo(commentDto.getText()));
        assertThat(newCommentDto.getAuthorName(), equalTo(userDto2.getName()));
        assertNotNull(newCommentDto.getCreated());

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment comment = query.setParameter("id", newCommentDto.getId()).getSingleResult();

        assertNotNull(comment.getId());
        assertThat(comment.getAuthor().getName(), equalTo(userDto2.getName()));
        assertThat(comment.getItemId(), equalTo(itemDtoResponse.getId()));
        assertThat(comment.getText(), equalTo(commentDto.getText()));
        assertNotNull(comment.getCreated());
    }

    @Test
    void addCommentTestIllegalUserId() {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        UserDto newUserDto2 = userService.createUser(userDto2);

        assertThat(newUserDto2.getName(), equalTo(userDto2.getName()));
        assertThat(newUserDto2.getEmail(), equalTo(userDto2.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(
                        commentDto,
                        itemDtoResponse.getId(),
                        newUserDto2.getId()
                )
        );

        assertThat(
                throwable.getMessage(),
                equalTo("Пользователь c ID " + newUserDto2.getId() +
                ", оставляющий коментарий не брал вещь в аренду"));
    }

    @Test
    void addCommentTestEmptyTextComment() throws InterruptedException {
        UserDto newUserDto = userService.createUser(userDto);

        assertThat(newUserDto.getName(), equalTo(userDto.getName()));
        assertThat(newUserDto.getEmail(), equalTo(userDto.getEmail()));

        UserDto newUserDto2 = userService.createUser(userDto2);

        assertThat(newUserDto2.getName(), equalTo(userDto2.getName()));
        assertThat(newUserDto2.getEmail(), equalTo(userDto2.getEmail()));

        ItemDtoResponse itemDtoResponse = itemService.createItem(itemDtoRequest, newUserDto.getId());

        assertThat(itemDtoResponse.getName(), equalTo(itemDtoRequest.getName()));
        assertThat(itemDtoResponse.getDescription(), equalTo(itemDtoRequest.getDescription()));
        assertThat(itemDtoResponse.getAvailable(), equalTo(itemDtoRequest.getAvailable()));

        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
                itemDtoResponse.getId(),
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2)
        );

        BookingDtoResponse bookingDtoResponse = bookingService.createBooking(bookingDtoRequest, newUserDto2.getId());
        bookingService.updateBookingStatus(bookingDtoResponse.getId(), newUserDto.getId(), "true");

        TimeUnit.SECONDS.sleep(3);

        CommentDto newCommentDto = new CommentDto();
        newCommentDto.setId(20L);
        newCommentDto.setText("");
        newCommentDto.setAuthorName("Author");

        Throwable throwable = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(
                        newCommentDto,
                        itemDtoResponse.getId(),
                        newUserDto2.getId()
                )
        );

        assertThat(throwable.getMessage(), equalTo("Текст коментария не передан"));
    }
}