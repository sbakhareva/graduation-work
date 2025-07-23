package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.model.CommentEntity;

import java.util.Collections;
import java.util.List;

/**
 * Маппер для преобразования списка комментариев {@link CommentEntity} в объект {@link Comments}
 */

@Component
public class CommentsDTOMapper {

    private final CommentDTOMapper commentDTOMapper;

    public CommentsDTOMapper(CommentDTOMapper commentDTOMapper) {
        this.commentDTOMapper = commentDTOMapper;
    }

    public Comments toDto(List<CommentEntity> comments) {
        if (comments == null || comments.isEmpty()) {
            return new Comments(0, Collections.EMPTY_LIST);
        }

        List<Comment> commentsDto = comments.stream()
                .map(commentDTOMapper::toDto)
                .toList();

        return new Comments(commentsDto.size(), commentsDto);
    }
}
