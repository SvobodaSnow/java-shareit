package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, Long itemId, Long userId) {
        return new Comment(
                null,
                userId,
                itemId,
                commentDto.getText(),
                LocalDateTime.now()
        );
    }

    public static CommentDto toCommentDto(Comment comment, String authorName) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                authorName,
                comment.getCreated()
        );
    }
}
