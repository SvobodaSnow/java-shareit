package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.exceptions.model.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Primary
@Service
public class ItemServiceImp implements ItemService {
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private CommentStorage commentStorage;

    @Override
    public ItemDto createItem(Item item) {
        checkNameItem(item);
        checkDescriptionItem(item);
        checkAvailableItem(item);
        checkOwnerId(item);
        return ItemMapper.toItemDtoWithoutBooking(itemStorage.save(item), null);
    }

    @Override
    public List<ItemDto> getAllItemsByUser(Long userId) {
        List<Item> itemList = itemStorage.findByOwner(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            List<Comment> comments = commentStorage.findByItemId(item.getId());
            List<CommentDto> commentDtoList = new ArrayList<>();
            for (Comment comment : comments) {
                commentDtoList.add(CommentMapper.toCommentDto(
                        comment,
                        userService.getUserById(comment.getAuthorId()).getName())
                );
            }
            itemDtoList.add(
                    ItemMapper.toItemDto(
                        item,
                        getLastBooking(item.getId()),
                        getNextBooking(item.getId()),
                        commentDtoList
                    )
            );
        }
        return itemDtoList;
    }

    @Override
    public ItemDto getItemByIdWithBooking(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        List<Comment> comments = commentStorage.findByItemId(item.getId());
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtoList.add(CommentMapper.toCommentDto(
                    comment,
                    userService.getUserById(comment.getAuthorId()).getName())
            );
        }
        if (item.getOwner().equals(userId)) {
            return ItemMapper.toItemDto(
                    item,
                    getLastBooking(item.getId()),
                    getNextBooking(item.getId()),
                    commentDtoList
            );
        }
        return ItemMapper.toItemDtoWithoutBooking(item, commentDtoList);
    }

    @Override
    public ItemDto updateItem(Item item) {
        Item oldItem = getItemById(item.getId());
        if (!oldItem.getOwner().equals(item.getOwner())) {
            throw new NotFoundException("ID владельца: " + oldItem.getOwner() +
                    " и пользователя: " + item.getOwner() + ", изменяющего вещь, не совпадают");
        }
        if (item.getAvailable() == null && item.getName() == null && item.getDescription() == null) {
            throw new ValidationException("Не указаны новые поля для вещи");
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        return ItemMapper.toItemDtoWithoutBooking(itemStorage.save(item), null);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<Item> itemList;
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            itemList = itemStorage.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(
                            text,
                            text
                    );
            List<ItemDto> itemDtoList = new ArrayList<>();
            for (Item item : itemList) {
                List<Comment> comments = commentStorage.findByItemId(item.getId());
                List<CommentDto> commentDtoList = new ArrayList<>();
                for (Comment comment : comments) {
                    commentDtoList.add(CommentMapper.toCommentDto(
                            comment,
                            userService.getUserById(comment.getAuthorId()).getName())
                    );
                }
                itemDtoList.add(
                        ItemMapper.toItemDto(
                                item,
                                getLastBooking(item.getId()),
                                getNextBooking(item.getId()),
                                commentDtoList
                        )
                );
            }
            return itemDtoList;
        }
    }

    @Override
    public CommentDto addComment(Comment comment) {
        if (comment.getText() == null || comment.getText().isEmpty()) {
            throw new ValidationException("Текст коментария не передан");
        }
        List<Booking> bookingList = bookingService.getBookingByItemIdAndBookerId(
                comment.getAuthorId(),
                comment.getItemId()
        );
        if (bookingList.size() == 0) {
            throw new ValidationException("Пользователь c ID " + comment.getAuthorId() +
                    ", оставляющий коментарий не брал вещь в аренду");
        }
        Comment commentNew = commentStorage.save(comment);

        return CommentMapper.toCommentDto(commentNew, userService.getUserById(comment.getAuthorId()).getName());
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemStorage.getById(itemId);
    }

    private BookingForItemDto getLastBooking(Long itemId) {
        Booking lastBooking = bookingService.getLastBooking(itemId);
        BookingForItemDto lastBookingDto;
        if (lastBooking != null) {
            lastBookingDto = new BookingForItemDto(
                    lastBooking.getId(),
                    lastBooking.getBooker().getId(),
                    lastBooking.getStart(),
                    lastBooking.getEnd()
            );
        } else {
            lastBookingDto = null;
        }
        return lastBookingDto;
    }

    private BookingForItemDto getNextBooking(Long itemId) {
        Booking nextBooking = bookingService.getNextBooking(itemId);
        BookingForItemDto nextBookingDto;
        if (nextBooking != null) {
            nextBookingDto = new BookingForItemDto(
                    nextBooking.getId(),
                    nextBooking.getBooker().getId(),
                    nextBooking.getStart(),
                    nextBooking.getEnd()
            );
        } else {
            nextBookingDto = null;
        }
        return nextBookingDto;
    }

    private void checkNameItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Не указано имя предмета");
        }
    }

    private void checkDescriptionItem(Item item) {
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Не указано описание предмета");
        }
    }

    private void checkAvailableItem(Item item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("Не указана доступность товара");
        }
    }

    private void checkOwnerId(Item item) {
        userService.getUserById(item.getOwner()).getName();
    }
}
